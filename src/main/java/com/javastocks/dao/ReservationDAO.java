package com.javastocks.dao;

import com.javastocks.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationDAO {
    
    private ArticleDAO articleDAO = new ArticleDAO();
    private CoureurDAO coureurDAO = new CoureurDAO();
    private TypeEpreuveDAO typeEpreuveDAO = new TypeEpreuveDAO();
    
    // ==================== CREATE ====================
    public int creer(Reservation reservation) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Debut de transaction
            
            // 1. Verifier la disponibilite des stocks et determiner le statut
            boolean stockSuffisant = true;
            for (Reservation.LigneReservation ligne : reservation.getLignes()) {
                Article article = articleDAO.consulter(ligne.getArticle().getId());
                if (article == null) {
                    throw new SQLException("Article ID " + ligne.getArticle().getId() + " inexistant");
                }
                if (article.getQuantite() < ligne.getQuantite()) {
                    stockSuffisant = false;
                    break;
                }
            }
            
            // 2. Definir le statut
            String statut = stockSuffisant ? "validee" : "en_attente";
            reservation.setStatut(statut);
            
            // 3. Inserer la reservation
            String sqlReservation = "INSERT INTO reservation (date_reservation, coureur_id, type_epreuve_id, statut) " +
                                   "VALUES (?, ?, ?, ?) RETURNING id";
            pstmt = conn.prepareStatement(sqlReservation);
            pstmt.setDate(1, new java.sql.Date(reservation.getDate().getTime()));
            pstmt.setInt(2, reservation.getCoureur().getId());
            pstmt.setInt(3, reservation.getTypeEpreuve().getId());
            pstmt.setString(4, statut);
            
            rs = pstmt.executeQuery();
            int reservationId = -1;
            if (rs.next()) {
                reservationId = rs.getInt(1);
                reservation.setId(reservationId);
            }
            
            // 4. Inserer les lignes de reservation
            if (reservationId != -1) {
                String sqlLigne = "INSERT INTO reservation_article (reservation_id, article_id, quantite) " +
                                 "VALUES (?, ?, ?)";
                pstmt = conn.prepareStatement(sqlLigne);
                
                for (Reservation.LigneReservation ligne : reservation.getLignes()) {
                    pstmt.setInt(1, reservationId);
                    pstmt.setInt(2, ligne.getArticle().getId());
                    pstmt.setInt(3, ligne.getQuantite());
                    pstmt.executeUpdate();
                    
                    // Si reservation validee, mettre a jour le stock
                    if ("validee".equals(statut)) {
                        Article article = articleDAO.consulter(ligne.getArticle().getId());
                        int nouveauStock = article.getQuantite() - ligne.getQuantite();
                        articleDAO.mettreAJourStock(article.getId(), nouveauStock);
                        
                        // Verifier si le nouveau stock passe sous le seuil
                        if (nouveauStock < article.getSeuilReappro()) {
                            System.out.println("ALERTE: L'article '" + article.getLibelle() + 
                                             "' est en dessous du seuil de reapprovisionnement!");
                        }
                    }
                }
            }
            
            conn.commit(); // Validation de la transaction
            reservation.creer(); // Appel methode generique
            System.out.println("Reservation creee avec succes! ID: " + reservationId + " (Statut: " + statut + ")");
            return reservationId;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Annulation en cas d'erreur
                    System.err.println("Transaction annulee");
                }
            } catch (SQLException ex) {
                System.err.println("Erreur lors du rollback: " + ex.getMessage());
            }
            System.err.println("Erreur lors de la creation de la reservation: " + e.getMessage());
            return -1;
        } finally {
            // Nettoyage des ressources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restauration auto-commit
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture des ressources: " + e.getMessage());
            }
        }
    }
    
    // ==================== READ ====================
    public Reservation consulter(int id) {
        String sql = "SELECT r.*, c.nom as c_nom, c.prenom as c_prenom, te.libelle as te_libelle " +
                    "FROM reservation r " +
                    "JOIN coureur c ON r.coureur_id = c.id " +
                    "JOIN type_epreuve te ON r.type_epreuve_id = te.id " +
                    "WHERE r.id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                reservation.setDate(rs.getDate("date_reservation"));
                reservation.setStatut(rs.getString("statut"));
                
                // Recuperer le coureur
                Coureur coureur = new Coureur();
                coureur.setId(rs.getInt("coureur_id"));
                coureur.setNom(rs.getString("c_nom"));
                coureur.setPrenom(rs.getString("c_prenom"));
                reservation.setCoureur(coureur);
                
                // Recuperer le type d'epreuve
                TypeEpreuve typeEpreuve = new TypeEpreuve();
                typeEpreuve.setId(rs.getInt("type_epreuve_id"));
                typeEpreuve.setLibelle(rs.getString("te_libelle"));
                reservation.setTypeEpreuve(typeEpreuve);
                
                // Charger les lignes de reservation
                chargerLignesReservation(reservation);
                
                reservation.consulter(); // Appel methode generique
                return reservation;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la consultation de la reservation: " + e.getMessage());
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
    
    private void chargerLignesReservation(Reservation reservation) {
        String sql = "SELECT ra.*, a.libelle, a.categorie, a.taille, a.couleur, a.volume, a.poids, a.quantite as stock_actuel " +
                    "FROM reservation_article ra " +
                    "JOIN article a ON ra.article_id = a.id " +
                    "WHERE ra.reservation_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservation.getId());
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Article article = mapToArticle(rs);
                int quantite = rs.getInt("quantite");
                reservation.ajouterLigne(article, quantite);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des lignes: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
    }
    
    // CORRECTION ICI - Utiliser les codes B, T, DS et BigDecimal
    private Article mapToArticle(ResultSet rs) throws SQLException {
        String categorie = rs.getString("categorie");
        Article article = null;
        
        if ("T".equals(categorie)) {  // Textile
            Textile textile = new Textile();
            textile.setTaille(rs.getString("taille"));
            textile.setCouleur(rs.getString("couleur"));
            article = textile;
        } else if ("B".equals(categorie)) {  // Boisson
            Boisson boisson = new Boisson();
            boisson.setVolume(rs.getBigDecimal("volume"));  // CORRIGE: getBigDecimal
            article = boisson;
        } else if ("DS".equals(categorie)) {  // Denrée Sèche
            DenreeSeche denree = new DenreeSeche();
            denree.setPoids(rs.getBigDecimal("poids"));  // CORRIGE: getBigDecimal au lieu de getInt
            article = denree;
        }
        
        if (article != null) {
            article.setId(rs.getInt("article_id"));
            article.setLibelle(rs.getString("libelle"));
            article.setQuantite(rs.getInt("stock_actuel"));
        }
        
        return article;
    }
    
    // ==================== LISTES ====================
    public List<Reservation> listerToutes() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT id FROM reservation ORDER BY date_reservation DESC";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Reservation reservation = consulter(rs.getInt("id"));
                if (reservation != null) {
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage des reservations: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return reservations;
    }
    
    public List<Reservation> listerReservationsEnAttente() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT id FROM reservation WHERE statut = 'en_attente' ORDER BY date_reservation";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Reservation reservation = consulter(rs.getInt("id"));
                if (reservation != null) {
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage des reservations en attente: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return reservations;
    }
    
    public List<Reservation> listerParDate(Date date) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT id FROM reservation WHERE date_reservation = ? ORDER BY id";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, new java.sql.Date(date.getTime()));
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Reservation reservation = consulter(rs.getInt("id"));
                if (reservation != null) {
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage par date: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return reservations;
    }
    
    public List<Reservation> listerParCoureur(int coureurId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT id FROM reservation WHERE coureur_id = ? ORDER BY date_reservation DESC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, coureurId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Reservation reservation = consulter(rs.getInt("id"));
                if (reservation != null) {
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage par coureur: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return reservations;
    }
    
    // ==================== UPDATE ====================
    public boolean validerReservation(int reservationId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            Reservation reservation = consulter(reservationId);
            if (reservation == null) {
                System.err.println("Reservation introuvable: " + reservationId);
                return false;
            }
            
            if (!"en_attente".equals(reservation.getStatut())) {
                System.err.println("La reservation n'est pas en attente (statut: " + reservation.getStatut() + ")");
                return false;
            }
            
            // Verifier les stocks
            for (Reservation.LigneReservation ligne : reservation.getLignes()) {
                Article article = articleDAO.consulter(ligne.getArticle().getId());
                if (article == null) {
                    throw new SQLException("Article ID " + ligne.getArticle().getId() + " inexistant");
                }
                if (article.getQuantite() < ligne.getQuantite()) {
                    System.err.println("Stock insuffisant pour l'article: " + article.getLibelle() +
                                     " (dispo: " + article.getQuantite() + ", demande: " + ligne.getQuantite() + ")");
                    return false;
                }
            }
            
            // Mettre a jour les stocks
            for (Reservation.LigneReservation ligne : reservation.getLignes()) {
                Article article = articleDAO.consulter(ligne.getArticle().getId());
                int nouveauStock = article.getQuantite() - ligne.getQuantite();
                articleDAO.mettreAJourStock(article.getId(), nouveauStock);
            }
            
            // Mettre a jour le statut de la reservation
            String sql = "UPDATE reservation SET statut = 'validee' WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            int rows = pstmt.executeUpdate();
            
            conn.commit();
            
            if (rows > 0) {
                System.out.println("Reservation " + reservationId + " validee avec succes!");
                return true;
            }
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Erreur rollback: " + ex.getMessage());
            }
            System.err.println("Erreur lors de la validation: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return false;
    }
    
    public boolean annulerReservation(int reservationId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            Reservation reservation = consulter(reservationId);
            if (reservation == null) return false;
            
            // Si la reservation etait validee, remettre les stocks
            if ("validee".equals(reservation.getStatut())) {
                for (Reservation.LigneReservation ligne : reservation.getLignes()) {
                    Article article = articleDAO.consulter(ligne.getArticle().getId());
                    int nouveauStock = article.getQuantite() + ligne.getQuantite();
                    articleDAO.mettreAJourStock(article.getId(), nouveauStock);
                }
            }
            
            // Supprimer la reservation (physiquement)
            String sql = "DELETE FROM reservation_article WHERE reservation_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            pstmt.executeUpdate();
            
            sql = "DELETE FROM reservation WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            int rows = pstmt.executeUpdate();
            
            conn.commit();
            
            if (rows > 0) {
                System.out.println("Reservation " + reservationId + " annulee avec succes!");
                return true;
            }
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Erreur rollback: " + ex.getMessage());
            }
            System.err.println("Erreur lors de l'annulation: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return false;
    }
    
    // ==================== STATISTIQUES / HISTORIQUE ====================
    public int compterReservationsParDate(Date date) {
        String sql = "SELECT COUNT(*) FROM reservation WHERE date_reservation = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, new java.sql.Date(date.getTime()));
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur comptage par date: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return 0;
    }
    
    public int compterReservationsParCoureur(int coureurId) {
        String sql = "SELECT COUNT(*) FROM reservation WHERE coureur_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, coureurId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur comptage par coureur: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return 0;
    }
    
    public int compterReservationsParTypeEpreuve(int typeEpreuveId) {
        String sql = "SELECT COUNT(*) FROM reservation WHERE type_epreuve_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, typeEpreuveId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur comptage par type epreuve: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return 0;
    }
    
    public int compterReservationsParDateEtType(Date date, int typeEpreuveId) {
        String sql = "SELECT COUNT(*) FROM reservation WHERE date_reservation = ? AND type_epreuve_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, new java.sql.Date(date.getTime()));
            pstmt.setInt(2, typeEpreuveId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur comptage par date et type: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture ressources: " + e.getMessage());
            }
        }
        return 0;
    }
}