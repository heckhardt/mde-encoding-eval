# Repository Structure

- `data/` contains evaluation results in CSV/PDF format
  - `data/raw/` contains raw stats for all runs
- `images/` contains diagrams for the EMF models and mutation rules as well as the database schema
- `input/` contains EMF XMI files for all problem instances
- `plot/` contains the published plots/diagrams and R scripts they were generated with
- `src/main/java/` contains the Java source code for running the evaluation (see the collapsible section below for details)
- `compose.yaml` contains the database configuration for Docker
- `query.sql` contains a collection of SQL query statements used to retrieve relevant data
- `schema.sql` contains the database schema

<details>

<summary>Source code structure</summary>

All source code is located within the `io.github.sekassel.moea` package.
At the top level, three important classes can be found:
- `Main.java` is the entrypoint to the application and contains some general configuration values
- `Runner.java` is responsible for reading the XMI problem specifications as well as constructing and running the evolutionary algorithms
- `Evaluation.java` is responsible for calculating the normalized hypervolume after all algorithms have terminated

The actual problem/algorithm implementation is structured as follows: 
- The generated sources for the CRA and knapsack EMF models are located within the `model` package
- The `operator` package is home to the implementations of the mutation operators for the CRA and knapsack models as well as the generic IntArray representation, each within their respective packages
  - The `RandomMutation` class is the generic operator responsible for randomly selecting an applicable operator at runtime and measuring the `copy`, `match` and `mutate` durations
- The `problem` package contain the MOEA Framework-specific implementations of the CRA and knapsack problems
- The `store` package contains the `DataStore` interface and two implementations:
  - `NOPDataStore` is a no-op implementation used during warmup
  - `PGDataStore` is backed by the (preconfigured) PostgreSQL database
- The `termination` package houses the termination condition implementation of reaching a hypervolume steady-state/plateau
- Utilities for serializing algorithm configurations as JSON are located within the `util` package
- The MOEA Framework-specific variable implementations for the EMF models as well as the IntArray representation can be found in the `variable` package 

Finally, the `generator.KnapsackGenerator` class is a standalone application to generate knapsack problem instances as per Michalewicz and Arabas[^1].

[^1]: Michalewicz, Z., Arabas, J. (1994). Genetic algorithms for the 0/1 knapsack problem. In: Raś, Z.W., Zemankova, M. (eds) Methodologies for Intelligent Systems. ISMIS 1994. Lecture Notes in Computer Science, vol 869. Springer, Berlin, Heidelberg. https://doi.org/10.1007/3-540-58495-1_14

</details>


# Running the evaluation

While some attempts were made to make the source code as easy-to-use as possible, some manual work is still required to reproduce the data presented below.
The following explains the steps necessary to set up the database, build the source code, and run the evaluation.

