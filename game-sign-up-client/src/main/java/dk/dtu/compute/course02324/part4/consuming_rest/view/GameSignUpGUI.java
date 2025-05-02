package dk.dtu.compute.course02324.part4.consuming_rest.view;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.OnlineController;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.GameState;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.*;


public class GameSignUpGUI extends Pane {
    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);

    private final VBox window = new VBox();

    private final HBox mainButtons = new HBox();

    private final HBox overviewBox = new HBox();

    private final VBox userOverviewBox = new VBox();

    private final VBox gameOverviewBox = new VBox();

    private final ScrollPane scrollPane = new ScrollPane(window);

    private final OnlineController onlineController;

    private User user = null;


    public GameSignUpGUI(OnlineController onlineController) {
        this.onlineController = onlineController;

        // Scroll pane settings
        scrollPane.setMinWidth(500);
        scrollPane.setMinHeight(500);
        scrollPane.setMaxWidth(500);
        scrollPane.setMaxHeight(500);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // User overview box settings
        userOverviewBox.setSpacing(5.0);
        userOverviewBox.setPadding(new Insets(10));
        userOverviewBox.setMaxWidth(200);
        userOverviewBox.setMaxHeight(5000);

        // Game overview box settings
        gameOverviewBox.setSpacing(5.0);
        gameOverviewBox.setPadding(new Insets(10));
        gameOverviewBox.setMinWidth(300);
        gameOverviewBox.setMaxWidth(300);
        gameOverviewBox.setMaxHeight(5000);

        mainButtons.setSpacing(20);

        overviewBox.setPadding(new Insets(10));
        overviewBox.getChildren().addAll(gameOverviewBox, userOverviewBox);

        window.setPadding(new Insets(10));
        window.setSpacing(10.0);

        this.getChildren().add(scrollPane);

        update(user);
    }


    private void addGameToView(Game game, User user) {
        TextArea infoField = new TextArea();
        infoField.setWrapText(true);
        infoField.setEditable(false);
        infoField.setMaxWidth(300);
        infoField.setMaxHeight(80);
        infoField.setText(game.toString());

        Button joinButton = new Button("Join");
        joinButton.setOnAction(
                e -> {
                    Game newGame = new Game();
                    newGame.setUid(game.getUid());

                    User newUser = new User();
                    newUser.setUid(user.getUid());

                    Player player = new Player();
                    player.setGame(newGame);
                    player.setName(newUser.getName());
                    player.setUser(newUser);

                    onlineController.addPlayer(player);

                    update(user);
                });
        joinButton.setDisable(true);
        if (user != null && game.getPlayers() != null) {
            if (game.getPlayers().size() < game.getMaxPlayers()) {
                joinButton.setDisable(false);
                for (Player player : game.getPlayers()) {
                    if (player.getName().equals(user.getName())) {
                        joinButton.setDisable(true);
                        break;
                    }
                }
            }
        }

        Button leaveButton = new Button("Leave");
        leaveButton.setOnAction(
                e -> {
                    List<Player> players = game.getPlayers();
                    if (players != null && user != null) {
                        for (Player element : players) {
                            if (element.getUser().getUid() == user.getUid()) {
                                onlineController.deletePlayer(element);
                                break;
                            }
                        }
                    }
                    update(user);
                });
        leaveButton.setDisable(true);
        if (user != null && game.getPlayers() != null) {
            for (Player player : game.getPlayers()) {
                if (player.getName().equals(user.getName())) {
                    if (!user.getName().equals(game.getOwner().getName())) {
                        leaveButton.setDisable(false);
                        break;
                    }

                }
            }
        }

        Button startButton = new Button("Start");
        startButton.setOnAction(
                e -> {
                    game.setState(GameState.ACTIVE);
                    onlineController.updateGame(game);
                    update(user);
                });
        startButton.setDisable(true);
        if (user != null && game.getPlayers() != null) {
            if (game.getPlayers().size() >= game.getMinPlayers()) {
                if (game.getOwner().getName().equals(user.getName())) {
                    startButton.setDisable(false);
                }
            }
        }


        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(
                e -> {
                    List<Player> players = game.getPlayers();
                    if (players != null && user != null) {
                        for (Player element : players) {
                            onlineController.deletePlayer(element);
                        }
                    }
                    onlineController.deleteGame(game);

                    update(user);
                });
        deleteButton.setDisable(true);
        if (user != null) {
            if (game.getOwner().getName().equals(user.getName())) {
                deleteButton.setDisable(false);
            }
        }


        HBox buttons = new HBox(joinButton, leaveButton, startButton, deleteButton);
        buttons.setSpacing(20.0);

        VBox box = new VBox(infoField, buttons);
        box.setSpacing(10.0);

        gameOverviewBox.getChildren().add(box);
    }


    private void createNewGame(User user) {
        if (user != null) {
            TextInputDialog gameNameDialog = new TextInputDialog();
            gameNameDialog.setTitle("Game name");
            gameNameDialog.setHeaderText("Enter a game name");
            Optional<String> gameName = gameNameDialog.showAndWait();

            ChoiceDialog<Integer> minDialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
            minDialog.setTitle("Min players");
            minDialog.setHeaderText("Select Minimum number of players");
            Optional<Integer> minPlayers = minDialog.showAndWait();

            ChoiceDialog<Integer> maxDialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(4), PLAYER_NUMBER_OPTIONS);
            maxDialog.setTitle("Max players");
            maxDialog.setHeaderText("Select maximum number of players");
            Optional<Integer> maxPlayers = maxDialog.showAndWait();

            if (maxPlayers.isPresent() && minPlayers.isPresent() && gameName.isPresent()) {
                Game newGame = new Game();
                newGame.setName(gameName.get());
                newGame.setMinPlayers(minPlayers.get());
                newGame.setMaxPlayers(maxPlayers.get());
                newGame.setState(GameState.SIGNUP);
                newGame.setOwner(user);


                newGame = onlineController.addGame(newGame);
                if (newGame != null) {
                    addGameToView(newGame, user);
                }
            }
            update(user);
        }
    }

    private void addUserView() {
        userOverviewBox.getChildren().clear();

        Label userOverviewBoxLabel = new Label("Users: ");
        userOverviewBox.getChildren().add(userOverviewBoxLabel);

        List<User> users = onlineController.getUsers();

        if (users != null) {
            for (User user: users) {
                TextArea infoField = new TextArea();
                infoField.setWrapText(true);
                infoField.setEditable(false);
                infoField.setText(user.getName());
                infoField.setMaxWidth(100);
                infoField.setMaxHeight(30);

                userOverviewBox.getChildren().add(infoField);
            }
        }
    }

    private void update(User user) {
        gameOverviewBox.getChildren().clear();
        userOverviewBox.getChildren().clear();
        mainButtons.getChildren().clear();
        window.getChildren().clear();


        Label userTextLabel = new Label("Please log in!");
        if (user != null) {
            userTextLabel = new Label("Logged in as: " + user.getName());
        }
        userTextLabel.setPadding(new Insets(0, 0, 10, 0));

        Label gameOverviewBoxLabel = new Label("Games: ");
        gameOverviewBox.getChildren().add(gameOverviewBoxLabel);

        List<Game> games = onlineController.getOpenGames();
        if (games != null) {
            for (Game game : games) {
                addGameToView(game, user);
            }
        }

        addUserView();

        updateMainbuttons();

        window.getChildren().addAll(userTextLabel, mainButtons, overviewBox);
    }

    private void updateMainbuttons() {
        Button signInButton = new Button("Sign in");
        signInButton.setOnAction(
                e -> {
                    TextInputDialog signInDialog = new TextInputDialog();
                    signInDialog.setTitle("Sign in");
                    signInDialog.setHeaderText("Enter user name");
                    Optional<String> userName = signInDialog.showAndWait();
                    if (userName.isPresent()) {
                        User newUser = onlineController.signIn(userName.get());
                        if (newUser != null) {
                            this.user = newUser;
                        }
                    }
                    update(user);
                }
        );

        Button signOutButton = new Button("Sign out");
        signOutButton.setOnAction(
                e -> {
                    this.user = null;
                    update(null);
                }
        );

        Button signUpButton = new Button("Sign up");
        signUpButton.setOnAction(
                e -> {
                    TextInputDialog signUpDialog = new TextInputDialog();
                    signUpDialog.setTitle("Sign up as a user");
                    signUpDialog.setHeaderText("Enter user name");
                    Optional<String> userName = signUpDialog.showAndWait();
                    if (userName.isPresent()) {
                        User newUser = onlineController.signUp(userName.get());
                        if (newUser != null) {
                            this.user = newUser;
                        }
                    }
                    update(user);
                }
        );

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> update(user));

        Button createGameButton = new Button("Add new game");
        createGameButton.setOnAction(e -> {
            createNewGame(user);
        });
        createGameButton.setDisable(true);
        if (user != null) {
            createGameButton.setDisable(false);
        }

        mainButtons.getChildren().addAll(signInButton, signOutButton, signUpButton, refreshButton, createGameButton);
    }
}
