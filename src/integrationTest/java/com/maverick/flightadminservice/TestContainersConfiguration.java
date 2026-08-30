package com.maverick.flightadminservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;

import org.testcontainers.mssqlserver.MSSQLServerContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {

    @Bean
    @ServiceConnection
    MSSQLServerContainer sqlServerContainer() {

        return new MSSQLServerContainer(
                "mcr.microsoft.com/mssql/server:2022-CU20-ubuntu-22.04"
        )
                .acceptLicense();
    }
}