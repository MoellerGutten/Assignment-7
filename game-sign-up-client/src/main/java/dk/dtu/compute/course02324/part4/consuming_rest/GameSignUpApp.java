package dk.dtu.compute.course02324.part4.consuming_rest;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperGames;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperPlayers;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple JavaFX application with a simple GUI for manually
 * maintaining a list of Persons.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameSignUpApp extends Application {

    /**
     * The stage of the GUI of this test application.
     */
    private Stage stage;
    private VBox gameInfoBox;
    private VBox gameInfoBox2;
    private List<VBox> infoBoxes;
    private List<VBox> infoBoxes2;
    /**
     * The pane on which the actual interaction with the
     * list of persons will be added.
     */
    private Pane root;
    private GridPane gamesPane;

    RestClient customClient = RestClient.builder().
            // requestFactory(new HttpComponentsClientHttpRequestFactory()).
                    baseUrl("http://localhost:8080").
            build();

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        root = new Pane();

        Button addNewGameButton = new Button("Add Game");

        addNewGameButton.setOnAction(eee -> {
            openAddGameMenu();
        });

        gamesPane = new GridPane();
        gamesPane.setPadding(new Insets(5));
        gamesPane.setHgap(5);
        gamesPane.setVgap(5);

        update();

        ScrollPane scrollPane = new ScrollPane(gamesPane);
        scrollPane.setMinWidth(300);
        scrollPane.setMaxWidth(300);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox box = new VBox(root, addNewGameButton, scrollPane);
        Scene scene = new Scene(box);

        this.stage.setScene(scene);

        this.stage.setTitle("Game sign up");
        this.stage.setResizable(false);
        this.stage.sizeToScene();
        this.stage.show();
    }

    void update() {
        gamesPane.getChildren().clear();
        List<Game> games = customClient.get().uri("/game").retrieve().body(HALWrapperGames.class).getGames();
        infoBoxes = new ArrayList<>();
        infoBoxes2 = new ArrayList<>();
        int i = 0;
        for (Game game : games) {
            String gameName = game.getName();
            int minPlayers = game.getMinPlayers();
            int maxPlayers = game.getMaxPlayers();
            // Long request for getting list of players for each game. This is converted to a list of names to display.
            List<String> playersPerGame = customClient.get().uri("/game/"+ game.getUid() +"/players")
                    .retrieve().body(HALWrapperPlayers.class).getPlayers()
                    .stream().map(Player::getName).toList();

            gameInfoBox = new VBox(root,
                    new Label("game: " + gameName),
                    new Label("Minimum players: " + minPlayers),
                    new Label("Maximum players: " + maxPlayers),
                    new Label(playersPerGame.toString()));
            gameInfoBox.setSpacing(3);
            gameInfoBox.setPadding(new Insets(4, 8, 4, 8));
            gameInfoBox.setStyle("""
                    -fx-border-color: black;
                    -fx-border-width: 2;
                    -fx-background-color: lightgrey;
                    -fx-border-radius: 4;""");

            gameInfoBox2 = new VBox(root,
                    new Label("game: " + gameName),
                    new Label("Minimum players: " + minPlayers),
                    new Label("Maximum players: " + maxPlayers),
                    new Label(playersPerGame.toString()));
            gameInfoBox2.setSpacing(3);
            gameInfoBox2.setPadding(new Insets(4, 8, 4, 8));
            gameInfoBox2.setStyle("""
                    -fx-border-color: black;
                    -fx-border-width: 2;
                    -fx-background-color: lightgrey;
                    -fx-border-radius: 4;""");

            infoBoxes.add(gameInfoBox);
            infoBoxes2.add(gameInfoBox2);

            Button signUpButton = new Button("Sign Up!");
            // Copy for lambda for some reason
            int finalI = i;
            signUpButton.setOnAction(e -> {
                openSignUpMenu(game, finalI);
            });

            HBox entry = new HBox(root, infoBoxes.get(i), signUpButton);
            entry.setSpacing(5.0);
            entry.setAlignment(Pos.CENTER);
            gamesPane.add(entry, 0, i);
            i++;
        }
    }

    void openAddGameMenu() {
        Label gameNameLabel = new Label("Enter a name for the game: ");
        Label minimumLabel = new Label("Minimum amount of players: ");
        Label maximumLabel = new Label("Maximum amount of players: ");

        TextField gameNameField = new TextField("Default");
        TextField maximumPlayersField = new TextField("2");
        TextField minimumPlayersField = new TextField("2");

        maximumPlayersField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        minimumPlayersField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

        Button createGameButton = new Button("Create game");
        VBox box = new VBox(root, gameNameLabel, gameNameField, minimumLabel, minimumPlayersField, maximumLabel, maximumPlayersField, createGameButton);
        Scene scene = new Scene(box, 200, 250);
        Stage createStage = new Stage();
        createStage.setScene(scene);
        createStage.setTitle("Creating game");
        createStage.setResizable(false);
        createStage.sizeToScene();
        createStage.show();

        createGameButton.setOnAction(eeee -> {
            try {
                String name = gameNameField.getText();
                int minPlayers = Integer.parseInt(minimumPlayersField.getText());
                int maxPlayers = Integer.parseInt(maximumPlayersField.getText());
                Game game = new Game();
                game.setName(name);
                game.setMinPlayers(minPlayers);
                game.setMaxPlayers(maxPlayers);
                customClient.post()
                        .uri("/game").accept(MediaType.APPLICATION_JSON)
                        .body(game).retrieve().toEntity(new ParameterizedTypeReference<Game>() {
                        });

                createStage.close();
                update();
            } catch (Exception exception) {
                // should be handled better
                System.out.println("Invalid input");
            }
        });
    }

    void openSignUpMenu(Game game, int i) {
        List<Player> players = customClient.get().uri("/player").retrieve().body(HALWrapperPlayers.class).getPlayers();

        Label userLabel = new Label("User: ");
        Label playerLabel = new Label("Player: ");

        TextField userField = new TextField("");
        TextField playerField = new TextField("");

        HBox userHbox = new HBox(userLabel, userField);
        HBox playerHbox = new HBox(playerLabel, playerField);

        Button signUpInsideButton = new Button("Sign Up");

        VBox box = new VBox(root, infoBoxes2.get(i), userHbox, playerHbox, signUpInsideButton);

        Scene signUpScene = new Scene(box, 200, 250);
        Stage signUpStage = new Stage();
        signUpStage.setScene(signUpScene);
        signUpStage.setTitle("Signing up for game");
        signUpStage.setResizable(false);
        signUpStage.sizeToScene();
        signUpStage.show();

        signUpInsideButton.setOnAction(ee -> {
            String body = "http://localhost:8080/game/" + game.getUid();
            int playerUID = -1;
            for (Player player : players) {
                if (Objects.equals(player.getName(), playerField.getText())) {
                    playerUID = (int) player.getUid();
                }
            }
            if (playerUID != -1) {
                customClient.put().uri("/player/"+ playerUID +"/game").
                        header("Content-Type", "text/uri-list").
                        body(body).retrieve().toEntity(Player.class);
                System.out.println("Trying to do patch");
            }
            update();
            signUpStage.close();
        });
    }
    /**
     * The main method used to start the JavaFX application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
