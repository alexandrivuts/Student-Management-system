package org.example.Server.DAO;

import org.example.Server.Entities.ScholarshipAmount;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ScholarshipAmountDAO implements DAO<ScholarshipAmount> {
    private final SessionFactory sessionFactory;

    public ScholarshipAmountDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(ScholarshipAmount scholarshipAmount) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(scholarshipAmount);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(ScholarshipAmount scholarshipAmount) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(scholarshipAmount);
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
            ScholarshipAmount scholarshipAmount = session.get(ScholarshipAmount.class, id);
            if (scholarshipAmount != null) {
                session.delete(scholarshipAmount);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public ScholarshipAmount find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(ScholarshipAmount.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ScholarshipAmount> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<ScholarshipAmount> query = session.createQuery("from ScholarshipAmount", ScholarshipAmount.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Find the scholarship amount based on the student's average grade.
     *
     * @param average the average grade of the student.
     * @return the ScholarshipAmount if found, otherwise null.
     */

    public ScholarshipAmount findByAverage(float average) {
        try (Session session = sessionFactory.openSession()) {
            Query<ScholarshipAmount> query = session.createQuery(
                    "from ScholarshipAmount where :average >= min_average and :average <= max_average",
                    ScholarshipAmount.class
            );
            query.setParameter("average", average);
            List<ScholarshipAmount> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
