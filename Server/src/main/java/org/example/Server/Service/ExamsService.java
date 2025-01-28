package org.example.Server.Service;

import org.example.Server.DAO.ExamsDAO;
import org.example.Server.Entities.Exams;
import org.example.Server.Utility.HibernateUtil;
import org.example.Server.Interfaces.Service;

import java.util.List;

public class ExamsService implements Service<Exams> {

    private static ExamsDAO examsDAO;

    public ExamsService() {
        examsDAO = new ExamsDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public void insert(Exams exam) {
        if (isExamValid(exam)) {
            examsDAO.insert(exam);
        } else {
            throw new IllegalArgumentException("Invalid exam data.");
        }
    }

    @Override
    public void update(Exams exam) {
        if (isExamValid(exam)) {
            examsDAO.update(exam);
        } else {
            throw new IllegalArgumentException("Invalid exam data.");
        }
    }

    @Override
    public void delete(int id) {
        Exams exam = examsDAO.find(id);
        if (exam == null) {
            throw new IllegalArgumentException("Exam not found for deletion.");
        }
        examsDAO.delete(id);
    }

    @Override
    public Exams findById(int id) {
        Exams exam = examsDAO.find(id);
        if (exam == null) {
            throw new IllegalArgumentException("Exam not found.");
        }
        return exam;
    }

    @Override
    public List<Exams> findAll() {
        List<Exams> exams = examsDAO.findAll();
        if (exams.isEmpty()) {
            throw new IllegalStateException("No exams found.");
        }
        return exams;
    }

    /**
     * Поиск экзаменов по курсу.
     * @param course номер курса
     * @return список экзаменов
     */
    public List<Exams> findByCourse(int course) {
        List<Exams> exams = examsDAO.findByCourse(course);
        if (exams == null || exams.isEmpty()) {
            throw new IllegalStateException("No exams found for course: " + course);
        }
        return exams;
    }

    /**
     * Проверка корректности экзамена.
     * @param exam экзамен
     * @return true, если экзамен валиден
     */
    private boolean isExamValid(Exams exam) {
        return exam.getSubject() != null && !exam.getSubject().isEmpty() && exam.getCourse() > 0;
    }
}
