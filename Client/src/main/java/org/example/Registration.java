package org.example;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Entities.User;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;
import org.example.Utility.GsonConfig;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends Application {
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private TextField textfieldUsername;
    @FXML private PasswordField passwordfieldPassword;
    @FXML private TextField textfieldName;
    @FXML private TextField textfieldLastName;
    @FXML private TextField textfieldPhonenumber;
    @FXML private TextField textfieldEmail;
    @FXML private DatePicker datepickerBirthday;
    @FXML private Label errorLabel;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Registration.class.getResource("/register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 915, 480);
        stage.setTitle("Регистрация");
        stage.setScene(scene);
        stage.show();
    }

    public void loginButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void registerButton_Pressed(ActionEvent actionEvent) throws IOException {
        // Reset error label
        errorLabel.setVisible(false);

        // Perform validation checks
        if (!isValidInput()) {
            return;
        }

        User user = new User();
        Gson gson = GsonConfig.createGson();
        user.setName(textfieldName.getText());
        user.setSurname(textfieldLastName.getText());
        user.setUsername(textfieldUsername.getText());
        user.setPassword(passwordfieldPassword.getText());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if (datepickerBirthday.getValue() == null) {
            showError("Дата не выбрана! Пожалуйста, выберите дату.");
            return;
        }
        user.setBirthday(datepickerBirthday.getValue().format(formatter));
        user.setEmail(textfieldEmail.getText());
        user.setPhoneNumber(textfieldPhonenumber.getText());

        Request request = new Request();
        request.setRequestType(RequestType.REGISTER);
        request.setMessage(new Gson().toJson(user));

        ClientSocket.getInstance().getWriter().println(new Gson().toJson(request));
        ClientSocket.getInstance().getWriter().flush();
        try {
            String responseLine = ClientSocket.getReader().readLine();
            Response response = new Gson().fromJson(responseLine, Response.class);
            System.out.println(response.getMessage());

            if (response.getSuccess()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ответ от сервера");
                alert.setHeaderText(null);
                alert.setContentText("Регистрация выполнена успешно");
                alert.showAndWait();

                Stage stage = (Stage) registerButton.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/student-view/user-menu.fxml"));
                Scene newScene = new Scene(root);
                stage.setScene(newScene);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ответ от сервера");
                alert.setHeaderText(null);
                alert.setContentText("Ошибка: " + response.getMessage());
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка при получении ответа от сервера: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private boolean isValidInput() {
        // Check for empty fields
        if (textfieldName.getText().isEmpty() || textfieldLastName.getText().isEmpty() ||
                textfieldUsername.getText().isEmpty() || passwordfieldPassword.getText().isEmpty() ||
                textfieldEmail.getText().isEmpty() || textfieldPhonenumber.getText().isEmpty() ||
                datepickerBirthday.getValue() == null) {
            showError("Все поля обязательны для заполнения.");
            return false;
        }

        // Validate email
        if (!isValidEmail(textfieldEmail.getText())) {
            showError("Неверный формат email.");
            return false;
        }

        // Validate phone number (basic check for length and digits)
        if (!isValidPhoneNumber(textfieldPhonenumber.getText())) {
            showError("Неверный формат номера телефона.");
            return false;
        }

        // Validate password (e.g., at least 6 characters)
        if (passwordfieldPassword.getText().length() < 6) {
            showError("Пароль должен содержать минимум 6 символов.");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Basic check: only digits, length should be 10-15
        return phoneNumber.matches("\\d{10,15}");
    }

    public static void main(String[] args) {
        launch();
    }
}
