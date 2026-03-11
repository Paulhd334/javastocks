package com.javastocks.dao;

import com.javastocks.model.TypeEpreuve;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeEpreuveDAO {
    
    // CREATE
    public int creer(TypeEpreuve typeEpreuve) {
        String sql = "INSERT INTO type_epreuve (libelle) VALUES (?) RETURNING id";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, typeEpreuve.getLibelle());
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                typeEpreuve.setId(id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("Erreur création type épreuve: " + e.getMessage());
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
    public TypeEpreuve consulter(int id) {
        String sql = "SELECT * FROM type_epreuve WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                TypeEpreuve typeEpreuve = new TypeEpreuve();
                typeEpreuve.setId(rs.getInt("id"));
                typeEpreuve.setLibelle(rs.getString("libelle"));
                return typeEpreuve;
            }
        } catch (SQLException e) {
            System.err.println("Erreur consultation type épreuve: " + e.getMessage());
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
    public List<TypeEpreuve> listerTous() {
        List<TypeEpreuve> types = new ArrayList<>();
        String sql = "SELECT * FROM type_epreuve ORDER BY libelle";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                TypeEpreuve typeEpreuve = new TypeEpreuve();
                typeEpreuve.setId(rs.getInt("id"));
                typeEpreuve.setLibelle(rs.getString("libelle"));
                types.add(typeEpreuve);
            }
        } catch (SQLException e) {
            System.err.println("Erreur listage types épreuve: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return types;
    }
    
    // UPDATE
    public boolean modifier(TypeEpreuve typeEpreuve) {
        String sql = "UPDATE type_epreuve SET libelle = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, typeEpreuve.getLibelle());
            pstmt.setInt(2, typeEpreuve.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modification type épreuve: " + e.getMessage());
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
        String sql = "DELETE FROM type_epreuve WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur suppression type épreuve: " + e.getMessage());
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