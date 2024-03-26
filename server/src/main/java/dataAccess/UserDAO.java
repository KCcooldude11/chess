package dataAccess;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import model.UserData;

import java.sql.*;

public class UserDAO implements IUserDAO {
    private Connection connection;
    private BCryptPasswordEncoder passwordEncoder;

    public UserDAO(Connection connection) {
        this.connection = connection;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String password = rs.getString("password");
                String email = rs.getString("email");
                return new UserData(username, password, email);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        String hashedPassword = passwordEncoder.encode(password);
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                throw new DataAccessException("A user with the given username already exists.");
            } else {
                throw new DataAccessException("Failed to create user: " + e.getMessage());
            }
        }
    }

    public boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return passwordEncoder.matches(providedClearTextPassword, hashedPassword);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to verify user: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void clearAllUsers() throws DataAccessException {
        String sql = "DELETE FROM users";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear all users: " + e.getMessage());
        }
    }
}
