package com.beastiehut.javaru.web;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestJDBC {

    public static void main(String args[]) {
        // Снова используем файл для вывода из-за кодировки
        try {
            System.setOut(new PrintStream("out.txt"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return;
        }

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/students";

            connection = DriverManager.getConnection(url, "root", "mbfAATwb2408");
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM students");

            while (resultSet.next()) {
                String str = resultSet.getString(1) + ":" + resultSet.getString(2);
                printString(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Эта часть позволяет нам закрыть все открытые ресуры
            // В противном случае возмжожны проблемы. Поэтому будьте
            // всегда аккуратны при работе с коннектами
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.err.println("Error: " + ex.getMessage());
            }
        }
    }

    // Снова используем этот метод для вывода из-за кодировки
    public static void printString(Object s) {
        try {
            System.out.println(new String(s.toString().getBytes("windows-1251"), "windows-1252"));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}