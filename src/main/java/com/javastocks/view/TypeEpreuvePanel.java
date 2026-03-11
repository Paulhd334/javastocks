package com.javastocks.view;

import com.javastocks.dao.TypeEpreuveDAO;
import com.javastocks.model.TypeEpreuve;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TypeEpreuvePanel extends JPanel {
    
    private MainFrame mainFrame;
    private TypeEpreuveDAO typeEpreuveDAO;
    
    // Composants UI
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtLibelle;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnActualiser;
    
    public TypeEpreuvePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.typeEpreuveDAO = new TypeEpreuveDAO();
        
        setLayout(new BorderLayout());
        
        // Titre
        JLabel title = new JLabel("Gestion des Types d'Épreuve", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(title, BorderLayout.NORTH);
        
        // Panel principal avec split
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.3);
        
        // Haut : formulaire
        splitPane.setTopComponent(createFormPanel());
        
        // Bas : tableau des types d'épreuve
        splitPane.setBottomComponent(createTablePanel());
        
        add(splitPane, BorderLayout.CENTER);
        
        // Charger les données
        actualiserTable();
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Formulaire type d'épreuve"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Label et champ
        JLabel lblLibelle = new JLabel("Libellé:");
        txtLibelle = new JTextField(30);
        
        // Ligne 1 : Libellé
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblLibelle, gbc);
        gbc.gridx = 1;
        panel.add(txtLibelle, gbc);
        
        // Ligne 2 : Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(e -> ajouterTypeEpreuve());
        
        btnModifier = new JButton("Modifier");
        btnModifier.addActionListener(e -> modifierTypeEpreuve());
        
        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> supprimerTypeEpreuve());
        
        btnActualiser = new JButton("Actualiser");
        btnActualiser.addActionListener(e -> actualiserTable());
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnActualiser);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Libellé"};
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
                chargerTypeSelectionne();
            }
        });
        
        return new JScrollPane(table);
    }
    
    private void ajouterTypeEpreuve() {
        String libelle = txtLibelle.getText().trim();
        
        if (libelle.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez saisir un libellé", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            TypeEpreuve typeEpreuve = new TypeEpreuve(libelle);
            int id = typeEpreuveDAO.creer(typeEpreuve);
            
            if (id > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Type d'épreuve ajouté avec succès! ID: " + id);
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
    
    private void modifierTypeEpreuve() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez un type d'épreuve à modifier");
            return;
        }
        
        String libelle = txtLibelle.getText().trim();
        
        if (libelle.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez saisir un libellé", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            TypeEpreuve typeEpreuve = new TypeEpreuve(libelle);
            typeEpreuve.setId(id);
            
            if (typeEpreuveDAO.modifier(typeEpreuve)) {
                JOptionPane.showMessageDialog(this, 
                    "Type d'épreuve modifié avec succès!");
                actualiserTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerTypeEpreuve() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez un type d'épreuve à supprimer");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String libelle = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Supprimer le type d'épreuve '" + libelle + "' ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (typeEpreuveDAO.supprimer(id)) {
                    JOptionPane.showMessageDialog(this, 
                        "Type d'épreuve supprimé avec succès!");
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
        List<TypeEpreuve> types = typeEpreuveDAO.listerTous();
        
        for (TypeEpreuve t : types) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getLibelle()
            });
        }
    }
    
    private void chargerTypeSelectionne() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            txtLibelle.setText((String) tableModel.getValueAt(selectedRow, 1));
        }
    }
    
    private void viderFormulaire() {
        txtLibelle.setText("");
    }
}