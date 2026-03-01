package repository;

import exception.DatabaseOperationException;
import model.Player;
import util.DatabaseHandler;

import java.sql.*;
import java.util.Optional;

public class PlayerRepository {

    public Optional<Player> findByName(String name) {
        String sql = "SELECT ID, Name FROM Players WHERE Name = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                long id = resultSet.getLong("ID");
                String playerName = resultSet.getString("Name");

                Player player = new Player(playerName);
                player.setId(id);

                return Optional.of(player);
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Ошибка при поиске игрока по имени", e);
        }
    }


    public Player create(Player player) {
        String sql = "INSERT INTO Players (Name) VALUES (?)";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, player.getName());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                long id = generatedKeys.getLong(1);
                player.setId(id);
            } else {
                throw new SQLException("Создание игрока не удалось, ID не получен.");
            }

            return player;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Ошибка при создании игрока", e);
        }
    }
}