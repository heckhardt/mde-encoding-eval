# Repository Structure

- `data/` contains evaluation results in CSV/PDF format
- `input/` contains the EMF XMI file for each problem instance
- `src/` contains the Java source code for running the evaluation
  - _TODO_
- `compose.yaml` contains the database configuration for Docker
- `query.sql` contains the SQL query statements to retrieve the data as it is presented below
- `schema.sql` contains the database schema

# Running the evaluation

While some attempts were made to make the source code as easy-to-use as possible, some manual work is still required to reproduce the data presented below.
The following explains the steps necessary to set up the database, build the source code, and run the evaluation.

## Prerequisites
- JDK 21 (Eclipse Temurin binaries are available [here](https://adoptium.net/de/temurin/releases/?os=any&arch=any&version=21))
- PostgreSQL 17

### Database setup
The easiest way to set up the database is to run it in a container via [Docker](https://www.docker.com/) and the provided `compose.yaml` file by running `docker compose up -d` from the repository root.
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
If this is not the case, adjust these values in `src/main/java/Main.java` accordingly before proceeding with the build step._

Once the database server is set up, the database schema can be created by applying the `schema.sql` SQL file to the database:
```bash
psql -U postgres -d postgres -f schema.sql
```
This will create three tables, along with relevant indices:
- `run` contains information about each run (the problem, instance, algorithm, configuration etc.)
- `stats` contains timing and quality information about each iteration of each run
- `solution` contains the objective and constraint values for each solution at each iteration of every run. _This will be by far the largest table. Ensure at least ~30GB of storage space are available to the database server to prevent issues._

Additionally, some functions and aggregates are created to simplify querying the data later on, as well as two views providing the (median) cumulative stats from the beginning to each iteration of every run.  

</details>

### Configuring and running the evaluation
The runner is configured for a machine with at least 24 cores/logical processors.
If this is not the case, adjust the `parallelism` and `maximumPoolSize` in `src/main/java/Main.java` accordingly.
`parallelism` represents the number of threads that will be used to run multiple algorithms in parallel, i.e., it should not exceed the number of cores and leave some headroom for the database server (if running locally) and operating system.
`maximumPoolSize` should be **at least** as high as `parallelism`, otherwise some threads will be left waiting for a database connection to become available between iterations. _This delay is **not** included in the timing measurements, but can drastically increase the (already high) total runtime duration of the evaluation._

Finally, to run the evaluation, first build a runnable JAR file via `gradlew shadowJar`.
Then, start the runner with the following command:
```bash
java -jar -XX:+UseZGC -XX:+ZGenerational -XX:SoftMaxHeapSize=12g -Xmx16g build/libs/moea-evaluation-1.0-SNAPSHOT-all.jar
```
The values for `SoftMaxHeapSize` and `Xmx` can be adjusted, if needed, to allow running on machines with lower available memory. 

This will first perform a JVM warmup step, followed by 50 timed runs for each problem instance and representation, running 20 algorithms in parallel (by default).
After all algorithms have terminated, the hypervolume is calculated for each iteration, normalized according to the reference set, i.e., the upper and lower bounds of the objectives of best solutions that were found throughout all runs for a given instance. 


# Evaluation Results

## Descriptive Statistics

_TODO_

## Influence of Population Size

**Table 1:** Hypervolume ratio between population sizes for the knapsack problem [ [CSV](data/PopSize_Hv.csv) | [PDF]() ]

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


**Table 2:** Millisecond duration ratio between population sizes for the knapsack problem [ [CSV](data/PopSize_Dur.csv) | [PDF]() ]

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
