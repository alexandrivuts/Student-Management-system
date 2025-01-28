package org.example.student;

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
import org.example.Entities.Group;
import org.example.Entities.Student;
import org.example.Entities.User;
import org.example.TCP.CurrentSession;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;

public class MyProfileStudent {

    @FXML
    private Label surnameLabel;
    @FXML
    private Label nameLabel;

    @FXML
    private Label idLabel;
    @FXML
    private Label courseLabel;
    @FXML
    private Label groupLabel;
    @FXML
    private Label facultyLabel;
    @FXML
    private Label specialtyLabel;
    @FXML
    private Label averageGradeLabel;

    @FXML private Button myProfileButton;
    @FXML private Button mySessionButton;
    @FXML private Button myScholarshipButton;
    @FXML private Button allStudentsButton;
    @FXML private Button logOutButton;

    @FXML
    public void initialize() {
        loadProfileInfo(); // Загружаем данные профиля при инициализации
    }


    public void loadProfileInfo() {
        int currentProfileId = CurrentSession.getInstance().getCurrentProfileId();

        System.out.println("Текущий профиль ID перед отправкой запроса: " + currentProfileId);

        if (currentProfileId == 0) {
            System.out.println("Ошибка: текущий ID профиля равен 0");
            return;
        }

        Request request = new Request();
        request.setRequestType(RequestType.GET_MY_PROFILE);
        request.setMessage(String.valueOf(currentProfileId));

        new Thread(() -> {
            try {
                ClientSocket.getInstance().getWriter().println(new Gson().toJson(request));
                ClientSocket.getInstance().getWriter().flush();
                System.out.println("Запрос данных профиля отправлен");

                String responseLine = ClientSocket.getReader().readLine();
                Response response = new Gson().fromJson(responseLine, Response.class);

                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        String[] responseData = response.getMessage().split("\\|");

                        if (responseData.length == 3) {
                            User user = new Gson().fromJson(responseData[0], User.class);
                            Student student = new Gson().fromJson(responseData[1], Student.class);
                            Group group = new Gson().fromJson(responseData[2], Group.class);

                            showProfileInfo(user, student, group);
                        } else {
                            System.out.println("Не удалось загрузить все данные профиля.");
                        }
                    } else {
                        System.out.println("Не удалось загрузить информацию о профиле: " + response.getMessage());
                    }
                });
            } catch (IOException e) {
                System.out.println("Ошибка при загрузке данных профиля: " + e.getMessage());
            }
        }).start();
    }

    private void showProfileInfo(User user, Student student, Group group) {
        if (user == null || student == null || group == null) {
            System.out.println("Данные отсутствуют.");
            return;
        }
        surnameLabel.setText(user.getSurname());
        nameLabel.setText(user.getName());
        idLabel.setText(String.valueOf(user.getUser_id()));
        courseLabel.setText(String.valueOf(group.getCourse()));
        groupLabel.setText(String.valueOf(group.getGroupNumber()));
        facultyLabel.setText(group.getFaculty());
        specialtyLabel.setText(group.getSpecialization());

        // Обработка null для среднего балла
        averageGradeLabel.setText(student.getAverageGrade() != null ?
                String.format("%.2f", student.getAverageGrade()) : "Не доступно");
    }



    public void myProfileButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) myProfileButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-view/MyProfileStudent.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void allStudentsButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) allStudentsButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-view/all-students.fxml"));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-view/user-menu.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}
