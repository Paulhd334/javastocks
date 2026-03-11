package com.javastocks.view;

import com.javastocks.dao.FournisseurDAO;
import com.javastocks.model.Fournisseur;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FournisseurPanel extends JPanel {
    
    private MainFrame mainFrame;
    private FournisseurDAO fournisseurDAO;
    
    // Composants UI
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtRue, txtCp, txtVille, txtTel, txtEmail;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnActualiser;
    
    public FournisseurPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.fournisseurDAO = new FournisseurDAO();
        
        setLayout(new BorderLayout());
        
        // Titre
        JLabel title = new JLabel("Gestion des Fournisseurs", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(title, BorderLayout.NORTH);
        
        // Panel principal avec split
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        
        // Haut : formulaire
        splitPane.setTopComponent(createFormPanel());
        
        // Bas : tableau des fournisseurs
        splitPane.setBottomComponent(createTablePanel());
        
        add(splitPane, BorderLayout.CENTER);
        
        // Charger les données
        actualiserTable();
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Formulaire fournisseur"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Labels et champs
        JLabel lblNom = new JLabel("Nom:");
        JLabel lblRue = new JLabel("Rue:");
        JLabel lblCp = new JLabel("Code postal:");
        JLabel lblVille = new JLabel("Ville:");
        JLabel lblTel = new JLabel("Téléphone:");
        JLabel lblEmail = new JLabel("Email:");
        
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
        btnAjouter.addActionListener(e -> ajouterFournisseur());
        
        btnModifier = new JButton("Modifier");
        btnModifier.addActionListener(e -> modifierFournisseur());
        
        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> supprimerFournisseur());
        
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
                chargerFournisseurSelectionne();
            }
        });
        
        return new JScrollPane(table);
    }
    
    private void ajouterFournisseur() {
        try {
            Fournisseur f = new Fournisseur();
            f.setNom(txtNom.getText());
            f.setRue(txtRue.getText());
            f.setCp(txtCp.getText());
            f.setVille(txtVille.getText());
            f.setTel(txtTel.getText());
            f.setEmail(txtEmail.getText());
            
            int id = fournisseurDAO.creer(f);
            if (id > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Fournisseur ajouté avec succès! ID: " + id);
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
    
    private void modifierFournisseur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez un fournisseur à modifier");
            return;
        }
        
        try {
            Fournisseur f = new Fournisseur();
            f.setId((int) tableModel.getValueAt(selectedRow, 0));
            f.setNom(txtNom.getText());
            f.setRue(txtRue.getText());
            f.setCp(txtCp.getText());
            f.setVille(txtVille.getText());
            f.setTel(txtTel.getText());
            f.setEmail(txtEmail.getText());
            
            if (fournisseurDAO.modifier(f)) {
                JOptionPane.showMessageDialog(this, 
                    "Fournisseur modifié avec succès!");
                actualiserTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerFournisseur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez un fournisseur à supprimer");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Supprimer le fournisseur " + nom + " ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (fournisseurDAO.supprimer(id)) {
                JOptionPane.showMessageDialog(this, 
                    "Fournisseur supprimé avec succès!");
                actualiserTable();
                viderFormulaire();
            }
        }
    }
    
    private void actualiserTable() {
        tableModel.setRowCount(0);
        List<Fournisseur> fournisseurs = fournisseurDAO.listerTous();
        
        for (Fournisseur f : fournisseurs) {
            tableModel.addRow(new Object[]{
                f.getId(),
                f.getNom(),
                f.getRue(),
                f.getCp(),
                f.getVille(),
                f.getTel(),
                f.getEmail()
            });
        }
    }
    
    private void chargerFournisseurSelectionne() {
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