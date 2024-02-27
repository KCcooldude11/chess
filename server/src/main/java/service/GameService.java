package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.request.*;
import model.end.*;

public class GameService {

    private final IGameDAO gameDAO;
    private final IAuthDAO authDAO;

    public GameService(IGameDAO gameDAO, IAuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }
    public CreateGameEnd createGame(CreateGameReq req, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuthToken(authToken);
        if(authData == null) {
            throw new DataAccessException("Unauthorized");
        }

        if(req.gameName() == null || req.gameName().isEmpty()) {
            throw new DataAccessException("Bad request");
        }

        Integer gameID = gameDAO.createGame(req.gameName());
        return new CreateGameEnd(gameID);
    }
    public ListGamesEnd listGames(ListGamesReq req) throws DataAccessException {
        AuthData authData = authDAO.getAuthToken(req.authToken());

        if(authData == null) {
            throw new DataAccessException("Unauthorized");
        }

        return new ListGamesEnd(gameDAO.listGames());
    }
    public void joinGame(JoinGameReq req, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuthToken(authToken);

        if(authData == null) {
            throw new DataAccessException("Unauthorized");
        }

        GameData game = gameDAO.getGame(req.gameID());
        if(game == null) {
            throw new DataAccessException("Bad request");
        }

        if(req.playerColor() == null) {
            return;
        }

        AuthData auth = authDAO.getAuthToken(authToken);
        if(req.playerColor().equals("WHITE")) {
            if(game.whiteUsername() == null) {
                game = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            } else {
                throw new DataAccessException("Already taken");
            }
        } else {
            if(game.blackUsername() == null) {
                game = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            } else {
                throw new DataAccessException("Already taken");
            }
        }

        gameDAO.updateGame(game);
    }

}
