package org.example.student;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.TCP.CurrentSession;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class MyScholarship {
    @FXML
    private Button myProfileButton;
    @FXML private Button mySessionButton;
    @FXML private Button myScholarshipButton;
    @FXML private Button allStudentsButton;
    @FXML private Button logOutButton;

    @FXML
    private Label scholarshipAmountLabel; // Для отображения суммы стипендии
    @FXML
    private Label averageGradeLabel; // Для отображения среднего балла
    @FXML
    private ImageView scholarshipImage; // Картинка для стипендии
    @FXML
    private ImageView noScholarshipImage; // Картинка для отсутствия стипендии

    @FXML
    public void initialize() {
        // Установка начальных изображений
        scholarshipImage.setImage(new Image(getClass().getResource("/images/money.jpeg").toString()));
        noScholarshipImage.setImage(new Image(getClass().getResource("/images/chill-boy.jpg").toString()));

        loadSessionInfo();
    }

    public void loadSessionInfo() {
        int currentProfileId = CurrentSession.getInstance().getCurrentProfileId();

        if (currentProfileId == 0) {
            System.out.println("Ошибка: текущий ID профиля равен 0");
            return;
        }

        Request request = new Request();
        request.setRequestType(RequestType.GET_MY_SCHOLARSHIP);
        request.setMessage(String.valueOf(currentProfileId));

        new Thread(() -> {
            try {
                ClientSocket.getInstance().getWriter().println(new Gson().toJson(request));
                ClientSocket.getInstance().getWriter().flush();
                String responseLine = ClientSocket.getReader().readLine();
                Response response = new Gson().fromJson(responseLine, Response.class);

                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        JsonObject jsonObject = new Gson().fromJson(response.getMessage(), JsonObject.class);

                        if (jsonObject.has("average_grade") && !jsonObject.get("average_grade").isJsonNull()) {
                            double averageGrade = jsonObject.get("average_grade").getAsDouble();
                            averageGradeLabel.setText("" + averageGrade);

                            if (jsonObject.has("scholarship_amount") && !jsonObject.get("scholarship_amount").isJsonNull() && averageGrade >= 5) {
                                int scholarshipAmount = jsonObject.get("scholarship_amount").getAsInt();
                                scholarshipAmountLabel.setText(scholarshipAmount + " рублей");

                                // Показать картинку для стипендии
                                scholarshipImage.setVisible(true);
                                noScholarshipImage.setVisible(false);
                            } else {
                                scholarshipAmountLabel.setText("Вы не получаете стипендии!");

                                // Показать картинку для отсутствия стипендии
                                scholarshipImage.setVisible(false);
                                noScholarshipImage.setVisible(true);
                            }
                        } else {
                            averageGradeLabel.setText("Средний балл: не доступен");
                            scholarshipAmountLabel.setText("Вы не получаете стипендии!");

                            scholarshipImage.setVisible(false);
                            noScholarshipImage.setVisible(true);
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


    // Метод для отправки среднего балла на сервер и получения стипендии
    private void getScholarshipAmountFromServer(double averageGrade) {
        Request request = new Request();
        request.setRequestType(RequestType.GET_SCHOLARSHIP_AMOUNT);
        request.setMessage(String.valueOf(averageGrade)); // Отправляем только средний балл

        new Thread(() -> {
            try {
                // Отправляем запрос на сервер
                ClientSocket.getInstance().getWriter().println(new Gson().toJson(request));
                ClientSocket.getInstance().getWriter().flush();

                // Получаем ответ от сервера
                String responseLine = ClientSocket.getReader().readLine();
                Response response = new Gson().fromJson(responseLine, Response.class);

                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        // Сервер возвращает сумму стипендии
                        int scholarshipAmount = Integer.parseInt(response.getMessage());

                        if (averageGrade >= 5) {
                            scholarshipAmountLabel.setText("Сумма стипендии: " + scholarshipAmount + " рублей");
                            scholarshipImage.setVisible(true); // Показать картинку стипендии
                            noScholarshipImage.setVisible(false); // Скрыть картинку отсутствия стипендии
                        } else {
                            scholarshipAmountLabel.setText("Вы не получаете стипендии!");
                            scholarshipImage.setVisible(false); // Скрыть картинку стипендии
                            noScholarshipImage.setVisible(true); // Показать картинку отсутствия стипендии
                        }
                    } else {
                        scholarshipAmountLabel.setText("Ошибка получения данных");
                        System.out.println("Не удалось получить сумму стипендии: " + response.getMessage());
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> scholarshipAmountLabel.setText("Ошибка сервера"));
                System.out.println("Ошибка при получении стипендии: " + e.getMessage());
            }
        }).start();
    }


    // Обработчики событий для кнопок
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
