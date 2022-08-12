package com.github.sentrionic.olympusblog.repository;

import org.testcontainers.containers.PostgreSQLContainer;

public abstract class BaseRepositoryTest {
    static PostgreSQLContainer postgresContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres:alpine")
            .withDatabaseName("olympus_blog_test")
            .withUsername("root")
            .withPassword("password")
            .withReuse(true);

    static {
        postgresContainer.start();
    }
}
