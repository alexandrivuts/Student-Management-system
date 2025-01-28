package org.example.Server.DAO;

import org.example.Server.Entities.Group;
import org.example.Server.Interfaces.DAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class GroupDAO implements DAO<Group> {
    private final SessionFactory sessionFactory;

    public GroupDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insert(Group group) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(group);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void update(Group group) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(group);
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
            Group group = session.get(Group.class, id);
            if (group != null) {
                session.delete(group);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Group find(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Group.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Group> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Group> query = session.createQuery("from Group", Group.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Group> findByFaculty(String faculty) {
        try (Session session = sessionFactory.openSession()) {
            Query<Group> query = session.createQuery("from Group where faculty = :faculty", Group.class);
            query.setParameter("faculty", faculty);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Group findByGroupNumber(int groupNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<Group> query = session.createQuery("FROM Group WHERE groupNumber = :groupNumber", Group.class);
            query.setParameter("groupNumber", groupNumber);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Group> findByCourse(int course) {
        try (Session session = sessionFactory.openSession()) {
            Query<Group> query = session.createQuery("from Group where course = :course", Group.class);
            query.setParameter("course", course);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
