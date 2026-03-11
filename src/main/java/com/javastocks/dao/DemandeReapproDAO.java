package com.javastocks.dao;

import com.javastocks.model.*;
import java.sql.*;
import java.util.*;

public class DemandeReapproDAO {
    
    // CREATE
    public int creer(DemandeReappro demande) {
        String sql = "INSERT INTO demande_reappro (motif, date_demande, numero_commande, fournisseur_id, point_livraison_id) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // Générer un numéro de commande
            java.util.Date utilDate = new java.util.Date();
            String numeroCommande = "CMD-" + new java.text.SimpleDateFormat("yyyyMMdd").format(utilDate) + 
                                   "-" + (System.currentTimeMillis() % 1000);
            demande.setNumeroCommande(numeroCommande);
            
            pstmt.setString(1, demande.getMotif());
            pstmt.setDate(2, new java.sql.Date(utilDate.getTime())); // date_demande
            pstmt.setString(3, demande.getNumeroCommande());
            pstmt.setInt(4, demande.getFournisseur().getId());
            pstmt.setInt(5, demande.getPointLivraison().getId());
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt(1);
                
                // Insérer les lignes de la demande
                String sqlLigne = "INSERT INTO demande_article (demande_id, article_id, quantite) VALUES (?, ?, ?)";
                PreparedStatement pstmtLigne = conn.prepareStatement(sqlLigne);
                
                for (DemandeReappro.LigneDemande ligne : demande.getLignes()) {
                    pstmtLigne.setInt(1, id);
                    pstmtLigne.setInt(2, ligne.getArticle().getId());
                    pstmtLigne.setInt(3, ligne.getQuantite());
                    pstmtLigne.executeUpdate();
                }
                pstmtLigne.close();
                
                return id;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur création demande: " + e.getMessage());
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
    public DemandeReappro consulter(int id) {
        String sql = "SELECT d.*, f.nom as f_nom, f.rue as f_rue, f.cp as f_cp, f.ville as f_ville, " +
                    "f.tel as f_tel, f.email as f_email, " +
                    "p.nom as p_nom, p.rue as p_rue, p.cp as p_cp, p.ville as p_ville, " +
                    "p.tel as p_tel, p.email as p_email " +
                    "FROM demande_reappro d " +
                    "JOIN fournisseur f ON d.fournisseur_id = f.id " +
                    "JOIN point_livraison p ON d.point_livraison_id = p.id " +
                    "WHERE d.id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Créer le fournisseur
                Fournisseur fournisseur = new Fournisseur();
                fournisseur.setId(rs.getInt("fournisseur_id"));
                fournisseur.setNom(rs.getString("f_nom"));
                fournisseur.setRue(rs.getString("f_rue"));
                fournisseur.setCp(rs.getString("f_cp"));
                fournisseur.setVille(rs.getString("f_ville"));
                fournisseur.setTel(rs.getString("f_tel"));
                fournisseur.setEmail(rs.getString("f_email"));
                
                // Créer le point de livraison
                PointLivraison point = new PointLivraison();
                point.setId(rs.getInt("point_livraison_id"));
                point.setNom(rs.getString("p_nom"));
                point.setRue(rs.getString("p_rue"));
                point.setCp(rs.getString("p_cp"));
                point.setVille(rs.getString("p_ville"));
                point.setTel(rs.getString("p_tel"));
                point.setEmail(rs.getString("p_email"));
                
                // Créer la demande
                DemandeReappro demande = new DemandeReappro(
                    rs.getString("motif"),
                    rs.getDate("date_demande"),
                    fournisseur,
                    point
                );
                demande.setId(rs.getInt("id"));
                demande.setNumeroCommande(rs.getString("numero_commande"));
                
                // Charger les lignes
                chargerLignes(demande);
                
                return demande;
            }
        } catch (SQLException e) {
            System.err.println("Erreur consultation demande: " + e.getMessage());
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
    
    private void chargerLignes(DemandeReappro demande) {
        String sql = "SELECT da.*, a.libelle, a.categorie " +
                    "FROM demande_article da " +
                    "JOIN article a ON da.article_id = a.id " +
                    "WHERE da.demande_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, demande.getId());
            rs = pstmt.executeQuery();
            
            ArticleDAO articleDAO = new ArticleDAO();
            
            while (rs.next()) {
                Article article = articleDAO.consulter(rs.getInt("article_id"));
                if (article != null) {
                    int quantite = rs.getInt("quantite");
                    demande.ajouterLigne(article, quantite);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur chargement lignes: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
    }
    
    // READ - toutes
    public List<DemandeReappro> listerToutes() {
        List<DemandeReappro> demandes = new ArrayList<>();
        String sql = "SELECT id FROM demande_reappro ORDER BY date_demande DESC";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                DemandeReappro demande = consulter(rs.getInt("id"));
                if (demande != null) {
                    demandes.add(demande);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur listage demandes: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return demandes;
    }
    
    // DELETE
    public boolean supprimer(int id) {
        String sql = "DELETE FROM demande_article WHERE demande_id = ?";
        String sql2 = "DELETE FROM demande_reappro WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Supprimer les lignes d'abord
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
            
            // Supprimer la demande
            pstmt = conn.prepareStatement(sql2);
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            
            conn.commit();
            return rows > 0;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Erreur rollback: " + ex.getMessage());
            }
            System.err.println("Erreur suppression demande: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
    }
}