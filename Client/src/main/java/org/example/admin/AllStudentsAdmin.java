package org.example.admin;

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
import org.example.student.UserMenu;
import org.example.Utility.ClientSocket;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class AllStudentsAdmin extends Application {

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
    @FXML private TextField searchField;
    @FXML private Button searchButton;

    private final Gson gson = new Gson();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserMenu.class.getResource("/admin-view/all-studentsAdmin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 915, 480);
        stage.setTitle("Все студенты");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("student_id"));
        columnSurname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnCourse.setCellValueFactory(new PropertyValueFactory<>("course"));
        columnGroup.setCellValueFactory(new PropertyValueFactory<>("groupNumber"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        loadStudents();
    }
    private ObservableList<Student> studentsList = FXCollections.observableArrayList();

    public void onSearchButtonClicked(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();

        ObservableList<Student> filteredList = studentsList.filtered(student ->
                student.getLastname().toLowerCase().contains(searchText) ||
                        student.getName().toLowerCase().contains(searchText)
        );

        tableView.setItems(filteredList);
    }
    private void loadStudents() {
        try {
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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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

