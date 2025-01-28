package org.example.Entities;

import jakarta.persistence.*;

import java.io.Serializable;

public class Student implements Serializable {

    private int student_id;
    private Float averageGrade;

    private User user;

    private Group group;

    private String name;

    private String lastname;

    private int groupNumber;

    private String email;

    private String specialization;
    private String faculty;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }
    private int course;

    public void setCourse(int course) {
        this.course = course;
    }

    public int getCourse() {
        return course;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastname(){
        return lastname;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public int getStudent_id() {
        return student_id;
    }
    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Float getAverageGrade() {
        return averageGrade;
    }
    public void setAverageGrade(Float averageGrade) {
        this.averageGrade = averageGrade;
    }

    public Group getGroup() {
        return group;
    }
    public void setGroup(Group group) {
        this.group = group;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getFaculty() {
        return faculty;
    }
}
