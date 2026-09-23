package com.lab.dao;

import com.lab.bean.StudentBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL Driver not found: " + e.getMessage());
        }
    }

    private static String getJdbcUrl() {
        String envUrl = System.getenv("DB_URL");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            return envUrl.trim();
        }
        return "jdbc:mysql://localhost:3306/studentdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static String getJdbcUser() {
        String envUser = System.getenv("DB_USER");
        return (envUser != null && !envUser.trim().isEmpty()) ? envUser.trim() : "root";
    }

    private static String getJdbcPassword() {
        String envPass = System.getenv("DB_PASSWORD");
        return (envPass != null && !envPass.trim().isEmpty()) ? envPass.trim() : "password";
    }

    private Connection getConnection() throws SQLException {
        String url = getJdbcUrl();
        String user = getJdbcUser();
        String password = getJdbcPassword();
        return DriverManager.getConnection(url, user, password);
    }

    public boolean addStudent(StudentBean student) {
        String sql = "INSERT INTO students (name, email, course, gpa) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getCourse());
            stmt.setDouble(4, student.getGpa());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[DB ERROR] addStudent failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<StudentBean> getAllStudents() {
        List<StudentBean> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                StudentBean s = new StudentBean();
                s.setStudentId(rs.getInt("student_id"));
                s.setName(rs.getString("name"));
                s.setEmail(rs.getString("email"));
                s.setCourse(rs.getString("course"));
                s.setGpa(rs.getDouble("gpa"));
                list.add(s);
            }
        } catch (Exception e) {
            System.err.println("[DB ERROR] getAllStudents failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStudent(StudentBean student) {
        String sql = "UPDATE students SET name = ?, email = ?, course = ?, gpa = ? WHERE student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getCourse());
            stmt.setDouble(4, student.getGpa());
            stmt.setInt(5, student.getStudentId());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[DB ERROR] updateStudent failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[DB ERROR] deleteStudent failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}