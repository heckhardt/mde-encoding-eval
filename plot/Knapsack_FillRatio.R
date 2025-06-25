library(ggplot2)
library(tidyr)
library(dplyr)
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
with duration as (select run_id, sum(step + should_terminate) as total from stats group by run_id),
     run_ext as (select *, (configuration ->> 'fillRatio')::double precision as fill_ratio
                 from run)
select 'Model ' || substring(instance from length('Input_') + 1 for 1)  as model,
       fill_ratio,
       percentile_cont(0.5) within group ( order by total) / 1000000000 as med_total,
       percentile_cont(0.5) within group ( order by hypervolume )          med_hypervolume
from run_ext r
         join duration d on r.id = d.run_id
         join stats s on r.id = s.run_id and r.total_iterations = s.iteration
where problem = 'Knapsack'
  and fill_ratio is not null
group by instance, fill_ratio
  "
)

dbDisconnect(con)

scale <- max(df$med_total) / max(df$med_hypervolume)

df$med_hypervolume <- df$med_hypervolume * scale
df <- pivot_longer(df, starts_with("med_"))

fill_ratio <- data.frame(model = unique(df$model),
                         knapsacks = c(2, 2, 2, 3, 3, 3, 4, 4, 4)) %>%
  mutate(value = knapsacks / (knapsacks + 1))

ggplot(df, aes(
  x = fill_ratio,
  y = value,
  color = name,
  shape = name
)) +
  geom_vline(data = fill_ratio, aes(xintercept = value), linetype = 3) +
  geom_line(linewidth = 0.25) +
  geom_point(size = 0.75) +
  scale_x_continuous(name = "Fill Ratio", breaks = seq(0, 1, 0.2)) +
  scale_y_continuous(
    name = "Median Total Duration (s)",
    sec.axis = sec_axis(
      ~ . / scale,
      name = "Median Hypervolume",
      breaks = seq(0, 1, 1 / (scale / 1000)),
      labels = function(x)
        sprintf("%.2f", x)
    )
  ) +
  scale_color_discrete(name = "Variable",
                       labels = c("Median Hypervolume", "Median Total Duration")) +
  scale_shape_discrete(name = "Variable",
                       labels = c("Median Hypervolume", "Median Total Duration")) +
  theme_gray(base_size = 8) +
  theme(
    legend.position = "bottom",
    legend.margin = margin(),
    legend.box.spacing = unit(4, "pt")
  ) +
  facet_wrap( ~ model)
