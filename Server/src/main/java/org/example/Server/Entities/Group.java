package org.example.Server.Entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "student_groups")
public class Group implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id", nullable = false)
    private int group_id;

    @Column(name = "groupNumber", unique = true)
    private int groupNumber;

    @Column(name = "course", unique = true)
    private int course;

    @Column(name = "faculty", unique = false)
    private String faculty;

    @Column(name = "specialization", unique = false)
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
