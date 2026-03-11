package com.javastocks.view;

import com.javastocks.dao.ArticleDAO;
import com.javastocks.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal; // IMPORTANT: Ajouter cet import
import java.util.List;

public class ArticlePanel extends JPanel {
    
    private ArticleDAO articleDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtLibelle, txtQuantite, txtTaille, txtCouleur, txtVolume, txtPoids;
    private JComboBox<String> comboCategories;
    private JButton btnAjouter, btnModifier, btnConsulter, btnSupprimer, btnActualiser;
    private MainFrame mainFrame;
    
    public ArticlePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.articleDAO = new ArticleDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        chargerArticles();
    }
    
    private void initComponents() {
        // Panneau du titre
        JLabel titleLabel = new JLabel("GESTION DES ARTICLES", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);
        
        // Panneau central avec la table
        String[] columns = {"ID", "Libellé", "Catégorie", "Taille", "Couleur", "Volume (cl)", "Poids (g)", "Quantité", "Seuil"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                remplirChampsDepuisSelection();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        add(scrollPane, BorderLayout.CENTER);
        
        // Panneau de saisie
        JPanel saisiePanel = new JPanel(new GridBagLayout());
        saisiePanel.setBorder(BorderFactory.createTitledBorder("Saisie article"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Libellé
        gbc.gridx = 0; gbc.gridy = 0;
        saisiePanel.add(new JLabel("Libellé:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        txtLibelle = new JTextField(20);
        saisiePanel.add(txtLibelle, gbc);
        
        // Catégorie
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        saisiePanel.add(new JLabel("Catégorie:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        comboCategories = new JComboBox<>(new String[]{"Textile", "Boisson", "Denrée sèche"});
        comboCategories.addActionListener(e -> updateFieldsForCategory());
        saisiePanel.add(comboCategories, gbc);
        
        // Quantité
        gbc.gridx = 2; gbc.gridy = 1;
        saisiePanel.add(new JLabel("Quantité:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        txtQuantite = new JTextField(10);
        saisiePanel.add(txtQuantite, gbc);
        
        // Taille (Textile)
        gbc.gridx = 0; gbc.gridy = 2;
        saisiePanel.add(new JLabel("Taille:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        txtTaille = new JTextField(10);
        saisiePanel.add(txtTaille, gbc);
        
        // Couleur (Textile)
        gbc.gridx = 2; gbc.gridy = 2;
        saisiePanel.add(new JLabel("Couleur:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2;
        txtCouleur = new JTextField(10);
        saisiePanel.add(txtCouleur, gbc);
        
        // Volume (Boisson)
        gbc.gridx = 0; gbc.gridy = 3;
        saisiePanel.add(new JLabel("Volume (cl):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        txtVolume = new JTextField(10);
        saisiePanel.add(txtVolume, gbc);
        
        // Poids (Denrée)
        gbc.gridx = 2; gbc.gridy = 3;
        saisiePanel.add(new JLabel("Poids (g):"), gbc);
        gbc.gridx = 3; gbc.gridy = 3;
        txtPoids = new JTextField(10);
        saisiePanel.add(txtPoids, gbc);
        
        add(saisiePanel, BorderLayout.NORTH);
        
        // Panneau des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnAjouter = new JButton("AJOUTER");
        btnAjouter.setBackground(new Color(0, 153, 76));
        btnAjouter.setForeground(Color.WHITE);
        btnAjouter.addActionListener(e -> ajouterArticle());
        
        btnModifier = new JButton("MODIFIER");
        btnModifier.setBackground(new Color(255, 153, 0));
        btnModifier.setForeground(Color.WHITE);
        btnModifier.addActionListener(e -> modifierArticle());
        
        btnConsulter = new JButton("CONSULTER");
        btnConsulter.setBackground(new Color(0, 102, 204));
        btnConsulter.setForeground(Color.WHITE);
        btnConsulter.addActionListener(e -> consulterArticle());
        
        btnSupprimer = new JButton("SUPPRIMER");
        btnSupprimer.setBackground(new Color(204, 0, 0));
        btnSupprimer.setForeground(Color.WHITE);
        btnSupprimer.addActionListener(e -> supprimerArticle());
        
        btnActualiser = new JButton("ACTUALISER");
        btnActualiser.addActionListener(e -> chargerArticles());
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnConsulter);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnActualiser);
        
        JButton btnRetour = new JButton("RETOUR MENU");
        btnRetour.addActionListener(e -> mainFrame.showPanel("MENU"));
        buttonPanel.add(btnRetour);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        updateFieldsForCategory();
    }
    
    private void updateFieldsForCategory() {
        String categorie = (String) comboCategories.getSelectedItem();
        boolean isTextile = "Textile".equals(categorie);
        boolean isBoisson = "Boisson".equals(categorie);
        boolean isDenree = "Denrée sèche".equals(categorie);
        
        txtTaille.setEnabled(isTextile);
        txtCouleur.setEnabled(isTextile);
        txtVolume.setEnabled(isBoisson);
        txtPoids.setEnabled(isDenree);
    }
    
    private void chargerArticles() {
        try {
            tableModel.setRowCount(0);
            List<Article> articles = articleDAO.listerTous();
            
            for (Article article : articles) {
                Object[] row = new Object[9];
                row[0] = article.getId();
                row[1] = article.getLibelle();
                row[7] = article.getQuantite();
                row[8] = article.getSeuilReappro();
                
                if (article instanceof Textile) {
                    row[2] = "Textile";
                    row[3] = ((Textile) article).getTaille();
                    row[4] = ((Textile) article).getCouleur();
                    row[5] = "";
                    row[6] = "";
                } else if (article instanceof Boisson) {
                    row[2] = "Boisson";
                    row[3] = "";
                    row[4] = "";
                    // CORRECTION: BigDecimal vers String pour l'affichage
                    BigDecimal volume = ((Boisson) article).getVolume();
                    row[5] = volume != null ? volume.toString() : "";
                    row[6] = "";
                } else if (article instanceof DenreeSeche) {
                    row[2] = "Denrée";
                    row[3] = "";
                    row[4] = "";
                    row[5] = "";
                    // CORRECTION: BigDecimal vers String pour l'affichage
                    BigDecimal poids = ((DenreeSeche) article).getPoids();
                    row[6] = poids != null ? poids.toString() : "";
                }
                
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void remplirChampsDepuisSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            txtLibelle.setText(tableModel.getValueAt(selectedRow, 1).toString());
            String categorie = tableModel.getValueAt(selectedRow, 2).toString();
            
            if ("Textile".equals(categorie)) {
                comboCategories.setSelectedItem("Textile");
                txtTaille.setText(tableModel.getValueAt(selectedRow, 3).toString());
                txtCouleur.setText(tableModel.getValueAt(selectedRow, 4).toString());
                txtVolume.setText("");
                txtPoids.setText("");
            } else if ("Boisson".equals(categorie)) {
                comboCategories.setSelectedItem("Boisson");
                txtTaille.setText("");
                txtCouleur.setText("");
                txtVolume.setText(tableModel.getValueAt(selectedRow, 5).toString());
                txtPoids.setText("");
            } else if ("Denrée".equals(categorie)) {
                comboCategories.setSelectedItem("Denrée sèche");
                txtTaille.setText("");
                txtCouleur.setText("");
                txtVolume.setText("");
                txtPoids.setText(tableModel.getValueAt(selectedRow, 6).toString());
            }
            
            txtQuantite.setText(tableModel.getValueAt(selectedRow, 7).toString());
        }
    }
    
    private void ajouterArticle() {
        try {
            String libelle = txtLibelle.getText().trim();
            if (libelle.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le libellé est obligatoire");
                return;
            }
            
            int quantite;
            try {
                quantite = Integer.parseInt(txtQuantite.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantité invalide");
                return;
            }
            
            String categorie = (String) comboCategories.getSelectedItem();
            Article article = null;
            
            if ("Textile".equals(categorie)) {
                String taille = txtTaille.getText().trim();
                String couleur = txtCouleur.getText().trim();
                if (taille.isEmpty() || couleur.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Taille et couleur obligatoires pour textile");
                    return;
                }
                article = new Textile(libelle, quantite, taille, couleur);
                
            } else if ("Boisson".equals(categorie)) {
                String volumeStr = txtVolume.getText().trim();
                if (volumeStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Volume obligatoire pour boisson");
                    return;
                }
                try {
                    // CORRECTION: Utiliser BigDecimal au lieu de int
                    BigDecimal volume = new BigDecimal(volumeStr);
                    article = new Boisson(libelle, quantite, volume);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Volume invalide (utilisez le format: 33.00)");
                    return;
                }
                
            } else if ("Denrée sèche".equals(categorie)) {
                String poidsStr = txtPoids.getText().trim();
                if (poidsStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Poids obligatoire pour denrée sèche");
                    return;
                }
                try {
                    // CORRECTION: Utiliser BigDecimal au lieu de int
                    BigDecimal poids = new BigDecimal(poidsStr);
                    article = new DenreeSeche(libelle, quantite, poids);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Poids invalide (utilisez le format: 0.50)");
                    return;
                }
            }
            
            if (article != null) {
                int id = articleDAO.creer(article);
                if (id > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Article créé avec succès! ID: " + id);
                    chargerArticles();
                    viderChamps();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la création", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void modifierArticle() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un article");
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Article article = articleDAO.consulter(id);
            
            if (article != null) {
                article.setLibelle(txtLibelle.getText().trim());
                
                try {
                    article.setQuantite(Integer.parseInt(txtQuantite.getText().trim()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Quantité invalide");
                    return;
                }
                
                if (article instanceof Textile) {
                    ((Textile) article).setTaille(txtTaille.getText().trim());
                    ((Textile) article).setCouleur(txtCouleur.getText().trim());
                    
                } else if (article instanceof Boisson) {
                    try {
                        // CORRECTION: Utiliser BigDecimal
                        BigDecimal volume = new BigDecimal(txtVolume.getText().trim());
                        ((Boisson) article).setVolume(volume);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Volume invalide (format: 33.00)");
                        return;
                    }
                    
                } else if (article instanceof DenreeSeche) {
                    try {
                        // CORRECTION: Utiliser BigDecimal
                        BigDecimal poids = new BigDecimal(txtPoids.getText().trim());
                        ((DenreeSeche) article).setPoids(poids);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Poids invalide (format: 0.50)");
                        return;
                    }
                }
                
                if (articleDAO.modifier(article)) {
                    JOptionPane.showMessageDialog(this, 
                        "Article modifié avec succès!");
                    chargerArticles();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la modification", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void consulterArticle() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un article");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Article article = articleDAO.consulter(id);
        
        if (article != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("ID: ").append(article.getId()).append("\n");
            sb.append("Libellé: ").append(article.getLibelle()).append("\n");
            sb.append("Quantité: ").append(article.getQuantite()).append("\n");
            
            if (article instanceof Textile) {
                sb.append("Catégorie: Textile\n");
                sb.append("Taille: ").append(((Textile) article).getTaille()).append("\n");
                sb.append("Couleur: ").append(((Textile) article).getCouleur());
            } else if (article instanceof Boisson) {
                sb.append("Catégorie: Boisson\n");
                // CORRECTION: Afficher BigDecimal correctement
                BigDecimal volume = ((Boisson) article).getVolume();
                sb.append("Volume: ").append(volume != null ? volume.toString() : "0").append(" cl");
            } else if (article instanceof DenreeSeche) {
                sb.append("Catégorie: Denrée sèche\n");
                // CORRECTION: Afficher BigDecimal correctement
                BigDecimal poids = ((DenreeSeche) article).getPoids();
                sb.append("Poids: ").append(poids != null ? poids.toString() : "0").append(" g");
            }
            
            JOptionPane.showMessageDialog(this, sb.toString(), 
                "Consultation article", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void supprimerArticle() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un article");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment supprimer cet article ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            if (articleDAO.supprimer(id)) {
                JOptionPane.showMessageDialog(this, "Article supprimé (logique)");
                chargerArticles();
                viderChamps();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression");
            }
        }
    }
    
    private void viderChamps() {
        txtLibelle.setText("");
        txtQuantite.setText("");
        txtTaille.setText("");
        txtCouleur.setText("");
        txtVolume.setText("");
        txtPoids.setText("");
        comboCategories.setSelectedIndex(0);
    }
}