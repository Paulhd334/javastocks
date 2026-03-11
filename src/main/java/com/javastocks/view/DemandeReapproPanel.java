package com.javastocks.view;

import com.javastocks.dao.*;
import com.javastocks.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class DemandeReapproPanel extends JPanel {
    
    private MainFrame mainFrame;
    private DemandeReapproDAO demandeReapproDAO;
    private ArticleDAO articleDAO;
    private FournisseurDAO fournisseurDAO;
    private PointLivraisonDAO pointLivraisonDAO;
    
    // Composants UI
    private JTable tableDemandes;
    private DefaultTableModel tableModel;
    
    private JComboBox<Fournisseur> cmbFournisseur;
    private JComboBox<PointLivraison> cmbPointLivraison;
    private JComboBox<String> cmbMotif;
    private JTable tableArticles;
    private DefaultTableModel articleModel;
    private JButton btnAjouterLigne, btnRetirerLigne, btnCreerDemande, btnValider, btnAnnuler;
    private JLabel lblTotalArticles, lblTotalQuantite;
    
    private List<DemandeReappro.LigneDemande> panier;
    
    public DemandeReapproPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.demandeReapproDAO = new DemandeReapproDAO();
        this.articleDAO = new ArticleDAO();
        this.fournisseurDAO = new FournisseurDAO();
        this.pointLivraisonDAO = new PointLivraisonDAO();
        this.panier = new ArrayList<>();
        
        setLayout(new BorderLayout());
        
        // Titre
        JLabel title = new JLabel("Gestion des Demandes de Réapprovisionnement", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(title, BorderLayout.NORTH);
        
        // Panel principal avec onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Nouvelle demande", createNouvelleDemandePanel());
        tabbedPane.addTab("Liste des demandes", createListeDemandesPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createNouvelleDemandePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel entête
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("Informations générales"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fournisseur
        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(new JLabel("Fournisseur:"), gbc);
        
        cmbFournisseur = new JComboBox<>();
        chargerFournisseurs();
        gbc.gridx = 1; gbc.gridwidth = 3;
        headerPanel.add(cmbFournisseur, gbc);
        
        // Point de livraison
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        headerPanel.add(new JLabel("Point de livraison:"), gbc);
        
        cmbPointLivraison = new JComboBox<>();
        chargerPointsLivraison();
        gbc.gridx = 1; gbc.gridwidth = 3;
        headerPanel.add(cmbPointLivraison, gbc);
        
        // Motif
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        headerPanel.add(new JLabel("Motif:"), gbc);
        
        String[] motifs = {"R - Réapprovisionnement", "NP - Nouveaux produits", "UR - Urgence réapprovisionnement"};
        cmbMotif = new JComboBox<>(motifs);
        gbc.gridx = 1; gbc.gridwidth = 3;
        headerPanel.add(cmbMotif, gbc);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Panel central
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Articles à commander"));
        
        // Sélection d'article
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectPanel.add(new JLabel("Article:"));
        
        JComboBox<Article> cmbArticle = new JComboBox<>();
        chargerArticles(cmbArticle);
        selectPanel.add(cmbArticle);
        
        selectPanel.add(new JLabel("Quantité:"));
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
        String[] columns = {"ID", "Libellé", "Catégorie", "Stock actuel", "Quantité"};
        articleModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableArticles = new JTable(articleModel);
        tableArticles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        btnRetirerLigne = new JButton("Retirer la ligne sélectionnée");
        btnRetirerLigne.addActionListener(e -> retirerLigne());
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(tableArticles), BorderLayout.CENTER);
        tablePanel.add(btnRetirerLigne, BorderLayout.SOUTH);
        
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Récapitulatif
        JPanel recapPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotalArticles = new JLabel("Total articles: 0");
        lblTotalQuantite = new JLabel("Total quantités: 0");
        recapPanel.add(lblTotalArticles);
        recapPanel.add(Box.createHorizontalStrut(20));
        recapPanel.add(lblTotalQuantite);
        
        centerPanel.add(recapPanel, BorderLayout.SOUTH);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        btnCreerDemande = new JButton("Créer la demande");
        btnCreerDemande.addActionListener(e -> creerDemande());
        
        btnAnnuler = new JButton("Vider le panier");
        btnAnnuler.addActionListener(e -> viderPanier());
        
        bottomPanel.add(btnCreerDemande);
        bottomPanel.add(btnAnnuler);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createListeDemandesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tableau des demandes
        String[] columns = {"ID", "Date", "Fournisseur", "Point livraison", "Motif", "N° Commande"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableDemandes = new JTable(tableModel);
        tableDemandes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tableDemandes);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton btnDetails = new JButton("Voir détails");
        btnDetails.addActionListener(e -> voirDetailsDemande());
        
        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> supprimerDemande());
        
        JButton btnActualiser = new JButton("Actualiser");
        btnActualiser.addActionListener(e -> chargerDemandes());
        
        buttonPanel.add(btnDetails);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnActualiser);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Charger les demandes
        chargerDemandes();
        
        return panel;
    }
    
    private void chargerFournisseurs() {
        cmbFournisseur.removeAllItems();
        List<Fournisseur> fournisseurs = fournisseurDAO.listerTous();
        for (Fournisseur f : fournisseurs) {
            cmbFournisseur.addItem(f);
        }
    }
    
    private void chargerPointsLivraison() {
        cmbPointLivraison.removeAllItems();
        List<PointLivraison> points = pointLivraisonDAO.listerTous();
        for (PointLivraison p : points) {
            cmbPointLivraison.addItem(p);
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
        
        // Vérifier doublon
        for (DemandeReappro.LigneDemande ligne : panier) {
            if (ligne.getArticle().getId() == article.getId()) {
                JOptionPane.showMessageDialog(this,
                    "Cet article est déjà dans la liste",
                    "Attention",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        panier.add(new DemandeReappro.LigneDemande(article, quantite));
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
        
        for (DemandeReappro.LigneDemande ligne : panier) {
            Article a = ligne.getArticle();
            articleModel.addRow(new Object[]{
                a.getId(),
                a.getLibelle(),
                a instanceof Boisson ? "Boisson" :
                a instanceof Textile ? "Textile" : "Denrée",
                a.getQuantite(),
                ligne.getQuantite()
            });
            totalQte += ligne.getQuantite();
        }
        
        lblTotalArticles.setText("Total articles: " + panier.size());
        lblTotalQuantite.setText("Total quantités: " + totalQte);
    }
    
    private void viderPanier() {
        panier.clear();
        actualiserPanier();
    }
    
    private void creerDemande() {
        if (cmbFournisseur.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un fournisseur");
            return;
        }
        if (cmbPointLivraison.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un point de livraison");
            return;
        }
        if (panier.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ajoutez au moins un article");
            return;
        }
        
        try {
            String motifComplet = (String) cmbMotif.getSelectedItem();
            String motif = motifComplet.substring(0, motifComplet.indexOf(" -"));
            
            Fournisseur fournisseur = (Fournisseur) cmbFournisseur.getSelectedItem();
            PointLivraison point = (PointLivraison) cmbPointLivraison.getSelectedItem();
            
            DemandeReappro demande = new DemandeReappro(motif, new Date(), fournisseur, point);
            
            // Ajouter les lignes
            for (DemandeReappro.LigneDemande ligne : panier) {
                demande.ajouterLigne(ligne.getArticle(), ligne.getQuantite());
            }
            
            int id = demandeReapproDAO.creer(demande);
            
            if (id > 0) {
                demande.setId(id);
                demande.creer(); // Appel méthode générique
                
                JOptionPane.showMessageDialog(this,
                    "Demande de réapprovisionnement créée avec succès!\nNuméro: " + id);
                viderPanier();
                chargerDemandes();
                
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
    
    private void chargerDemandes() {
        tableModel.setRowCount(0);
        List<DemandeReappro> demandes = demandeReapproDAO.listerToutes();
        
        for (DemandeReappro d : demandes) {
            tableModel.addRow(new Object[]{
                d.getId(),
                d.getDate(),
                d.getFournisseur().getNom(),
                d.getPointLivraison().getNom(),
                d.getMotif(),
                d.getNumeroCommande()
            });
        }
    }
    
    private void voirDetailsDemande() {
        int selectedRow = tableDemandes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une demande");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        DemandeReappro demande = demandeReapproDAO.consulter(id);
        
        if (demande != null) {
            StringBuilder details = new StringBuilder();
            details.append("Demande n° ").append(demande.getId()).append("\n");
            details.append("Date: ").append(demande.getDate()).append("\n");
            details.append("Fournisseur: ").append(demande.getFournisseur().getNom()).append("\n");
            details.append("Point livraison: ").append(demande.getPointLivraison().getNom()).append("\n");
            details.append("Motif: ").append(demande.getMotif()).append("\n");
            details.append("N° commande: ").append(demande.getNumeroCommande()).append("\n\n");
            details.append("Articles:\n");
            
            for (DemandeReappro.LigneDemande ligne : demande.getLignes()) {
                details.append("  - ").append(ligne.getArticle().getLibelle())
                       .append(" x ").append(ligne.getQuantite()).append("\n");
            }
            
            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Détails de la demande", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void supprimerDemande() {
        int selectedRow = tableDemandes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une demande");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Supprimer cette demande ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (demandeReapproDAO.supprimer(id)) {
                JOptionPane.showMessageDialog(this, "Demande supprimée");
                chargerDemandes();
            }
        }
    }
}