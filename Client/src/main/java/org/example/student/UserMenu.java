package org.example.student;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class UserMenu extends Application {

    @FXML private Button myProfileButton;
    @FXML private Button mySessionButton;
    @FXML private Button myScholarshipButton;
    @FXML private Button allStudentsButton;
    @FXML private Button logOutButton;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserMenu.class.getResource("/student-view/user-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 915, 480);
        stage.setTitle("Меню пользователя");
        stage.setScene(scene);
        stage.show();
    }


    public void allStudentsButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) allStudentsButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-view/all-students.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
    public void myProfileButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) myProfileButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-view/MyProfileStudent.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
    public void mySessionButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) mySessionButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-view/my-session.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
    public void myScholarshipButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) myScholarshipButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-view/my-scholarship.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
    public void logOutButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) logOutButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}

