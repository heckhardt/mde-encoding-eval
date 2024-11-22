package io.github.sekassel.moea;

import org.apache.commons.lang3.ArrayUtils;
import org.moeaframework.core.Indicator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.indicator.WFGNormalizedHypervolume;
import org.moeaframework.problem.ProblemStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public class Evaluation {
    private static final Logger LOGGER = LoggerFactory.getLogger(Evaluation.class);

    private final DataSource ds;
    private final ExecutorService executor;

    public Evaluation(DataSource ds, ExecutorService executor) {
        this.ds = ds;
        this.executor = executor;
    }

    public void calculateNormalizedHypervolume(String problem, String instance, int numberOfObjectives) {
        final NondominatedPopulation referenceSet = getReferenceSet(problem, instance);
        final Indicator indicator = new WFGNormalizedHypervolume(new ProblemStub(numberOfObjectives), referenceSet);
        final String sql = """
                select stats.run_id, stats.iteration
                from stats
                         join run on stats.run_id = run.id
                where problem = ?
                  and instance = ?
                  and hypervolume is null
                """;
        try (final Connection conn = ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, problem);
            ps.setString(2, instance);
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final int runId = rs.getInt(1);
                    final int iteration = rs.getInt(2);
                    executor.execute(() -> {
                        final double hypervolume = calculateNormalizedHypervolume(runId, iteration, indicator);
                        updateStats(runId, iteration, hypervolume);
                    });
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private double calculateNormalizedHypervolume(int runId, int iteration, Indicator indicator) {
        final String sql = """
                select objectives
                from solution
                where run_id = ?
                  and iteration = ?
                """;
        try (final Connection conn = ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, runId);
            ps.setInt(2, iteration);
            try (final ResultSet rs = ps.executeQuery()) {
                final NondominatedPopulation approximationSet = getNondominatedPopulation(rs, 1);
                return indicator.evaluate(approximationSet);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return -1;
    }

    private void updateStats(long runId, int iteration, double hypervolume) {
        final String sql = """
                update stats
                set hypervolume = ?
                where run_id = ?
                  and iteration = ?
                """;
        try (final Connection conn = ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, hypervolume);
            ps.setLong(2, runId);
            ps.setInt(3, iteration);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private NondominatedPopulation getReferenceSet(String problem, String instance) {
        final String sql = """
                select distinct on (objectives) objectives
                from solution
                         join run on solution.run_id = run.id and solution.iteration = run.total_iterations
                where problem = ?
                  and instance = ?
                """;
        try (final Connection conn = ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, problem);
            ps.setString(2, instance);
            try (final ResultSet rs = ps.executeQuery()) {
                return getNondominatedPopulation(rs, 1);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    private static NondominatedPopulation getNondominatedPopulation(ResultSet rs, int columnIndex)
            throws SQLException {
        final NondominatedPopulation population = new NondominatedPopulation();
        while (rs.next()) {
            final double[] objectives = ArrayUtils.toPrimitive(((Double[]) rs.getArray(columnIndex).getArray()));
            final Solution solution = new Solution(0, objectives.length);
            solution.setObjectives(objectives);
            population.add(solution);
        }
        return population;
    }
}
