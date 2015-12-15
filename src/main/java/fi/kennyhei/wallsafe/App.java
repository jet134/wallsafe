package fi.kennyhei.wallsafe;

import fi.kennyhei.wallsafe.controller.MainController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        WallSafeFactory factory = new WallSafeFactory();
        MainController mainController = factory.getMainController();

        Scene scene = new Scene(mainController.getView());
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setScene(scene);
        stage.setTitle("WallSafe");
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