## Prerequisites
- JDK 21 (Eclipse Temurin binaries are available [here](https://adoptium.net/de/temurin/releases/?os=any&arch=any&version=21))
- PostgreSQL 17

### Database setup
The easiest way to set up the database is to run it in a container via [Docker](https://www.docker.com/) and the provided [Compose file](compose.yaml) by running `docker compose up -d` from the repository root.
This will configure the database server with reasonable defaults for running the evaluation, exactly as they were used to collect the data presented below, and apply the required schema.

<details>

<summary>Manual configuration</summary>

For manual configuration, the following config values are recommended:
```bash
# postgresql.conf

shared_buffers = 8GB
wal_level = minimal
max_wal_senders = 0
max_wal_size = 2GB
```
_The source code expects the database server to be available on localhost:5432 and the database to be named `postgres` with a user named `postgres` and password `postgres`.
If this is not the case, adjust these values in `src/main/java/**/Main.java` accordingly before proceeding with the build step._

Once the database server is set up, the database schema can be applied using the `schema.sql` file:
```bash
psql -U postgres -d postgres -f schema.sql
```
This will create three tables, along with relevant indices:
- `run` contains information about each run (the problem, instance, algorithm, configuration etc.)
- `stats` contains timing and quality information about each iteration of each run
- `solution` contains the objective and constraint values for each solution at each iteration of every run. _This will be by far the largest table. Ensure at least ~30GB of storage space are available to the database server to prevent issues._

Additionally, some functions and aggregates are created to simplify querying the data later on, as well as two views providing the (median) cumulative stats from the beginning to each iteration of every run.  

</details>

## Configuring and running the evaluation
The runner is configured for a machine with at least 24 cores/logical processors.
If this is not the case, adjust the `parallelism` and `maximumPoolSize` parameters in `src/main/java/**/Main.java` accordingly.
`parallelism` represents the number of threads that will be used to run multiple algorithms in parallel, i.e., it should not exceed the number of cores and leave some headroom for the database server (if running locally) and operating system.
`maximumPoolSize` should be **at least** as high as `parallelism`, otherwise some threads will be left waiting for a database connection to become available between iterations. _This delay is **not** included in the timing measurements, but can drastically increase the (already high) total runtime duration of the evaluation._

Finally, to run the evaluation, first build a runnable JAR file via `gradlew shadowJar`.
Then, start the runner with the following command:
```bash
java -jar -XX:+UseZGC -XX:+ZGenerational -XX:SoftMaxHeapSize=12g -Xmx16g build/libs/moea-evaluation-1.0-SNAPSHOT-all.jar
```
The values for `SoftMaxHeapSize` and `Xmx` can be adjusted, if needed, to allow running on machines with lower available memory. 

The runner will first perform a JVM warmup step, followed by 50 timed runs for each problem instance and representation, running 20 algorithms in parallel (by default).
After all algorithms have terminated, the hypervolume is calculated for each iteration, normalized according to the reference set, i.e., the upper and lower bounds of the objectives of best solutions that were found throughout all runs for a given instance. 


# Evaluation Results

## Descriptive Statistics

<details>

<summary>Hypervolume</summary>

**Table 1:** Full descriptive statistics for the hypervolume at termination [ [CSV](data/Stats_Hv.csv) | [PDF](data/Stats_Hv.pdf) ]

<table>
 <thead>
  <tr>
   <th style="text-align:left;">Problem</th>
   <th style="text-align:left;">Instance</th>
   <th style="text-align:left;">Repr</th>
   <th style="text-align:right;">Mean</th>
   <th style="text-align:right;">Med</th>
   <th style="text-align:right;">SD</th>
   <th style="text-align:right;">Var</th>
   <th style="text-align:right;">Min</th>
   <th style="text-align:right;">Max</th>
   <th style="text-align:right;">Skew</th>
   <th style="text-align:right;">Kurt</th>
   <th style="text-align:right;">MAD</th>
  </tr>
 </thead>
<tbody>
  <tr>
   <td style="text-align:left;" rowspan="10">CRA</td>
   <td style="text-align:left;" rowspan="2">A</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.585</td>
   <td style="text-align:right;">0.587</td>
   <td style="text-align:right;">0.014</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.540</td>
   <td style="text-align:right;">0.610</td>
   <td style="text-align:right;">-0.628</td>
   <td style="text-align:right;">0.828</td>
   <td style="text-align:right;">0.012</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.608</td>
   <td style="text-align:right;">0.610</td>
   <td style="text-align:right;">0.007</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.575</td>
   <td style="text-align:right;">0.610</td>
   <td style="text-align:right;">-3.569</td>
   <td style="text-align:right;">11.625</td>
   <td style="text-align:right;">0.000</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">B</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.652</td>
   <td style="text-align:right;">0.657</td>
   <td style="text-align:right;">0.026</td>
   <td style="text-align:right;">0.001</td>
   <td style="text-align:right;">0.572</td>
   <td style="text-align:right;">0.695</td>
   <td style="text-align:right;">-0.854</td>
   <td style="text-align:right;">0.561</td>
   <td style="text-align:right;">0.022</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.698</td>
   <td style="text-align:right;">0.701</td>
   <td style="text-align:right;">0.015</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.633</td>
   <td style="text-align:right;">0.720</td>
   <td style="text-align:right;">-1.768</td>
   <td style="text-align:right;">4.541</td>
   <td style="text-align:right;">0.011</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">C</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.575</td>
   <td style="text-align:right;">0.582</td>
   <td style="text-align:right;">0.034</td>
   <td style="text-align:right;">0.001</td>
   <td style="text-align:right;">0.478</td>
   <td style="text-align:right;">0.645</td>
   <td style="text-align:right;">-0.653</td>
   <td style="text-align:right;">0.674</td>
   <td style="text-align:right;">0.025</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.623</td>
   <td style="text-align:right;">0.617</td>
   <td style="text-align:right;">0.040</td>
   <td style="text-align:right;">0.002</td>
   <td style="text-align:right;">0.536</td>
   <td style="text-align:right;">0.727</td>
   <td style="text-align:right;">0.247</td>
   <td style="text-align:right;">0.255</td>
   <td style="text-align:right;">0.039</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">D</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.434</td>
   <td style="text-align:right;">0.435</td>
   <td style="text-align:right;">0.046</td>
   <td style="text-align:right;">0.002</td>
   <td style="text-align:right;">0.324</td>
   <td style="text-align:right;">0.528</td>
   <td style="text-align:right;">-0.196</td>
   <td style="text-align:right;">-0.331</td>
   <td style="text-align:right;">0.048</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.507</td>
   <td style="text-align:right;">0.510</td>
   <td style="text-align:right;">0.048</td>
   <td style="text-align:right;">0.002</td>
   <td style="text-align:right;">0.384</td>
   <td style="text-align:right;">0.584</td>
   <td style="text-align:right;">-0.579</td>
   <td style="text-align:right;">-0.179</td>
   <td style="text-align:right;">0.046</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">E</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.405</td>
   <td style="text-align:right;">0.404</td>
   <td style="text-align:right;">0.049</td>
   <td style="text-align:right;">0.002</td>
   <td style="text-align:right;">0.289</td>
   <td style="text-align:right;">0.519</td>
   <td style="text-align:right;">-0.009</td>
   <td style="text-align:right;">-0.004</td>
   <td style="text-align:right;">0.043</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.453</td>
   <td style="text-align:right;">0.458</td>
   <td style="text-align:right;">0.061</td>
   <td style="text-align:right;">0.004</td>
   <td style="text-align:right;">0.318</td>
   <td style="text-align:right;">0.582</td>
   <td style="text-align:right;">-0.136</td>
   <td style="text-align:right;">-0.529</td>
   <td style="text-align:right;">0.059</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="18">Knapsack</td>
   <td style="text-align:left;" rowspan="2">A</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.509</td>
   <td style="text-align:right;">0.511</td>
   <td style="text-align:right;">0.054</td>
   <td style="text-align:right;">0.003</td>
   <td style="text-align:right;">0.363</td>
   <td style="text-align:right;">0.600</td>
   <td style="text-align:right;">-0.364</td>
   <td style="text-align:right;">-0.358</td>
   <td style="text-align:right;">0.054</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.646</td>
   <td style="text-align:right;">0.656</td>
   <td style="text-align:right;">0.043</td>
   <td style="text-align:right;">0.002</td>
   <td style="text-align:right;">0.515</td>
   <td style="text-align:right;">0.722</td>
   <td style="text-align:right;">-1.094</td>
   <td style="text-align:right;">0.989</td>
   <td style="text-align:right;">0.036</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">B</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.312</td>
   <td style="text-align:right;">0.308</td>
   <td style="text-align:right;">0.063</td>
   <td style="text-align:right;">0.004</td>
   <td style="text-align:right;">0.205</td>
   <td style="text-align:right;">0.440</td>
   <td style="text-align:right;">0.183</td>
   <td style="text-align:right;">-0.920</td>
   <td style="text-align:right;">0.070</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.513</td>
   <td style="text-align:right;">0.528</td>
   <td style="text-align:right;">0.090</td>
   <td style="text-align:right;">0.008</td>
   <td style="text-align:right;">0.347</td>
   <td style="text-align:right;">0.668</td>
   <td style="text-align:right;">-0.424</td>
   <td style="text-align:right;">-1.108</td>
   <td style="text-align:right;">0.097</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">C</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.360</td>
   <td style="text-align:right;">0.359</td>
   <td style="text-align:right;">0.088</td>
   <td style="text-align:right;">0.008</td>
   <td style="text-align:right;">0.130</td>
   <td style="text-align:right;">0.570</td>
   <td style="text-align:right;">-0.128</td>
   <td style="text-align:right;">-0.172</td>
   <td style="text-align:right;">0.096</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.533</td>
   <td style="text-align:right;">0.537</td>
   <td style="text-align:right;">0.075</td>
   <td style="text-align:right;">0.006</td>
   <td style="text-align:right;">0.344</td>
   <td style="text-align:right;">0.758</td>
   <td style="text-align:right;">0.162</td>
   <td style="text-align:right;">0.568</td>
   <td style="text-align:right;">0.070</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">D</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.390</td>
   <td style="text-align:right;">0.390</td>
   <td style="text-align:right;">0.022</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.322</td>
   <td style="text-align:right;">0.442</td>
   <td style="text-align:right;">-0.252</td>
   <td style="text-align:right;">0.856</td>
   <td style="text-align:right;">0.018</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.446</td>
   <td style="text-align:right;">0.447</td>
   <td style="text-align:right;">0.018</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.402</td>
   <td style="text-align:right;">0.481</td>
   <td style="text-align:right;">-0.322</td>
   <td style="text-align:right;">-0.534</td>
   <td style="text-align:right;">0.021</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">E</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.299</td>
   <td style="text-align:right;">0.301</td>
   <td style="text-align:right;">0.015</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.272</td>
   <td style="text-align:right;">0.330</td>
   <td style="text-align:right;">0.110</td>
   <td style="text-align:right;">-0.582</td>
   <td style="text-align:right;">0.014</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.417</td>
   <td style="text-align:right;">0.419</td>
   <td style="text-align:right;">0.011</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.395</td>
   <td style="text-align:right;">0.442</td>
   <td style="text-align:right;">0.126</td>
   <td style="text-align:right;">-0.387</td>
   <td style="text-align:right;">0.009</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">F</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.203</td>
   <td style="text-align:right;">0.203</td>
   <td style="text-align:right;">0.010</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.188</td>
   <td style="text-align:right;">0.232</td>
   <td style="text-align:right;">0.696</td>
   <td style="text-align:right;">-0.040</td>
   <td style="text-align:right;">0.010</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.375</td>
   <td style="text-align:right;">0.375</td>
   <td style="text-align:right;">0.011</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.350</td>
   <td style="text-align:right;">0.397</td>
   <td style="text-align:right;">-0.068</td>
   <td style="text-align:right;">-0.285</td>
   <td style="text-align:right;">0.010</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">G</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.096</td>
   <td style="text-align:right;">0.096</td>
   <td style="text-align:right;">0.006</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.076</td>
   <td style="text-align:right;">0.107</td>
   <td style="text-align:right;">-0.731</td>
   <td style="text-align:right;">1.414</td>
   <td style="text-align:right;">0.005</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.133</td>
   <td style="text-align:right;">0.133</td>
   <td style="text-align:right;">0.005</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.124</td>
   <td style="text-align:right;">0.144</td>
   <td style="text-align:right;">0.137</td>
   <td style="text-align:right;">-0.705</td>
   <td style="text-align:right;">0.005</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">H</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.077</td>
   <td style="text-align:right;">0.077</td>
   <td style="text-align:right;">0.004</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.067</td>
   <td style="text-align:right;">0.083</td>
   <td style="text-align:right;">-0.717</td>
   <td style="text-align:right;">0.255</td>
   <td style="text-align:right;">0.003</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.128</td>
   <td style="text-align:right;">0.128</td>
   <td style="text-align:right;">0.003</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.119</td>
   <td style="text-align:right;">0.135</td>
   <td style="text-align:right;">-0.162</td>
   <td style="text-align:right;">0.831</td>
   <td style="text-align:right;">0.003</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">I</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.067</td>
   <td style="text-align:right;">0.066</td>
   <td style="text-align:right;">0.003</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.061</td>
   <td style="text-align:right;">0.073</td>
   <td style="text-align:right;">0.086</td>
   <td style="text-align:right;">-0.771</td>
   <td style="text-align:right;">0.003</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.124</td>
   <td style="text-align:right;">0.124</td>
   <td style="text-align:right;">0.003</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.117</td>
   <td style="text-align:right;">0.133</td>
   <td style="text-align:right;">0.288</td>
   <td style="text-align:right;">-0.051</td>
   <td style="text-align:right;">0.004</td>
  </tr>
</tbody>
</table>

<details>

<summary>R script</summary>

```R
library(e1071)
library(stringr)
library(dplyr)
library(boot)
library(RPostgres)

con <- dbConnect(
  RPostgres::Postgres(),
  user = "postgres",
  password = "postgres",
  dbname = "postgres",
  port = 60010
)

df <- dbGetQuery(
  con,
  "select problem, instance, representation, (configuration ->> 'populationSize')::integer as population_size, hypervolume
from run
         join stats on run.id = stats.run_id and run.total_iterations = stats.iteration"
)

dbDisconnect(con)

result <- df %>%
  group_by(problem, instance, representation) %>%
  summarise(
    mean = mean(hypervolume),
    median = median(hypervolume),
    sd = sd(hypervolume),
    var = var(hypervolume),
    min = min(hypervolume),
    max = max(hypervolume),
    skewness = skewness(hypervolume),
    kurtosis = kurtosis(hypervolume),
    mad = mad(hypervolume)
  )


write.csv(result, col.names = FALSE)
```

</details>

</details>


<details>

<summary>Total Duration</summary>

**Table 2:** Full descriptive statistics for the total duration in milliseconds [ [CSV](data/Stats_Dur_Total.csv) | [PDF](data/Stats_Dur_Total.pdf) ]

<table>
 <thead>
  <tr>
   <th style="text-align:left;">Problem</th>
   <th style="text-align:left;">Instance</th>
   <th style="text-align:left;">Repr</th>
   <th style="text-align:right;">Mead</th>
   <th style="text-align:right;">Med</th>
   <th style="text-align:right;">SD</th>
   <th style="text-align:right;">Var</th>
   <th style="text-align:right;">Min</th>
   <th style="text-align:right;">Max</th>
   <th style="text-align:right;">Skew</th>
   <th style="text-align:right;">Kurt</th>
   <th style="text-align:right;">MAD</th>
  </tr>
 </thead>
<tbody>
  <tr>
   <td style="text-align:left;" rowspan="10">CRA</td>
   <td style="text-align:left;" rowspan="2">A</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">63.561</td>
   <td style="text-align:right;">64.104</td>
   <td style="text-align:right;">20.022</td>
   <td style="text-align:right;">4.008930e+02</td>
   <td style="text-align:right;">25.512</td>
   <td style="text-align:right;">106.856</td>
   <td style="text-align:right;">0.132</td>
   <td style="text-align:right;">-0.561</td>
   <td style="text-align:right;">20.729</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">93.326</td>
   <td style="text-align:right;">90.834</td>
   <td style="text-align:right;">14.274</td>
   <td style="text-align:right;">2.037380e+02</td>
   <td style="text-align:right;">63.812</td>
   <td style="text-align:right;">135.697</td>
   <td style="text-align:right;">0.401</td>
   <td style="text-align:right;">0.031</td>
   <td style="text-align:right;">14.609</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">B</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">153.389</td>
   <td style="text-align:right;">156.517</td>
   <td style="text-align:right;">29.983</td>
   <td style="text-align:right;">8.989990e+02</td>
   <td style="text-align:right;">79.064</td>
   <td style="text-align:right;">229.502</td>
   <td style="text-align:right;">-0.009</td>
   <td style="text-align:right;">-0.079</td>
   <td style="text-align:right;">29.577</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">485.504</td>
   <td style="text-align:right;">470.921</td>
   <td style="text-align:right;">109.302</td>
   <td style="text-align:right;">1.194693e+04</td>
   <td style="text-align:right;">240.850</td>
   <td style="text-align:right;">720.141</td>
   <td style="text-align:right;">0.128</td>
   <td style="text-align:right;">-0.788</td>
   <td style="text-align:right;">118.152</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">C</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">879.421</td>
   <td style="text-align:right;">878.202</td>
   <td style="text-align:right;">164.983</td>
   <td style="text-align:right;">2.721929e+04</td>
   <td style="text-align:right;">582.002</td>
   <td style="text-align:right;">1210.641</td>
   <td style="text-align:right;">0.063</td>
   <td style="text-align:right;">-0.699</td>
   <td style="text-align:right;">170.409</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">2929.430</td>
   <td style="text-align:right;">2761.405</td>
   <td style="text-align:right;">753.066</td>
   <td style="text-align:right;">5.671085e+05</td>
   <td style="text-align:right;">1635.813</td>
   <td style="text-align:right;">5117.966</td>
   <td style="text-align:right;">0.838</td>
   <td style="text-align:right;">0.392</td>
   <td style="text-align:right;">630.628</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">D</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">12941.304</td>
   <td style="text-align:right;">12645.129</td>
   <td style="text-align:right;">2364.791</td>
   <td style="text-align:right;">5.592238e+06</td>
   <td style="text-align:right;">8390.451</td>
   <td style="text-align:right;">17976.413</td>
   <td style="text-align:right;">0.346</td>
   <td style="text-align:right;">-0.634</td>
   <td style="text-align:right;">2134.847</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">25295.192</td>
   <td style="text-align:right;">24494.663</td>
   <td style="text-align:right;">5207.906</td>
   <td style="text-align:right;">2.712229e+07</td>
   <td style="text-align:right;">14672.592</td>
   <td style="text-align:right;">37649.470</td>
   <td style="text-align:right;">0.431</td>
   <td style="text-align:right;">-0.122</td>
   <td style="text-align:right;">5075.495</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">E</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">90197.480</td>
   <td style="text-align:right;">91826.843</td>
   <td style="text-align:right;">15175.717</td>
   <td style="text-align:right;">2.303024e+08</td>
   <td style="text-align:right;">60443.891</td>
   <td style="text-align:right;">121553.844</td>
   <td style="text-align:right;">-0.013</td>
   <td style="text-align:right;">-0.948</td>
   <td style="text-align:right;">17330.381</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">106187.238</td>
   <td style="text-align:right;">109471.200</td>
   <td style="text-align:right;">24709.949</td>
   <td style="text-align:right;">6.105816e+08</td>
   <td style="text-align:right;">65414.218</td>
   <td style="text-align:right;">160803.482</td>
   <td style="text-align:right;">0.062</td>
   <td style="text-align:right;">-0.844</td>
   <td style="text-align:right;">24095.914</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="18">Knapsack</td>
   <td style="text-align:left;" rowspan="2">A</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">1079.042</td>
   <td style="text-align:right;">1075.504</td>
   <td style="text-align:right;">161.868</td>
   <td style="text-align:right;">2.620127e+04</td>
   <td style="text-align:right;">769.858</td>
   <td style="text-align:right;">1382.437</td>
   <td style="text-align:right;">0.204</td>
   <td style="text-align:right;">-0.959</td>
   <td style="text-align:right;">172.647</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">53600.301</td>
   <td style="text-align:right;">54470.898</td>
   <td style="text-align:right;">6180.768</td>
   <td style="text-align:right;">3.820189e+07</td>
   <td style="text-align:right;">31559.307</td>
   <td style="text-align:right;">65199.158</td>
   <td style="text-align:right;">-1.201</td>
   <td style="text-align:right;">2.623</td>
   <td style="text-align:right;">4454.872</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">B</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">3210.247</td>
   <td style="text-align:right;">3220.463</td>
   <td style="text-align:right;">397.013</td>
   <td style="text-align:right;">1.576195e+05</td>
   <td style="text-align:right;">2391.919</td>
   <td style="text-align:right;">4149.151</td>
   <td style="text-align:right;">0.196</td>
   <td style="text-align:right;">-0.422</td>
   <td style="text-align:right;">403.031</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">215792.003</td>
   <td style="text-align:right;">218768.048</td>
   <td style="text-align:right;">30706.482</td>
   <td style="text-align:right;">9.428881e+08</td>
   <td style="text-align:right;">153301.000</td>
   <td style="text-align:right;">276872.087</td>
   <td style="text-align:right;">-0.116</td>
   <td style="text-align:right;">-0.953</td>
   <td style="text-align:right;">32559.089</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">C</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">6493.482</td>
   <td style="text-align:right;">6535.075</td>
   <td style="text-align:right;">689.937</td>
   <td style="text-align:right;">4.760129e+05</td>
   <td style="text-align:right;">4464.823</td>
   <td style="text-align:right;">7666.709</td>
   <td style="text-align:right;">-0.454</td>
   <td style="text-align:right;">-0.183</td>
   <td style="text-align:right;">825.193</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">435273.985</td>
   <td style="text-align:right;">440797.537</td>
   <td style="text-align:right;">41843.822</td>
   <td style="text-align:right;">1.750905e+09</td>
   <td style="text-align:right;">339216.687</td>
   <td style="text-align:right;">553692.971</td>
   <td style="text-align:right;">0.130</td>
   <td style="text-align:right;">0.456</td>
   <td style="text-align:right;">36419.095</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">D</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">4172.780</td>
   <td style="text-align:right;">4205.138</td>
   <td style="text-align:right;">697.683</td>
   <td style="text-align:right;">4.867621e+05</td>
   <td style="text-align:right;">2474.958</td>
   <td style="text-align:right;">5384.623</td>
   <td style="text-align:right;">-0.037</td>
   <td style="text-align:right;">-0.754</td>
   <td style="text-align:right;">766.969</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">70701.128</td>
   <td style="text-align:right;">69867.706</td>
   <td style="text-align:right;">12375.553</td>
   <td style="text-align:right;">1.531543e+08</td>
   <td style="text-align:right;">43218.266</td>
   <td style="text-align:right;">92057.025</td>
   <td style="text-align:right;">-0.066</td>
   <td style="text-align:right;">-0.890</td>
   <td style="text-align:right;">13228.866</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">E</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">9577.562</td>
   <td style="text-align:right;">9378.702</td>
   <td style="text-align:right;">1360.233</td>
   <td style="text-align:right;">1.850234e+06</td>
   <td style="text-align:right;">6856.536</td>
   <td style="text-align:right;">13247.451</td>
   <td style="text-align:right;">0.369</td>
   <td style="text-align:right;">-0.316</td>
   <td style="text-align:right;">1301.779</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">236912.647</td>
   <td style="text-align:right;">236576.420</td>
   <td style="text-align:right;">20460.815</td>
   <td style="text-align:right;">4.186450e+08</td>
   <td style="text-align:right;">186020.568</td>
   <td style="text-align:right;">288099.389</td>
   <td style="text-align:right;">-0.001</td>
   <td style="text-align:right;">0.180</td>
   <td style="text-align:right;">20608.483</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">F</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">18141.916</td>
   <td style="text-align:right;">17856.719</td>
   <td style="text-align:right;">1689.847</td>
   <td style="text-align:right;">2.855584e+06</td>
   <td style="text-align:right;">15413.138</td>
   <td style="text-align:right;">23276.047</td>
   <td style="text-align:right;">0.969</td>
   <td style="text-align:right;">0.779</td>
   <td style="text-align:right;">1694.468</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">614118.755</td>
   <td style="text-align:right;">617177.219</td>
   <td style="text-align:right;">39248.089</td>
   <td style="text-align:right;">1.540413e+09</td>
   <td style="text-align:right;">541938.215</td>
   <td style="text-align:right;">727491.986</td>
   <td style="text-align:right;">0.298</td>
   <td style="text-align:right;">-0.309</td>
   <td style="text-align:right;">46312.628</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">G</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">6682.920</td>
   <td style="text-align:right;">6585.628</td>
   <td style="text-align:right;">1369.217</td>
   <td style="text-align:right;">1.874756e+06</td>
   <td style="text-align:right;">4202.909</td>
   <td style="text-align:right;">10439.122</td>
   <td style="text-align:right;">0.293</td>
   <td style="text-align:right;">-0.573</td>
   <td style="text-align:right;">1569.362</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">77310.456</td>
   <td style="text-align:right;">78467.483</td>
   <td style="text-align:right;">10151.034</td>
   <td style="text-align:right;">1.030435e+08</td>
   <td style="text-align:right;">52309.126</td>
   <td style="text-align:right;">92200.470</td>
   <td style="text-align:right;">-0.869</td>
   <td style="text-align:right;">0.084</td>
   <td style="text-align:right;">9604.418</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">H</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">15956.375</td>
   <td style="text-align:right;">15796.814</td>
   <td style="text-align:right;">2050.762</td>
   <td style="text-align:right;">4.205624e+06</td>
   <td style="text-align:right;">10711.438</td>
   <td style="text-align:right;">20570.998</td>
   <td style="text-align:right;">-0.163</td>
   <td style="text-align:right;">-0.093</td>
   <td style="text-align:right;">2229.537</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">342762.680</td>
   <td style="text-align:right;">354599.370</td>
   <td style="text-align:right;">38408.673</td>
   <td style="text-align:right;">1.475226e+09</td>
   <td style="text-align:right;">235620.337</td>
   <td style="text-align:right;">395426.326</td>
   <td style="text-align:right;">-1.094</td>
   <td style="text-align:right;">0.486</td>
   <td style="text-align:right;">30489.632</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">I</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">30998.297</td>
   <td style="text-align:right;">30831.150</td>
   <td style="text-align:right;">2979.541</td>
   <td style="text-align:right;">8.877666e+06</td>
   <td style="text-align:right;">25622.660</td>
   <td style="text-align:right;">36154.315</td>
   <td style="text-align:right;">-0.057</td>
   <td style="text-align:right;">-1.039</td>
   <td style="text-align:right;">3410.880</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">785973.643</td>
   <td style="text-align:right;">832321.589</td>
   <td style="text-align:right;">124146.143</td>
   <td style="text-align:right;">1.541226e+10</td>
   <td style="text-align:right;">522825.830</td>
   <td style="text-align:right;">956328.109</td>
   <td style="text-align:right;">-0.935</td>
   <td style="text-align:right;">-0.454</td>
   <td style="text-align:right;">87577.011</td>
  </tr>
</tbody>
</table>

<details>

<summary>R script</summary>

```R
library(e1071)
library(stringr)
library(dplyr)
library(boot)
library(RPostgres)

con <- dbConnect(
  RPostgres::Postgres(),
  user = "postgres",
  password = "postgres",
  dbname = "postgres",
  port = 60010
)

df <- dbGetQuery(
  con,
  "with duration as (select run_id, sum(step + should_terminate) / 1000000 as total from stats group by run_id)
select problem, instance, representation, (configuration ->> 'populationSize')::integer as population_size, total
from run
         join duration on run.id = duration.run_id"
)

dbDisconnect(con)

result <- df %>%
  group_by(problem, instance, representation) %>%
  summarise(
    mean = mean(total),
    median = median(total),
    sd = sd(total),
    var = var(total),
    min = min(total),
    max = max(total),
    skewness = skewness(total),
    kurtosis = kurtosis(total),
    mad = mad(total)
  )

write.csv(result, col.names = FALSE)
```

</details>

</details>


<details>

<summary>Duration per Iteration</summary>

**Table 3:** Full descriptive statistics for the average duration per iteration in milliseconds [ [CSV](data/Stats_Dur_PerIt.csv) | [PDF](data/Stats_Dur_PerIt.pdf) ]

<table>
 <thead>
  <tr>
   <th style="text-align:left;">Problem</th>
   <th style="text-align:left;">Instance</th>
   <th style="text-align:left;">Repr</th>
   <th style="text-align:right;">Mean</th>
   <th style="text-align:right;">Med</th>
   <th style="text-align:right;">SD</th>
   <th style="text-align:right;">Var</th>
   <th style="text-align:right;">Min</th>
   <th style="text-align:right;">Max</th>
   <th style="text-align:right;">Skew</th>
   <th style="text-align:right;">Kurt</th>
   <th style="text-align:right;">MAD</th>
  </tr>
 </thead>
<tbody>
  <tr>
   <td style="text-align:left;" rowspan="10">CRA</td>
   <td style="text-align:left;" rowspan="2">A</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.691</td>
   <td style="text-align:right;">0.751</td>
   <td style="text-align:right;">0.157</td>
   <td style="text-align:right;">0.025</td>
   <td style="text-align:right;">0.330</td>
   <td style="text-align:right;">0.890</td>
   <td style="text-align:right;">-0.924</td>
   <td style="text-align:right;">-0.405</td>
   <td style="text-align:right;">0.109</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">1.158</td>
   <td style="text-align:right;">1.165</td>
   <td style="text-align:right;">0.099</td>
   <td style="text-align:right;">0.010</td>
   <td style="text-align:right;">0.872</td>
   <td style="text-align:right;">1.318</td>
   <td style="text-align:right;">-0.765</td>
   <td style="text-align:right;">0.096</td>
   <td style="text-align:right;">0.104</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">B</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.871</td>
   <td style="text-align:right;">0.876</td>
   <td style="text-align:right;">0.077</td>
   <td style="text-align:right;">0.006</td>
   <td style="text-align:right;">0.739</td>
   <td style="text-align:right;">1.081</td>
   <td style="text-align:right;">0.362</td>
   <td style="text-align:right;">-0.290</td>
   <td style="text-align:right;">0.086</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">3.054</td>
   <td style="text-align:right;">3.130</td>
   <td style="text-align:right;">0.367</td>
   <td style="text-align:right;">0.135</td>
   <td style="text-align:right;">2.211</td>
   <td style="text-align:right;">3.527</td>
   <td style="text-align:right;">-0.455</td>
   <td style="text-align:right;">-1.029</td>
   <td style="text-align:right;">0.408</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">C</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">3.093</td>
   <td style="text-align:right;">3.099</td>
   <td style="text-align:right;">0.130</td>
   <td style="text-align:right;">0.017</td>
   <td style="text-align:right;">2.840</td>
   <td style="text-align:right;">3.496</td>
   <td style="text-align:right;">0.566</td>
   <td style="text-align:right;">0.669</td>
   <td style="text-align:right;">0.123</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">10.197</td>
   <td style="text-align:right;">10.490</td>
   <td style="text-align:right;">0.995</td>
   <td style="text-align:right;">0.989</td>
   <td style="text-align:right;">7.081</td>
   <td style="text-align:right;">11.851</td>
   <td style="text-align:right;">-1.201</td>
   <td style="text-align:right;">1.332</td>
   <td style="text-align:right;">0.580</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">D</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">31.716</td>
   <td style="text-align:right;">32.710</td>
   <td style="text-align:right;">2.405</td>
   <td style="text-align:right;">5.784</td>
   <td style="text-align:right;">25.415</td>
   <td style="text-align:right;">35.688</td>
   <td style="text-align:right;">-0.902</td>
   <td style="text-align:right;">0.076</td>
   <td style="text-align:right;">1.694</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">49.124</td>
   <td style="text-align:right;">48.320</td>
   <td style="text-align:right;">2.908</td>
   <td style="text-align:right;">8.455</td>
   <td style="text-align:right;">43.681</td>
   <td style="text-align:right;">57.074</td>
   <td style="text-align:right;">0.890</td>
   <td style="text-align:right;">0.741</td>
   <td style="text-align:right;">1.871</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">E</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">155.512</td>
   <td style="text-align:right;">157.176</td>
   <td style="text-align:right;">10.408</td>
   <td style="text-align:right;">108.323</td>
   <td style="text-align:right;">120.712</td>
   <td style="text-align:right;">174.508</td>
   <td style="text-align:right;">-0.938</td>
   <td style="text-align:right;">0.975</td>
   <td style="text-align:right;">7.704</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">144.699</td>
   <td style="text-align:right;">153.154</td>
   <td style="text-align:right;">21.413</td>
   <td style="text-align:right;">458.535</td>
   <td style="text-align:right;">82.182</td>
   <td style="text-align:right;">170.610</td>
   <td style="text-align:right;">-1.501</td>
   <td style="text-align:right;">1.343</td>
   <td style="text-align:right;">11.455</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="18">Knapsack</td>
   <td style="text-align:left;" rowspan="2">A</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">2.511</td>
   <td style="text-align:right;">2.509</td>
   <td style="text-align:right;">0.205</td>
   <td style="text-align:right;">0.042</td>
   <td style="text-align:right;">2.168</td>
   <td style="text-align:right;">2.957</td>
   <td style="text-align:right;">0.288</td>
   <td style="text-align:right;">-1.022</td>
   <td style="text-align:right;">0.263</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">130.231</td>
   <td style="text-align:right;">133.913</td>
   <td style="text-align:right;">8.756</td>
   <td style="text-align:right;">76.667</td>
   <td style="text-align:right;">105.198</td>
   <td style="text-align:right;">142.130</td>
   <td style="text-align:right;">-1.514</td>
   <td style="text-align:right;">0.976</td>
   <td style="text-align:right;">1.204</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">B</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">5.795</td>
   <td style="text-align:right;">5.783</td>
   <td style="text-align:right;">0.296</td>
   <td style="text-align:right;">0.087</td>
   <td style="text-align:right;">5.310</td>
   <td style="text-align:right;">6.460</td>
   <td style="text-align:right;">0.219</td>
   <td style="text-align:right;">-0.647</td>
   <td style="text-align:right;">0.313</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">367.184</td>
   <td style="text-align:right;">370.277</td>
   <td style="text-align:right;">9.516</td>
   <td style="text-align:right;">90.560</td>
   <td style="text-align:right;">335.451</td>
   <td style="text-align:right;">381.971</td>
   <td style="text-align:right;">-1.285</td>
   <td style="text-align:right;">1.085</td>
   <td style="text-align:right;">4.900</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">C</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">10.474</td>
   <td style="text-align:right;">10.489</td>
   <td style="text-align:right;">0.259</td>
   <td style="text-align:right;">0.067</td>
   <td style="text-align:right;">9.887</td>
   <td style="text-align:right;">10.939</td>
   <td style="text-align:right;">-0.096</td>
   <td style="text-align:right;">-0.712</td>
   <td style="text-align:right;">0.283</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">705.312</td>
   <td style="text-align:right;">712.532</td>
   <td style="text-align:right;">18.970</td>
   <td style="text-align:right;">359.859</td>
   <td style="text-align:right;">658.377</td>
   <td style="text-align:right;">729.689</td>
   <td style="text-align:right;">-1.082</td>
   <td style="text-align:right;">-0.192</td>
   <td style="text-align:right;">9.025</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">D</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">8.134</td>
   <td style="text-align:right;">8.123</td>
   <td style="text-align:right;">0.160</td>
   <td style="text-align:right;">0.026</td>
   <td style="text-align:right;">7.875</td>
   <td style="text-align:right;">8.494</td>
   <td style="text-align:right;">0.351</td>
   <td style="text-align:right;">-0.763</td>
   <td style="text-align:right;">0.169</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">177.730</td>
   <td style="text-align:right;">186.723</td>
   <td style="text-align:right;">18.778</td>
   <td style="text-align:right;">352.616</td>
   <td style="text-align:right;">116.957</td>
   <td style="text-align:right;">191.909</td>
   <td style="text-align:right;">-1.813</td>
   <td style="text-align:right;">2.124</td>
   <td style="text-align:right;">2.341</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">E</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">15.160</td>
   <td style="text-align:right;">15.026</td>
   <td style="text-align:right;">0.410</td>
   <td style="text-align:right;">0.168</td>
   <td style="text-align:right;">14.608</td>
   <td style="text-align:right;">16.177</td>
   <td style="text-align:right;">1.156</td>
   <td style="text-align:right;">-0.008</td>
   <td style="text-align:right;">0.171</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">462.654</td>
   <td style="text-align:right;">470.522</td>
   <td style="text-align:right;">18.875</td>
   <td style="text-align:right;">356.282</td>
   <td style="text-align:right;">412.462</td>
   <td style="text-align:right;">485.959</td>
   <td style="text-align:right;">-1.515</td>
   <td style="text-align:right;">1.009</td>
   <td style="text-align:right;">5.921</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">F</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">24.124</td>
   <td style="text-align:right;">24.067</td>
   <td style="text-align:right;">0.760</td>
   <td style="text-align:right;">0.577</td>
   <td style="text-align:right;">22.419</td>
   <td style="text-align:right;">27.542</td>
   <td style="text-align:right;">1.389</td>
   <td style="text-align:right;">6.434</td>
   <td style="text-align:right;">0.458</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">873.366</td>
   <td style="text-align:right;">886.382</td>
   <td style="text-align:right;">35.406</td>
   <td style="text-align:right;">1253.620</td>
   <td style="text-align:right;">801.348</td>
   <td style="text-align:right;">912.788</td>
   <td style="text-align:right;">-1.247</td>
   <td style="text-align:right;">-0.156</td>
   <td style="text-align:right;">13.411</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">G</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">16.039</td>
   <td style="text-align:right;">15.987</td>
   <td style="text-align:right;">0.336</td>
   <td style="text-align:right;">0.113</td>
   <td style="text-align:right;">15.454</td>
   <td style="text-align:right;">17.022</td>
   <td style="text-align:right;">0.608</td>
   <td style="text-align:right;">0.115</td>
   <td style="text-align:right;">0.319</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">227.502</td>
   <td style="text-align:right;">238.314</td>
   <td style="text-align:right;">22.879</td>
   <td style="text-align:right;">523.460</td>
   <td style="text-align:right;">164.738</td>
   <td style="text-align:right;">246.047</td>
   <td style="text-align:right;">-1.743</td>
   <td style="text-align:right;">1.626</td>
   <td style="text-align:right;">5.749</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">H</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">25.000</td>
   <td style="text-align:right;">25.580</td>
   <td style="text-align:right;">1.327</td>
   <td style="text-align:right;">1.760</td>
   <td style="text-align:right;">21.509</td>
   <td style="text-align:right;">26.427</td>
   <td style="text-align:right;">-1.097</td>
   <td style="text-align:right;">-0.009</td>
   <td style="text-align:right;">0.772</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">563.557</td>
   <td style="text-align:right;">586.801</td>
   <td style="text-align:right;">48.692</td>
   <td style="text-align:right;">2370.874</td>
   <td style="text-align:right;">442.064</td>
   <td style="text-align:right;">604.761</td>
   <td style="text-align:right;">-1.507</td>
   <td style="text-align:right;">0.469</td>
   <td style="text-align:right;">3.937</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">I</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">37.674</td>
   <td style="text-align:right;">38.046</td>
   <td style="text-align:right;">1.158</td>
   <td style="text-align:right;">1.340</td>
   <td style="text-align:right;">32.916</td>
   <td style="text-align:right;">39.374</td>
   <td style="text-align:right;">-2.217</td>
   <td style="text-align:right;">6.011</td>
   <td style="text-align:right;">0.688</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">990.428</td>
   <td style="text-align:right;">1049.208</td>
   <td style="text-align:right;">125.525</td>
   <td style="text-align:right;">15756.530</td>
   <td style="text-align:right;">678.848</td>
   <td style="text-align:right;">1077.255</td>
   <td style="text-align:right;">-1.519</td>
   <td style="text-align:right;">0.470</td>
   <td style="text-align:right;">10.415</td>
  </tr>
</tbody>
</table>

<details>

<summary>R script</summary>

```R
library(e1071)
library(stringr)
library(dplyr)
library(boot)
library(RPostgres)

con <- dbConnect(
  RPostgres::Postgres(),
  user = "postgres",
  password = "postgres",
  dbname = "postgres",
  port = 60010
)

df <- dbGetQuery(
  con,
  "with duration as (select run_id, sum(step + should_terminate) / 1000000 as total from stats group by run_id)
select problem, instance, representation, (configuration ->> 'populationSize')::integer as population_size, total / total_iterations as total
from run
         join duration on run.id = duration.run_id"
)

dbDisconnect(con)

result <- df %>%
  group_by(problem, instance, representation) %>%
  summarise(
    mean = mean(total),
    median = median(total),
    sd = sd(total),
    var = var(total),
    min = min(total),
    max = max(total),
    skewness = skewness(total),
    kurtosis = kurtosis(total),
    mad = mad(total)
  )

write.csv(result)
```

</details>

</details>


## Influence of Population Size

<details>

<summary>Hypervolume</summary>

**Table 4:** Hypervolume ratio between population sizes for the knapsack problem [ [CSV](data/PopSize_Hv.csv) | [PDF](data/PopSize_Hv.pdf) ]

<table>
 <thead>
  <tr>
   <th style="text-align: left;" colspan="3"></th>
   <th style="text-align: center;" colspan="2">50%</th>
   <th style="text-align: center;" colspan="2">20%</th>
   <th style="text-align: center;" colspan="2">10%</th>
  </tr>
  <tr>
   <th style="text-align:left;">Instance</th>
   <th style="text-align:left;">Repr</th>
   <th style="text-align:right;">Ref</th>
   <th style="text-align:right;">Cmp</th>
   <th style="text-align:right;">Ratio</th>
   <th style="text-align:right;">Cmp</th>
   <th style="text-align:right;">Ratio</th>
   <th style="text-align:right;">Cmp</th>
   <th style="text-align:right;">Ratio</th>
  </tr>
 </thead>
<tbody>
  <tr>
   <td style="text-align:left;" rowspan="2">A</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.511</td>
   <td style="text-align:right;">0.412</td>
   <td style="text-align:right;">0.807</td>
   <td style="text-align:right;">0.124</td>
   <td style="text-align:right;">0.242</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.000</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.656</td>
   <td style="text-align:right;">0.587</td>
   <td style="text-align:right;">0.894</td>
   <td style="text-align:right;">0.374</td>
   <td style="text-align:right;">0.570</td>
   <td style="text-align:right;">0.185</td>
   <td style="text-align:right;">0.281</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">B</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.308</td>
   <td style="text-align:right;">0.171</td>
   <td style="text-align:right;">0.556</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.000</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.528</td>
   <td style="text-align:right;">0.277</td>
   <td style="text-align:right;">0.526</td>
   <td style="text-align:right;">0.159</td>
   <td style="text-align:right;">0.302</td>
   <td style="text-align:right;">0.062</td>
   <td style="text-align:right;">0.117</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">C</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.359</td>
   <td style="text-align:right;">0.115</td>
   <td style="text-align:right;">0.319</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.000</td>
   <td style="text-align:right;">0.000</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.537</td>
   <td style="text-align:right;">0.360</td>
   <td style="text-align:right;">0.670</td>
   <td style="text-align:right;">0.169</td>
   <td style="text-align:right;">0.315</td>
   <td style="text-align:right;">0.026</td>
   <td style="text-align:right;">0.049</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">D</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.390</td>
   <td style="text-align:right;">0.313</td>
   <td style="text-align:right;">0.804</td>
   <td style="text-align:right;">0.244</td>
   <td style="text-align:right;">0.627</td>
   <td style="text-align:right;">0.202</td>
   <td style="text-align:right;">0.518</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.447</td>
   <td style="text-align:right;">0.391</td>
   <td style="text-align:right;">0.875</td>
   <td style="text-align:right;">0.343</td>
   <td style="text-align:right;">0.767</td>
   <td style="text-align:right;">0.313</td>
   <td style="text-align:right;">0.701</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">E</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.301</td>
   <td style="text-align:right;">0.244</td>
   <td style="text-align:right;">0.813</td>
   <td style="text-align:right;">0.197</td>
   <td style="text-align:right;">0.657</td>
   <td style="text-align:right;">0.168</td>
   <td style="text-align:right;">0.559</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.419</td>
   <td style="text-align:right;">0.386</td>
   <td style="text-align:right;">0.922</td>
   <td style="text-align:right;">0.353</td>
   <td style="text-align:right;">0.843</td>
   <td style="text-align:right;">0.324</td>
   <td style="text-align:right;">0.773</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">F</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.203</td>
   <td style="text-align:right;">0.157</td>
   <td style="text-align:right;">0.777</td>
   <td style="text-align:right;">0.122</td>
   <td style="text-align:right;">0.600</td>
   <td style="text-align:right;">0.097</td>
   <td style="text-align:right;">0.476</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.375</td>
   <td style="text-align:right;">0.343</td>
   <td style="text-align:right;">0.916</td>
   <td style="text-align:right;">0.308</td>
   <td style="text-align:right;">0.821</td>
   <td style="text-align:right;">0.282</td>
   <td style="text-align:right;">0.753</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">G</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.096</td>
   <td style="text-align:right;">0.083</td>
   <td style="text-align:right;">0.860</td>
   <td style="text-align:right;">0.065</td>
   <td style="text-align:right;">0.672</td>
   <td style="text-align:right;">0.054</td>
   <td style="text-align:right;">0.557</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.133</td>
   <td style="text-align:right;">0.119</td>
   <td style="text-align:right;">0.900</td>
   <td style="text-align:right;">0.104</td>
   <td style="text-align:right;">0.782</td>
   <td style="text-align:right;">0.089</td>
   <td style="text-align:right;">0.668</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">H</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.077</td>
   <td style="text-align:right;">0.067</td>
   <td style="text-align:right;">0.869</td>
   <td style="text-align:right;">0.055</td>
   <td style="text-align:right;">0.709</td>
   <td style="text-align:right;">0.045</td>
   <td style="text-align:right;">0.579</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.128</td>
   <td style="text-align:right;">0.118</td>
   <td style="text-align:right;">0.918</td>
   <td style="text-align:right;">0.104</td>
   <td style="text-align:right;">0.808</td>
   <td style="text-align:right;">0.092</td>
   <td style="text-align:right;">0.718</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">I</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">0.066</td>
   <td style="text-align:right;">0.059</td>
   <td style="text-align:right;">0.894</td>
   <td style="text-align:right;">0.049</td>
   <td style="text-align:right;">0.738</td>
   <td style="text-align:right;">0.040</td>
   <td style="text-align:right;">0.608</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">0.124</td>
   <td style="text-align:right;">0.115</td>
   <td style="text-align:right;">0.927</td>
   <td style="text-align:right;">0.102</td>
   <td style="text-align:right;">0.826</td>
   <td style="text-align:right;">0.092</td>
   <td style="text-align:right;">0.740</td>
  </tr>
</tbody>
</table>

<details>

<summary>R script</summary>

```R
library(stringr)
library(tidyr)
library(dplyr)
library(RPostgres)

con <- dbConnect(
  RPostgres::Postgres(),
  user = "postgres",
  password = "postgres",
  dbname = "postgres"
)

df <- dbGetQuery(
  con,
  "with median as (select problem,
                       instance,
                       representation,
                       (configuration ->> 'populationSize')::integer              as population_size,
                       percentile_cont(0.5) within group ( order by hypervolume ) as hypervolume
                from run
                         join stats on run.id = stats.run_id and run.total_iterations = stats.iteration
                group by problem, instance, representation, population_size),
     baseline as (select distinct on (problem, instance, representation) *
                  from median
                  order by problem, instance, representation, population_size desc)
select b.problem,
       b.instance,
       b.representation,
       b.hypervolume                                  as ref_hv,
       m.population_size / b.population_size::numeric as factor,
       m.hypervolume                                  as cmp_hv,
       m.hypervolume / b.hypervolume                  as ratio
from baseline b
         join median m on b.problem = m.problem and
                          b.instance = m.instance and
                          b.representation = m.representation and
                          b.population_size != m.population_size
order by b.problem, b.instance, m.population_size, b.representation"
)

dbDisconnect(con)

result <- df %>%
  filter(problem == "Knapsack") %>%
  select(!problem) %>%
  mutate(model = str_match(instance, "Input_(\\w)\\.xmi")[, 2]) %>%
  relocate(model) %>%
  select(!instance) %>%
  pivot_wider(names_from = factor, values_from = c(cmp_hv, ratio)) %>%
  relocate(c(cmp_hv_0.5, ratio_0.5), .after = ref_hv) %>%
  relocate(c(cmp_hv_0.2, ratio_0.2), .after = ratio_0.5) %>%
  relocate(c(cmp_hv_0.1, ratio_0.1), .after = ratio_0.2)

write.csv(result)
```

</details>

</details>


<details>

<summary>Total Duration</summary>

**Table 5:** Millisecond duration ratio between population sizes for the knapsack problem [ [CSV](data/PopSize_Dur.csv) | [PDF](data/PopSize_Dur.pdf) ]

<table>
 <thead>
  <tr>
   <th style="text-align: left;" colspan="3"></th>
   <th style="text-align: center;" colspan="2">50%</th>
   <th style="text-align: center;" colspan="2">20%</th>
   <th style="text-align: center;" colspan="2">10%</th>
  </tr>
  <tr>
   <th style="text-align:left;">Instance</th>
   <th style="text-align:left;">Repr</th>
   <th style="text-align:right;">Ref</th>
   <th style="text-align:right;">Cmp</th>
   <th style="text-align:right;">Ratio</th>
   <th style="text-align:right;">Cmp</th>
   <th style="text-align:right;">Ratio</th>
   <th style="text-align:right;">Cmp</th>
   <th style="text-align:right;">Ratio</th>
  </tr>
 </thead>
<tbody>
  <tr>
   <td style="text-align:left;" rowspan="2">A</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">1075.504</td>
   <td style="text-align:right;">335.406</td>
   <td style="text-align:right;">0.312</td>
   <td style="text-align:right;">148.851</td>
   <td style="text-align:right;">0.138</td>
   <td style="text-align:right;">63.357</td>
   <td style="text-align:right;">0.059</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">54470.898</td>
   <td style="text-align:right;">33331.024</td>
   <td style="text-align:right;">0.612</td>
   <td style="text-align:right;">11612.936</td>
   <td style="text-align:right;">0.213</td>
   <td style="text-align:right;">4441.444</td>
   <td style="text-align:right;">0.082</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">B</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">3220.463</td>
   <td style="text-align:right;">1093.218</td>
   <td style="text-align:right;">0.339</td>
   <td style="text-align:right;">304.991</td>
   <td style="text-align:right;">0.095</td>
   <td style="text-align:right;">140.783</td>
   <td style="text-align:right;">0.044</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">218768.048</td>
   <td style="text-align:right;">103778.011</td>
   <td style="text-align:right;">0.474</td>
   <td style="text-align:right;">41755.355</td>
   <td style="text-align:right;">0.191</td>
   <td style="text-align:right;">16881.808</td>
   <td style="text-align:right;">0.077</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">C</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">6535.075</td>
   <td style="text-align:right;">2258.960</td>
   <td style="text-align:right;">0.346</td>
   <td style="text-align:right;">451.063</td>
   <td style="text-align:right;">0.069</td>
   <td style="text-align:right;">287.314</td>
   <td style="text-align:right;">0.044</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">440797.537</td>
   <td style="text-align:right;">233299.670</td>
   <td style="text-align:right;">0.529</td>
   <td style="text-align:right;">86434.798</td>
   <td style="text-align:right;">0.196</td>
   <td style="text-align:right;">39293.718</td>
   <td style="text-align:right;">0.089</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">D</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">4205.138</td>
   <td style="text-align:right;">1174.168</td>
   <td style="text-align:right;">0.279</td>
   <td style="text-align:right;">187.304</td>
   <td style="text-align:right;">0.045</td>
   <td style="text-align:right;">68.450</td>
   <td style="text-align:right;">0.016</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">69867.706</td>
   <td style="text-align:right;">34493.888</td>
   <td style="text-align:right;">0.494</td>
   <td style="text-align:right;">12751.938</td>
   <td style="text-align:right;">0.183</td>
   <td style="text-align:right;">6217.588</td>
   <td style="text-align:right;">0.089</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">E</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">9378.702</td>
   <td style="text-align:right;">2508.885</td>
   <td style="text-align:right;">0.268</td>
   <td style="text-align:right;">539.137</td>
   <td style="text-align:right;">0.057</td>
   <td style="text-align:right;">293.450</td>
   <td style="text-align:right;">0.031</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">236576.420</td>
   <td style="text-align:right;">135069.748</td>
   <td style="text-align:right;">0.571</td>
   <td style="text-align:right;">55456.571</td>
   <td style="text-align:right;">0.234</td>
   <td style="text-align:right;">26513.293</td>
   <td style="text-align:right;">0.112</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">F</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">17856.719</td>
   <td style="text-align:right;">5263.803</td>
   <td style="text-align:right;">0.295</td>
   <td style="text-align:right;">1335.095</td>
   <td style="text-align:right;">0.075</td>
   <td style="text-align:right;">633.265</td>
   <td style="text-align:right;">0.035</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">617177.219</td>
   <td style="text-align:right;">350845.682</td>
   <td style="text-align:right;">0.568</td>
   <td style="text-align:right;">139282.419</td>
   <td style="text-align:right;">0.226</td>
   <td style="text-align:right;">70203.353</td>
   <td style="text-align:right;">0.114</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">G</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">6585.628</td>
   <td style="text-align:right;">1846.864</td>
   <td style="text-align:right;">0.280</td>
   <td style="text-align:right;">395.159</td>
   <td style="text-align:right;">0.060</td>
   <td style="text-align:right;">110.670</td>
   <td style="text-align:right;">0.017</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">78467.483</td>
   <td style="text-align:right;">43503.523</td>
   <td style="text-align:right;">0.554</td>
   <td style="text-align:right;">17151.149</td>
   <td style="text-align:right;">0.219</td>
   <td style="text-align:right;">7634.321</td>
   <td style="text-align:right;">0.097</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">H</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">15796.814</td>
   <td style="text-align:right;">4899.069</td>
   <td style="text-align:right;">0.310</td>
   <td style="text-align:right;">1040.127</td>
   <td style="text-align:right;">0.066</td>
   <td style="text-align:right;">332.421</td>
   <td style="text-align:right;">0.021</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">354599.370</td>
   <td style="text-align:right;">189800.590</td>
   <td style="text-align:right;">0.535</td>
   <td style="text-align:right;">73550.367</td>
   <td style="text-align:right;">0.207</td>
   <td style="text-align:right;">35002.265</td>
   <td style="text-align:right;">0.099</td>
  </tr>
  <tr>
   <td style="text-align:left;" rowspan="2">I</td>
   <td style="text-align:left;">IntArray</td>
   <td style="text-align:right;">30831.150</td>
   <td style="text-align:right;">9441.259</td>
   <td style="text-align:right;">0.306</td>
   <td style="text-align:right;">2084.313</td>
   <td style="text-align:right;">0.068</td>
   <td style="text-align:right;">794.106</td>
   <td style="text-align:right;">0.026</td>
  </tr>
  <tr>
   <td style="text-align:left;">Model</td>
   <td style="text-align:right;">832321.589</td>
   <td style="text-align:right;">472604.589</td>
   <td style="text-align:right;">0.568</td>
   <td style="text-align:right;">174486.022</td>
   <td style="text-align:right;">0.210</td>
   <td style="text-align:right;">80790.500</td>
   <td style="text-align:right;">0.097</td>
  </tr>
</tbody>
</table>

<details>

<summary>R script</summary>

```R
library(stringr)
library(tidyr)
library(dplyr)
library(RPostgres)

con <- dbConnect(
  RPostgres::Postgres(),
  user = "postgres",
  password = "postgres",
  dbname = "postgres"
)

df <- dbGetQuery(
  con,
  "with duration as (select run_id, sum(step + should_terminate) / 1000000 as total from stats group by run_id),
     median as (select problem,
                       instance,
                       representation,
                       (configuration ->> 'populationSize')::integer        as population_size,
                       percentile_cont(0.5) within group ( order by total ) as total
                from run
                         join duration on run.id = duration.run_id
                group by problem, instance, representation, population_size),
     baseline as (select distinct on (problem, instance, representation) *
                  from median
                  order by problem, instance, representation, population_size desc)
select b.problem,
       b.instance,
       b.representation,
       b.total                                        as ref_total,
       m.population_size / b.population_size::numeric as factor,
       m.total                                        as cmp_total,
       m.total / b.total                              as ratio
from baseline b
         join median m on b.problem = m.problem and
                          b.instance = m.instance and
                          b.representation = m.representation and
                          b.population_size != m.population_size
order by b.problem, b.instance, m.population_size, b.representation"
)

dbDisconnect(con)

result <- df %>%
  filter(problem == "Knapsack") %>%
  select(!problem) %>%
  mutate(model = str_match(instance, "(?:TTC_)?Input(?:RDG)?_(\\w)\\.xmi")[, 2]) %>%
  relocate(model) %>%
  select(!instance) %>%
  pivot_wider(names_from = factor, values_from = c(cmp_total, ratio)) %>%
  relocate(c(cmp_total_0.5, ratio_0.5), .after = ref_total) %>%
  relocate(c(cmp_total_0.2, ratio_0.2), .after = ratio_0.5) %>%
  relocate(c(cmp_total_0.1, ratio_0.1), .after = ratio_0.2)

write.csv(result)
```

</details>

</details>


## Custom Copy Implementation

<details>

<summary>Total Duration</summary>

**Table 6:** Median total duration for the knapsack problem using the EcoreUtil-based vs. a custom copy implementation and resulting improvement [ [CSV](data/FastCopy_Dur.csv) | [PDF](data/FastCopy_Dur.pdf) ]

<table>
 <thead>
  <tr>
   <th style="text-align:left;">Instance</th>
   <th style="text-align:right;">Ref</th>
   <th style="text-align:right;">Cmp</th>
   <th style="text-align:right;">Ratio</th>
  </tr>
 </thead>
<tbody>
  <tr>
   <td style="text-align:left;">A</td>
   <td style="text-align:right;">54470.90</td>
   <td style="text-align:right;">23653.09</td>
   <td style="text-align:right;">0.434</td>
  </tr>
  <tr>
   <td style="text-align:left;">B</td>
   <td style="text-align:right;">218768.05</td>
   <td style="text-align:right;">111201.01</td>
   <td style="text-align:right;">0.508</td>
  </tr>
  <tr>
   <td style="text-align:left;">C</td>
   <td style="text-align:right;">440797.54</td>
   <td style="text-align:right;">268865.77</td>
   <td style="text-align:right;">0.610</td>
  </tr>
  <tr>
   <td style="text-align:left;">D</td>
   <td style="text-align:right;">69867.71</td>
   <td style="text-align:right;">39429.32</td>
   <td style="text-align:right;">0.564</td>
  </tr>
  <tr>
   <td style="text-align:left;">E</td>
   <td style="text-align:right;">236576.42</td>
   <td style="text-align:right;">134867.21</td>
   <td style="text-align:right;">0.570</td>
  </tr>
  <tr>
   <td style="text-align:left;">F</td>
   <td style="text-align:right;">617177.22</td>
   <td style="text-align:right;">388436.76</td>
   <td style="text-align:right;">0.629</td>
  </tr>
  <tr>
   <td style="text-align:left;">G</td>
   <td style="text-align:right;">78467.48</td>
   <td style="text-align:right;">43898.11</td>
   <td style="text-align:right;">0.559</td>
  </tr>
  <tr>
   <td style="text-align:left;">H</td>
   <td style="text-align:right;">354599.37</td>
   <td style="text-align:right;">205978.15</td>
   <td style="text-align:right;">0.581</td>
  </tr>
  <tr>
   <td style="text-align:left;">I</td>
   <td style="text-align:right;">832321.59</td>
   <td style="text-align:right;">536869.02</td>
   <td style="text-align:right;">0.645</td>
  </tr>
</tbody>
</table>

<details>

<summary>R script</summary>

```R
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
with duration as (select run_id, sum(step + should_terminate) / 1000000 as total from stats group by run_id),
     median as (select problem,
                       instance,
                       representation,
                       percentile_cont(0.5) within group ( order by total ) as total
                from run
                         join duration on run.id = duration.run_id
                group by problem, instance, representation),
     baseline as (select *
                  from median
                  where representation = 'Model')
select substring(b.instance from length('Input_') + 1 for 1) as model,
       b.total                                               as ref_total,
       m.total                                               as cmp_total,
       m.total / b.total                                     as ratio
from baseline b
         join median m on b.problem = m.problem and
                          b.instance = m.instance
where m.representation = 'Model.FastCopy'
order by b.instance
  "
)

dbDisconnect(con)
write.csv(df)
```

</details>

</details>
