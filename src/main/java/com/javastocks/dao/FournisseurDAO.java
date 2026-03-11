package com.javastocks.dao;

import com.javastocks.model.Fournisseur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO {
    
    // CREATE
    public int creer(Fournisseur fournisseur) {
        String sql = "INSERT INTO fournisseur (nom, rue, cp, ville, tel, email) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fournisseur.getNom());
            pstmt.setString(2, fournisseur.getRue());
            pstmt.setString(3, fournisseur.getCp());
            pstmt.setString(4, fournisseur.getVille());
            pstmt.setString(5, fournisseur.getTel());
            pstmt.setString(6, fournisseur.getEmail());
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                fournisseur.setId(id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("Erreur création fournisseur: " + e.getMessage());
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
    public Fournisseur consulter(int id) {
        String sql = "SELECT * FROM fournisseur WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToFournisseur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur consultation fournisseur: " + e.getMessage());
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
    public List<Fournisseur> listerTous() {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur ORDER BY nom";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                fournisseurs.add(mapResultSetToFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur listage fournisseurs: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return fournisseurs;
    }
    
    // UPDATE
    public boolean modifier(Fournisseur fournisseur) {
        String sql = "UPDATE fournisseur SET nom = ?, rue = ?, cp = ?, ville = ?, " +
                    "tel = ?, email = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fournisseur.getNom());
            pstmt.setString(2, fournisseur.getRue());
            pstmt.setString(3, fournisseur.getCp());
            pstmt.setString(4, fournisseur.getVille());
            pstmt.setString(5, fournisseur.getTel());
            pstmt.setString(6, fournisseur.getEmail());
            pstmt.setInt(7, fournisseur.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modification fournisseur: " + e.getMessage());
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
        String sql = "DELETE FROM fournisseur WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur suppression fournisseur: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
    }
    
    // Recherche par nom (utile pour l'interface)
    public List<Fournisseur> rechercherParNom(String nom) {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur WHERE nom ILIKE ? ORDER BY nom";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + nom + "%");
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                fournisseurs.add(mapResultSetToFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur recherche fournisseurs: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return fournisseurs;
    }
    
    // Mapping ResultSet -> Fournisseur
    private Fournisseur mapResultSetToFournisseur(ResultSet rs) throws SQLException {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(rs.getInt("id"));
        fournisseur.setNom(rs.getString("nom"));
        fournisseur.setRue(rs.getString("rue"));
        fournisseur.setCp(rs.getString("cp"));
        fournisseur.setVille(rs.getString("ville"));
        fournisseur.setTel(rs.getString("tel"));
        fournisseur.setEmail(rs.getString("email"));
        return fournisseur;
    }
}