package org.example.Entities;

import jakarta.persistence.*;

import java.io.Serializable;

public class Group implements Serializable {

    private int group_id;

    private int groupNumber;

    private int course;

    private String faculty;

    private String specialization;

    public int getGroup_id() {
        return group_id;
    }
    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getGroupNumber() {
        return groupNumber;
    }
    public void setGroupNumber(Integer groupNumber) {
        this.groupNumber = groupNumber;
    }

    public int getCourse() {
        return course;
    }
    public void setCourse(Integer course) {
        this.course = course;
    }

    public String getFaculty() {
        return faculty;
    }
    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
