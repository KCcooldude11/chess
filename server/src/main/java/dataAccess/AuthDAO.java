package dataAccess;

import model.AuthData;
import java.util.UUID;
import java.util.HashMap;

public class AuthDAO implements IAuthDAO {
    static private final HashMap<String, AuthData> authTokens = new HashMap<>();
    @Override
    public AuthData getAuthToken(String authToken) {
        return authTokens.get(authToken);
    }
    @Override
    public String createAuthToken(String username) {
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, username);
        authTokens.put(authToken, auth);
        return authToken;
    }
    @Override
    public void deleteAuthToken(String authToken) {
        authTokens.remove(authToken);
    }
    @Override
    public void clearAuthTokens() {
        authTokens.clear();
    }

}