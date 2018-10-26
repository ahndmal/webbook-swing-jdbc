package com.beastiehut.javaru.web;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ManagementSystem {

    private static Connection connection;
    private static ManagementSystem instance;

    private ManagementSystem() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/students";

            connection = DriverManager.getConnection(url, "root", "root");

        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }

    public static synchronized ManagementSystem getInstance() throws Exception {

        if (instance == null) {
            instance = new ManagementSystem();
        }
        return instance;
    }

    public List<Group> getGroups() throws SQLException {

        List<Group> groups = new ArrayList<Group>();

        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT group_id, groupName, curator, speciality FROM groups");

            while (resultSet.next()) {
                Group group = new Group();
                group.setGroupId(resultSet.getInt(1));
                group.setNameGroup(resultSet.getString(2));
                group.setCurator(resultSet.getString(3));
                group.setSpeciality(resultSet.getString(4));

                groups.add(group);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
        return groups;
    }

    public Collection<Student> getAllStudents() throws SQLException {

        Collection<Student> students = new ArrayList<Student>();

        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT student_id, firstName, patronymic, surName, " +
                            "sex, dateOfBirth, group_id, educationYear FROM students " +
                            "ORDER BY surName, firstName, patronymic");
            while (resultSet.next()) {
                Student st = new Student(resultSet);
                students.add(st);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }

        return students;
    }

    public Collection<Student> getStudentsFromGroup(Group group, int year) throws SQLException {

        Collection<Student> students = new ArrayList<Student>();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement(
                    "SELECT student_id, firstName, patronymic, surName, " +
                            "sex, dateOfBirth, group_id, educationYear FROM students " +
                            "WHERE group_id=? AND educationYear=? " +
                            "ORDER BY surName, firstName, patronymic");
            stmt.setInt(1, group.getGroupId());
            stmt.setInt(2, year);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Student st = new Student(rs);

                students.add(st);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }

        return students;
    }

    public void moveStudentsToGroup(Group oldGroup, int oldYear, Group newGroup, int newYear) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(
                    "UPDATE students SET group_id=?, educationYear=? " +
                            "WHERE group_id=? AND educationYear=?");
            stmt.setInt(1, newGroup.getGroupId());
            stmt.setInt(2, newYear);
            stmt.setInt(3, oldGroup.getGroupId());
            stmt.setInt(4, oldYear);
            stmt.execute();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public void removeStudentsFromGroup(Group group, int year) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(
                    "DELETE FROM students WHERE group_id=? AND educationYear=?");
            stmt.setInt(1, group.getGroupId());
            stmt.setInt(2, year);
            stmt.execute();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public void insertStudent(Student student) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(
                    "INSERT INTO students " +
                            "(firstName, patronymic, surName, sex, dateOfBirth, group_id, educationYear) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getPatronymic());
            stmt.setString(3, student.getSurName());
            stmt.setString(4, new String(new char[]{student.getSex()}));
            stmt.setDate(5, new Date(student.getDateOfBirth().getTime()));
            stmt.setInt(6, student.getGroupId());
            stmt.setInt(7, student.getEducationYear());
            stmt.execute();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public void updateStudent(Student student) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(
                    "UPDATE students SET " +
                            "firstName=?, patronymic=?, surName=?, " +
                            "sex=?, dateOfBirth=?, group_id=?, educationYear=?" +
                            "WHERE student_id=?");
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getPatronymic());
            stmt.setString(3, student.getSurName());
            stmt.setString(4, new String(new char[]{student.getSex()}));
            stmt.setDate(5, new Date(student.getDateOfBirth().getTime()));
            stmt.setInt(6, student.getGroupId());
            stmt.setInt(7, student.getEducationYear());
            stmt.setInt(8, student.getStudentId());
            stmt.execute();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public void deleteStudent(Student student) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(
                    "DELETE FROM students WHERE student_id=?");
            stmt.setInt(1, student.getStudentId());
            stmt.execute();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
}