package org.example.admin;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.Entities.User;
import org.example.TCP.CurrentSession;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;

public class MyProfileAdmin {

    @FXML
    private Label surnameLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label birthdayLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneNumberLabel;

    @FXML
    private Button myProfileButton;
    @FXML
    private Button allStudentsButton;
    @FXML
    private Button logOutButton;
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
    public void initialize() {
        loadAdminProfileInfo();
    }

    public void loadAdminProfileInfo() {
        int currentUserId = CurrentSession.getInstance().getCurrentProfileId();

        if (currentUserId == 0) {
            System.out.println("Ошибка: ID пользователя равен 0");
            return;
        }

        Request request = new Request();
        request.setRequestType(RequestType.GET_MY_PROFILE_ADMIN);
        request.setMessage(String.valueOf(currentUserId));

        new Thread(() -> {
            try {
                ClientSocket.getInstance().getWriter().println(new Gson().toJson(request));
                ClientSocket.getInstance().getWriter().flush();

                String responseLine = ClientSocket.getReader().readLine();
                Response response = new Gson().fromJson(responseLine, Response.class);

                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        User user = new Gson().fromJson(response.getMessage(), User.class);
                        if (user != null) {
                            displayAdminInfo(user);
                        } else {
                            System.out.println("Данные профиля не соответствуют роли бухгалтера.");
                        }
                    } else {
                        System.out.println("Ошибка загрузки профиля: " + response.getMessage());
                    }
                });
            } catch (IOException e) {
                System.out.println("Ошибка при загрузке данных профиля: " + e.getMessage());
            }
        }).start();
    }

    private void displayAdminInfo(User user) {
        if (user == null) {
            System.out.println("Данные администратора отсутствуют.");
            return;
        }

        surnameLabel.setText(user.getSurname());
        nameLabel.setText(user.getName());
        idLabel.setText(String.valueOf(user.getUser_id()));
        birthdayLabel.setText(user.getBirthday().toString());
        emailLabel.setText(user.getEmail());
        phoneNumberLabel.setText(user.getPhoneNumber());
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view/admin-menu.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}
