package com.mitra.infrastructure.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("databaseLatency")
public class DatabaseLatencyHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseLatencyHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            long start = System.currentTimeMillis();
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            long latency = System.currentTimeMillis() - start;

            return Health.up()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("latency_ms", latency)
                    .build();
        } catch (Exception e) {
            log.error("Database connectivity check failed", e);
            return Health.down(e).build();
        }
    }
}
