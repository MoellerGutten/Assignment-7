package dk.dtu.compute.course02324.part4.consuming_rest;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperGames;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperPlayers;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperUsers;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public class OnlineController {
    private final RestClient customClient;

    public OnlineController() {
        customClient = RestClient.builder().
                // requestFactory(new HttpComponentsClientHttpRequestFactory()).
                        baseUrl("http://localhost:8080").
                build();

    }

    public List<Game> getGames() {
        return customClient.get().uri("/game").retrieve().body(HALWrapperGames.class).getGames();
    }

    public List<Game> getOpenGames() {
        return customClient.get().uri("/games/opengames").retrieve().body(new ParameterizedTypeReference<>() {});
    }

    public List<String> getPlayerPerGame(Game game) {
        return customClient.get().uri("/game/"+ game.getUid() +"/players")
                .retrieve().body(HALWrapperPlayers.class).getPlayers()
                .stream().map(Player::getName).toList();
    }

    public void createGame(Game game) {
        customClient.post().uri("/games").body(game).retrieve().body(Game.class);
    }

    public List<Player> getPlayers() {
        return customClient.get().uri("/player").retrieve().body(HALWrapperPlayers.class).getPlayers();
    }

    public void connectPlayerToGame(Game game, long playerUID) {
        String body = "http://localhost:8080/game/" + game.getUid();
        customClient.put().uri("/player/"+ playerUID +"/game").
                header("Content-Type", "text/uri-list").
                body(body).retrieve().toEntity(Player.class);
    }

    public List<User> getUsers() {
        return customClient.get().uri("/user").retrieve().body(HALWrapperUsers.class).getUsers();
    }

    public List<User> searchUserByName(String name) {
        return customClient.get().uri("/users/searchusers?name="+name).retrieve().body(new ParameterizedTypeReference<>() {});
    }

    public void createUser(User user) {
        customClient.post().uri("/users").body(user).retrieve().body(User.class);
    }

    public void joinGame(Game game, String signedInUser) {
        try {
            if (game.getMaxPlayers() > getPlayerPerGame(game).size() && !userInGame(game, signedInUser)) {
                Game newGame = new Game();
                newGame.setUid(game.getUid());

                User user = new User();
                user.setUid(searchUserByName(signedInUser).get(0).getUid());

                Player player = new Player();
                player.setName(user.getName());
                player.setUser(user);
                player.setGame(newGame);

                customClient.post().
                        uri("/players").
                        body(player).
                        retrieve().
                        body(Player.class);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean userInGame(Game game, String signedInUser) {
        for (String playerName : getPlayerPerGame(game)) {
            if (playerName.equals(signedInUser)) {
                return true;
            }
        }
        return false;
    }

}
