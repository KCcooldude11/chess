package model.request;

public class ListGamesReq {
    private String authToken;

    public ListGamesReq(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
