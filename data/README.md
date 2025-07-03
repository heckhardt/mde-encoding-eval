# Result Data

This directory contains the data collected throughout the experiments, both raw and aggregate.
The files in this directory contain the aggregate results for various experiments:
- Files with names beginning with `Stats` contain the descriptive statistics for the default experiments regarding the hypervolume (`Hv`) as well as the total duration (`Dur_Total`) and duration per iteration (`Dur_PerIt`) in milliseconds.
- Those beginning with `PopSize` contain the results for reduced population sizes, including median total duration in milliseconds (`Dur`) and hypervolume (`Hv`).
  `Ref` refers to the results from the baseline (i.e., reference) results, while the `Cmp` columns show the results using reduces population sizes noted above.
  `Ratio` means the ratio between the baseline and corresponding comparison values.
- The `FastCopy` results contain the aggregate data for the additional experiments with a custom copy implementation regarding the total duration in milliseconds.
  `Ref`, `Cmp`, and `Ratio` again refer to the baseline and the newly collected results, as well as the ratio between them.
- Raw, non-aggregated data for all setups is located within the `raw/` directory 