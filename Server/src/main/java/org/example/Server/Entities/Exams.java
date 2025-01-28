package org.example.Server.Entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "exams")
public class Exams implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id", nullable = false)
    private int exam_id;

    @Column(name = "subject", unique = true)
    private String subject;

    @Column(name = "course", unique = true)
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

