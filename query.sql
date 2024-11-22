-- median stats
with cumulative_stats as (select run_id,
                                 iteration,
                                 sum(step + should_terminate)
                                 over (partition by run_id order by iteration rows between unbounded preceding and current row ) as total,
                                 hypervolume
                          from stats)
select problem,
       instance,
       representation,
       (configuration ->> 'populationSize')::integer              as population_size,
       iteration,
       percentile_cont(0.5) within group ( order by total )       as total,
       percentile_cont(0.5) within group ( order by hypervolume ) as hypervolume
from run
         join cumulative_stats on run.id = cumulative_stats.run_id
group by problem, instance, representation, population_size, iteration;


-- model iteration with median hypervolume >= integer array at termination
with run_ext as (select *, (configuration ->> 'population_size')::integer as population_size from run),
     stats_at_term as (select problem,
                              instance,
                              representation,
                              population_size,
                              percentile_cont(0.5) within group ( order by hypervolume ) as med_hv
                       from run_ext r
                                join stats s on r.id = s.run_id and r.total_iterations = s.iteration
                       group by problem, instance, representation, population_size),
     stats_per_it as (select problem,
                             instance,
                             representation,
                             population_size,
                             iteration,
                             percentile_cont(0.5) within group ( order by hypervolume ) as med_hv
                      from run_ext r
                               join stats s on r.id = s.run_id
                      group by problem, instance, representation, population_size, iteration)
select distinct on (spi.problem, spi.instance, spi.representation, spi.population_size) spi.*
from stats_at_term sat
         join stats_per_it spi
              on sat.problem = spi.problem and
                 sat.instance = spi.instance and
                 sat.population_size = spi.population_size and
                 sat.med_hv <= spi.med_hv
where sat.representation = 'IntArray'
  and spi.representation = 'Model'
order by spi.problem, spi.instance, spi.representation, spi.population_size, iteration;


-- median hypervolume per iteration
with run_ext as (select *, (configuration ->> 'population_size')::integer as population_size from run),
     med_stats as (select problem,
                          instance,
                          representation,
                          population_size,
                          iteration,
                          percentile_cont(0.5) within group ( order by hypervolume ) as hypervolume
                   from run_ext r
                            join stats s on r.id = s.run_id
                   group by problem, instance, representation, population_size, iteration),
     end_stats as (select problem,
                          instance,
                          representation,
                          population_size,
                          percentile_cont(0.5) within group ( order by hypervolume ) as hypervolume
                   from run_ext r
                            join stats s on r.id = s.run_id and r.total_iterations = s.iteration
                   group by problem, instance, representation, population_size)
select distinct r.problem,
                r.instance,
                r.representation,
                r.population_size,
                m.iteration,
                m.hypervolume
from run_ext r
         join med_stats m on r.problem = m.problem and
                             r.instance = m.instance and
                             r.representation = m.representation and
                             r.population_size = med_stats.population_size
         join end_stats e on r.problem = e.problem and
                             r.instance = e.instance and
                             r.representation = e.representation and
                             r.population_size = e.population_size and
                             m.hypervolume <= e.hypervolume;


-- median hypervolume change per iteration
with change as (select run_id,
                       iteration,
                       hypervolume - lag(hypervolume, 1, 0) over (partition by run_id order by iteration) as hypervolume
                from stats)
select problem,
       instance,
       representation,
       (configuration ->> 'populationSize')::integer              as population_size,
       iteration,
       percentile_cont(0.5) within group ( order by hypervolume ) as hypervolume_change
from run
         join change on run.id = change.run_id
group by problem, instance, representation, population_size, iteration;


-- median duration
with duration as (select run_id,
                         sum(evaluate) as evaluate,
                         sum(copy)     as copy,
                         sum(match)    as match,
                         sum(mutate)   as mutate
                  from stats
                  group by run_id)
select problem,
       instance,
       representation,
       (configuration ->> 'populationSize')::integer           as population_size,
       percentile_cont(0.5) within group ( order by evaluate ) as evaluate,
       percentile_cont(0.5) within group ( order by copy )     as copy,
       percentile_cont(0.5) within group ( order by match )    as match,
       percentile_cont(0.5) within group ( order by mutate )   as mutate
from run
         join duration on run.id = duration.run_id
group by problem, instance, representation, population_size;


-- outer diameter
with od_i as (select run_id, ordinality, max(objective) - min(objective) as od
              from run
                       join solution on run.id = solution.run_id and run.total_iterations = solution.iteration,
                   unnest(objectives) with ordinality as objective
              group by run_id, ordinality)
select run_id, max(od) as outer_diameter
from od_i
group by run_id;


-- reference point
with solution_unnest as (select problem, instance, ordinality, max(objective) as max_objective
                         from run
                                  join solution
                                       on run.id = solution.run_id and run.total_iterations = solution.iteration,
                              unnest(objectives) with ordinality as objective
                         group by problem, instance, ordinality)
