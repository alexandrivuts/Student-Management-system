package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.Utility.ClientSocket;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientSocket.getInstance().getSocket();
        Parent root = FXMLLoader.load(getClass().getResource("/login-view.fxml"));
        primaryStage.setTitle("Авторизация");
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}