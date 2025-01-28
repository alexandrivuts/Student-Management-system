package org.example.Server.Service;

import org.example.Server.DAO.GroupDAO;
import org.example.Server.Entities.Group;
import org.example.Server.Utility.HibernateUtil;
import org.example.Server.Interfaces.Service;

import java.util.List;

public class GroupService implements Service<Group> {

    private static GroupDAO groupDAO;

    public GroupService() {
        groupDAO = new GroupDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public void insert(Group group) {
        if (isGroupNumberUnique(group.getGroupNumber())) {
            groupDAO.insert(group);
        } else {
            throw new IllegalArgumentException("Group number must be unique.");
        }
    }

    @Override
    public void update(Group group) {
        if (isGroupNumberUniqueForUpdate(group.getGroupNumber(), group.getGroup_id())) {
            groupDAO.update(group);
        } else {
            throw new IllegalArgumentException("Group number must be unique.");
        }
    }

    @Override
    public void delete(int id) {
        Group group = groupDAO.find(id);
        if (group == null) {
            throw new IllegalArgumentException("Group not found for deletion.");
        }
        groupDAO.delete(id);
    }

    @Override
    public Group findById(int id) {
        Group group = groupDAO.find(id);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        return group;
    }

    @Override
    public List<Group> findAll() {
        List<Group> groups = groupDAO.findAll();
        if (groups.isEmpty()) {
            throw new IllegalStateException("No groups found.");
        }
        return groups;
    }

    public Group findByGroupNumber(int groupNumber) {
        Group group = groupDAO.findByGroupNumber(groupNumber);
        if (group == null) {
            throw new IllegalArgumentException("Group not found for group number: " + groupNumber);
        }
        return group;
    }

    public List<Group> findByFaculty(String faculty) {
        List<Group> groups = groupDAO.findByFaculty(faculty);
        if (groups.isEmpty()) {
            throw new IllegalStateException("No groups found for faculty: " + faculty);
        }
        return groups;
    }

    public List<Group> findByCourse(int course) {
        List<Group> groups = groupDAO.findByCourse(course);
        if (groups.isEmpty()) {
            throw new IllegalStateException("No groups found for course: " + course);
        }
        return groups;
    }


    private boolean isGroupNumberUnique(int groupNumber) {
        List<Group> groups = groupDAO.findAll();
        return groups.stream().noneMatch(g -> g.getGroupNumber() == groupNumber);
    }

    private boolean isGroupNumberUniqueForUpdate(int groupNumber, int groupId) {
        List<Group> groups = groupDAO.findAll();
        return groups.stream()
                .filter(g -> g.getGroup_id() != groupId)
                .noneMatch(g -> g.getGroupNumber() == groupNumber);
    }
}
