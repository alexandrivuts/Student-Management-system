package org.example.student;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.Entities.Student;
import org.example.Entities.Exams;
import org.example.Entities.Grades;
import org.example.Entities.User;
import org.example.TCP.CurrentSession;
import org.example.TCP.Request;
import org.example.TCP.RequestType;
import org.example.TCP.Response;
import org.example.Utility.ClientSocket;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class MySession {

    @FXML
    private Label course;
    @FXML
    private Label name;
    @FXML
    private Label surname;

    @FXML
    private Label subject1;

    @FXML
    private Label subject2;

    @FXML
    private Label subject3;

    @FXML
    private Label subject4;

    @FXML
    private Label subject5;

    @FXML
    private Label grade1;

    @FXML
    private Label grade2;

    @FXML
    private Label grade3;

    @FXML
    private Label grade4;

    @FXML
    private Label grade5;

    @FXML
    private Button myProfileButton;
    @FXML private Button mySessionButton;
    @FXML private Button myScholarshipButton;
    @FXML private Button allStudentsButton;
    @FXML private Button logOutButton;

    @FXML
    public void initialize() {
        loadSessionInfo();
    }

    public void loadSessionInfo() {
        int currentProfileId = CurrentSession.getInstance().getCurrentProfileId();

        if (currentProfileId == 0) {
            System.out.println("Ошибка: текущий ID профиля равен 0");
            return;
        }

        Request request = new Request();
        request.setRequestType(RequestType.GET_MY_SESSION);
        request.setMessage(String.valueOf(currentProfileId));

        new Thread(() -> {
            try {
                ClientSocket.getInstance().getWriter().println(new Gson().toJson(request));
                ClientSocket.getInstance().getWriter().flush();
                System.out.println("Запрос экзаменационной сессии отправлен");

                String responseLine = ClientSocket.getReader().readLine();
                Response response = new Gson().fromJson(responseLine, Response.class);

                Platform.runLater(() -> {
                    if (response.getSuccess()) {
                        JsonObject jsonObject = new Gson().fromJson(response.getMessage(), JsonObject.class);

                        User user = new Gson().fromJson(jsonObject.get("user"), User.class);
                        Student student = new Gson().fromJson(jsonObject.get("student"), Student.class);

                        Type examsListType = new TypeToken<List<Exams>>(){}.getType();
                        List<Exams> examsList = new Gson().fromJson(jsonObject.get("exams"), examsListType);

                        Type gradesListType = new TypeToken<List<Grades>>(){}.getType();
                        List<Grades> gradesList = new Gson().fromJson(jsonObject.get("grades"), gradesListType);

                        showSessionInfo(user, student, examsList, gradesList);
                    } else {
                        System.out.println("Не удалось загрузить информацию о профиле: " + response.getMessage());
                    }
                });
            } catch (IOException e) {
                System.out.println("Ошибка при загрузке данных профиля: " + e.getMessage());
            }
        }).start();
    }

    private void showSessionInfo(User user, Student student, List<Exams> examsList, List<Grades> gradesList) {
        if (user == null || student == null || examsList == null) {
            System.out.println("Данные отсутствуют.");
            return;
        }

        name.setText(user.getName());
        surname.setText(user.getSurname());
        course.setText(String.valueOf(student.getGroup().getCourse()));

        for (int i = 0; i < examsList.size(); i++) {
            Exams exam = examsList.get(i);
            Grades grade = i < gradesList.size() ? gradesList.get(i) : null;

            switch (i) {
                case 0:
                    subject1.setText(exam.getSubject());
                    grade1.setText(grade != null ? grade.getGrade().toString() : "Нет оценки");
                    break;
                case 1:
                    subject2.setText(exam.getSubject());
                    grade2.setText(grade != null ? grade.getGrade().toString() : "Нет оценки");
                    break;
                case 2:
                    subject3.setText(exam.getSubject());
                    grade3.setText(grade != null ? grade.getGrade().toString() : "Нет оценки");
                    break;
                case 3:
                    subject4.setText(exam.getSubject());
                    grade4.setText(grade != null ? grade.getGrade().toString() : "Нет оценки");
                    break;
                case 4:
                    subject5.setText(exam.getSubject());
                    grade5.setText(grade != null ? grade.getGrade().toString() : "Нет оценки");
                    break;
            }
        }
    }


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
