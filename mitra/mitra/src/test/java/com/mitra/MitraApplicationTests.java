package com.mitra;

import com.mitra.infrastructure.config.TestSecurityConfig;
import com.mitra.infrastructure.persistence.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class MitraApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {}
}
