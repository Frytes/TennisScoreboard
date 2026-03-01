package repository;

import exception.DatabaseOperationException;
import model.Match;
import model.Player;
import util.DatabaseHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchRepository {
    public void save(Match match){
        String sql = "INSERT INTO Matches (Player1, Player2, Winner, Score) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, match.getPlayer1().getId());
            preparedStatement.setLong(2, match.getPlayer2().getId());
            preparedStatement.setLong(3, match.getWinner().getId());
            String stringScore = String.format("%d - %d",
                    match.getScore().getPlayer1sets(),
                    match.getScore().getPlayer2sets());
            preparedStatement.setString(4, stringScore);
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    match.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Создание матча не удалось: ID не получен.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Ошибка при обновление матча", e);
        }
    }

    public List<Match> findAllByFilter(String playerName, int limit, int offset) {
        StringBuilder sql = new StringBuilder("""
            SELECT 
                m.ID AS MatchID,
                p1.ID AS Player1ID, p1.Name AS Player1Name,
                p2.ID AS Player2ID, p2.Name AS Player2Name,
                w.ID AS WinnerID, w.Name AS WinnerName,
                m.Score
            FROM Matches m
            JOIN Players p1 ON m.Player1 = p1.ID
            JOIN Players p2 ON m.Player2 = p2.ID
            JOIN Players w ON m.Winner = w.ID
        """);

        boolean hasFilter = playerName != null && !playerName.trim().isEmpty();

        if (hasFilter) {
            sql.append(" WHERE p1.Name ILIKE ? OR p2.Name ILIKE ?");
        }

        sql.append(" ORDER BY m.ID DESC LIMIT ? OFFSET ?");

        List<Match> matches = new ArrayList<>();

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (hasFilter) {
                String pattern = "%" + playerName + "%";
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
            }

            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex, offset);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long player1Id = rs.getLong("Player1ID");
                String player1Name = rs.getString("Player1Name");
                Player player1 = new Player(player1Name);
                player1.setId(player1Id);

                long player2Id = rs.getLong("Player2ID");
                String player2Name = rs.getString("Player2Name");
                Player player2 = new Player(player2Name);
                player2.setId(player2Id);

                long winnerId = rs.getLong("WinnerID");
                String winnerName = rs.getString("WinnerName");
                Player winner = new Player(winnerName);
                winner.setId(winnerId);

                Match match = new Match(player1, player2);
                match.setId(rs.getLong("MatchID"));
                match.setWinner(winner);

                String scoreStr = rs.getString("Score");
                if (scoreStr != null && scoreStr.contains(" - ")) {
                    String[] parts = scoreStr.split(" - ");
                    match.getScore().setPlayer1sets(Integer.parseInt(parts[0]));
                    match.getScore().setPlayer2sets(Integer.parseInt(parts[1]));
                }

                matches.add(match);
            }

            return matches;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Ошибка при поиске матчей по фильтру", e);
        }
    }

    public long countMatchesByFilter(String playerName) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM Matches m
            JOIN Players p1 ON m.Player1 = p1.ID
            JOIN Players p2 ON m.Player2 = p2.ID
        """);


        boolean hasFilter = playerName != null && !playerName.trim().isEmpty();

        if (hasFilter) {
            sql.append(" WHERE p1.Name ILIKE ? OR p2.Name ILIKE ?");
        }

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            if (hasFilter) {
                String pattern = "%" + playerName + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Ошибка при подсчете количества матчей", e);
        }
    }
}
