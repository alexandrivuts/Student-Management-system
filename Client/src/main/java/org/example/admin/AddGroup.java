package org.example.admin;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Entities.Group;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AddGroup extends Application {

    @FXML
    private TextField groupNumberField;
    @FXML
    private ComboBox<String> courseComboBox; // ComboBox для курса
    @FXML
    private ComboBox<String> facultyComboBox; // ComboBox для факультета
    @FXML
    private TextField specializationField;
    @FXML
    private Button logOutButton;
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

    private final Gson gson = new Gson();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/admin-view/add-new-group.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 915, 480);
        stage.setTitle("Добавление группы");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        // Инициализация ComboBox для факультетов
        List<String> faculties = Arrays.asList("ФКП", "ФИТиУ", "ФКСиС","ИЭФ","ВФ","ФИБ","ФРЭ");
        facultyComboBox.getItems().addAll(faculties);

        // Инициализация ComboBox для курсов
        List<String> courses = Arrays.asList("1", "2", "3", "4");
        courseComboBox.getItems().addAll(courses);
    }

    @FXML
    public void addGroup(ActionEvent actionEvent) {
        String groupNumber = groupNumberField.getText().trim();
        String course = courseComboBox.getValue(); // Получаем выбранный курс
        String faculty = facultyComboBox.getValue(); // Получаем выбранный факультет
        String specialization = specializationField.getText(); // Получаем выбранную специализацию

        // Проверка на пустые поля
        if (groupNumber.isEmpty() || course == null || faculty == null || specialization == null) {
            showError("Ошибка", "Пожалуйста, заполните все поля.");
            return;
        }

        // Проверка на правильность ввода номера группы (должен быть из 6 цифр)
        if (!groupNumber.matches("\\d{6}")) {
            showError("Ошибка", "Номер группы должен содержать ровно 6 цифр.");
            return;
        }

        // Создание объекта Group
        Group group = new Group();
        group.setGroupNumber(Integer.parseInt(groupNumber));
        group.setCourse(Integer.parseInt(course));
        group.setFaculty(faculty);
        group.setSpecialization(specialization);

        // Формирование запроса
        Request request = new Request();
        request.setRequestType(RequestType.ADD_GROUP);
        request.setMessage(gson.toJson(group));

        ClientSocket.getInstance().getWriter().println(gson.toJson(request));
        ClientSocket.getInstance().getWriter().flush();

        try {
            // Чтение ответа от сервера
            String responseLine = ClientSocket.getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                showSuccess("Успех", "Группа успешно добавлена.");
                clearFields();
            } else {
                showError("Ошибка", "Ошибка при добавлении группы: " + response.getMessage());
            }
        } catch (IOException e) {
            showError("Ошибка", "Ошибка при получении ответа от сервера: " + e.getMessage());
        }
    }

    private void clearFields() {
        groupNumberField.clear();
        courseComboBox.getSelectionModel().clearSelection();
        facultyComboBox.getSelectionModel().clearSelection();
        specializationField.clear();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view/admin-menu.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}
