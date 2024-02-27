package server;

import static spark.Spark.delete;
import service.ClearService;
import service.ServiceException;
import com.google.gson.Gson;
import util.MessageResponse;
public class AdminHandler {

    private ClearService clearService;
    private Gson gson = new Gson();

    public AdminHandler(ClearService clearService) {
        this.clearService = clearService;
        setupRoutes();
    }

    public void setupRoutes() {
        // Endpoint to clear all data from the database
        delete("/db", (req, res) -> {
            try {
                clearService.clearAllData();
                res.status(200); // HTTP 200 OK
                res.type("application/json");
                return gson.toJson(new MessageResponse("All data cleared successfully."));
            } catch (ServiceException e) {
                res.status(500); // HTTP 500 Internal Server Error
                return gson.toJson(new MessageResponse("Failed to clear data: " + e.getMessage()));            }
        });
    }
}