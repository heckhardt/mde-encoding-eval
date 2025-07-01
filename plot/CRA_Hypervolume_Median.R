library(ggplot2)
library(RPostgres)

con <- dbConnect(
  RPostgres::Postgres(),
  user = "postgres",
  password = "postgres",
  dbname = "postgres"
)

stats <- dbGetQuery(
  con,
  "
with med_stats as (select problem,
                          instance,
                          representation,
                          iteration,
                          percentile_cont(0.5) within group ( order by hypervolume ) as hypervolume
                   from run r
                            join stats s on r.id = s.run_id
                   group by problem, instance, representation, iteration),
     end_stats as (select problem,
                          instance,
                          representation,
                          percentile_cont(0.5) within group ( order by hypervolume ) as hypervolume
                   from run r
                            join stats s on r.id = s.run_id and r.total_iterations = s.iteration
                   group by problem, instance, representation),
     end_iteration as (select distinct on (m.problem, m.instance, m.representation) m.*
                       from med_stats m
                                join end_stats e
                                     on m.problem = e.problem and
                                        m.instance = e.instance and
                                        m.representation = e.representation and
                                        m.hypervolume <= e.hypervolume
                       order by m.problem, m.instance, m.representation, iteration desc)
select m.*,
       substring(m.instance from length('TTC_InputRDG_') + 1 for 1) as model
from med_stats m
         join end_iteration e
              on m.problem = e.problem and
                 m.instance = e.instance and
                 m.representation = e.representation and
                 m.iteration <= e.iteration
where m.problem = 'CRA'
  "
)

lines <- dbGetQuery(
  con,
  "
with med_stats as (select problem,
                          instance,
                          representation,
                          iteration,
                          percentile_cont(0.5) within group ( order by hypervolume ) as hypervolume
                   from run r
                            join stats s on r.id = s.run_id
                   group by problem, instance, representation, iteration),
     end_stats as (select problem,
                          instance,
                          representation,
                          percentile_cont(0.5) within group ( order by hypervolume ) as hypervolume
                   from run r
                            join stats s on r.id = s.run_id and r.total_iterations = s.iteration
                   group by problem, instance, representation)
select distinct on (m.problem, m.instance, m.representation) m.problem,
                                                             substring(m.instance from length('TTC_InputRDG_') + 1 for 1) as model,
                                                             m.representation,
                                                             m.iteration,
                                                             e.hypervolume
from med_stats m
         join end_stats e
              on m.problem = e.problem and
                 m.instance = e.instance and
                 m.hypervolume <= e.hypervolume
where m.problem = 'CRA'
  and m.representation = 'Model'
  and e.representation = 'IntArray'
order by m.problem, m.instance, m.representation, iteration desc
  "
)

dbDisconnect(con)

ggplot(stats, aes(x = iteration, y = hypervolume, color = representation)) +
  geom_hline(data = lines,
             aes(yintercept = hypervolume),
             linetype = 3) +
  geom_vline(data = lines,
             aes(xintercept = iteration),
             linetype = 3) +
  geom_line() +
  labs(x = "Iteration", y = "Normalized Hypervolume", color = "Representation") +
  theme_gray(base_size = 8) +
  theme(
    legend.position = "bottom",
    legend.margin = margin(),
    legend.box.spacing = unit(4, "pt")
  ) +
  facet_wrap(vars(model))
