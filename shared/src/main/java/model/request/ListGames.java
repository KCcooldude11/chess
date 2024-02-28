package model.request;

public class ListGames {
    private String authToken;

    public ListGames(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

}
