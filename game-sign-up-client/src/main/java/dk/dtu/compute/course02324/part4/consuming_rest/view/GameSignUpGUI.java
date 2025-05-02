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

    private final OnlineController onlineController;

    private User user = null;


    public GameSignUpGUI(OnlineController onlineController) {
        this.onlineController = onlineController;

        // scrollPane widget settings
        ScrollPane scrollPane = new ScrollPane(window);
        scrollPane.setMinWidth(500);
        scrollPane.setMinHeight(500);
        scrollPane.setMaxWidth(500);
        scrollPane.setMaxHeight(500);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // userOverviewBox widget settings
        userOverviewBox.setSpacing(5.0);
        userOverviewBox.setPadding(new Insets(10));
        userOverviewBox.setMaxWidth(200);
        userOverviewBox.setMaxHeight(5000);

        // gameOverviewBox widget settings
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


    /**
     * Adds a game from the database to the gui
     * @param game the game to display
     * @param user the user connected
     */
    private void addGameToView(Game game, User user) {
        // TextArea widget settings
        TextArea infoField = new TextArea();
        infoField.setWrapText(true);
        infoField.setEditable(false);
        infoField.setMaxWidth(300);
        infoField.setMaxHeight(80);
        infoField.setText(game.toString());

        // Join button logic
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

        // leave button logic
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

        // start button logic
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


        // delete button logic
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(
                e -> {
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


    /**
     * Prompts the user for game name, minPlayers and maxPlayers
     * and makes a post to create a game with the correct values
     * @param user the user to be connected as owner
     */
    private void createNewGame(User user) {
        if (user != null) {
            // Game name dialog
            TextInputDialog gameNameDialog = new TextInputDialog();
            gameNameDialog.setTitle("Game name");
            gameNameDialog.setHeaderText("Enter a game name");
            Optional<String> gameName = gameNameDialog.showAndWait();

            // Min players dialog
            ChoiceDialog<Integer> minDialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
            minDialog.setTitle("Min players");
            minDialog.setHeaderText("Select Minimum number of players");
            Optional<Integer> minPlayers = minDialog.showAndWait();

            // Max players dialog
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

    /**
     * Adds the users in the database to the gui
     */
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

    /**
     * Updates the widgets in the gui, to check if there is new info from the backend
     * @param user the user logged in
     */
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

        updateMainButtons();

        window.getChildren().addAll(userTextLabel, mainButtons, overviewBox);
    }

    /**
     * Updates the main buttons visually (sign in, sign out, sign up, refresh, add new game)
     */
    private void updateMainButtons() {
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
