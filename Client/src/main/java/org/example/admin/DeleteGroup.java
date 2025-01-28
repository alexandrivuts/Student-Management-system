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
import org.example.Entities.Group;
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

public class DeleteGroup extends Application {

    @FXML
    private TableView<Group> tableView;

    @FXML
    private TableColumn<Group, Integer> columnId;

    @FXML
    private TableColumn<Group, String> columnGroupNumber;

    @FXML
    private TableColumn<Group, String> columnCourse;

    @FXML
    private TableColumn<Group, Integer> columnFaculty;

    @FXML
    private TableColumn<Group, String> columnSpecialization;

    @FXML
    private Button myProfileButton;

    @FXML
    private Button removeGroupButton;

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

    @FXML private TextField searchField;  // Добавляем поле для поиска
    @FXML private Button searchButton;

    private final Gson gson = new Gson();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserMenu.class.getResource("/admin-view/add-new-group.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 915, 480);
        stage.setTitle("Все группы");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        // Устанавливаем привязку столбцов
        columnId.setCellValueFactory(new PropertyValueFactory<>("group_id"));
        columnGroupNumber.setCellValueFactory(new PropertyValueFactory<>("groupNumber"));
        columnCourse.setCellValueFactory(new PropertyValueFactory<>("course"));
        columnFaculty.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        columnSpecialization.setCellValueFactory(new PropertyValueFactory<>("specialization"));

        loadGroups();
    }
    private ObservableList<Group> groupList = FXCollections.observableArrayList();

    public void onSearchButtonClicked(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();

        ObservableList<Group> filteredList = groupList.filtered(group ->
                String.valueOf(group.getGroupNumber()).contains(searchText) ||
                        group.getFaculty().toLowerCase().contains(searchText.toLowerCase())
        );

        tableView.setItems(filteredList);
    }
    private void loadGroups() {
        try {
            Request groupsRequest = new Request();
            groupsRequest.setRequestType(RequestType.GET_GROUPS);

            ClientSocket.getInstance().getWriter().println(new Gson().toJson(groupsRequest));
            ClientSocket.getInstance().getWriter().flush();

            String responseLine = ClientSocket.getReader().readLine();
            Response response = new Gson().fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                String parts[] = response.getMessage().split(";");
                String GroupsJson = parts[0];
                Type groupListType = new TypeToken<List<Group>>() {}.getType();
                List<Group> groups = gson.fromJson(GroupsJson, groupListType);
                groupList.clear();
                groupList.addAll(groups);

                tableView.setItems(groupList);
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

    @FXML
    public void removeGroupButton_Pressed(ActionEvent actionEvent) {
        // Получаем выбранную группу
        Group selectedGroup = tableView.getSelectionModel().getSelectedItem();

        if (selectedGroup == null) {
            showError("Ошибка", "Пожалуйста, выберите группу для удаления.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Подтверждение удаления");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Вы уверены, что хотите удалить группу: " + selectedGroup.getGroupNumber() + "?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Request deleteRequest = new Request();
                    deleteRequest.setRequestType(RequestType.DELETE_GROUP);
                    deleteRequest.setMessage(String.valueOf(selectedGroup.getGroup_id()));

                    ClientSocket.getInstance().getWriter().println(new Gson().toJson(deleteRequest));
                    ClientSocket.getInstance().getWriter().flush();

                    String responseLine = ClientSocket.getReader().readLine();
                    Response serverResponse = new Gson().fromJson(responseLine, Response.class);

                    if (serverResponse.getSuccess()) {
                        groupList.remove(selectedGroup);
                        tableView.setItems(groupList);
                        showError("Успех", "Группа успешно удалена.");
                    } else {
                        showError("Ошибка", serverResponse.getMessage());
                    }
                } catch (IOException e) {
                    showError("Ошибка подключения", "Не удалось подключиться к серверу.");
                } catch (Exception e) {
                    showError("Ошибка", "Произошла ошибка при удалении группы.");
                }
            }
        });
    }

}


