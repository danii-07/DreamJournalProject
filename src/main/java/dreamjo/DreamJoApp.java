package dreamjo;

import javafx.application.Application; // this imports the base application class for javaFX.
import javafx.fxml.FXMLLoader; // here it imports the fxmlLoader class to load fxml files.
import javafx.scene.Parent; // here the Parent class, which is the base class for UI elements.
import javafx.scene.Scene; // here the Scene class, which holds the UI content.
import javafx.stage.Stage; // and here the Stage class, which represents the application window.

public class DreamJoApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dreamjo/DreamJournal.fxml")); // i create a fxml loader to load my fxml file.
            Parent root = loader.load(); // after loading i create the root UI element.

            Scene scene = new Scene(root, 800, 600); // setting the scene and correct dimensions. 

            primaryStage.setTitle("Dream Journal");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // launching the app (main method)
    }
}