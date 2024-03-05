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

    // Constructor that accepts a database connection
    public GameDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Integer createGame(String gameName, ChessGame game) {
        String sql = "INSERT INTO games (gameName, gameState) VALUES (?, ?)";
        String gameState = gson.toJson(game); // Serialize the ChessGame object to a JSON string
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, gameName);
            pstmt.setString(2, gameState);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Retrieve the auto-generated gameID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        String sql = "SELECT * FROM games WHERE gameID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, gameID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String gameName = rs.getString("gameName");
                String gameStateJson = rs.getString("gameState");
                ChessGame game = gson.fromJson(gameStateJson, ChessGame.class); // Deserialize the JSON string to a ChessGame object
                return new GameData(gameID, null, null, gameName, game);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM games";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Adjust according to your GameData constructor
                games.add(new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                        rs.getString("blackUsername"), rs.getString("gameName"),
                        null)); // Placeholder for ChessGame, adjust as needed
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) {
        String sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameState = ? WHERE gameID = ?";
        String gameState = gson.toJson(game.getGame()); // Serialize the ChessGame object to a JSON string

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, game.getWhiteUsername());
            pstmt.setString(2, game.getBlackUsername());
            pstmt.setString(3, game.getGameName());
            pstmt.setString(4, gameState);
            pstmt.setInt(5, game.getGameID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void clearAllGames() {
        String sql = "DELETE FROM games";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
