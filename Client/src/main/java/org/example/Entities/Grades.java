package org.example.Entities;

import jakarta.persistence.*;

import java.io.Serializable;

public class Grades implements Serializable {

    private int grade_id;

    private Student student;

    private Exams exams;

    private Float grade;

    public int getGrade_id() {
        return grade_id;
    }
    public void setGrade_id(int grade_id) {
        this.grade_id = grade_id;
    }

    public Float getGrade() {
        return grade;
    }
    public void setGrade(Float grade) {
        this.grade = grade;
    }

    public Exams getExams() {
        return exams;
    }
    public void setExams(Exams exams) {
        this.exams = exams;
    }

    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }
}
