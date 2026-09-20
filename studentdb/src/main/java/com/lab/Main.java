package com.lab;

import com.lab.bean.StudentBean;
import com.lab.dao.StudentDAO;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Student Management System ---");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Update Student Record");
            System.out.println("4. Exit");
            System.out.print("Select an option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();
                    System.out.print("Enter Course: ");
                    String course = sc.nextLine();
                    System.out.print("Enter GPA: ");
                    double gpa = sc.nextDouble();

                    StudentBean newStudent = new StudentBean(name, email, course, gpa);
                    if (dao.addStudent(newStudent)) {
                        System.out.println("Record added successfully!");
                    } else {
                        System.out.println("Operation failed.");
                    }
                    break;

                case 2:
                    List<StudentBean> students = dao.getAllStudents();
                    System.out.println("\n--- Registered Students ---");
                    for (StudentBean s : students) {
                        System.out.println(s);
                    }
                    break;

                case 3:
                    System.out.print("Enter Student ID to update: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("New Name: ");
                    String uName = sc.nextLine();
                    System.out.print("New Email: ");
                    String uEmail = sc.nextLine();
                    System.out.print("New Course: ");
                    String uCourse = sc.nextLine();
                    System.out.print("New GPA: ");
                    double uGpa = sc.nextDouble();

                    StudentBean updateBean = new StudentBean(uName, uEmail, uCourse, uGpa);
                    updateBean.setStudentId(id);
                    if (dao.updateStudent(updateBean)) {
                        System.out.println("Student record updated successfully!");
                    } else {
                        System.out.println("Update failed.");
                    }
                    break;

                case 4:
                    System.out.println("Exiting application.");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}