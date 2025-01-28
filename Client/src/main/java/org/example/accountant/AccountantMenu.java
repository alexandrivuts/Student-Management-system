package org.example.accountant;

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

public class AccountantMenu extends Application {

    @FXML private Button myProfileButton;
    @FXML private Button allStudentsButton;
    @FXML private Button tableScholarshipButton;
    @FXML private Button changeScholarshipButton;
    @FXML private Button logOutButton;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserMenu.class.getResource("/accountant-view/accountant-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 915, 480);
        stage.setTitle("Меню бухгалтера");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/accountant-view/all-studentsAccountant.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void myProfileButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) myProfileButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/accountant-view/my-profile-accountant.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void tableScholarshipButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) tableScholarshipButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/accountant-view/scholarships-amount.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void changeScholarshipButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) changeScholarshipButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/accountant-view/change-scholarships.fxml"));
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

