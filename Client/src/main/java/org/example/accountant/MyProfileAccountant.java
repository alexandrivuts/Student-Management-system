package org.example.accountant;

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

public class MyProfileAccountant {

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

    @FXML private Button myProfileButton;
    @FXML private Button allStudentsButton;
    @FXML private Button tableScholarshipButton;
    @FXML private Button changeScholarshipButton;
    @FXML private Button logOutButton;

    // Инициализация данных при загрузке экрана
    @FXML
    public void initialize() {
        loadAccountantProfileInfo(); // Загружаем данные профиля при инициализации
    }

    // Метод для загрузки данных профиля бухгалтера
    public void loadAccountantProfileInfo() {
        int currentUserId = CurrentSession.getInstance().getCurrentProfileId();

        if (currentUserId == 0) {
            System.out.println("Ошибка: ID пользователя равен 0");
            return;
        }

        // Формируем запрос
        Request request = new Request();
        request.setRequestType(RequestType.GET_MY_PROFILE_ACCOUNTANT); // Тип запроса для получения данных пользователя
        request.setMessage(String.valueOf(currentUserId)); // Передаем ID пользователя

        new Thread(() -> {
            try {
                // Отправляем запрос
                ClientSocket.getInstance().getWriter().println(new Gson().toJson(request));
                ClientSocket.getInstance().getWriter().flush();

                // Получаем ответ от сервера
                String responseLine = ClientSocket.getReader().readLine();
                Response response = new Gson().fromJson(responseLine, Response.class);

                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        User user = new Gson().fromJson(response.getMessage(), User.class);
                        if (user != null) { // Проверяем роль пользователя
                            displayAccountantInfo(user);
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

    // Метод для отображения данных бухгалтера
    private void displayAccountantInfo(User user) {
        if (user == null) {
            System.out.println("Данные пользователя отсутствуют.");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/accountant-view/accountant-menu.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}
