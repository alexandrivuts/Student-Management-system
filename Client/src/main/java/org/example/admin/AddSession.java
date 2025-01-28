package org.example.admin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.example.Entities.Exams;
import org.example.Entities.Grades;
import org.example.Entities.Student;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.Entities.User;
import org.example.TCP.CurrentSession;
import org.example.TCP.RequestType;
import org.example.TCP.Request;
import org.example.TCP.Response;
import org.example.student.UserMenu;
import org.example.Utility.ClientSocket;
import javafx.scene.control.Alert;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class AddSession extends Application {

    @FXML
    private Label subject1;
    @FXML
    private TextField grade1;
    @FXML
    private Label subject2;
    @FXML
    private TextField grade2;
    @FXML
    private Label subject3;
    @FXML
    private TextField grade3;
    @FXML
    private Label subject4;
    @FXML
    private TextField grade4;
    @FXML
    private Label subject5;
    @FXML
    private TextField grade5;

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
    @FXML
    private Button logOutButton;
    @FXML
    private Button addSessionForStudentButton;

    @FXML private TextField searchField;  // Добавляем поле для поиска
    @FXML private Button searchButton;

    private final Gson gson = new Gson();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserMenu.class.getResource("/admin-view/add-session.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 915, 480);
        stage.setTitle("Заполнение сессии");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        // Привязка столбцов
        columnId.setCellValueFactory(new PropertyValueFactory<>("student_id"));
        columnSurname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnCourse.setCellValueFactory(new PropertyValueFactory<>("course"));
        columnGroup.setCellValueFactory(new PropertyValueFactory<>("groupNumber"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Добавляем обработчик выбора студента
        tableView.setOnMouseClicked(event -> onStudentSelected(event));

        // Загружаем студентов
        loadStudents();
    }

    @FXML
    private void onStudentSelected(javafx.scene.input.MouseEvent event) {
        // Получаем выбранного студента
        Student selectedStudent = tableView.getSelectionModel().getSelectedItem();

        if (selectedStudent != null) {
            // Заполняем поля на основе выбранного студента
            loadSessionInfo(selectedStudent);
        }
    }

    private ObservableList<Student> studentsList = FXCollections.observableArrayList(); // Хранение студентов

    @FXML
    public void onSearchButtonClicked(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();

        // Фильтруем студентов по фамилии и имени
        ObservableList<Student> filteredList = studentsList.filtered(student ->
                student.getLastname().toLowerCase().contains(searchText) ||
                        student.getName().toLowerCase().contains(searchText)
        );

        // Обновляем данные в таблице
        tableView.setItems(filteredList);
    }

    private void refreshStudentTable() {
        loadStudents();
    }
    private void loadStudents() {
        try {
            // Ваш код для получения списка студентов с сервера
            Request studentRequest = new Request();
            studentRequest.setRequestType(RequestType.GET_STUDENTS);

            ClientSocket.getInstance().getWriter().println(new Gson().toJson(studentRequest));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getReader().readLine();
            Response response = new Gson().fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                String parts[] = response.getMessage().split(";");
                String StudentsJson = parts[0];
                Type studentListType = new TypeToken<List<Student>>() {}.getType();
                List<Student> students = gson.fromJson(StudentsJson, studentListType);
                studentsList.clear();
                studentsList.addAll(students);
                tableView.setItems(studentsList);
            } else {
                showError("Ошибка загрузки данных", response.getMessage());
            }
        } catch (IOException e) {
            showError("Ошибка подключения", "Не удалось подключиться к серверу.");
        } catch (Exception e) {
            showError("Ошибка загрузки данных", "Произошла ошибка при обработке данных.");
        }
    }

    public void loadSessionInfo(Student selectedStudent) {
        if (selectedStudent == null) {
            System.out.println("Студент не выбран.");
            return;
        }

        // Формируем запрос, передаем ID студента
        Request request = new Request();
        request.setRequestType(RequestType.GET_MY_SUBJECTS);
        request.setMessage(String.valueOf(selectedStudent.getUser().getUser_id())); // Добавляем ID студента в сообщение запроса

        new Thread(() -> {
            try {
                // Отправляем запрос на сервер
                ClientSocket.getInstance().getWriter().println(new Gson().toJson(request));
                ClientSocket.getInstance().getWriter().flush();
                System.out.println("Запрос экзаменационной сессии отправлен для студента ID: " + selectedStudent.getUser().getUser_id());

                // Получаем ответ от сервера
                String responseLine = ClientSocket.getReader().readLine();
                Response response = new Gson().fromJson(responseLine, Response.class);

                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        // Сервер возвращает объект с данными пользователя, студента, экзаменов и оценок
                        JsonObject jsonObject = new Gson().fromJson(response.getMessage(), JsonObject.class);

                        User user = new Gson().fromJson(jsonObject.get("user"), User.class);
                        Student student = new Gson().fromJson(jsonObject.get("student"), Student.class);

                        // Используем TypeToken для правильной десериализации List
                        Type examsListType = new TypeToken<List<Exams>>(){}.getType();
                        List<Exams> examsList = new Gson().fromJson(jsonObject.get("exams"), examsListType);

                        showSessionInfo(user, student, examsList);
                    } else {
                        System.out.println("Не удалось загрузить информацию о сессии: " + response.getMessage());
                    }
                });
            } catch (IOException e) {
                System.out.println("Ошибка при загрузке данных сессии: " + e.getMessage());
            }
        }).start();
    }


    private void showSessionInfo(User user, Student student, List<Exams> examsList) {
        if (user == null || student == null || examsList == null) {
            System.out.println("Данные отсутствуют.");
            return;
        }

        for (int i = 0; i < examsList.size(); i++) {
            Exams exam = examsList.get(i);

            // Устанавливаем предметы и оценки
            switch (i) {
                case 0:
                    subject1.setText(exam.getSubject());
                    break;
                case 1:
                    subject2.setText(exam.getSubject());
                    break;
                case 2:
                    subject3.setText(exam.getSubject());
                    break;
                case 3:
                    subject4.setText(exam.getSubject());
                    break;
                case 4:
                    subject5.setText(exam.getSubject());
                    break;
            }
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void addSessionForStudentButton_Pressed(ActionEvent actionEvent) throws IOException {
        if (grade1.getText().isEmpty() || grade2.getText().isEmpty() || grade3.getText().isEmpty() ||
                grade4.getText().isEmpty() || grade5.getText().isEmpty()) {
            showError("Ошибка", "Пожалуйста, заполните все поля с оценками.");
            return;
        }

        // Получаем данные об оценках
        String grade1Value = grade1.getText();
        String grade2Value = grade2.getText();
        String grade3Value = grade3.getText();
        String grade4Value = grade4.getText();
        String grade5Value = grade5.getText();

        // Получаем выбранного студента из таблицы
        Student selectedStudent = tableView.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showError("Ошибка", "Пожалуйста, выберите студента.");
            return;
        }

        Request request = new Request();
        request.setRequestType(RequestType.ADD_SESSION);

        String sessionData = selectedStudent.getStudent_id() + ";" + grade1Value + ";" + grade2Value + ";" +
                grade3Value + ";" + grade4Value + ";" + grade5Value;

        request.setMessage(sessionData);

        ClientSocket.getInstance().getWriter().println(new Gson().toJson(request));
        ClientSocket.getInstance().getWriter().flush();

        String responseLine = ClientSocket.getReader().readLine();
        Response response = new Gson().fromJson(responseLine, Response.class);

        if (response.getSuccess()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успех");
            alert.setHeaderText(null);
            alert.setContentText("Сессия успешно добавлена для студента!");
            alert.showAndWait();
        } else {
            showError("Ошибка", response.getMessage());
        }
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

