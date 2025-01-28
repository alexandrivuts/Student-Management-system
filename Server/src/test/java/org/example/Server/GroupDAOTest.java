package org.example.Server;

import org.example.Server.DAO.GroupDAO;
import org.example.Server.Entities.Group;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupDAOTest {
    private static SessionFactory sessionFactory;
    private GroupDAO groupDAO;

    @BeforeAll
    static void setupSessionFactory() {
        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml") // Убедитесь, что MySQL используется
                .buildSessionFactory();
    }

    @BeforeEach
    void setup() {
        groupDAO = new GroupDAO(sessionFactory);
    }

    @AfterEach
    void cleanup() {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            session.createQuery("DELETE FROM Group").executeUpdate(); // Очистка таблицы Group
            transaction.commit();
        }
    }

    @AfterAll
    static void tearDownSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void testInsert() {
        // Arrange
        Group group = new Group();
        group.setGroupNumber(472303);
        group.setFaculty("ИЭФ");
        group.setCourse(1);
        group.setSpecialization("ИСИТвЭ");

        // Act
        groupDAO.insert(group);

        // Assert
        List<Group> groups = groupDAO.findAll();
        assertEquals(1, groups.size(), "Ожидалось, что в таблице будет 1 запись.");
        assertEquals(472303, groups.get(0).getGroupNumber(), "Групповой номер должен быть 472303.");
    }
}
