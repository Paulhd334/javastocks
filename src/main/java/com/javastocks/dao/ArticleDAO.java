package com.javastocks.dao;

import com.javastocks.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ArticleDAO {
    
    public int creer(Article article) {
        String sql = "INSERT INTO article (libelle, quantite, indicateur_sl, categorie, taille, couleur, volume, poids, seuil_reappro) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, article.getLibelle());
            pstmt.setInt(2, article.getQuantite());
            pstmt.setBoolean(3, article.isIndicateurSL());
            
            if (article instanceof Textile) {
                pstmt.setString(4, "T");
                pstmt.setString(5, ((Textile) article).getTaille());
                pstmt.setString(6, ((Textile) article).getCouleur());
                pstmt.setNull(7, Types.NUMERIC);
                pstmt.setNull(8, Types.NUMERIC);
            } else if (article instanceof Boisson) {
                pstmt.setString(4, "B");
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setNull(6, Types.VARCHAR);
                BigDecimal volume = ((Boisson) article).getVolume();
                pstmt.setBigDecimal(7, volume);
                pstmt.setNull(8, Types.NUMERIC);
            } else if (article instanceof DenreeSeche) {
                pstmt.setString(4, "DS");
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.NUMERIC);
                BigDecimal poids = ((DenreeSeche) article).getPoids();
                pstmt.setBigDecimal(8, poids);
            } else {
                return -1;
            }
            
            pstmt.setInt(9, article.getSeuilReappro());
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                article.setId(id);
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    
    public Article consulter(int id) {
        String sql = "SELECT * FROM article WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToArticle(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public boolean modifier(Article article) {
        String sql = "UPDATE article SET libelle = ?, quantite = ?, indicateur_sl = ?, " +
                    "taille = ?, couleur = ?, volume = ?, poids = ?, seuil_reappro = ? " +
                    "WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, article.getLibelle());
            pstmt.setInt(2, article.getQuantite());
            pstmt.setBoolean(3, article.isIndicateurSL());
            
            if (article instanceof Textile) {
                pstmt.setString(4, ((Textile) article).getTaille());
                pstmt.setString(5, ((Textile) article).getCouleur());
                pstmt.setNull(6, Types.NUMERIC);
                pstmt.setNull(7, Types.NUMERIC);
            } else if (article instanceof Boisson) {
                pstmt.setNull(4, Types.VARCHAR);
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setBigDecimal(6, ((Boisson) article).getVolume());
                pstmt.setNull(7, Types.NUMERIC);
            } else if (article instanceof DenreeSeche) {
                pstmt.setNull(4, Types.VARCHAR);
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setNull(6, Types.NUMERIC);
                pstmt.setBigDecimal(7, ((DenreeSeche) article).getPoids());
            } else {
                return false;
            }
            
            pstmt.setInt(8, article.getSeuilReappro());
            pstmt.setInt(9, article.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean supprimer(int id) {
        String sql = "UPDATE article SET indicateur_sl = TRUE WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<Article> listerTous() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article WHERE indicateur_sl = FALSE ORDER BY id";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Article article = mapResultSetToArticle(rs);
                if (article != null) {
                    articles.add(article);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return articles;
    }
    
    public List<Article> listerEnRupture() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article WHERE indicateur_sl = FALSE AND quantite < seuil_reappro ORDER BY id";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Article article = mapResultSetToArticle(rs);
                if (article != null) {
                    articles.add(article);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return articles;
    }
    
    // ========== METHODE AJOUTEE POUR CORRIGER LES ERREURS ==========
    public boolean mettreAJourStock(int articleId, int nouvelleQuantite) {
        String sql = "UPDATE article SET quantite = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, nouvelleQuantite);
            pstmt.setInt(2, articleId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private Article mapResultSetToArticle(ResultSet rs) throws SQLException {
        String categorie = rs.getString("categorie");
        Article article = null;
        
        if ("T".equals(categorie)) {
            Textile textile = new Textile();
            textile.setTaille(rs.getString("taille"));
            textile.setCouleur(rs.getString("couleur"));
            article = textile;
        } else if ("B".equals(categorie)) {
            Boisson boisson = new Boisson();
            boisson.setVolume(rs.getBigDecimal("volume"));
            article = boisson;
        } else if ("DS".equals(categorie)) {
            DenreeSeche denree = new DenreeSeche();
            denree.setPoids(rs.getBigDecimal("poids"));
            article = denree;
        }
        
        if (article != null) {
            article.setId(rs.getInt("id"));
            article.setLibelle(rs.getString("libelle"));
            article.setQuantite(rs.getInt("quantite"));
            article.setIndicateurSL(rs.getBoolean("indicateur_sl"));
            // article.setSeuilReappro(rs.getInt("seuil_reappro"));  OFF POUR EVITER LES PROBLEMES DE SEUIL REAPPRO NON INITIALISE POUR LES ARTICLES NON ALIMENTAIRES
        }
        
        return article;
    }
}