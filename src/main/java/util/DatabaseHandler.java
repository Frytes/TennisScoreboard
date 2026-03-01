package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import exception.DatabaseOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;


public class DatabaseHandler {
    private static final HikariDataSource dataSource;

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

            String jdbcUrl = System.getenv("DB_URL");
            if (jdbcUrl == null) {
                jdbcUrl = properties.getProperty("db.url");
            }

            String username = System.getenv("DB_USER");
            if (username == null) {
                username = properties.getProperty("db.user");
            }

            String password = System.getenv("DB_PASSWORD");
            if (password == null) {
                password = properties.getProperty("db.password");
            }

            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName(properties.getProperty("db.driver.name"));

            config.setMaximumPoolSize(20);

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
                throw new DatabaseOperationException("Файл schema.sql не найден! Проверь папку resources.");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String sql = reader.lines().collect(Collectors.joining("\n"));

                try (Connection connection = getConnection();
                     Statement statement = connection.createStatement()) {

                    statement.execute(sql);
                }
            }
        } catch (Exception e) {
            throw new DatabaseOperationException("Ошибка при инициализации БД", e);
        }
    }
}