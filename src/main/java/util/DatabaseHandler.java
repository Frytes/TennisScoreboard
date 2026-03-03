package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import exception.DatabaseOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

public final class DatabaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHandler.class);
    private static final HikariDataSource dataSource;
    private static final int DEFAULT_POOL_SIZE = 10;

    private DatabaseHandler() {}

    static {
        try {
            Properties properties = new Properties();
            try (InputStream input = DatabaseHandler.class.getClassLoader()
                    .getResourceAsStream("application.properties")) {
                if (input == null) {
                    throw new DatabaseOperationException("Не удалось найти файл application.properties");
                }
                properties.load(input);
            }

            HikariConfig config = new HikariConfig();

            String jdbcUrl = getEnvOrDefault("DB_URL", properties.getProperty("db.url"));
            String username = getEnvOrDefault("DB_USER", properties.getProperty("db.user"));
            String password = getEnvOrDefault("DB_PASSWORD", properties.getProperty("db.password"));
            String driverClass = properties.getProperty("db.driver.name");

            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName(driverClass);

            String poolSizeStr = getEnvOrDefault("DB_POOL_SIZE", properties.getProperty("db.pool.size"));
            configurePoolSize(config, poolSizeStr);

            dataSource = new HikariDataSource(config);
            initDb();

        } catch (IOException e) {
            throw new DatabaseOperationException("Ошибка конфигурации БД", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void initDb() {
        try (InputStream inputStream = DatabaseHandler.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (inputStream == null) {
                logger.warn("Файл schema.sql не найден! Пропуск инициализации схемы.");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String sql = reader.lines().collect(Collectors.joining("\n"));
                try (Connection connection = getConnection();
                     Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                    logger.info("Database schema initialized successfully.");
                }
            }
        } catch (Exception e) {
            throw new DatabaseOperationException("Ошибка при инициализации схемы БД", e);
        }
    }

    private static void configurePoolSize(HikariConfig config, String poolSizeStr) {
        try {
            int poolSize = Integer.parseInt(poolSizeStr);
            if (poolSize <= 0) {
                logger.warn("Invalid pool size in config: {}. Using default: {}", poolSize, DEFAULT_POOL_SIZE);
                config.setMaximumPoolSize(DEFAULT_POOL_SIZE);
            } else {
                config.setMaximumPoolSize(poolSize);
            }
        } catch (NumberFormatException e) {
            logger.error("Error parsing pool size: {}. Using default: {}", poolSizeStr, DEFAULT_POOL_SIZE);
            config.setMaximumPoolSize(DEFAULT_POOL_SIZE);
        }
    }

    private static String getEnvOrDefault(String envKey, String defaultValue) {
        String envValue = System.getenv(envKey);
        return envValue != null ? envValue : defaultValue;
    }
}