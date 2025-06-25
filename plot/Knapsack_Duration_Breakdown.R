library(ggplot2)
library(stringr)
library(tidyr)
library(RPostgres)

con <- con <- dbConnect(
  RPostgres::Postgres(),
  user = "postgres",
  password = "postgres",
  dbname = "postgres"
)

df <- dbGetQuery(
  con,
  "
with duration as (select run_id,
                         sum(evaluate) as evaluate,
                         sum(copy)     as copy,
                         sum(match)    as match,
                         sum(mutate)   as mutate
                  from stats
                  group by run_id)
select problem,
       substring(instance from length('Input_') + 1 for 1)               as model,
       representation,
       percentile_cont(0.5) within group ( order by evaluate ) / 1000000 as evaluate,
       percentile_cont(0.5) within group ( order by copy ) / 1000000     as copy,
       percentile_cont(0.5) within group ( order by match ) / 1000000    as match,
       percentile_cont(0.5) within group ( order by mutate ) / 1000000   as mutate
from run r
         join duration d on r.id = d.run_id
where problem = 'Knapsack'
group by problem, instance, representation
  "
)

dbDisconnect(con)

df <- pivot_longer(df, cols = c(evaluate, copy, match, mutate))

ggplot(df, aes(x = model, y = value, fill = representation)) +
  scale_y_log10() +
  geom_col(position = position_dodge()) +
  labs(x = "Instance", y = "Duration (ms)", fill = "Representation") +
  theme_gray(base_size = 8) +
  theme(
    legend.position = "bottom",
    legend.margin = margin(),
    legend.box.spacing = unit(4, "pt")
  ) +
  facet_wrap(vars(name), labeller = labeller(.default = str_to_title))
