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

        if(req.getGameName() == null || req.getGameName().isEmpty()) {
            throw new DataAccessException("Bad request");
        }

        Integer gameID = gameDAO.createGame(req.getGameName());
        return new CreateGameEnd(gameID);
    }
    public ListGamesEnd listGames(ListGamesReq req) throws DataAccessException {
        AuthData authData = authDAO.getAuthToken(req.getAuthToken());

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

        GameData game = gameDAO.getGame(req.getGameID());
        if(game == null) {
            throw new DataAccessException("Bad request");
        }

        if(req.getPlayerColor() == null) {
            return;
        }

        AuthData auth = authDAO.getAuthToken(authToken);
        if(req.getPlayerColor().equals("WHITE")) {
            if(game.getWhiteUsername() == null) {
                game = new GameData(game.getGameID(), auth.getUsername(), game.getBlackUsername(), game.getGameName(), game.getGame());
            } else {
                throw new DataAccessException("Already taken");
            }
        } else {
            if(game.getBlackUsername() == null) {
                game = new GameData(game.getGameID(), game.getWhiteUsername(), auth.getUsername(), game.getGameName(), game.getGame());
            } else {
                throw new DataAccessException("Already taken");
            }
        }

        gameDAO.updateGame(game);
    }

}
