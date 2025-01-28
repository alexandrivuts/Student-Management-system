package org.example.Server.DAO;

import org.example.Server.Entities.Exams;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ExamsDAO implements DAO<Exams> {
    private final SessionFactory sessionFactory;

    public ExamsDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(Exams exam) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(exam);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Exams exam) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(exam);
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
            Exams exam = session.get(Exams.class, id);
            if (exam != null) {
                session.delete(exam);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Exams find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Exams.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Exams> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Exams> query = session.createQuery("from Exams", Exams.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Exams> findByCourse(int course) {
        try (Session session = sessionFactory.openSession()) {
            Query<Exams> query = session.createQuery("from Exams where course = :course", Exams.class);
            query.setParameter("course", course);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
