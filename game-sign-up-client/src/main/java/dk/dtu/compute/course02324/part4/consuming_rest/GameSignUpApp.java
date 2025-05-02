package dk.dtu.compute.course02324.part4.consuming_rest;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.OnlineController;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import dk.dtu.compute.course02324.part4.consuming_rest.view.GameSignUpGUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;


public class GameSignUpApp extends Application {

    private Stage stage;

    private Pane root;

    private GameSignUpGUI GUI = null;

    private final OnlineController onlineController = new OnlineController();


    @Override
    public void start(Stage stage) throws Exception {
        root = new Pane();
        GUI = new GameSignUpGUI(onlineController);
        root.getChildren().add(GUI);

        VBox box = new VBox(root);
        Scene mainScene = new Scene(box);

        this.stage = stage;
        this.stage.setScene(mainScene);
        this.stage.setTitle("Game Sign Up");
        this.stage.setResizable(false);
        this.stage.sizeToScene();
        this.stage.show();
    }


    /**
     * The main method used to start the JavaFX application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
