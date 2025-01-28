package org.example.Server.DAO;

import org.example.Server.Entities.Exams;
import org.example.Server.Entities.Grades;
import org.example.Server.Entities.Student;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class GradesDAO implements DAO<Grades> {
    private final SessionFactory sessionFactory;

    public GradesDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(Grades grade) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(grade);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Grades grade) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(grade);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Grades grade = session.get(Grades.class, id);
            if (grade != null) {
                session.delete(grade);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Grades find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Grades.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Grades> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Grades> query = session.createQuery("from Grades", Grades.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Grades> findByStudentId(int studentId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Grades> query = session.createQuery("from Grades where student.id = :studentId", Grades.class);
            query.setParameter("studentId", studentId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Grades> findByExamId(int examId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Grades> query = session.createQuery("from Grades where exams.id = :examId", Grades.class);
            query.setParameter("examId", examId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean existsByStudentAndExams(Student student, Exams exam) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT COUNT(g) FROM Grades g WHERE g.student = :student AND g.exams = :exam";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("student", student);
            query.setParameter("exam", exam);
            Long count = query.uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Grades findByStudentAndExam(int studentId, int examId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Grades g WHERE g.student.student_id = :studentId AND g.exams.exam_id = :examId";
            Query<Grades> query = session.createQuery(hql, Grades.class);
            query.setParameter("studentId", studentId);
            query.setParameter("examId", examId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
