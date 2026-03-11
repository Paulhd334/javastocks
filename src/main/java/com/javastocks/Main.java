package com.javastocks;

import com.javastocks.view.MainFrame;
import com.javastocks.dao.DatabaseConnection;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.sql.SQLException;

public class Main {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (testDatabaseConnection()) {
                    configureLookAndFeel();
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                } else {
                    showConnectionError();
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Erreur fatale: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private static boolean testDatabaseConnection() {
        try {
            var conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            // Connexion échouée
        }
        return false;
    }
    
    private static void configureLookAndFeel() {
        // Style noir et blanc épuré façon entreprise
        try {
            // Utiliser Metal Look and Feel (sobre)
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            
            // Palette noir et blanc uniquement
            UIManager.put("control", new ColorUIResource(245, 245, 245));     // Gris très clair
            UIManager.put("text", new ColorUIResource(50, 50, 50));           // Gris foncé
            UIManager.put("nimbusBase", new ColorUIResource(100, 100, 100));  // Gris moyen
            UIManager.put("nimbusBlueGrey", new ColorUIResource(220, 220, 220));
            UIManager.put("nimbusSelectionBackground", new ColorUIResource(180, 180, 180));
            
            // Boutons
            UIManager.put("Button.background", new ColorUIResource(240, 240, 240));
            UIManager.put("Button.foreground", new ColorUIResource(50, 50, 50));
            UIManager.put("Button.select", new ColorUIResource(200, 200, 200));
            UIManager.put("Button.focus", new ColorUIResource(220, 220, 220));
            
            // Panels
            UIManager.put("Panel.background", new ColorUIResource(255, 255, 255));
            
            // Menus
            UIManager.put("MenuBar.background", new ColorUIResource(230, 230, 230));
            UIManager.put("Menu.background", new ColorUIResource(230, 230, 230));
            UIManager.put("MenuItem.background", new ColorUIResource(255, 255, 255));
            UIManager.put("MenuItem.selectionBackground", new ColorUIResource(200, 200, 200));
            
            // Tables
            UIManager.put("Table.background", new ColorUIResource(255, 255, 255));
            UIManager.put("Table.foreground", new ColorUIResource(50, 50, 50));
            UIManager.put("Table.selectionBackground", new ColorUIResource(210, 210, 210));
            UIManager.put("Table.selectionForeground", new ColorUIResource(50, 50, 50));
            UIManager.put("Table.gridColor", new ColorUIResource(200, 200, 200));
            
            // Bordures
            UIManager.put("TitledBorder.titleColor", new ColorUIResource(80, 80, 80));
            
        } catch (Exception e) {
            // Fallback au look système en cas d'erreur
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Use default
            }
        }
    }
    
    private static void showConnectionError() {
        String message = "ERREUR DE CONNEXION A LA BASE DE DONNEES\n\n" +
                         "Verifiez que:\n" +
                         "- PostgreSQL est demarre (service postgresql-x64-17)\n" +
                         "- La base 'javastocks' existe\n" +
                         "- Le port 5433 est accessible";
        
        JOptionPane.showMessageDialog(null, message, "Erreur critique", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}