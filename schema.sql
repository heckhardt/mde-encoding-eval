-- TABLES --

create table run
(
    id               integer generated always as identity
        constraint run_pk
            primary key,
    problem          text  not null,
    instance         text  not null,
    representation   text  not null,
    algorithm        text  not null,
    configuration    jsonb not null,
    total_iterations integer
);

create table solution
(
    id          integer generated always as identity
        constraint solution_pk
            primary key,
    run_id      integer            not null
        constraint solution_run_id_fk
            references run on delete cascade,
    iteration   integer            not null,
    objectives  double precision[] not null,
    constraints double precision[]
);

create index solution_run_id_iteration_index
    on solution (run_id, iteration);

create table stats
(
    id               integer generated always as identity
        constraint stats_pk
            primary key,
    run_id           integer not null
        constraint stats_run_id_fk
            references run on delete cascade,
    iteration        integer not null,
    step             bigint,
    evaluate         bigint,
    copy             bigint,
    match            bigint,
    mutate           bigint,
    should_terminate bigint,
    hypervolume      double precision
);

create index stats_run_id_iteration_index
    on stats (run_id, iteration);


-- FUNCTIONS & AGGREGATES --

create or replace function array_min(a double precision[], b double precision[])
    returns double precision[]
as
$$
declare
    index  integer;
    result double precision[];
begin
    for index in 1..greatest(cardinality(a), cardinality(b))
        loop
            if a[index] < b[index] then
                result[index] := a[index];
            else
                result[index] := b[index];
            end if;
        end loop;

    return result;
end;
$$
    language plpgsql
    immutable;

create aggregate array_min(double precision[]) (
    sfunc = array_min,
    stype = double precision[],
    initcond = '{}'
    );

create or replace function array_max(a double precision[], b double precision[])
    returns double precision[]
as
$$
declare
    index  integer;
    result double precision[];
begin
    for index in 1..greatest(cardinality(a), cardinality(b))
        loop
            if a[index] > b[index] then
                result[index] := a[index];
            else
                result[index] := b[index];
            end if;
        end loop;

    return result;
end;
$$
    language plpgsql
    immutable;

create aggregate array_max(double precision[]) (
    sfunc = array_max,
    stype = double precision[],
    initcond = '{}'
    );

create function dominates(a double precision[], b double precision[])
    returns boolean
as
$$
declare
    index  integer;
    result boolean := false;
begin
    for index in 1..greatest(cardinality(a), cardinality(b))
        loop
            if a[index] > b[index] then
                return false;
            end if;
            if a[index] < b[index] then
                result := true;
            end if;
        end loop;
    return result;
end;
$$
    language plpgsql
    immutable;

create function feasible(constraints double precision[]) returns boolean as
$$
declare
    index integer;
begin
    for index in 1..cardinality(constraints)
        loop
            if constraints[index] != 0 then
                return false;
            end if;
        end loop;
    return true;
end;
$$
    language plpgsql
    immutable;


-- VIEWS --

create view cumulative_stats as
select run_id,
       iteration,
       sum(step)
       over (partition by run_id order by iteration rows between unbounded preceding and current row) as step,
       sum(evaluate)
       over (partition by run_id order by iteration rows between unbounded preceding and current row) as evaluate,
       sum(copy)
       over (partition by run_id order by iteration rows between unbounded preceding and current row) as copy,
       sum(match)
       over (partition by run_id order by iteration rows between unbounded preceding and current row) as match,
       sum(mutate)
       over (partition by run_id order by iteration rows between unbounded preceding and current row) as mutate,
       sum(should_terminate)
       over (partition by run_id order by iteration rows between unbounded preceding and current row) as should_terminate,
       hypervolume
from stats;

create view median_cumulative_stats as
select problem,
       instance,
       representation,
       (configuration ->> 'populationSize')::integer                   as population_size,
       iteration,
       percentile_cont(0.5) within group ( order by step )             as step,
       percentile_cont(0.5) within group ( order by evaluate )         as evaluate,
       percentile_cont(0.5) within group ( order by copy )             as copy,
       percentile_cont(0.5) within group ( order by match )            as match,
       percentile_cont(0.5) within group ( order by mutate )           as mutate,
       percentile_cont(0.5) within group ( order by should_terminate ) as should_terminate,
       percentile_cont(0.5) within group ( order by hypervolume )      as hypervolume
from run
         join cumulative_stats on run.id = cumulative_stats.run_id
group by problem, instance, representation, population_size, iteration;
