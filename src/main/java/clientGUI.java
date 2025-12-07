package com.example.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class clientGUI extends Application {

    @Override
    public void start(Stage stage) {
        clientController controller = new clientController();
        Scene scene = new Scene(controller.noteUI(), 600, 400);

        stage.setScene(scene);
        stage.setTitle("notes client");
        stage.show();

        controller.connect();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
