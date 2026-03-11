package com.javastocks.view;

import com.javastocks.dao.PointLivraisonDAO;
import com.javastocks.model.PointLivraison;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PointLivraisonPanel extends JPanel {
    
    private MainFrame mainFrame;
    private PointLivraisonDAO pointLivraisonDAO;
    
    // Composants UI
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtRue, txtCp, txtVille, txtTel, txtEmail;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnActualiser;
    
    public PointLivraisonPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.pointLivraisonDAO = new PointLivraisonDAO();
        
        setLayout(new BorderLayout());
        
        // Titre
        JLabel title = new JLabel("Gestion des Points de Livraison", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(title, BorderLayout.NORTH);
        
        // Panel principal avec split
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        
        // Haut : formulaire
        splitPane.setTopComponent(createFormPanel());
        
        // Bas : tableau
        splitPane.setBottomComponent(createTablePanel());
        
        add(splitPane, BorderLayout.CENTER);
        
        // Charger les données
        actualiserTable();
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Formulaire point de livraison"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Labels
        JLabel lblNom = new JLabel("Nom:");
        JLabel lblRue = new JLabel("Rue:");
        JLabel lblCp = new JLabel("Code postal:");
        JLabel lblVille = new JLabel("Ville:");
        JLabel lblTel = new JLabel("Téléphone:");
        JLabel lblEmail = new JLabel("Email:");
        
        // Champs
        txtNom = new JTextField(20);
        txtRue = new JTextField(20);
        txtCp = new JTextField(10);
        txtVille = new JTextField(20);
        txtTel = new JTextField(15);
        txtEmail = new JTextField(25);
        
        // Ligne 1 : Nom
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblNom, gbc);
        gbc.gridx = 1;
        panel.add(txtNom, gbc);
        
        // Ligne 2 : Rue
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblRue, gbc);
        gbc.gridx = 1;
        panel.add(txtRue, gbc);
        
        // Ligne 3 : CP et Ville
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblCp, gbc);
        gbc.gridx = 1;
        panel.add(txtCp, gbc);
        
        gbc.gridx = 2;
        panel.add(lblVille, gbc);
        gbc.gridx = 3;
        panel.add(txtVille, gbc);
        
        // Ligne 4 : Téléphone
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(lblTel, gbc);
        gbc.gridx = 1;
        panel.add(txtTel, gbc);
        
        // Ligne 5 : Email
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(lblEmail, gbc);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);
        
        // Ligne 6 : Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(e -> ajouterPointLivraison());
        
        btnModifier = new JButton("Modifier");
        btnModifier.addActionListener(e -> modifierPointLivraison());
        
        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> supprimerPointLivraison());
        
        btnActualiser = new JButton("Actualiser");
        btnActualiser.addActionListener(e -> actualiserTable());
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnActualiser);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 4;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Nom", "Rue", "CP", "Ville", "Téléphone", "Email"};
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
                chargerPointSelectionne();
            }
        });
        
        return new JScrollPane(table);
    }
    
    private void ajouterPointLivraison() {
        try {
            PointLivraison p = new PointLivraison();
            p.setNom(txtNom.getText());
            p.setRue(txtRue.getText());
            p.setCp(txtCp.getText());
            p.setVille(txtVille.getText());
            p.setTel(txtTel.getText());
            p.setEmail(txtEmail.getText());
            
            int id = pointLivraisonDAO.creer(p);
            if (id > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Point de livraison ajouté avec succès! ID: " + id);
                actualiserTable();
                viderFormulaire();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void modifierPointLivraison() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez un point de livraison à modifier");
            return;
        }
        
        try {
            PointLivraison p = new PointLivraison();
            p.setId((int) tableModel.getValueAt(selectedRow, 0));
            p.setNom(txtNom.getText());
            p.setRue(txtRue.getText());
            p.setCp(txtCp.getText());
            p.setVille(txtVille.getText());
            p.setTel(txtTel.getText());
            p.setEmail(txtEmail.getText());
            
            if (pointLivraisonDAO.modifier(p)) {
                JOptionPane.showMessageDialog(this, 
                    "Point de livraison modifié avec succès!");
                actualiserTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerPointLivraison() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez un point de livraison à supprimer");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Supprimer le point de livraison " + nom + " ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (pointLivraisonDAO.supprimer(id)) {
                JOptionPane.showMessageDialog(this, 
                    "Point de livraison supprimé avec succès!");
                actualiserTable();
                viderFormulaire();
            }
        }
    }
    
    private void actualiserTable() {
        tableModel.setRowCount(0);
        List<PointLivraison> points = pointLivraisonDAO.listerTous();
        
        for (PointLivraison p : points) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getNom(),
                p.getRue(),
                p.getCp(),
                p.getVille(),
                p.getTel(),
                p.getEmail()
            });
        }
    }
    
    private void chargerPointSelectionne() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            txtNom.setText((String) tableModel.getValueAt(selectedRow, 1));
            txtRue.setText((String) tableModel.getValueAt(selectedRow, 2));
            txtCp.setText((String) tableModel.getValueAt(selectedRow, 3));
            txtVille.setText((String) tableModel.getValueAt(selectedRow, 4));
            txtTel.setText((String) tableModel.getValueAt(selectedRow, 5));
            txtEmail.setText((String) tableModel.getValueAt(selectedRow, 6));
        }
    }
    
    private void viderFormulaire() {
        txtNom.setText("");
        txtRue.setText("");
        txtCp.setText("");
        txtVille.setText("");
        txtTel.setText("");
        txtEmail.setText("");
    }
}