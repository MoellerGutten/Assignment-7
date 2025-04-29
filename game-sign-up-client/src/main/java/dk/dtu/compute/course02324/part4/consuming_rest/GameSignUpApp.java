package dk.dtu.compute.course02324.part4.consuming_rest;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.GameState;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperGames;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperPlayers;
import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperUsers;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
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
    private String signedInUser;
    private Label signedInUserLabel;
    private OnlineController onlineController;
    /**
     * The pane on which the actual interaction with the
     * list of persons will be added.
     */
    private Pane root;
    private GridPane gamesPane;



    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.onlineController = new OnlineController();

        root = new Pane();

        Button addNewGameButton = new Button("Add Game");

        addNewGameButton.setOnAction(e -> {
            openAddGameMenu();
        });

        Button signInButton = new Button("Sign in");
        signedInUserLabel = new Label("No user selected currently");
        signInButton.setOnAction(e -> {
            openSignInMenu();
        });

        Button signOutButton = new Button("Sign out");
        signOutButton.setOnAction(e -> {
            signedInUser = null;
            update();
        });

        Button signUpButton = new Button("Sign up");
        signUpButton.setOnAction(e -> openSignUpUserMenu());


        gamesPane = new GridPane();
        gamesPane.setPadding(new Insets(5));
        gamesPane.setHgap(5);
        gamesPane.setVgap(5);

        update();

        ScrollPane scrollPane = new ScrollPane(gamesPane);
        scrollPane.setMinWidth(400);
        scrollPane.setMaxWidth(500);
        scrollPane.setMinHeight(300);
        scrollPane.setMaxHeight(450);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox box = new VBox(root,signedInUserLabel,signInButton,signOutButton, signUpButton,addNewGameButton, scrollPane);
        Scene scene = new Scene(box);

        this.stage.setScene(scene);

        this.stage.setTitle("Game sign up");
        this.stage.setResizable(false);
        this.stage.sizeToScene();
        this.stage.show();
    }

    void update() {

        if (signedInUser == null) {
            signedInUserLabel.setText("No user selected currently");
        } else {
            signedInUserLabel.setText("Current user: " + signedInUser);
        }

        gamesPane.getChildren().clear();
        List<Game> games = onlineController.getGames();
        infoBoxes = new ArrayList<>();
        infoBoxes2 = new ArrayList<>();
        int i = 0;
        for (Game game : games) {
            String gameName = game.getName();
            int minPlayers = game.getMinPlayers();
            int maxPlayers = game.getMaxPlayers();
            String owner = "";
            if (game.getOwner() != null) {
                owner = game.getOwner().getName();
                System.out.println(owner);
            } else {
                owner = "No dumbass owner";
            }

            List<String> playersPerGame = onlineController.getPlayerPerGame(game);
            gameInfoBox = new VBox(root,
                    new Label("game: " + gameName),
                    new Label("Minimum players: " + minPlayers),
                    new Label("Maximum players: " + maxPlayers),
                    new Label(playersPerGame.toString()),
                    new Label("Owner: " + owner));

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
                    new Label(playersPerGame.toString()),
                    new Label(owner));
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

            Button joinButton = new Button("Join");
            Button leaveButton = new Button("Leave");
            Button startButton = new Button("Start");
            Button deleteButton = new Button("Delete");

            if (signedInUser == null) {
                joinButton.setDisable(true);
                leaveButton.setDisable(true);
                startButton.setDisable(true);
                deleteButton.setDisable(true);
            } else {
                joinButton.setDisable(false);
                leaveButton.setDisable(false);
                startButton.setDisable(false);
                deleteButton.setDisable(false);
            }

            joinButton.setOnAction(e -> {
                onlineController.joinGame(game, signedInUser);
                update();
            });


            HBox entry = new HBox(root, infoBoxes.get(i), joinButton, leaveButton, startButton, deleteButton);
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
        createStage.initModality(Modality.APPLICATION_MODAL);
        createStage.show();

        createGameButton.setOnAction(e -> {
            try {
                String name = gameNameField.getText();
                int minPlayers = Integer.parseInt(minimumPlayersField.getText());
                int maxPlayers = Integer.parseInt(maximumPlayersField.getText());
                Game game = new Game();
                game.setName(name);
                game.setState(GameState.SIGNUP);
                game.setMinPlayers(minPlayers);
                game.setMaxPlayers(maxPlayers);
                User owner = onlineController.searchUserByName(signedInUser).get(0);
                System.out.println(owner);
                game.setOwner(owner);
                onlineController.createGame(game);

                createStage.close();
                update();
            } catch (Exception exception) {
                // should be handled better
                System.out.println(exception.getMessage());
            }
        });
    }

    void openSignUpMenu(Game game, int i) {
        List<Player> players = onlineController.getPlayers();

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
        signUpStage.initModality(Modality.APPLICATION_MODAL);
        signUpStage.show();

        signUpInsideButton.setOnAction(ee -> {
            int playerUID = -1;
            for (Player player : players) {
                if (Objects.equals(player.getName(), playerField.getText())) {
                    playerUID = (int) player.getUid();
                }
            }
            if (playerUID != -1) {
                onlineController.connectPlayerToGame(game, playerUID);
                System.out.println("Trying to do patch");
            }
            update();
            signUpStage.close();
        });
    }

    void openSignUpUserMenu() {
        Stage stage = new Stage();

        Label text = new Label("Create a user for online RoboRally");
        TextField userName = new TextField();

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> stage.close());
        Button register = new Button("Sign Up");
        register.setOnAction(e -> {
            String name = userName.getText();
            if (name != null) {
                User newUser = new User();
                newUser.setName(name);
                onlineController.createUser(newUser);
            }

            List<User> users = onlineController.getUsers();
            for (User user : users) {
                if (Objects.equals(user.getName(), userName.getText())) {
                    signedInUser = user.getName();
                    System.out.println(signedInUser);
                }
            }

            stage.close();
            update();
        });

        HBox buttons = new HBox(cancel, register);
        VBox vbox = new VBox(text, userName, buttons);

        Scene scene = new Scene(vbox);
        stage.setTitle("Register user");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();

    }

    void openSignInMenu() {
        Stage stage = new Stage();

        Label text = new Label("Register as user for online RoboRally");
        TextField userName = new TextField();

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> stage.close());
        Button register = new Button("Sign in");
        register.setOnAction(e -> {
            String name = userName.getText();
            if (name != null) {
                stage.close();
                List<User> users = onlineController.searchUserByName(name);

                signedInUser = users.get(0).getName();
                System.out.println(signedInUser);
                update();
            }
        });

        HBox buttons = new HBox(cancel, register);
        VBox vbox = new VBox(text, userName, buttons);

        Scene scene = new Scene(vbox);
        stage.setTitle("Register user");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();

    }


    /**
     * The main method used to start the JavaFX application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
