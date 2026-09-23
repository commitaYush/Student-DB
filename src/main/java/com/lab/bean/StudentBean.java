package com.lab.bean;

import java.io.Serializable;

public class StudentBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private int studentId;
    private String name;
    private String email;
    private String course;
    private double gpa;

    public StudentBean() {}

    public StudentBean(String name, String email, String course, double gpa) {
        this.name = name;
        this.email = email;
        this.course = course;
        this.gpa = gpa;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    // Aliases to avoid casing conflicts
    public double getGPA() { return gpa; }
    public void setGPA(double gpa) { this.gpa = gpa; }
}