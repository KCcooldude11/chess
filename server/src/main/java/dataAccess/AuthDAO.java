package dataAccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthDAO implements IAuthDAO {
    private Connection connection;

    // Constructor that accepts a database connection
    public AuthDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM auth_tokens WHERE authToken = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                return new AuthData(authToken, username);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get auth token due to SQL error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String createAuthToken(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        String sql = "INSERT INTO auth_tokens (authToken, username) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            return authToken;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create auth token due to SQL error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth_tokens WHERE authToken = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth token due to SQL error: " + e.getMessage());
        }
    }

    @Override
    public void clearAuthTokens() throws DataAccessException {
        String sql = "DELETE FROM auth_tokens";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear auth tokens due to SQL error: " + e.getMessage());
        }
    }
}
