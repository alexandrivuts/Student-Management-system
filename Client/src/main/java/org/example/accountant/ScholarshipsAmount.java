package org.example.accountant;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.example.Entities.ScholarshipAmount;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.TCP.RequestType;
import org.example.TCP.Request;
import org.example.TCP.Response;
import org.example.student.UserMenu;
import org.example.Utility.ClientSocket;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

public class ScholarshipsAmount extends Application {

    @FXML
    private TableView<ScholarshipAmount> tableView;

    @FXML
    private TableColumn<ScholarshipAmount, Integer> columnId;

    @FXML
    private TableColumn<ScholarshipAmount, BigDecimal> columnMinAverage;

    @FXML
    private TableColumn<ScholarshipAmount, BigDecimal> columnMaxAverage;
    @FXML
    private TableColumn<ScholarshipAmount, Float> columnAmount;


    @FXML private Button myProfileButton;
    @FXML private Button allStudentsButton;
    @FXML private Button tableScholarshipButton;
    @FXML private Button changeScholarshipButton;
    @FXML private Button logOutButton;

    private final Gson gson = new Gson();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserMenu.class.getResource("/accountant-view/scholarships-amount.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 915, 480);
        stage.setTitle("Стипендии");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        // Устанавливаем привязку столбцов
        columnId.setCellValueFactory(new PropertyValueFactory<>("amount_id"));
        columnMinAverage.setCellValueFactory(new PropertyValueFactory<>("min_average"));
        columnMaxAverage.setCellValueFactory(new PropertyValueFactory<>("max_average"));
        columnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Загружаем данные стипендий
        loadScholarshipsAmount();
    }

    private void loadScholarshipsAmount() {
        try {
            // Создаем объект запроса
            Request scholarshipRequest = new Request();
            scholarshipRequest.setRequestType(RequestType.GET_SCHOLARSHIPS);

            // Отправляем запрос на сервер
            ClientSocket.getInstance().getWriter().println(new Gson().toJson(scholarshipRequest));
            ClientSocket.getInstance().getWriter().flush();

            // Читаем ответ с сервера
            String responseLine = ClientSocket.getReader().readLine();
            Response response = new Gson().fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                String parts[] = response.getMessage().split(";");
                String ScholarshipJson = parts[0];
                Type scholarshipListType = new TypeToken<List<ScholarshipAmount>>() {}.getType();
                List<ScholarshipAmount> scholarships = gson.fromJson(ScholarshipJson,scholarshipListType);
                ObservableList<ScholarshipAmount> scholarshipObservableList = FXCollections.observableArrayList(scholarships);
                tableView.setItems(scholarshipObservableList);
            } else {
                // Если ошибка, выводим сообщение
                showError("Ошибка загрузки данных", response.getMessage());
            }
        } catch (IOException e) {
            showError("Ошибка подключения", "Не удалось подключиться к серверу.");
        } catch (Exception e) {
            showError("Ошибка загрузки данных", "Произошла ошибка при обработке данных.");
        }
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/accountant-view/accountant-menu.fxml"));
        Parent root = loader.load();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}

