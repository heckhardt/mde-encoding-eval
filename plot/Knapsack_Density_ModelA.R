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
select instance, representation, -objectives[1] as x, -objectives[2] as y
from run r
         join solution s on r.id = s.run_id and r.total_iterations = s.iteration
where problem = 'Knapsack'
  and instance = 'Input_A.xmi'
  "
)

dbDisconnect(con)

ggplot(df, aes(x, y)) +
  stat_density_2d(aes(fill = after_stat(level)), geom = "polygon") +
  scale_fill_gradient(
    breaks = function(x) {
      low <- min(x)
      high <- max(x)
      c(low, high)
    },
    labels = c("Low", "High")
  ) +
  labs(x = "Knapsack #1 - Total Value", y = "Knapsack #2 - Total Value", fill = "Density") +
  theme_gray(base_size = 8) +
  theme(
    legend.position = "bottom",
    legend.margin = margin(),
    legend.box.spacing = unit(4, "pt"),
    legend.ticks = element_blank()
  ) +
  facet_wrap(~ representation)
