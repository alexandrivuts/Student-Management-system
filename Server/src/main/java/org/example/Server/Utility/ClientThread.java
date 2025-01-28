package org.example.Server.Utility;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import org.example.Server.Entities.*;
import org.example.Server.Entities.TCP.Request;
import org.example.Server.Entities.TCP.RequestType;
import org.example.Server.Entities.TCP.Response;
import org.example.Server.Service.*;
import org.example.TCP.CurrentSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mysql.cj.conf.PropertyKey.PASSWORD;
import static com.mysql.cj.conf.PropertyKey.logger;

public class ClientThread implements Runnable {
    private Socket clientsSocket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Gson gson;
    private Response response;
    private Request request;

    private UserService userService = new UserService();
    private StudentService studentService = new StudentService();
    private GroupService groupService = new GroupService();
    private ExamsService examsService = new ExamsService();
    private GradesService gradesService = new GradesService();

    private ScholarshipAmountService scholarshipAmountService = new ScholarshipAmountService();

    public ClientThread(Socket socket) throws IOException {
        this.clientsSocket = socket;
        request = new Request();
        gson = new Gson();
        response = new Response();
        writer = new PrintWriter(clientsSocket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(clientsSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            while (clientsSocket.isConnected()) {
                String line = reader.readLine();
                if (line == null) break;
                request = gson.fromJson(line, Request.class);
                request.setRequestType(RequestType.valueOf(request.getRequestType().name()));

                switch (request.getRequestType()) {
                    case REGISTER -> {
                        handleRegistration(request);
                        break;
                    }
                    case LOGIN -> {
                        handleLogin(request);
                        break;
                    }
                    case MENU_USER -> {
                        handleUserMenu(request);
                        break;
                    }
                    case GET_STUDENTS -> {
                        handleAllStudents(request);
                        break;
                    }
                    case GET_SCHOLARSHIPS -> {
                        handleAllScholarships(request);
                    }
                    case GET_GROUPS -> {
                        handleAllGroups(request);
                    }
                    case GET_MY_PROFILE -> {
                        handleGetMyProfile(request);
                    }
                    case GET_MY_PROFILE_ACCOUNTANT -> {
                        handleGetMyProfileAccountant(request);
                    }
                    case GET_MY_PROFILE_ADMIN -> {
                        handleGetMyProfileAdmin(request);
                    }
                    case GET_MY_SESSION -> {
                        handleGetMySession(request);
                    }
                    case GET_MY_SUBJECTS -> {
                        handleGetMySubjects(request);
                    }
                    case GET_MY_SCHOLARSHIP -> {
                        handleGetScholarship(request);
                    }
                    case UPDATE_SCHOLARSHIP -> {
                        handleUpdateScholarship(request);
                    }
                    case ADD_STUDENT -> {
                        handleAddStudent(request);
                    }
                    case DELETE_STUDENT -> {
                        handleDeleteStudent(request);
                    }
                    case ADD_SESSION -> {
                        handleAddSession(request);
                    }
                    case DELETE_GROUP -> {
                        handleDeleteGroup(request);
                    }
                    case ADD_GROUP -> {
                        handleAddGroup(request);
                    }
                    case LOGOUT-> {handleLogout(request);}
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка в потоке клиента: " + e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (clientsSocket != null) clientsSocket.close();
                System.out.println("Соединение с клиентом закрыто.");
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }

    public void handleRegistration(Request request) {
        User user = gson.fromJson(request.getMessage(), User.class);
        System.out.println("Полученный клиент: " + user);

        if (userService.isUsernameExists(user.getUsername())) {
            response.setMessage("Ошибка: Имя пользователя уже занято.");
        } else {
            try {
                Role userRole = userService.getRoleById(1);
                user.setRole(userRole);
                userService.insert(user);
                Student student = new Student();
                student.setUser(user);


                studentService.insert(student);

                response.setMessage("Регистрация прошла успешно.");
                response.setSuccess(true);
            } catch (Exception e) {
                response.setMessage("Ошибка регистрации: " + e.getMessage());
                response.setSuccess(false);
                e.printStackTrace();
            }
        }

        System.out.println(gson.toJson(response));
        writer.println(gson.toJson(response));
        writer.flush();
    }


    public void handleLogin(Request request) {
        Response response = new Response();
        try {
            User user = gson.fromJson(request.getMessage(), User.class);
            System.out.println("Попытка входа для пользователя: " + user.getUsername());

            User foundUser = userService.login(user.getUsername(), user.getPassword());

            if (foundUser != null) {
                System.out.println(foundUser.getUser_id());

                response.setMessage(new Gson().toJson(foundUser));
                response.setSuccess(true);
                response.setResponseType(RequestType.LOGIN);
            } else {
                response.setMessage("Ошибка: Профиль, связанный с пользователем, не найден.");
                response.setSuccess(false);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
        }

        System.out.println(gson.toJson(response));
        writer.println(gson.toJson(response));
        writer.flush();
    }

    public void handleUserMenu(Request request){
        response.setMessage( "Успешная загрузка меню пользователя" );
        response.setSuccess(true);
        System.out.println(response.getMessage());
        response.setResponseType(RequestType.MENU_USER);
    }

    public void handleAllStudents(Request request) {
        try {
            List<Student> students = studentService.findAll();

            for (Student student : students){
                if(student.getUser() != null){
                    student.setName(student.getUser().getName());
                    student.setLastname(student.getUser().getSurname());
                    student.setEmail(student.getUser().getEmail());

                }
                if(student.getGroup() !=null){
                    student.setGroupNumber(student.getGroup().getGroupNumber());
                    student.setCourse(student.getGroup().getCourse());
                }
            }
           String StudentsJson = gson.toJson(students);

            response.setMessage(StudentsJson + ";" );
            response.setSuccess(true);
            System.out.println(response.getMessage());
            response.setResponseType(RequestType.GET_STUDENTS);

            writer.println(gson.toJson(response));
            writer.flush();

        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

    public void handleAllScholarships(Request request) {
        try {
            List<ScholarshipAmount> scholarships = scholarshipAmountService.findAll();

            for (ScholarshipAmount scholarshipAmount : scholarships){
                    scholarshipAmount.setMin_average(scholarshipAmount.getMin_average());
                    scholarshipAmount.setMax_average(scholarshipAmount.getMax_average());
                    scholarshipAmount.setAmount(scholarshipAmount.getAmount());
            }
            String StudentsJson = gson.toJson(scholarships);

            response.setMessage(StudentsJson + ";" );
            response.setSuccess(true);
            System.out.println(response.getMessage());
            response.setResponseType(RequestType.GET_SCHOLARSHIPS);

            writer.println(gson.toJson(response));
            writer.flush();

        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

    public void handleAllGroups(Request request) {
        try {
            // Получение списка групп из сервиса
            List<Group> groups = groupService.findAll();

            // Преобразование списка групп в JSON
            String groupsJson = gson.toJson(groups);

            // Формирование ответа
            response.setMessage(groupsJson);  // Убираем ";"
            response.setSuccess(true);
            System.out.println("Groups loaded: " + response.getMessage());
            response.setResponseType(RequestType.GET_GROUPS);

            // Отправка ответа клиенту
            writer.println(gson.toJson(response));
            writer.flush();

        } catch (Exception e) {
            // Обработка ошибок
            response.setMessage("Ошибка загрузки групп: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }


    public void handleGetMyProfile(Request request) {
        try {
            int userId = Integer.parseInt(request.getMessage());

            System.out.println("Запрос на получение данных с userId: " + userId); // Логируем полученный ID

            if (userId == 0) {
                System.out.println("Ошибка: текущий ID пользователя равен 0");
                response.setMessage("Ошибка: текущий ID пользователя равен 0.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
                return;
            }

            // Получаем данные о студенте, пользователе и группе
            Student student = studentService.findByUserId(userId);
            User user = userService.findById(userId);

            // Логируем информацию о студенте и пользователе
            System.out.println("Данные о студенте: " + student);
            System.out.println("Данные о пользователе: " + user);

            if (student == null || user == null) {
                System.out.println("Студент или пользователь не найден.");
                response.setMessage("Данные о пользователе не найдены.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
                return;
            }

            Group group = groupService.findById(student.getGroup().getGroup_id());

            // Логируем данные о группе
            System.out.println("Данные о группе: " + group);

            // Запрос на получение оценок
            System.out.println("Запрос оценок для студента с ID: " + student.getStudent_id());
            List<Grades> gradesList = gradesService.findByStudentId(student.getStudent_id());

            if (gradesList == null || gradesList.isEmpty()) {
                System.out.println("Нет оценок для студента с ID: " + student.getStudent_id());
                student.setAverageGrade(null); // Нет оценок — установим null для среднего балла
            } else {
                double averageGrade = calculateAverageGrade(gradesList);
                student.setAverageGrade(averageGrade);
                System.out.println("Средний балл для студента: " + averageGrade);
            }

            // Если все данные успешно получены
            if (student != null && user != null && group != null) {
                String studentJson = gson.toJson(student);
                String userJson = gson.toJson(user);
                String groupJson = gson.toJson(group);

                System.out.println("Данные для отправки клиенту: " + userJson + "|" + studentJson + "|" + groupJson);
                response.setMessage(userJson + "|" + studentJson + "|" + groupJson);
                response.setSuccess(true);
            } else {
                System.out.println("Данные не найдены. Один из объектов равен null.");
                response.setMessage("Данные о пользователе не найдены.");
                response.setSuccess(false);
            }

            writer.println(gson.toJson(response));
            writer.flush();

        } catch (Exception e) {
            System.out.println("Ошибка при загрузке данных: " + e.getMessage());
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }



    public void handleGetMyProfileAccountant(Request request) {
        try {
            int userId = Integer.parseInt(request.getMessage());

            // Логируем ID пользователя
            System.out.println("Получен userId: " + userId);

            if (userId == 0) {
                System.out.println("Ошибка: текущий ID пользователя равен 0");
                response.setMessage("Ошибка: текущий ID пользователя равен 0.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
                return; // Прерываем выполнение, если ID равен 0
            }

            // Получаем информацию о бухгалтере по userId из базы данных
            User user = userService.findById(userId);

            if (user != null) {
                // Преобразуем данные в формат JSON
                String userJson = gson.toJson(user);

                // Устанавливаем успешный ответ
                response.setMessage(userJson);  // Соединяем данные через разделитель
                response.setSuccess(true);

                // Отправляем ответ клиенту
                writer.println(gson.toJson(response));
                writer.flush();
            } else {
                response.setMessage("Данные о пользователе не найдены.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
            }

        } catch (Exception e) {
            // Обработка ошибок
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

    public void handleGetMyProfileAdmin(Request request) {
        try {
            // Получаем userId из запроса
            int userId = Integer.parseInt(request.getMessage());

            // Логируем ID пользователя
            System.out.println("Получен userId: " + userId);

            if (userId == 0) {
                System.out.println("Ошибка: текущий ID пользователя равен 0");
                response.setMessage("Ошибка: текущий ID пользователя равен 0.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
                return; // Прерываем выполнение, если ID равен 0
            }

            // Получаем информацию о админе по userId из базы данных
            User user = userService.findById(userId);

            if (user != null) {
                // Преобразуем данные в формат JSON
                String userJson = gson.toJson(user);

                // Устанавливаем успешный ответ
                response.setMessage(userJson);  // Соединяем данные через разделитель
                response.setSuccess(true);

                // Отправляем ответ клиенту
                writer.println(gson.toJson(response));
                writer.flush();
            } else {
                response.setMessage("Данные о пользователе не найдены.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
            }

        } catch (Exception e) {
            // Обработка ошибок
            response.setMessage("Ошибка загрузки данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

    public void handleGetMySession(Request request) {
        try {
            int userId = Integer.parseInt(request.getMessage()); // Получаем userId из запроса

            System.out.println("Получен userId: " + userId);

            if (userId <= 0) {
                throw new IllegalArgumentException("ID пользователя равен 0.");
            }

            // Получаем информацию о студенте и пользователе
            Student student = studentService.findByUserId(userId);
            User user = userService.findById(userId);

            if (student == null || user == null) {
                response.setMessage("Данные о пользователе или студенте не найдены.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
                return;
            }

            // Логируем данные студента
            System.out.println("Данные о студенте: " + gson.toJson(student));

            // Получаем группу студента и курс из группы
            Group group = student.getGroup();
            int courseNumber = (group != null) ? group.getCourse() : 0;
            System.out.println("Курс студента из группы: " + courseNumber);

            if (courseNumber <= 0) {
                response.setMessage("Курс студента не указан или некорректен.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
                return;
            }

            // Получаем экзамены для курса
            List<Exams> examsList = examsService.findByCourse(courseNumber);
            List<Grades> gradesList = gradesService.findByStudentId(student.getStudent_id());

            // Формируем JSON-ответ
            JsonObject responseObject = new JsonObject();
            responseObject.add("user", gson.toJsonTree(user));
            responseObject.add("student", gson.toJsonTree(student));
            responseObject.add("exams", gson.toJsonTree(examsList));
            responseObject.add("grades", gson.toJsonTree(gradesList));

            response.setMessage(responseObject.toString());
            response.setSuccess(true);

            // Отправляем ответ клиенту
            writer.println(gson.toJson(response));
            writer.flush();

        } catch (IllegalArgumentException e) {
            response.setMessage("Ошибка: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных сессии: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

    public void handleGetMySubjects(Request request) {
        try {
            int userId = Integer.parseInt(request.getMessage()); // Получаем userId из запроса

            System.out.println("Получен userId: " + userId);

            if (userId <= 0) {
                throw new IllegalArgumentException("ID пользователя равен 0.");
            }

            // Получаем информацию о студенте и пользователе
            Student student = studentService.findByUserId(userId);
            User user = userService.findById(userId);

            if (student == null || user == null) {
                response.setMessage("Данные о студенте не найдены.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
                return;
            }

            // Логируем данные студента
            System.out.println("Данные о студенте: " + gson.toJson(student));

            // Получаем группу студента и курс из группы
            Group group = student.getGroup();
            int courseNumber = (group != null) ? group.getCourse() : 0;
            System.out.println("Курс студента из группы: " + courseNumber);

            if (courseNumber <= 0) {
                response.setMessage("Курс студента не указан или некорректен.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
                return;
            }

            // Получаем экзамены для курса
            List<Exams> examsList = examsService.findByCourse(courseNumber);

            // Формируем JSON-ответ
            JsonObject responseObject = new JsonObject();
            responseObject.add("user", gson.toJsonTree(user));
            responseObject.add("student", gson.toJsonTree(student));
            responseObject.add("exams", gson.toJsonTree(examsList));

            response.setMessage(responseObject.toString());
            response.setSuccess(true);

            // Отправляем ответ клиенту
            writer.println(gson.toJson(response));
            writer.flush();

        } catch (IllegalArgumentException e) {
            response.setMessage("Ошибка: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных сессии: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

    public void handleGetScholarship(Request request) {
        try {
            int userId = Integer.parseInt(request.getMessage()); // Получаем userId

            if (userId <= 0) {
                throw new IllegalArgumentException("ID пользователя равен 0.");
            }

            // Получаем информацию о студенте и пользователе
            Student student = studentService.findByUserId(userId);
            User user = userService.findById(userId);

            if (student == null || user == null) {
                response.setMessage("Данные о пользователе или студенте не найдены.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
                return;
            }

            // Получаем оценки студента
            List<Grades> gradesList = gradesService.findByStudentId(student.getStudent_id());

            if (gradesList == null || gradesList.isEmpty()) {
                response.setMessage("Оценки для студента не найдены.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
                return;
            }

            // Вычисляем средний балл
            double averageGrade = calculateAverageGrade(gradesList);
            student.setAverageGrade(averageGrade);
            studentService.update(student);
            // Получаем сумму стипендии на основе среднего балла
            ScholarshipAmount scholarshipAmount = getScholarshipAmount(averageGrade);

            // Формируем JSON-ответ
            JsonObject responseObject = new JsonObject();
            responseObject.add("user", gson.toJsonTree(user));           // Добавляем информацию о пользователе
            responseObject.add("grades", gson.toJsonTree(gradesList));   // Добавляем оценки студента
            responseObject.addProperty("average_grade", averageGrade);  // Добавляем средний балл

            if (scholarshipAmount != null) {
                responseObject.addProperty("scholarship_amount", scholarshipAmount.getAmount());
            } else {
                responseObject.addProperty("scholarship_amount", "Не найдено подходящей стипендии");
            }

            response.setMessage(gson.toJson(responseObject)); // Отправляем правильный JSON
            response.setSuccess(true);

            // Отправляем ответ клиенту
            writer.println(gson.toJson(response));
            writer.flush();

        } catch (IllegalArgumentException e) {
            response.setMessage("Ошибка: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        } catch (Exception e) {
            response.setMessage("Ошибка загрузки данных сессии: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

    // Метод для вычисления среднего балла
    private double calculateAverageGrade(List<Grades> gradesList) {
        double sum = 0;
        int count = 0;

        for (Grades grade : gradesList) {
            if (grade != null && grade.getGrade() != null) {
                sum += grade.getGrade();
                count++;
            }
        }

        return count > 0 ? sum / count : 0; // Возвращаем 0, если нет оценок
    }

    // Метод для получения суммы стипендии на основе среднего балла
    private ScholarshipAmount getScholarshipAmount(double averageGrade) {
        // Ищем стипендию, которая подходит по диапазону среднего балла
        List<ScholarshipAmount> scholarshipAmounts = scholarshipAmountService.findAll();

        // Преобразуем averageGrade в BigDecimal для точности при сравнении
        BigDecimal avgGrade = BigDecimal.valueOf(averageGrade);

        for (ScholarshipAmount scholarship : scholarshipAmounts) {
            // Используем compareTo для сравнения BigDecimal значений
            if (avgGrade.compareTo(scholarship.getMin_average()) >= 0 && avgGrade.compareTo(scholarship.getMax_average()) <= 0) {
                return scholarship; // Возвращаем объект стипендии
            }
        }

        return null; // Если не нашли подходящего диапазона
    }


    public void handleUpdateScholarship(Request request) {
        try {
            // Пример получения данных запроса. Предполагается, что данные стипендии передаются в поле 'data' в JSON.
            ScholarshipAmount updatedScholarship = gson.fromJson(request.getMessage(), ScholarshipAmount.class);

            // Получаем все стипендии из базы данных
            List<ScholarshipAmount> scholarships = scholarshipAmountService.findAll();

            // Найдем стипендию для обновления
            boolean found = false;
            for (ScholarshipAmount scholarshipAmount : scholarships) {
                // Сравниваем по ID, используя примитивный тип
                if (scholarshipAmount.getAmount_id() == updatedScholarship.getAmount_id()) {
                    // Обновляем значения
                    if (updatedScholarship.getMin_average() != null && updatedScholarship.getMax_average() != null) {
                        scholarshipAmount.setMin_average(updatedScholarship.getMin_average());
                        scholarshipAmount.setMax_average(updatedScholarship.getMax_average());
                        scholarshipAmount.setAmount(updatedScholarship.getAmount());

                        // Сохраняем изменения
                        scholarshipAmountService.update(scholarshipAmount);
                        found = true;
                        break;
                    } else {
                        throw new IllegalArgumentException("Минимальное или максимальное значение не может быть null.");
                    }
                }
            }

            // Формируем ответ
            if (found) {
                // Отправляем обновленные данные обратно
                String updatedScholarshipsJson = gson.toJson(scholarships);
                response.setMessage(updatedScholarshipsJson + ";");
                response.setSuccess(true);
                response.setResponseType(RequestType.GET_SCHOLARSHIPS);
                System.out.println("Стипендия обновлена: " + updatedScholarshipsJson);
            } else {
                response.setMessage("Стипендия не найдена.");
                response.setSuccess(false);
            }

            // Отправляем ответ клиенту
            writer.println(gson.toJson(response));
            writer.flush();

        } catch (Exception e) {
            response.setMessage("Ошибка при обновлении данных: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }

    public void handleAddStudent(Request request) {
        // Преобразуем сообщение из JSON в объект Student
        Student student = gson.fromJson(request.getMessage(), Student.class);
        System.out.println("Полученный студент: " + student);

        // Проверка, существует ли группа, к которой относится студент
        Group group = groupService.findByGroupNumber(student.getGroupNumber());
        if (group == null) {
            response.setMessage("Ошибка: Группа не существует.");
            response.setSuccess(false);
        } else {
            try {
                // Получаем объект User, связанный с этим студентом
                User user = student.getUser();

                // Проверка, существует ли уже студент с таким же username
                if (userService.isUsernameExists(user.getUsername())) {
                    response.setMessage("Ошибка: Имя пользователя уже занято.");
                    response.setSuccess(false);
                } else {
                    // Сохраняем пользователя
                    Role userRole = userService.getRoleById(1);  // Пример: роль студента
                    user.setRole(userRole);
                    userService.insert(user);

                    // Связываем студента с группой
                    student.setUser(user);
                    student.setGroup(group);
                    studentService.insert(student);

                    response.setMessage("Студент успешно добавлен.");
                    response.setSuccess(true);
                }
            } catch (Exception e) {
                response.setMessage("Ошибка добавления студента: " + e.getMessage());
                response.setSuccess(false);
                e.printStackTrace();
            }
        }

        // Отправляем ответ клиенту
        System.out.println(gson.toJson(response));
        writer.println(gson.toJson(response));
        writer.flush();
    }

    public void handleDeleteStudent(Request request) {
        try {
            int studentId = Integer.parseInt(request.getMessage());
            if (studentId <= 0) {
                throw new IllegalArgumentException("ID студента некорректен.");
            }

            // Найти студента
            Student student = studentService.findById(studentId);
            if (student == null) {
                response.setMessage("Студент с таким ID не найден.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                writer.flush();
                return;
            }

            // Удалить оценки студента
            List<Grades> gradesList = gradesService.findByStudentId(studentId);
            for (Grades grade : gradesList) {
                gradesService.delete(grade.getGrade_id());
            }

            // Удалить запись о студенте
            studentService.delete(studentId);

            // Удалить запись о пользователе
            int userId = student.getUser().getUser_id();
            userService.delete(userId);

            response.setMessage("Студент, его пользователь и оценки успешно удалены.");
            response.setSuccess(true);
            writer.println(gson.toJson(response));
            writer.flush();

        } catch (Exception e) {
            response.setMessage("Ошибка удаления: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
            writer.flush();
        }
    }





    public void handleAddSession(Request request) {
        try {
            // Получаем данные из запроса
            String message = request.getMessage();
            String[] data = message.split(";");

            // Проверяем корректность данных
            if (data.length != 6) {
                response.setMessage("Неверное количество данных в запросе.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                return;
            }

            // Извлекаем ID студента
            int studentId;
            try {
                studentId = Integer.parseInt(data[0]);
            } catch (NumberFormatException e) {
                response.setMessage("Неверный формат ID студента.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                return;
            }

            // Проверяем наличие студента
            Student student = studentService.findById(studentId);
            if (student == null) {
                response.setMessage("Студент не найден.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                return;
            }

            // Получаем группу студента и её курс
            Group group = student.getGroup();
            if (group == null) {
                response.setMessage("Группа студента не найдена.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                return;
            }
            int course = group.getCourse();

            // Получаем экзамены для курса
            List<Exams> exams = examsService.findByCourse(course);
            if (exams.isEmpty()) {
                response.setMessage("Нет экзаменов для курса.");
                response.setSuccess(false);
                writer.println(gson.toJson(response));
                return;
            }

            // Обработка оценок
            for (int i = 0; i < exams.size(); i++) {
                Exams exam = exams.get(i);
                float gradeValue;
                try {
                    gradeValue = Float.parseFloat(data[i + 1]);
                } catch (NumberFormatException e) {
                    response.setMessage("Некорректная оценка: " + data[i + 1]);
                    response.setSuccess(false);
                    writer.println(gson.toJson(response));
                    return;
                }

                // Проверяем, существует ли оценка
                Grades grade = gradesService.findByStudentAndExam(studentId, exam.getExam_id());
                if (grade != null) {
                    grade.setGrade(gradeValue);
                    gradesService.update(grade);
                } else {
                    Grades newGrade = new Grades();
                    newGrade.setStudent(student);
                    newGrade.setExams(exam);
                    newGrade.setGrade(gradeValue);
                    gradesService.insert(newGrade);
                }
            }

            // Успешный ответ
            response.setMessage("Сессия успешно добавлена или обновлена.");
            response.setSuccess(true);
            writer.println(gson.toJson(response));

        } catch (Exception e) {
            response.setMessage("Ошибка при добавлении сессии: " + e.getMessage());
            response.setSuccess(false);
            writer.println(gson.toJson(response));
        } finally {
            writer.flush();
        }
    }

    public void handleDeleteGroup(Request request) {
        Response response = new Response();

        try {
            // Получаем ID группы из запроса
            int groupId = Integer.parseInt(request.getMessage());

            // Удаляем группу через сервис
            groupService.delete(groupId);

            // Формируем успешный ответ
            response.setMessage("Группа успешно удалена.");
            response.setSuccess(true);
            response.setResponseType(RequestType.DELETE_GROUP);
            System.out.println("Group with ID " + groupId + " has been deleted.");
        } catch (IllegalArgumentException e) {
            // Группа не найдена или другие ошибки
            response.setMessage(e.getMessage());
            response.setSuccess(false);
        } catch (Exception e) {
            // Любые другие исключения
            response.setMessage("Ошибка при удалении группы: " + e.getMessage());
            response.setSuccess(false);
        }

        // Отправляем ответ клиенту
        writer.println(gson.toJson(response));
        writer.flush();
    }

    public void handleAddGroup(Request request) {
        Response response = new Response();

        try {
            // Преобразуем JSON-строку в объект Group
            Group newGroup = gson.fromJson(request.getMessage(), Group.class);

            // Выполняем добавление группы через сервис
            groupService.insert(newGroup);

            // Формируем успешный ответ
            response.setMessage("Группа успешно добавлена.");
            response.setSuccess(true);
            response.setResponseType(RequestType.ADD_GROUP);
            System.out.println("Group has been added: " + newGroup);
        } catch (IllegalArgumentException e) {
            // Обработка ошибок валидации или других бизнес-логик
            response.setMessage("Ошибка при добавлении группы: " + e.getMessage());
            response.setSuccess(false);
        } catch (Exception e) {
            // Обработка любых других исключений
            response.setMessage("Ошибка на сервере: " + e.getMessage());
            response.setSuccess(false);
        }

        // Отправляем ответ клиенту
        writer.println(gson.toJson(response));
        writer.flush();
    }
























    public void handleLogout(Request request) {
        // Например, просто подтверждаем успешный выход
        response.setMessage("Вы успешно вышли.");
        response.setSuccess(true);
        System.out.println(response.getMessage());
        response.setResponseType(RequestType.LOGOUT);
        writer.println(gson.toJson(response));
        writer.flush();
    }

}

