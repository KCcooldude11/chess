package model.request;

public class Logout {
    private String authToken;

    public Logout(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

}
