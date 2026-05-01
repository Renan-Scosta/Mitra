package com.mitra.infrastructure.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class DatabaseLatencyHealthIndicatorTest {

    private JdbcTemplate jdbcTemplate;
    private DatabaseLatencyHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        healthIndicator = new DatabaseLatencyHealthIndicator(jdbcTemplate);
    }

    @Test
    @DisplayName("Should return UP with latency detail when database is accessible")
    void shouldReturnUpWhenDatabaseAccessible() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("database", "PostgreSQL");
        assertThat(health.getDetails()).containsKey("latency_ms");
    }

    @Test
    @DisplayName("Should return DOWN with exception when database is inaccessible")
    void shouldReturnDownWhenDatabaseInaccessible() {
        var exception = new DataAccessResourceFailureException("Connection refused");
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenThrow(exception);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("error");
    }
}
