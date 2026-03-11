package com.javastocks.view;

import com.javastocks.dao.CoureurDAO;
import com.javastocks.model.Coureur;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CoureurPanel extends JPanel {
    
    private MainFrame mainFrame;
    private CoureurDAO coureurDAO;
    
    // Composants UI
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtPrenom;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnActualiser;
    
    public CoureurPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.coureurDAO = new CoureurDAO();
        
        setLayout(new BorderLayout());
        
        // Titre
        JLabel title = new JLabel("Gestion des Coureurs", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(title, BorderLayout.NORTH);
        
        // Panel principal avec split
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.3);
        
        // Haut : formulaire
        splitPane.setTopComponent(createFormPanel());
        
        // Bas : tableau des coureurs
        splitPane.setBottomComponent(createTablePanel());
        
        add(splitPane, BorderLayout.CENTER);
        
        // Charger les données
        actualiserTable();
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Formulaire coureur"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Labels et champs
        JLabel lblNom = new JLabel("Nom:");
        JLabel lblPrenom = new JLabel("Prénom:");
        
        txtNom = new JTextField(20);
        txtPrenom = new JTextField(20);
        
        // Ligne 1 : Nom
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblNom, gbc);
        gbc.gridx = 1;
        panel.add(txtNom, gbc);
        
        // Ligne 2 : Prénom
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblPrenom, gbc);
        gbc.gridx = 1;
        panel.add(txtPrenom, gbc);
        
        // Ligne 3 : Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(e -> ajouterCoureur());
        
        btnModifier = new JButton("Modifier");
        btnModifier.addActionListener(e -> modifierCoureur());
        
        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> supprimerCoureur());
        
        btnActualiser = new JButton("Actualiser");
        btnActualiser.addActionListener(e -> actualiserTable());
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnActualiser);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Nom", "Prénom"};
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
                chargerCoureurSelectionne();
            }
        });
        
        return new JScrollPane(table);
    }
    
    private void ajouterCoureur() {
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        
        if (nom.isEmpty() || prenom.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez saisir un nom et un prénom", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Coureur coureur = new Coureur(nom, prenom);
            int id = coureurDAO.creer(coureur);
            
            if (id > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Coureur ajouté avec succès! ID: " + id);
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
    
    private void modifierCoureur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez un coureur à modifier");
            return;
        }
        
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        
        if (nom.isEmpty() || prenom.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez saisir un nom et un prénom", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Coureur coureur = new Coureur(nom, prenom);
            coureur.setId(id);
            
            if (coureurDAO.modifier(coureur)) {
                JOptionPane.showMessageDialog(this, 
                    "Coureur modifié avec succès!");
                actualiserTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerCoureur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez un coureur à supprimer");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 1);
        String prenom = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Supprimer le coureur " + prenom + " " + nom + " ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (coureurDAO.supprimer(id)) {
                    JOptionPane.showMessageDialog(this, 
                        "Coureur supprimé avec succès!");
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
    }
    
    private void actualiserTable() {
        tableModel.setRowCount(0);
        List<Coureur> coureurs = coureurDAO.listerTous();
        
        for (Coureur c : coureurs) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getNom(),
                c.getPrenom()
            });
        }
    }
    
    private void chargerCoureurSelectionne() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            txtNom.setText((String) tableModel.getValueAt(selectedRow, 1));
            txtPrenom.setText((String) tableModel.getValueAt(selectedRow, 2));
        }
    }
    
    private void viderFormulaire() {
        txtNom.setText("");
        txtPrenom.setText("");
    }
}