package com.bsi.pusbin.config;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads variables from a .env file in the working directory.
 * System env and JVM -D flags always take precedence.
 * No-op when .env does not exist (production uses real env vars).
 */
public class DotEnvPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "dotenv";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path dotEnvPath = Paths.get(".env");
        if (!Files.exists(dotEnvPath)) {
            return;
        }

        Map<String, Object> properties = new HashMap<>();
        try {
            for (String line : Files.readAllLines(dotEnvPath)) {
                line = line.strip();
                if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
                    continue;
                }
                int idx = line.indexOf('=');
                String key = line.substring(0, idx).strip();
                String value = line.substring(idx + 1).strip();
                if (!environment.containsProperty(key)) {
                    properties.put(key, value);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read .env file", e);
        }

        if (!properties.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        }
    }
}
