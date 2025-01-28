package org.example.admin;

import com.google.gson.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Entities.Group;
import org.example.Entities.Student;
import org.example.Entities.User;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;
import java.util.regex.Pattern;

public class AddNewStudent extends Application {

    @FXML private TextField surnameField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> groupComboBox;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private DatePicker birthdayPicker;
    @FXML private Button addButton;
    @FXML private Button cancelButton;

    private final Gson gson = new Gson();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/add-new-student.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Добавление студента");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        loadGroups();
    }

    private void loadGroups() {
        Request request = new Request();
        request.setRequestType(RequestType.GET_GROUPS);

        ClientSocket.getInstance().getWriter().println(gson.toJson(request));
        ClientSocket.getInstance().getWriter().flush();

        try {
            String responseLine = ClientSocket.getReader().readLine();

            JsonObject responseObject = JsonParser.parseString(responseLine).getAsJsonObject();

            String message = responseObject.get("message").getAsString();

            JsonArray jsonArray = JsonParser.parseString(message).getAsJsonArray();

            Group[] groups = new Group[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject groupObject = jsonArray.get(i).getAsJsonObject();
                groups[i] = gson.fromJson(groupObject, Group.class);
            }

            groupComboBox.getItems().clear();

            for (Group group : groups) {
                groupComboBox.getItems().add(String.valueOf(group.getGroupNumber()));
            }

        } catch (IOException e) {
            showError("Ошибка", "Ошибка при загрузке групп: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            showError("Ошибка", "Ошибка синтаксиса JSON: " + e.getMessage());
        }
    }


    @FXML
    public void onAddButtonClicked(ActionEvent event) {
        String surname = surnameField.getText().trim();
        String name = nameField.getText().trim();
        String group = groupComboBox.getValue();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String birthday = birthdayPicker.getValue() != null ? birthdayPicker.getValue().toString() : "";

        if (surname.isEmpty() || name.isEmpty() || group == null ||
                email.isEmpty() || phone.isEmpty() || username.isEmpty() ||
                password.isEmpty() || birthday.isEmpty()) {
            showError("Ошибка", "Пожалуйста, заполните все поля.");
            return;
        }

        // Проверка email
        if (!isValidEmail(email)) {
            showError("Ошибка", "Некорректный email.");
            return;
        }

        // Проверка номера телефона
        if (!isValidPhoneNumber(phone)) {
            showError("Ошибка", "Некорректный номер телефона.");
            return;
        }

        // Создание объекта пользователя
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setBirthday(birthday);

        // Создание объекта студента
        Student student = new Student();
        student.setUser(user);
        student.setGroupNumber(Integer.parseInt(group));

        // Отправка запроса на сервер для добавления студента
        Request request = new Request();
        request.setRequestType(RequestType.ADD_STUDENT);
        request.setMessage(gson.toJson(student));

        ClientSocket.getInstance().getWriter().println(gson.toJson(request));
        ClientSocket.getInstance().getWriter().flush();

        try {
            String responseLine = ClientSocket.getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                showSuccess("Успех", "Студент успешно добавлен.");
                Stage stage = (Stage) addButton.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view/all-studentsAdmin.fxml"));
                Parent root = loader.load();
                Scene newScene = new Scene(root);
                stage.setScene(newScene);
            } else {
                showError("Ошибка", "Ошибка при добавлении студента: " + response.getMessage());
            }
        } catch (IOException e) {
            showError("Ошибка", "Ошибка при получении ответа от сервера: " + e.getMessage());
        }
    }

    @FXML
    public void onCancelButtonClicked(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view/admin-menu.fxml"));

        try {
            Parent root = loader.load();
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace(); // Логируем ошибку
            showError("Ошибка", "Не удалось загрузить FXML файл: " + e.getMessage()); // Показываем ошибку пользователю
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPhoneNumber(String phone) {
        String phoneRegex = "^\\+?[0-9]{7,15}$";
        return Pattern.matches(phoneRegex, phone);
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

}