select problem, instance, array_agg(max_objective order by ordinality) as reference_point
from solution_unnest
group by problem, instance;


-- model to integer array duration ratio
with duration as (select run_id,
                         sum(step)             as step,
                         sum(copy)             as copy,
                         sum(evaluate)         as evaluate,
                         sum(match)            as match,
                         sum(mutate)           as mutate,
                         sum(should_terminate) as should_terminate
                  from stats
                  group by run_id),
     median_duration as (select problem,
                                instance,
                                representation,
                                (configuration ->> 'populationSize')::integer                   as population_size,
                                percentile_cont(0.5) within group ( order by step )             as step,
                                percentile_cont(0.5) within group ( order by copy )             as copy,
                                percentile_cont(0.5) within group ( order by evaluate )         as evaluate,
                                percentile_cont(0.5) within group ( order by match )            as match,
                                percentile_cont(0.5) within group ( order by mutate )           as mutate,
                                percentile_cont(0.5) within group ( order by should_terminate ) as should_terminate
                         from run r
                                  join duration d on r.id = d.run_id
                         group by problem, instance, representation, population_size)
select a.problem,
       avg(a.step / b.step)                         as step,
       avg(a.copy / b.copy)                         as copy,
       avg(a.evaluate / b.evaluate)                 as evaluate,
       avg(a.match / b.match)                       as match,
       avg(a.mutate / b.mutate)                     as mutate,
       avg(a.should_terminate / b.should_terminate) as should_terminate
from median_duration a
         join median_duration b
              on a.problem = b.problem and a.instance = b.instance and a.population_size = b.population_size
where a.representation = 'Model'
  and b.representation = 'IntArray'
group by a.problem;


-- min/max % of total duration
with duration as (select run_id,
                         sum(step + stats.should_terminate) as total,
                         sum(step)                          as step,
                         sum(copy)                          as copy,
                         sum(evaluate)                      as evaluate,
                         sum(match)                         as match,
                         sum(mutate)                        as mutate,
                         sum(should_terminate)              as should_terminate
                  from stats
                  group by run_id),
     median_duration as (select problem,
                                instance,
                                representation,
                                (configuration ->> 'populationSize')::integer                   as population_size,
                                percentile_cont(0.5) within group ( order by total )            as total,
                                percentile_cont(0.5) within group ( order by step )             as step,
                                percentile_cont(0.5) within group ( order by copy )             as copy,
                                percentile_cont(0.5) within group ( order by evaluate )         as evaluate,
                                percentile_cont(0.5) within group ( order by match )            as match,
                                percentile_cont(0.5) within group ( order by mutate )           as mutate,
                                percentile_cont(0.5) within group ( order by should_terminate ) as should_terminate
                         from run r
                                  join duration d on r.id = d.run_id
                         group by problem, instance, representation, population_size),
     duration_ratio as (select problem,
                               instance,
                               representation,
                               population_size,
                               step / total             as step,
                               copy / total             as copy,
                               evaluate / total         as evaluate,
                               match / total            as match,
                               mutate / total           as mutate,
                               should_terminate / total as should_terminate
                        from median_duration)
select problem,
       representation,
       floor(min(step) * 1000) / 10             as step_lower,
       ceil(max(step) * 1000) / 10              as step_upper,
       floor(min(copy) * 1000) / 10             as copy_lower,
       ceil(max(copy) * 1000) / 10              as copy_upper,
       floor(min(evaluate) * 1000) / 10         as evaluate_lower,
       ceil(max(evaluate) * 1000) / 10          as evaluate_upper,
       floor(min(match) * 1000) / 10            as match_lower,
       ceil(max(match) * 1000) / 10             as match_upper,
       floor(min(mutate) * 1000) / 10           as mutate_lower,
       ceil(max(mutate) * 1000) / 10            as mutate_upper,
       floor(min(should_terminate) * 1000) / 10 as should_terminate_lower,
       ceil(max(should_terminate) * 1000) / 10  as should_termiante_upper
from duration_ratio
group by problem, representation;


-- median model duration at first iteration where median hypervolume >= integer array at termination
with run_ext as (select *, (configuration ->> 'populationSize')::integer as population_size from run),
     model_it_at_eq as (with med_hv_at_term as (select problem,
                                                       instance,
                                                       representation,
                                                       population_size,
                                                       percentile_cont(0.5) within group ( order by hypervolume ) as med_hv
                                                from run_ext r
                                                         join stats s on r.id = s.run_id and r.total_iterations = s.iteration
                                                group by problem, instance, representation, population_size),
                             med_hv_per_it as (select problem,
                                                      instance,
                                                      representation,
                                                      population_size,
                                                      iteration,
                                                      percentile_cont(0.5) within group ( order by hypervolume ) as med_hv
                                               from run_ext r
                                                        join stats s on r.id = s.run_id
                                               group by problem, instance, representation, population_size, iteration)
                        select distinct on (mhpi.problem, mhpi.instance, mhpi.representation, mhpi.population_size) mhpi.*
                        from med_hv_at_term mhat
                                 join med_hv_per_it mhpi
                                      on mhat.problem = mhpi.problem and
                                         mhat.instance = mhpi.instance and
                                         mhat.population_size = mhpi.population_size and
                                         mhat.med_hv <= mhpi.med_hv
                        where mhat.representation = 'IntArray'
                          and mhpi.representation = 'Model'
                        order by mhpi.problem, mhpi.instance, mhpi.representation, mhpi.population_size, iteration),
     cumulative_stats as (select run_id,
                                 iteration,
                                 sum(step + should_terminate)
                                 over (partition by run_id order by iteration rows between unbounded preceding and current row) as total,
                                 hypervolume
                          from stats)
