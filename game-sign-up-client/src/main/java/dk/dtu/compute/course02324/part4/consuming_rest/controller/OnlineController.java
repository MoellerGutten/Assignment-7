package dk.dtu.compute.course02324.part4.consuming_rest.controller;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.List;

public class OnlineController {
    public RestClient restClient;


    public OnlineController() {
        try {
            restClient = RestClient.builder().baseUrl("http://localhost:8080").build();
        } catch (Exception e) {
            System.out.println("Can't open localhost:8080");
            throw e;
        }
    }


    /**
     * Adds a game to the backend with the given object game.
     * This is executed by making a post request.
     * @param game the game to post
     * @return the game from the backend
     */
    public Game addGame(Game game) {
        try {
            return restClient
                    .post()
                    .uri("/games")
                    .body(game)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            System.out.println("Error" + e);
            return null;
        }
    }


    /**
     * Replaces the game in the backend with the given game with the same uid.
     * This is executed by making a put request.
     *
     * @param game the game to be put in the backend
     */
    public void updateGame(Game game) {
        try {
            restClient
                    .put()
                    .uri("/games/updategame")
                    .body(game)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            System.out.println("Error" + e);
        }
    }

    /**
     * Deletes a game with the backend with the same uid as the given game.
     * This is executed by making a delete request.
     *
     * @param game the game to be deleted in the backend
     */
    public void deleteGame(Game game) {
        try {
            ResponseEntity<Void> result = restClient
                    .delete()
                    .uri("/games/{uid}", game.getUid())
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.out.println("Error" + e);
        }
    }

    /**
     * Gets all the open games from the backend.
     * This is executed by making a get request.
     * @return the list of open games from the backend
     */
    public List<Game> getOpenGames() {
        try {
            return restClient.
                    get().
                    uri("/games/opengames").
                    retrieve().
                    body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            System.out.println("Error" + e);
            return null;
        }
    }

    /**
     * Gets all the list from the backend.
     * This is executed by making a get request.
     * @return the users from the backend
     */
    public List<User> getUsers() {
        try {
            return restClient.
                    get().
                    uri("/users").
                    retrieve().
                    body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            System.out.println("Error" + e);
            return null;
        }
    }


    /**
     * Gets alle the users with the name stored in userName
     * and returns the user retrieved from the backend.
     * This is executed by making a get request.
     *
     * @param userName the name of the user to log in as.
     * @return the user with the equal name as stored in userName
     */
    public User signIn(String userName) {
        try {
            List<User> users = restClient.
                    get().
                    uri(uriBuilder -> uriBuilder
                            .path("users/searchusers")
                            .queryParam("name", userName)
                            .build()).
                    retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (users != null) {
                for (User user : users) {
                    if (user.getName().equals(userName)) {
                        return user;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error" + e);
        }

        return null;
    }


    /**
     * Checks if the name stored in userName is a user, if not creates a user with that name in the backend
     * This is executed by making a get and post request.
     *
     * @param userName the name of the user to create
     * @return the user created in the backend
     */
    public User signUp(String userName) {
        try {
            User newUser = new User();
            newUser.setName(userName);
            List<User> users = restClient.
                    get().
                    uri(uriBuilder -> uriBuilder
                            .path("users/searchusers")
                            .queryParam("name", userName)
                            .build()).
                    retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            if (users != null) {
                if (users.isEmpty()) {
                    return restClient
                            .post()
                            .uri("/users")
                            .body(newUser)
                            .retrieve()
                            .body(new ParameterizedTypeReference<>() {});
                }
            }
        } catch (Exception e) {
            System.out.println("Error" + e);
        }

        return null;
    }


    /**
     * Adds a player to a game in the backend.
     * This is executed by making a post request.
     *
     * @param player the player to join a game
     */
    public void addPlayer(Player player) {
        try {
            restClient
                    .post()
                    .uri("/players")
                    .body(player)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            System.out.println("Error" + e);
        }
    }

    /**
     * Deletes a player in the backend.
     * This is executed by making a delete request
     *
     * @param player the player to be deleted
     */
    public void deletePlayer(Player player) {
        try {
            ResponseEntity<Void> result = restClient
                    .delete()
                    .uri("/players/{uid}", player.getUid())
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.out.println("Error" + e);
        }
    }
}
