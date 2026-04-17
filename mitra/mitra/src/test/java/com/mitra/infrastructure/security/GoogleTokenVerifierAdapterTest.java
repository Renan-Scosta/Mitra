package com.mitra.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GoogleTokenVerifierAdapterTest {

    private GoogleTokenVerifierAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new GoogleTokenVerifierAdapter("dummy-client-id");
    }

    @Test
    void shouldReturnEmptyWhenTokenIsInvalid() {
        Optional<String> result = adapter.verifyToken("invalid.token.string");
        assertTrue(result.isEmpty());
    }
    
    @Test
    void shouldReturnEmptyWhenTokenIsNull() {
         Optional<String> result = adapter.verifyToken(null);
         assertTrue(result.isEmpty());
    }
}
