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

public class ChangeScholarships extends Application {

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

    @FXML
    private TextField minAverageField;
    @FXML
    private TextField maxAverageField;
    @FXML
    private TextField amountField;
    @FXML
    private Button updateButton;

    @FXML
    private Button myProfileButton;
    @FXML
    private Button allStudentsButton;
    @FXML
    private Button tableScholarshipButton;
    @FXML
    private Button changeScholarshipButton;
    @FXML
    private Button logOutButton;

    private final Gson gson = new Gson();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserMenu.class.getResource("/accountant-view/change-scholarships.fxml"));
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

        // Добавляем слушателя для выбора строки в таблице
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Заполняем текстовые поля значениями выбранной стипендии
                minAverageField.setText(newValue.getMin_average().toString());
                maxAverageField.setText(newValue.getMax_average().toString());
                amountField.setText(newValue.getAmount().toString());
            }
        });
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

    @FXML
    public void updateButton_Pressed(ActionEvent actionEvent) {
        // Получаем выбранную строку из таблицы
        ScholarshipAmount selectedScholarship = tableView.getSelectionModel().getSelectedItem();
        if (selectedScholarship == null) {
            showError("Ошибка", "Пожалуйста, выберите стипендию для обновления.");
            return;
        }

        try {
            // Чтение новых значений из текстовых полей
            BigDecimal newMinAverage = new BigDecimal(minAverageField.getText());
            BigDecimal newMaxAverage = new BigDecimal(maxAverageField.getText());
            BigDecimal newAmount = new BigDecimal(amountField.getText());

            // Обновляем объект стипендии
            selectedScholarship.setMin_average(newMinAverage);
            selectedScholarship.setMax_average(newMaxAverage);
            selectedScholarship.setAmount(newAmount);

            // Создаем объект запроса для обновления
            Request updateRequest = new Request();
            updateRequest.setRequestType(RequestType.UPDATE_SCHOLARSHIP);

            // Преобразуем объект стипендии в JSON и передаем его как сообщение
            String scholarshipJson = new Gson().toJson(selectedScholarship);
            updateRequest.setMessage(scholarshipJson);  // Передаем данные через поле message

            // Отправляем запрос на сервер
            ClientSocket.getInstance().getWriter().println(new Gson().toJson(updateRequest));
            ClientSocket.getInstance().getWriter().flush();

            // Получаем ответ от сервера
            String responseLine = ClientSocket.getReader().readLine();
            Response response = new Gson().fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                loadScholarshipsAmount();  // Перезагружаем список стипендий
                showInfo("Успех", "Стипендия успешно обновлена!");

                // Сбрасываем текстовые поля
                minAverageField.clear();
                maxAverageField.clear();
                amountField.clear();
            } else {
                showError("Ошибка обновления", response.getMessage());
            }

        } catch (Exception e) {
            showError("Ошибка", "Произошла ошибка при обновлении данных.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

