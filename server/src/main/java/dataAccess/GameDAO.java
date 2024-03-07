package dataAccess;

import model.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import com.google.gson.Gson;
import chess.ChessGame;

public class GameDAO implements IGameDAO {
    private final Connection connection;
    private final Gson gson = new Gson();

    public GameDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.isEmpty()) {
            throw new DataAccessException("Game name cannot be null or empty.");
        }
        String sql = "INSERT INTO games (gameName, gameState) VALUES (?, ?)";
        ChessGame defaultGameState = new ChessGame(); // Assuming initialization logic here
        String gameStateJson = gson.toJson(defaultGameState);
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, gameName);
            pstmt.setString(2, gameStateJson);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create game: " + e.getMessage());
        }
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE gameID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, gameID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String gameName = rs.getString("gameName");
                String gameStateJson = rs.getString("gameState");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                ChessGame game = gson.fromJson(gameStateJson, ChessGame.class);
                return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve game data: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM games";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                games.add(new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                        rs.getString("blackUsername"), rs.getString("gameName"),
                        gson.fromJson(rs.getString("gameState"), ChessGame.class))); // Adjust the deserialization according to your needs
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games: " + e.getMessage());
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameState = ? WHERE gameID = ?";
        String gameState = gson.toJson(game.getGame());

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, game.getWhiteUsername());
            pstmt.setString(2, game.getBlackUsername());
            pstmt.setString(3, game.getGameName());
            pstmt.setString(4, gameState);
            pstmt.setInt(5, game.getGameID());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Failed to update game: Game does not exist.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game: " + e.getMessage());
        }
    }

    @Override
    public void clearAllGames() throws DataAccessException {
        String sql = "DELETE FROM games";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear all games: " + e.getMessage());
        }
    }
}

