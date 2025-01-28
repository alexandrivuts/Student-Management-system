package org.example;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.Entities.Role;
import org.example.Entities.User;
import org.example.TCP.CurrentSession;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;
import org.example.Utility.GsonConfig;

import java.io.IOException;

public class Login extends Application {
    @FXML private TextField textfieldUsername;
    @FXML private PasswordField passwordfieldPassword;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientSocket.getInstance().getSocket();
        Parent root = FXMLLoader.load(getClass().getResource("/login-view.fxml"));
        primaryStage.setTitle("Интегрированная информационная система");
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.show();
    }

    public void loginButton_Pressed(ActionEvent actionEvent) {
        String login = textfieldUsername.getText();
        String password = passwordfieldPassword.getText();
        Gson gson = GsonConfig.createGson();

        // Формируем запрос
        Request loginRequest = new Request();
        loginRequest.setRequestType(RequestType.LOGIN);
        User user = new User();
        user.setUsername(login);
        user.setPassword(password);
        loginRequest.setMessage(new Gson().toJson(user));

        ClientSocket.getInstance().getWriter().println(new Gson().toJson(loginRequest));
        ClientSocket.getInstance().getWriter().flush();

        try {
            // Читаем ответ от сервера
            String responseLine = ClientSocket.getReader().readLine();
            Response response = gson.fromJson(responseLine, Response.class);
            System.out.println(response.getMessage());

            if (response.getSuccess()) {
                System.out.println("JSON для десериализации: " + response.getMessage());
                User authenticatedUser = gson.fromJson(response.getMessage(), User.class);
                Role role = authenticatedUser.getRole();

                System.out.println("Десериализованный юзер: " + user);
                System.out.println("ID пользователя: " + authenticatedUser.getUser_id());
                //System.out.println(authenticatedUser.getUser_id());
                System.out.println("Сохранённый currentProfileId до установки: " + CurrentSession.getInstance().getCurrentProfileId());
                CurrentSession.getInstance().setCurrentProfileId(authenticatedUser.getUser_id());
                System.out.println("Сохранённый currentProfileId: " + CurrentSession.getInstance().getCurrentProfileId());

                // Уведомление об успешном входе
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ответ от сервера");
                alert.setHeaderText(null);
                alert.setContentText("Вход выполнен успешно");
                alert.showAndWait();

                // Определяем роль пользователя и загружаем соответствующее представление
                String viewPath;
                switch (role.getRole_id()) {
                    case 1: // User
                        viewPath = "/student-view/user-menu.fxml";
                        break;
                    case 2: // Admin
                        viewPath = "/admin-view/admin-menu.fxml";
                        break;
                    case 3: // Accountant
                        viewPath = "/accountant-view/accountant-menu.fxml";
                        break;
                    default:
                        // Если роль неизвестна, показать ошибку
                        Alert roleAlert = new Alert(Alert.AlertType.ERROR);
                        roleAlert.setTitle("Ошибка");
                        roleAlert.setHeaderText(null);
                        roleAlert.setContentText("Неизвестная роль пользователя.");
                        roleAlert.showAndWait();
                        return;
                }

                // Загрузка нового окна
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource(viewPath));
                Scene newScene = new Scene(root);
                stage.setScene(newScene);

            } else {
                // Уведомление об ошибке
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ответ от сервера");
                alert.setHeaderText(null);
                alert.setContentText("Ошибка: " + response.getMessage());
                alert.showAndWait();
            }
        } catch (IOException e) {
            // Уведомление о проблемах с соединением
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка при получении ответа от сервера: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void registerButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/register-view.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}
