package org.example.Server.Service;

import org.example.Server.DAO.StudentDAO;
import org.example.Server.Entities.Student;
import org.example.Server.Utility.HibernateUtil;
import org.example.Server.Interfaces.Service;

import java.util.List;

public class StudentService implements Service<Student> {

    private static StudentDAO studentDAO;

    public StudentService() {
        studentDAO = new StudentDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public void insert(Student student) {
        if (student.getUser() == null) {
            throw new IllegalArgumentException("Студент должен быть связан с пользователем.");
        }
        studentDAO.insert(student);
    }

    @Override
    public void update(Student student) {
        if (student.getStudent_id() <= 0) {
            throw new IllegalArgumentException("Некорректный ID студента.");
        }
        studentDAO.update(student);
    }

    @Override
    public void delete(int id) {
        Student existingStudent = studentDAO.find(id);
        if (existingStudent == null) {
            throw new IllegalArgumentException("Студент с ID " + id + " не найден.");
        }
        studentDAO.delete(id);
    }

    @Override
    public Student findById(int id) {
        Student student = studentDAO.find(id);
        if (student == null) {
            throw new IllegalArgumentException("Студент с ID " + id + " не найден.");
        }
        return student;
    }

    @Override
    public List<Student> findAll() {
        return studentDAO.findAll();
    }

    public List<Student> findByGroupId(int groupId) {
        List<Student> students = studentDAO.findAll().stream()
                .filter(student -> student.getGroup() != null && student.getGroup().getGroup_id() == groupId)
                .toList();
        if (students.isEmpty()) {
            throw new IllegalArgumentException("Нет студентов в группе с ID " + groupId);
        }
        return students;
    }

    public Student findByUsername(String username) {
        Student student = studentDAO.findByUsername(username);
        if (student == null) {
            throw new IllegalArgumentException("Студент с username " + username + " не найден.");
        }
        return student;
    }

    public Student findByUserId(int userId) {
        Student student = studentDAO.findByUserId(userId);
        if (student == null) {
            throw new IllegalArgumentException("Студент с user ID " + userId + " не найден.");
        }
        return student;
    }
}
