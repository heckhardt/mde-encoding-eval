library(ggplot2)
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
with duration as (select run_id, sum(step + should_terminate) as total from stats group by run_id)
select problem,
       substring(instance from length('TTC_InputRDG_') + 1 for 1) as model,
       representation,
       (configuration ->> 'populationSize')::integer              as population_size,
       total / 1000000000                                         as total
from run
         join duration on run.id = duration.run_id
where problem = 'CRA'
  "
)

dbDisconnect(con)

ggplot(df, aes(x = model, y = total, fill = representation)) +
  scale_y_log10() +
  geom_boxplot(
    size = 0,
    staplewidth = 0.5,
    outlier.size = 0.25,
    linewidth = 0.25
  ) +
  labs(x = "Instance", y = "Total Duration (s)", fill = "Representation") +
  theme_gray(base_size = 8) +
  theme(
    legend.position = "bottom",
    legend.margin = margin(),
    legend.box.spacing = unit(4, "pt")
  )
