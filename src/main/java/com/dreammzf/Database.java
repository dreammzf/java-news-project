package com.dreammzf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;
    
    public Database() {
        this.url = "jdbc:h2:file:./news_db;AUTO_SERVER=TRUE";
        this.user = "admin";
        this.password = "admin";
        connect();
        createTable();
    }
    
    private void connect() {
        try {
            Class.forName("org.h2.Driver");
            close();
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("DB connected");
        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Failed to connect to DB: " + e.getMessage());
        }
    }

    public void dropTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS news");
            System.out.println("News table dropped");
        } catch (SQLException e) {
            System.err.println("Failed to drop the news table: " + e.getMessage());
        }
    }

    private void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS news (" +
                "title VARCHAR NOT NULL," +
                "description TEXT NOT NULL," +
                "url VARCHAR NOT NULL," +
                "category VARCHAR," +
                "publish_date VARCHAR," +
                "image_url VARCHAR," +
                "source VARCHAR)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Failed to connect to the news table: " + e.getMessage());
        }
    }
    
    public void clearTable() {
        String sql = "DELETE FROM news";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Database cleared");
        } catch (SQLException e) {
            System.err.println("Failed to clear the news table: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void getTable() {
        String selectSQL = "SELECT * FROM news";
        try (Statement stmt = connection.createStatement()) {
            System.out.println("\nNews in database:");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-30s | %-40s | %-15s | %-10s | %-16s |\n", "Название", "Описание", "Источник", "Категория", "Дата и время");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
            try (ResultSet rs = stmt.executeQuery(selectSQL)) {
                while (rs.next()) {
                    System.out.printf("| %-30.30s | %-40.40s | %-15.15s | %-10.10s | %-16.16s |\n",
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("source"),
                            rs.getString("category"),
                            rs.getString("publish_date"));
                }
            }
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            System.err.println("Failed to read from news table: " + e.getMessage());
        }
    }

    public void insertNews(String title, String description, String url, String category, String publishDate, String imageUrl, String source) {
        String insert = "INSERT INTO news (title, description, url, category, publish_date, image_url, source) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, url);
            stmt.setString(4, category);
            stmt.setString(5, publishDate);
            stmt.setString(6, imageUrl);
            stmt.setString(7, source);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to insert news: " + e.getMessage());
        }
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        } finally {
            connection = null;
        }
    }
}