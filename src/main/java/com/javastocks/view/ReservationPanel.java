package com.javastocks.view;

import com.javastocks.dao.*;
import com.javastocks.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class ReservationPanel extends JPanel {
    
    private MainFrame mainFrame;
    private ReservationDAO reservationDAO;
    private CoureurDAO coureurDAO;
    private TypeEpreuveDAO typeEpreuveDAO;
    private ArticleDAO articleDAO;
    
    // Composants UI
    private JTable tableReservations;
    private DefaultTableModel tableModel;
    
    private JComboBox<Coureur> cmbCoureur;
    private JComboBox<TypeEpreuve> cmbTypeEpreuve;
    private JSpinner spinnerDate;
    private JTable tableArticles;
    private DefaultTableModel articleModel;
    private JButton btnAjouterLigne, btnRetirerLigne, btnCreerReservation, btnValider, btnAnnuler;
    private JLabel lblTotalArticles, lblTotalQuantite;
    
    private List<LigneReservationTemp> panier;
    
    // Classe temporaire pour les lignes de reservation
    private class LigneReservationTemp {
        Article article;
        int quantite;
        
        LigneReservationTemp(Article article, int quantite) {
            this.article = article;
            this.quantite = quantite;
        }
    }
    
    public ReservationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.reservationDAO = new ReservationDAO();
        this.coureurDAO = new CoureurDAO();
        this.typeEpreuveDAO = new TypeEpreuveDAO();
        this.articleDAO = new ArticleDAO();
        this.panier = new ArrayList<>();
        
        setLayout(new BorderLayout());
        
        // Titre
        JLabel title = new JLabel("Gestion des Reservations", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(title, BorderLayout.NORTH);
        
        // Panel principal avec onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Nouvelle reservation", createNouvelleReservationPanel());
        tabbedPane.addTab("Liste des reservations", createListeReservationsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createNouvelleReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel entete
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("Informations generales"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Date
        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(new JLabel("Date:"), gbc);
        
        spinnerDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerDate, "dd/MM/yyyy");
        spinnerDate.setEditor(dateEditor);
        spinnerDate.setValue(new Date());
        gbc.gridx = 1; gbc.gridwidth = 3;
        headerPanel.add(spinnerDate, gbc);
        
        // Coureur
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        headerPanel.add(new JLabel("Coureur:"), gbc);
        
        cmbCoureur = new JComboBox<>();
        chargerCoureurs();
        gbc.gridx = 1; gbc.gridwidth = 3;
        headerPanel.add(cmbCoureur, gbc);
        
        // Type d'epreuve
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        headerPanel.add(new JLabel("Type d'epreuve:"), gbc);
        
        cmbTypeEpreuve = new JComboBox<>();
        chargerTypesEpreuve();
        gbc.gridx = 1; gbc.gridwidth = 3;
        headerPanel.add(cmbTypeEpreuve, gbc);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Panel central
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Articles a reserver"));
        
        // Selection d'article
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectPanel.add(new JLabel("Article:"));
        
        JComboBox<Article> cmbArticle = new JComboBox<>();
        chargerArticles(cmbArticle);
        selectPanel.add(cmbArticle);
        
        selectPanel.add(new JLabel("Quantite:"));
        JSpinner spinnerQuantite = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        selectPanel.add(spinnerQuantite);
        
        btnAjouterLigne = new JButton("Ajouter");
        btnAjouterLigne.addActionListener(e -> {
            Article a = (Article) cmbArticle.getSelectedItem();
            int qte = (int) spinnerQuantite.getValue();
            ajouterLigne(a, qte);
        });
        selectPanel.add(btnAjouterLigne);
        
        centerPanel.add(selectPanel, BorderLayout.NORTH);
        
        // Table des articles
        String[] columns = {"ID", "Libelle", "Categorie", "Stock dispo", "Quantite"};
        articleModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableArticles = new JTable(articleModel);
        tableArticles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        btnRetirerLigne = new JButton("Retirer la ligne");
        btnRetirerLigne.addActionListener(e -> retirerLigne());
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(tableArticles), BorderLayout.CENTER);
        tablePanel.add(btnRetirerLigne, BorderLayout.SOUTH);
        
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Recapitulatif
        JPanel recapPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotalArticles = new JLabel("Total articles: 0");
        lblTotalQuantite = new JLabel("Total quantites: 0");
        recapPanel.add(lblTotalArticles);
        recapPanel.add(Box.createHorizontalStrut(20));
        recapPanel.add(lblTotalQuantite);
        
        centerPanel.add(recapPanel, BorderLayout.SOUTH);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        btnCreerReservation = new JButton("Creer la reservation");
        btnCreerReservation.addActionListener(e -> creerReservation());
        
        btnAnnuler = new JButton("Vider le panier");
        btnAnnuler.addActionListener(e -> viderPanier());
        
        bottomPanel.add(btnCreerReservation);
        bottomPanel.add(btnAnnuler);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createListeReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tableau des reservations
        String[] columns = {"ID", "Date", "Coureur", "Type epreuve", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableReservations = new JTable(tableModel);
        tableReservations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tableReservations);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton btnDetails = new JButton("Voir details");
        btnDetails.addActionListener(e -> voirDetailsReservation());
        
        btnValider = new JButton("Valider reservation");
        btnValider.addActionListener(e -> validerReservation());
        
        JButton btnSupprimer = new JButton("Annuler reservation");
        btnSupprimer.addActionListener(e -> annulerReservation());
        
        JButton btnActualiser = new JButton("Actualiser");
        btnActualiser.addActionListener(e -> chargerReservations());
        
        buttonPanel.add(btnDetails);
        buttonPanel.add(btnValider);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnActualiser);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Charger les reservations
        chargerReservations();
        
        return panel;
    }
    
    private void chargerCoureurs() {
        cmbCoureur.removeAllItems();
        List<Coureur> coureurs = coureurDAO.listerTous();
        for (Coureur c : coureurs) {
            cmbCoureur.addItem(c);
        }
    }
    
    private void chargerTypesEpreuve() {
        cmbTypeEpreuve.removeAllItems();
        List<TypeEpreuve> types = typeEpreuveDAO.listerTous();
        for (TypeEpreuve t : types) {
            cmbTypeEpreuve.addItem(t);
        }
    }
    
    private void chargerArticles(JComboBox<Article> cmb) {
        cmb.removeAllItems();
        List<Article> articles = articleDAO.listerTous();
        for (Article a : articles) {
            cmb.addItem(a);
        }
    }
    
    private void ajouterLigne(Article article, int quantite) {
        if (article == null) return;
        
        // Verifier si l'article est deja dans le panier
        for (LigneReservationTemp ligne : panier) {
            if (ligne.article.getId() == article.getId()) {
                JOptionPane.showMessageDialog(this,
                    "Cet article est deja dans la liste",
                    "Attention",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Verifier le stock
        if (quantite > article.getQuantite()) {
            JOptionPane.showMessageDialog(this,
                "Stock insuffisant! Disponible: " + article.getQuantite(),
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        panier.add(new LigneReservationTemp(article, quantite));
        actualiserPanier();
    }
    
    private void retirerLigne() {
        int selectedRow = tableArticles.getSelectedRow();
        if (selectedRow != -1) {
            panier.remove(selectedRow);
            actualiserPanier();
        }
    }
    
    private void actualiserPanier() {
        articleModel.setRowCount(0);
        int totalQte = 0;
        
        for (LigneReservationTemp ligne : panier) {
            Article a = ligne.article;
            articleModel.addRow(new Object[]{
                a.getId(),
                a.getLibelle(),
                a instanceof Boisson ? "Boisson" :
                a instanceof Textile ? "Textile" : "Denree",
                a.getQuantite(),
                ligne.quantite
            });
            totalQte += ligne.quantite;
        }
        
        lblTotalArticles.setText("Total articles: " + panier.size());
        lblTotalQuantite.setText("Total quantites: " + totalQte);
    }
    
    private void viderPanier() {
        panier.clear();
        actualiserPanier();
    }
    
    private void creerReservation() {
        if (cmbCoureur.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selectionnez un coureur");
            return;
        }
        if (cmbTypeEpreuve.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selectionnez un type d'epreuve");
            return;
        }
        if (panier.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ajoutez au moins un article");
            return;
        }
        
        try {
            Reservation reservation = new Reservation();
            reservation.setDate((Date) spinnerDate.getValue());
            reservation.setCoureur((Coureur) cmbCoureur.getSelectedItem());
            reservation.setTypeEpreuve((TypeEpreuve) cmbTypeEpreuve.getSelectedItem());
            
            // Ajouter les lignes
            for (LigneReservationTemp ligne : panier) {
                reservation.ajouterLigne(ligne.article, ligne.quantite);
            }
            
            int id = reservationDAO.creer(reservation);
            
            if (id > 0) {
                JOptionPane.showMessageDialog(this,
                    "Reservation creee avec succes!\nNumero: " + id + 
                    "\nStatut: " + reservation.getStatut());
                viderPanier();
                chargerReservations();
                
                // Basculer vers l'onglet liste
                ((JTabbedPane) getComponent(1)).setSelectedIndex(1);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void chargerReservations() {
        tableModel.setRowCount(0);
        List<Reservation> reservations = reservationDAO.listerToutes();
        
        for (Reservation r : reservations) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getDate(),
                r.getCoureur().getPrenom() + " " + r.getCoureur().getNom(),
                r.getTypeEpreuve().getLibelle(),
                r.getStatut()
            });
        }
    }
    
    private void voirDetailsReservation() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectionnez une reservation");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Reservation r = reservationDAO.consulter(id);
        
        if (r != null) {
            StringBuilder details = new StringBuilder();
            details.append("Reservation n° ").append(r.getId()).append("\n");
            details.append("Date: ").append(r.getDate()).append("\n");
            details.append("Coureur: ").append(r.getCoureur().getPrenom()).append(" ").append(r.getCoureur().getNom()).append("\n");
            details.append("Type epreuve: ").append(r.getTypeEpreuve().getLibelle()).append("\n");
            details.append("Statut: ").append(r.getStatut()).append("\n\n");
            details.append("Articles reserves:\n");
            
            for (Reservation.LigneReservation ligne : r.getLignes()) {
                details.append("  - ").append(ligne.getArticle().getLibelle())
                       .append(" x ").append(ligne.getQuantite()).append("\n");
            }
            
            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Details de la reservation", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void validerReservation() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectionnez une reservation");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String statut = (String) tableModel.getValueAt(selectedRow, 4);
        
        if (!"en_attente".equals(statut)) {
            JOptionPane.showMessageDialog(this, 
                "Seules les reservations en attente peuvent etre validees",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Valider cette reservation ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (reservationDAO.validerReservation(id)) {
                JOptionPane.showMessageDialog(this, "Reservation validee avec succes!");
                chargerReservations();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Impossible de valider: stock insuffisant",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void annulerReservation() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectionnez une reservation");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Annuler cette reservation ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (reservationDAO.annulerReservation(id)) {
                JOptionPane.showMessageDialog(this, "Reservation annulee");
                chargerReservations();
            }
        }
    }
}