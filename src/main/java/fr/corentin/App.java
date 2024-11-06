package fr.corentin;

import fr.corentin.controllers.MainController;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        MainController mainController = new MainController(stage);
        mainController.start();
    }

    public static void main(String[] args) {
        launch();
    }
}