package org.example.Entities;

import jakarta.persistence.*;

import java.io.Serializable;

public class Exams implements Serializable{

    private int exam_id;

    private String subject;

    private int course;

    public int getExam_id() {
        return exam_id;
    }
    public void setExam_id(int examId) {
        this.exam_id = exam_id;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getCourse() {
        return course;
    }
    public void setCourse(Integer course) {
        this.course = course;
    }
}

