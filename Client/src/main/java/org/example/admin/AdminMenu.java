package org.example.admin;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.student.UserMenu;
import javafx.scene.control.Alert;
import java.io.IOException;

public class AdminMenu extends Application {

    @FXML
    private Button myProfileButton;
    @FXML
    private Button allStudentsButton;
    @FXML
    private Button addStudentButton;
    @FXML
    private Button deleteStudentButton;
    @FXML
    private Button addSessionButton;
    @FXML
    private Button addGroupButton;
    @FXML
    private Button deleteGroupButton;
    @FXML
    private Button logOutButton;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserMenu.class.getResource("/admin-view/admin-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 915, 480);
        stage.setTitle("Меню представителя деканата");
        stage.setScene(scene);
        stage.show();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void allStudentsButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) allStudentsButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view/all-studentsAdmin.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void addStudentButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) addStudentButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view/add-new-student.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void deleteStudentButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) deleteStudentButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view/delete-student.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void addSessionButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) addSessionButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view/add-session.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void addGroupButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) addGroupButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view/add-new-group.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void deleteGroupButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) deleteGroupButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view/delete-group.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void myProfileButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) myProfileButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view/my-profile-admin.fxml"));
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