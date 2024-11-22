package io.github.sekassel.moea.store;

import io.github.sekassel.moea.util.ConfigUtil;
import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.postgresql.PGConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class PGDataStore implements DataStore {
    private final DataSource dataSource;

    public PGDataStore(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int initializeRun(AbstractEvolutionaryAlgorithm algorithm, String instance, String representation)
            throws SQLException {
        final String sql = """
                INSERT INTO run (problem, instance, representation, algorithm, configuration)
                VALUES (?, ?, ?, ?, ?::jsonb)
                RETURNING id
                """;
        try (final Connection conn = dataSource.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, algorithm.getProblem().getName());
            ps.setString(2, instance);
            ps.setString(3, representation);
            ps.setString(4, algorithm.getClass().getSimpleName());
            ps.setString(5, ConfigUtil.toJson(algorithm.getConfiguration()));
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new NoSuchElementException("Something went wrong during run initialization, database did not return any rows.");
                }
            }
        }
    }

    @Override
    public void saveResult(int runId, int iteration, NondominatedPopulation result) throws SQLException {
        final String sql = """
                INSERT INTO solution (run_id, iteration, objectives, constraints)
                VALUES (?, ?, ?, ?)
                """;
        try (final Connection conn = dataSource.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            final PGConnection pgConn = conn.unwrap(PGConnection.class);
            for (final Solution solution : result) {
                ps.setInt(1, runId);
                ps.setInt(2, iteration);
                ps.setArray(3, pgConn.createArrayOf("double precision", solution.getObjectives()));
                ps.setArray(4, pgConn.createArrayOf("double precision", solution.getConstraints()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public void saveStats(int runId, int iteration, long step, long evaluate, long copy, long match, long mutate,
                          long shouldTerminate) throws SQLException {
        final String sql = """
                INSERT INTO stats (run_id, iteration, step, evaluate, copy, match, mutate, should_terminate)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (final Connection conn = dataSource.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, runId);
            ps.setInt(2, iteration);
            ps.setLong(3, step);
            ps.setLong(4, evaluate);
            ps.setLong(5, copy);
            ps.setLong(6, match);
            ps.setLong(7, mutate);
            ps.setLong(8, shouldTerminate);
            ps.executeUpdate();
        }
    }

    @Override
    public void finalizeRun(int runId, int totalIterations) throws SQLException {
        final String sql = """
                UPDATE run
                SET total_iterations = ?
                WHERE id = ?
                """;
        try (final Connection conn = dataSource.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, totalIterations);
            ps.setInt(2, runId);
            ps.executeUpdate();
        }
    }
}
