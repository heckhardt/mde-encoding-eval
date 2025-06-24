library(ggplot2)
library(RPostgres)

con <- dbConnect(
  RPostgres::Postgres(),
  user = "postgres",
  password = "postgres",
  dbname = "postgres"
)

df <- dbGetQuery(
  con,
  "
select problem,
       substring(instance from length('TTC_InputRDG_') + 1 for 1) as model,
       representation,
       (configuration ->> 'populationSize')::integer              as population_size,
       hypervolume
from run r
         join stats s on r.id = s.run_id and r.total_iterations = s.iteration
where r.problem = 'CRA'
  "
)

dbDisconnect(con)

ggplot(df, aes(x = model, y = hypervolume, fill = representation)) +
  geom_boxplot(
    size = 0,
    staplewidth = 0.5,
    outlier.size = 0.25,
    linewidth = 0.25
  ) +
  labs(x = "Instance", y = "Normalized Hypervolume", fill = "Representation") +
  theme_gray(base_size = 8) +
  theme(
    legend.position = "bottom",
    legend.margin = margin(),
    legend.box.spacing = unit(4, "pt")
  )
