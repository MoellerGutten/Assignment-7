package dk.dtu.compute.course02324.part4.consuming_rest;

import dk.dtu.compute.course02324.part4.consuming_rest.controller.OnlineController;
import dk.dtu.compute.course02324.part4.consuming_rest.view.GameSignUpGUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class GameSignUpApp extends Application {


    @Override
    public void start(Stage stage) {
        OnlineController onlineController = new OnlineController();
        GameSignUpGUI GUI = new GameSignUpGUI(onlineController);

        Pane root = new Pane();
        root.getChildren().add(GUI);

        VBox box = new VBox(root);
        Scene mainScene = new Scene(box);

        stage.setScene(mainScene);
        stage.setTitle("Game Sign Up");
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
