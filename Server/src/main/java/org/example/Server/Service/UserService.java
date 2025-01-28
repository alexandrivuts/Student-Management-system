package org.example.Server.Service;

import org.example.Server.DAO.UserDAO;
import org.example.Server.Entities.User;
import org.example.Server.Entities.Role;
import org.example.Server.Utility.HibernateUtil;
import org.example.Server.Interfaces.Service;
import org.hibernate.Session;

import java.util.List;

public class UserService implements Service<User> {

    private static UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public void insert(User user) {
        try {
            if (isUsernameExists(user.getUsername())) {
                throw new IllegalArgumentException("Username is already taken.");
            }
            if (isUserValid(user)) {
                userDAO.insert(user);
            } else {
                System.out.println("Проверяемый пользователь: " + user);
                throw new IllegalArgumentException("Invalid user data.");

            }
        } catch (Exception e) {
            System.err.println("Ошибка при вставке пользователя: " + e.getMessage());
            throw e;
        }
    }


    @Override
    public void update(User user) {
        if (isUserValid(user)) {
            userDAO.update(user);
        } else {
            throw new IllegalArgumentException("Invalid user data.");
        }
    }

    @Override
    public void delete(int id) {
        User user = userDAO.find(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found for deletion.");
        }
        userDAO.delete(id);
    }

    @Override
    public User findById(int id) {
        User user = userDAO.find(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        List<User> users = userDAO.findAll();
        if (users.isEmpty()) {
            throw new IllegalStateException("No users found.");
        }
        return users;
    }

    public User findByUsername(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User with username " + username + " not found.");
        }
        return user;
    }

    /**
     * Проверка, существует ли пользователь с данным username.
     * @param username имя пользователя
     * @return true, если username занят
     */
    public static boolean isUsernameExists(String username) {
        User existingUser = userDAO.findByUsername(username);
        return existingUser != null;
    }

    /**
     * Логин пользователя по username и password.
     * @param username имя пользователя
     * @param password пароль пользователя
     * @return объект пользователя, если логин успешен, иначе null
     */
    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }
        return user;
    }

    /**
     * Получение роли пользователя по roleId.
     * @param roleId идентификатор роли
     * @return объект роли, если найдено, иначе null
     */
    public Role getRoleById(int roleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Role.class, roleId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Проверка корректности данных пользователя.
     * @param user пользователь
     * @return true, если данные пользователя валидны
     */
    private boolean isUserValid(User user) {
        return user.getUsername() != null && !user.getUsername().isEmpty() &&
                user.getPassword() != null && !user.getPassword().isEmpty() &&
                user.getName() != null && !user.getName().isEmpty() &&
                user.getSurname() != null && !user.getSurname().isEmpty() &&
                user.getEmail() != null && !user.getEmail().isEmpty() &&
                user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() &&
                user.getBirthday() != null && !user.getBirthday().isEmpty();
    }
}
