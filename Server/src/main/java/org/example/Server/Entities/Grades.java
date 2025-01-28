package org.example.Server.Entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "grades")
public class Grades implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id", nullable = false)
    private int grade_id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exams exams;

    @Column(name = "grade", unique = false)
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
