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
    public AuthData getAuthToken(String authToken) {
        String sql = "SELECT * FROM auth_tokens WHERE authToken = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                return new AuthData(authToken, username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String createAuthToken(String username) {
        String authToken = UUID.randomUUID().toString();
        String sql = "INSERT INTO auth_tokens (authToken, username) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            return authToken;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void clearAuthTokens() {
        String sql = "DELETE FROM auth_tokens";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAuthToken(String authToken) {
        String sql = "DELETE FROM auth_tokens WHERE authToken = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
