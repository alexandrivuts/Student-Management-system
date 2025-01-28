package org.example.student;

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
import org.example.Entities.Student;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.TCP.RequestType;
import org.example.TCP.Request;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class AllStudents extends Application {

    @FXML
    private TableView<Student> tableView;

    @FXML
    private TableColumn<Student, Integer> columnId;

    @FXML
    private TableColumn<Student, String> columnSurname;

    @FXML
    private TableColumn<Student, String> columnName;

    @FXML
    private TableColumn<Student, Integer> columnCourse;

    @FXML
    private TableColumn<Student, String> columnGroup;

    @FXML
    private TableColumn<Student, String> columnEmail;

    @FXML private Button myProfileButton;
    @FXML private Button mySessionButton;
    @FXML private Button myScholarshipButton;
    @FXML private Button allStudentsButton;
    @FXML private Button logOutButton;
    @FXML private TextField searchField;  // Добавляем поле для поиска
    @FXML private Button searchButton;

    private final Gson gson = new Gson();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserMenu.class.getResource("/student-view/all-students.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 915, 480);
        stage.setTitle("Все студенты");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        // Устанавливаем привязку столбцов
        columnId.setCellValueFactory(new PropertyValueFactory<>("student_id"));
        columnSurname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnCourse.setCellValueFactory(new PropertyValueFactory<>("course"));
        columnGroup.setCellValueFactory(new PropertyValueFactory<>("groupNumber"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Загружаем данные студентов
        loadStudents();
    }

    private ObservableList<Student> studentsList = FXCollections.observableArrayList(); // Хранение студентов

    // Функция, которая срабатывает при нажатии на кнопку поиска
    public void onSearchButtonClicked(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase(); // Получаем текст из поля поиска

        // Фильтруем студентов по фамилии и имени
        ObservableList<Student> filteredList = studentsList.filtered(student ->
                student.getLastname().toLowerCase().contains(searchText) ||
                        student.getName().toLowerCase().contains(searchText)
        );

        // Обновляем данные в таблице
        tableView.setItems(filteredList);
    }

    private void loadStudents() {
        try {
            // Создаем объект запроса
            Request studentRequest = new Request();
            studentRequest.setRequestType(RequestType.GET_STUDENTS);

            // Отправляем запрос на сервер
            ClientSocket.getInstance().getWriter().println(new Gson().toJson(studentRequest));
            ClientSocket.getInstance().getWriter().flush();

            // Читаем ответ с сервера
            String responseLine = ClientSocket.getReader().readLine();
            Response response = new Gson().fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                String parts[] = response.getMessage().split(";");
                String StudentsJson = parts[0];
                Type studentListType = new TypeToken<List<Student>>() {}.getType();
                List<Student> students = gson.fromJson(StudentsJson, studentListType);

                studentsList = FXCollections.observableArrayList(students); // Сохраняем весь список студентов
                tableView.setItems(studentsList); // Заполняем таблицу всеми студентами
            } else {
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

    public void myProfileButton_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) myProfileButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-view/MyProfileStudent.fxml"));
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

