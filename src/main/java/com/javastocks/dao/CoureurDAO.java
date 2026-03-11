package com.javastocks.dao;

import com.javastocks.model.Coureur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoureurDAO {
    
    // CREATE
    public int creer(Coureur coureur) {
        String sql = "INSERT INTO coureur (nom, prenom) VALUES (?, ?) RETURNING id";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, coureur.getNom());
            pstmt.setString(2, coureur.getPrenom());
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                coureur.setId(id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("Erreur création coureur: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return -1;
    }
    
    // READ - par ID
    public Coureur consulter(int id) {
        String sql = "SELECT * FROM coureur WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Coureur coureur = new Coureur();
                coureur.setId(rs.getInt("id"));
                coureur.setNom(rs.getString("nom"));
                coureur.setPrenom(rs.getString("prenom"));
                return coureur;
            }
        } catch (SQLException e) {
            System.err.println("Erreur consultation coureur: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return null;
    }
    
    // READ - tous
    public List<Coureur> listerTous() {
        List<Coureur> coureurs = new ArrayList<>();
        String sql = "SELECT * FROM coureur ORDER BY nom, prenom";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Coureur coureur = new Coureur();
                coureur.setId(rs.getInt("id"));
                coureur.setNom(rs.getString("nom"));
                coureur.setPrenom(rs.getString("prenom"));
                coureurs.add(coureur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur listage coureurs: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return coureurs;
    }
    
    // UPDATE
    public boolean modifier(Coureur coureur) {
        String sql = "UPDATE coureur SET nom = ?, prenom = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, coureur.getNom());
            pstmt.setString(2, coureur.getPrenom());
            pstmt.setInt(3, coureur.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modification coureur: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
    }
    
    // DELETE
    public boolean supprimer(int id) {
        String sql = "DELETE FROM coureur WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur suppression coureur: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
    }
}