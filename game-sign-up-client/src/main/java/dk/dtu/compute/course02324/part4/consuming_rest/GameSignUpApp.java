package dk.dtu.compute.course02324.part4.consuming_rest;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.paint.Color;



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

    /**
     * The pane on which the actual interaction with the
     * list of persons will be added.
     */
    private Pane root;


    /**
     * The method starting the application, which sets up the GUI
     * elements of this application.
     *
     * @param stage the stage for this application (provided by JavaFX)
     * @throws Exception if something should go wrong (required by super class)
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        root = new Pane();

        TextArea gameArea = new TextArea();
        gameArea.setWrapText(true);
        gameArea.setText("");
        gameArea.setEditable(false);
        gameArea.setScrollTop(Double.MAX_VALUE);

        Button addNewGameButton = new Button("Add Game");
        Button addPlayerToGameButton = new Button("Sign up!");


        VBox box = new VBox(root, addNewGameButton, gameArea, addPlayerToGameButton);
        Scene scene = new Scene(box);

        this.stage.setScene(scene);

        this.stage.setTitle("Game sign up");
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
