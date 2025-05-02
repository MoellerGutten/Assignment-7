package dk.dtu.compute.course02324.part4.consuming_rest.controller;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperPlayers;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperUsers;
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

    public Game addGame(Game game) {
        try {
            return restClient
                    .post()
                    .uri("/games")
                    .body(game)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Game updateGame(Game game) {
        try {
            return restClient
                    .put()
                    .uri("/games/updategame")
                    .body(game)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public boolean deleteGame(Game game) {
        try {
            ResponseEntity<Void> result = restClient
                    .delete()
                    .uri("/games/{uid}", game.getUid())
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }


    public List<Game> getOpenGames() {
        try {
            return restClient.
                    get().
                    uri("/games/opengames").
                    retrieve().
                    body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public List<User> getUsers() {
        try {
            return restClient.
                    get().
                    uri("/users").
                    retrieve().
                    body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }


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

            for (User user : users) {
                if (user.getName().equals(userName)) {
                    return user;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }


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
            if (users.isEmpty()) {
                return restClient
                        .post()
                        .uri("/users")
                        .body(newUser)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }


    public boolean addPlayer(Player player) {
        try {
            restClient
                    .post()
                    .uri("/players")
                    .body(player)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean deletePlayer(Player player) {
        try {
            ResponseEntity<Void> result = restClient
                    .delete()
                    .uri("/players/{uid}", player.getUid())
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
