package com.javastocks.dao;

import java.sql.*;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection = null;
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Charger la configuration
                Properties props = new Properties();
                InputStream input = DatabaseConnection.class.getClassLoader()
                        .getResourceAsStream("config/database.properties");
                
                if (input == null) {
                    // Configuration par défaut
                    String url = "jdbc:postgresql://localhost:5433/javastocks";
                    String user = "postgres";
                    String password = "postgres";
                    connection = DriverManager.getConnection(url, user, password);
                } else {
                    props.load(input);
                    String url = props.getProperty("db.url");
                    String user = props.getProperty("db.user");
                    String password = props.getProperty("db.password");
                    connection = DriverManager.getConnection(url, user, password);
                }
                
                System.out.println("Connexion à la base de données établie");
            } catch (Exception e) {
                System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
    
    // Gestion du garbage collector
    @Override
    protected void finalize() throws Throwable {
        try {
            closeConnection();
        } finally {
            super.finalize();
        }
    }
}