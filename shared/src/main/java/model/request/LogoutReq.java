package model.request;

public class LogoutReq {
    private String authToken;

    public LogoutReq(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

}
