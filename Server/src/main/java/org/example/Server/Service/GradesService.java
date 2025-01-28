package org.example.Server.Service;

import org.example.Server.DAO.GradesDAO;
import org.example.Server.Entities.Grades;
import org.example.Server.Entities.Student;
import org.example.Server.Entities.Exams;
import org.example.Server.Utility.HibernateUtil;
import org.example.Server.Interfaces.Service;

import java.util.ArrayList;
import java.util.List;

public class GradesService implements Service<Grades> {

    private static GradesDAO gradesDAO;

    public GradesService() {
        gradesDAO = new GradesDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public void insert(Grades grade) {
        if (isGradeValid(grade)) {
            gradesDAO.insert(grade);
        } else {
            throw new IllegalArgumentException("Invalid grade or associated student/exam.");
        }
    }

    @Override
    public void update(Grades grade) {
        if (isGradeValid(grade)) {
            gradesDAO.update(grade);
        } else {
            throw new IllegalArgumentException("Invalid grade or associated student/exam.");
        }
    }

    @Override
    public void delete(int id) {
        Grades grade = gradesDAO.find(id);
        if (grade == null) {
            throw new IllegalArgumentException("Grade not found for deletion.");
        }
        gradesDAO.delete(id);
    }

    @Override
    public Grades findById(int id) {
        Grades grade = gradesDAO.find(id);
        if (grade == null) {
            throw new IllegalArgumentException("Grade not found.");
        }
        return grade;
    }

    @Override
    public List<Grades> findAll() {
        List<Grades> grades = gradesDAO.findAll();
        if (grades.isEmpty()) {
            throw new IllegalStateException("No grades found.");
        }
        return grades;
    }

    public List<Grades> findByStudentId(int studentId) {
        List<Grades> grades = gradesDAO.findByStudentId(studentId);
        if (grades == null || grades.isEmpty()) {
            System.out.println("Оценки не найдены для студента с ID: " + studentId);
            return new ArrayList<>();
        }
        return grades;
    }


    public List<Grades> findByExamId(int examId) {
        List<Grades> grades = gradesDAO.findByExamId(examId);
        if (grades == null || grades.isEmpty()) {
            throw new IllegalStateException("No grades found for exam with ID: " + examId);
        }
        return grades;
    }

    public Grades findByStudentAndExam(int studentId, int examId) {
        return gradesDAO.findByStudentAndExam(studentId, examId);
    }

    private boolean isGradeValid(Grades grade) {
        return grade.getStudent() != null && grade.getExams() != null && grade.getGrade() >= 0;
    }

    /**
     * Проверка существования оценки для студента и экзамена.
     * @param studentId ID студента
     * @param examId ID экзамена
     * @return true, если оценка существует
     */
    public boolean gradeExists(int studentId, int examId) {
        // Поиск студента и экзамена по ID
        Student student = new Student();
        student.setStudent_id(studentId);

        Exams exam = new Exams();
        exam.setExam_id(examId);

        // Проверяем существование оценки для студента и экзамена
        return gradesDAO.existsByStudentAndExams(student, exam);
    }
}