select r.problem,
       r.instance,
       r.representation,
       r.population_size,
       cs.iteration,
       percentile_cont(0.5) within group ( order by cs.total )       as total,
       percentile_cont(0.5) within group ( order by cs.hypervolume ) as hypervolume
from run_ext r
         join model_it_at_eq miae on r.problem = miae.problem and
                                     r.instance = miae.instance and
                                     r.representation = miae.representation and
                                     r.population_size = miae.population_size
         join cumulative_stats cs on r.id = cs.run_id and miae.iteration = cs.iteration
group by r.problem, r.instance, r.representation, r.population_size, cs.iteration;


-- median integer array duration / hypervolume at termination
with duration as (select run_id, sum(step + should_terminate) as total, max(hypervolume) as hypervolume
                  from stats
                  group by run_id)
select problem,
       instance,
       representation,
       (configuration ->> 'populationSize')::integer                   as population_size,
       percentile_cont(0.5) within group ( order by total_iterations ) as iteration,
       percentile_cont(0.5) within group ( order by total )            as total,
       percentile_cont(0.5) within group ( order by hypervolume )      as hypervolume
from run r
         join duration d on r.id = d.run_id
where representation = 'IntArray'
group by problem, instance, representation, population_size;


-- median hypervolume and model to integer array ratio
with agg as (select problem,
                    instance,
                    representation,
                    (configuration ->> 'populationSize')::integer              as population_size,
                    percentile_cont(0.5) within group ( order by hypervolume ) as med_hv
             from run
                      join stats on run.id = stats.run_id and run.total_iterations = stats.iteration
             group by problem, instance, representation, population_size)
select a.problem,
       a.instance,
       a.population_size,
       b.med_hv            as med_hv_intarray,
       a.med_hv            as med_hv_model,
       a.med_hv / b.med_hv as ratio
from agg a
         join agg b on a.problem = b.problem
    and a.instance = b.instance
    and a.representation != b.representation
    and a.population_size = b.population_size
where a.representation = 'Model'
  and b.representation = 'IntArray';


-- median hypervolume at termination
select problem,
       instance,
       representation,
       (configuration ->> 'populationSize')::integer              as population_size,
       percentile_cont(0.5) within group ( order by hypervolume ) as med_hv
from run
         join stats on run.id = stats.run_id and run.total_iterations = stats.iteration
group by problem, instance, representation, population_size;


-- median hypervolume per iteration
select problem,
       instance,
       representation,
       (configuration ->> 'populationSize')::integer              as population_size,
       iteration,
       percentile_cont(0.5) within group ( order by hypervolume ) as med_hv
from run
         join stats on run.id = stats.run_id
group by problem, instance, representation, population_size, iteration;


-- total duration
with duration as (select run_id, sum(step + should_terminate) as total from stats group by run_id)
select problem, instance, representation, (configuration ->> 'populationSize')::integer as population_size, total
from run r
         join duration d on r.id = d.run_id;


-- average duration per iteration
with duration as (select run_id, sum(step + should_terminate) as total from stats group by run_id)
select problem,
       instance,
       representation,
       (configuration ->> 'populationSize')::integer as population_size,
       avg(total / total_iterations)
from run r
         join duration d on r.id = d.run_id
group by problem, instance, representation, population_size;


-- ratio of average duration per iteration from model to integer array representation
with duration as (with duration as (select run_id, sum(step + should_terminate) as total from stats group by run_id)
                  select problem,
                         instance,
                         representation,
                         (configuration ->> 'populationSize')::integer as population_size,
                         avg(total / total_iterations)                 as avg_per_it
                  from run r
                           join duration d on r.id = d.run_id
                  group by problem, instance, representation, population_size)
select a.problem,
       a.instance,
       a.population_size,
       a.avg_per_it                as model_avg_per_it,
       b.avg_per_it                as intarray_avg_per_it,
       a.avg_per_it / b.avg_per_it as ratio
from duration a
         join duration b on a.problem = b.problem and a.instance = b.instance and a.population_size = b.population_size
where a.representation = 'Model'
  and b.representation = 'IntArray';
