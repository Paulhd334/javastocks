package com.javastocks.dao;

import com.javastocks.model.PointLivraison;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PointLivraisonDAO {
    
    // CREATE
    public int creer(PointLivraison pointLivraison) {
        String sql = "INSERT INTO point_livraison (nom, rue, cp, ville, tel, email) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pointLivraison.getNom());
            pstmt.setString(2, pointLivraison.getRue());
            pstmt.setString(3, pointLivraison.getCp());
            pstmt.setString(4, pointLivraison.getVille());
            pstmt.setString(5, pointLivraison.getTel());
            pstmt.setString(6, pointLivraison.getEmail());
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                pointLivraison.setId(id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("Erreur création point livraison: " + e.getMessage());
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
    public PointLivraison consulter(int id) {
        String sql = "SELECT * FROM point_livraison WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPointLivraison(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur consultation point livraison: " + e.getMessage());
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
    public List<PointLivraison> listerTous() {
        List<PointLivraison> points = new ArrayList<>();
        String sql = "SELECT * FROM point_livraison ORDER BY nom";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                points.add(mapResultSetToPointLivraison(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur listage points livraison: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return points;
    }
    
    // UPDATE
    public boolean modifier(PointLivraison pointLivraison) {
        String sql = "UPDATE point_livraison SET nom = ?, rue = ?, cp = ?, ville = ?, " +
                    "tel = ?, email = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pointLivraison.getNom());
            pstmt.setString(2, pointLivraison.getRue());
            pstmt.setString(3, pointLivraison.getCp());
            pstmt.setString(4, pointLivraison.getVille());
            pstmt.setString(5, pointLivraison.getTel());
            pstmt.setString(6, pointLivraison.getEmail());
            pstmt.setInt(7, pointLivraison.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modification point livraison: " + e.getMessage());
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
        String sql = "DELETE FROM point_livraison WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur suppression point livraison: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
    }
    
    // Recherche par ville (utile)
    public List<PointLivraison> rechercherParVille(String ville) {
        List<PointLivraison> points = new ArrayList<>();
        String sql = "SELECT * FROM point_livraison WHERE ville ILIKE ? ORDER BY nom";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + ville + "%");
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                points.add(mapResultSetToPointLivraison(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur recherche points livraison: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return points;
    }
    
    // Mapping ResultSet -> PointLivraison
    private PointLivraison mapResultSetToPointLivraison(ResultSet rs) throws SQLException {
        PointLivraison point = new PointLivraison();
        point.setId(rs.getInt("id"));
        point.setNom(rs.getString("nom"));
        point.setRue(rs.getString("rue"));
        point.setCp(rs.getString("cp"));
        point.setVille(rs.getString("ville"));
        point.setTel(rs.getString("tel"));
        point.setEmail(rs.getString("email"));
        return point;
    }
}