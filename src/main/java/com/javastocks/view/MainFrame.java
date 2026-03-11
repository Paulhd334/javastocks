package com.javastocks.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Panels
    private ArticlePanel articlePanel;
    private CoureurPanel coureurPanel;
    private TypeEpreuvePanel typeEpreuvePanel;
    private ReservationPanel reservationPanel;
    private RupturePanel rupturePanel;
    private HistoriquePanel historiquePanel;
    private FournisseurPanel fournisseurPanel;
    private PointLivraisonPanel pointLivraisonPanel;
    private DemandeReapproPanel demandeReapproPanel;

    public MainFrame() {
        setTitle("Java Stocks - Gestion de stock");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Créer la barre de menu
        setJMenuBar(createMenuBar());

        // Layout principal
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialiser les panels
        articlePanel = new ArticlePanel(this);
        coureurPanel = new CoureurPanel(this);
        typeEpreuvePanel = new TypeEpreuvePanel(this);
        reservationPanel = new ReservationPanel(this);
        rupturePanel = new RupturePanel(this);
        historiquePanel = new HistoriquePanel(this);
        fournisseurPanel = new FournisseurPanel(this);
        pointLivraisonPanel = new PointLivraisonPanel(this);
        demandeReapproPanel = new DemandeReapproPanel(this);

        // Ajouter les panels au CardLayout
        mainPanel.add(articlePanel, "ARTICLES");
        mainPanel.add(coureurPanel, "COUREURS");
        mainPanel.add(typeEpreuvePanel, "TYPES_EPREUVE");
        mainPanel.add(reservationPanel, "RESERVATIONS");
        mainPanel.add(rupturePanel, "RUPTURES");
        mainPanel.add(historiquePanel, "HISTORIQUE");
        mainPanel.add(fournisseurPanel, "FOURNISSEURS");
        mainPanel.add(pointLivraisonPanel, "POINTS_LIVRAISON");
        mainPanel.add(demandeReapproPanel, "DEMANDES_REAPPRO");

        // Afficher le panel d'accueil par défaut
        showPanel("ARTICLES");

        add(mainPanel);
    }

    /**
     * Crée la barre de menu
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // === Menu Fichier ===
        JMenu menuFichier = new JMenu("Fichier");
        menuFichier.setMnemonic('F');

        JMenuItem quitter = new JMenuItem("Quitter");
        quitter.setMnemonic('Q');
        quitter.addActionListener(e -> System.exit(0));
        menuFichier.add(quitter);

        // === Menu Gestion ===
        JMenu menuGestion = new JMenu("Gestion");
        menuGestion.setMnemonic('G');

        JMenuItem articles = new JMenuItem("Articles");
        articles.setMnemonic('A');
        articles.addActionListener(e -> showPanel("ARTICLES"));

        JMenuItem coureurs = new JMenuItem("Coureurs");
        coureurs.setMnemonic('C');
        coureurs.addActionListener(e -> showPanel("COUREURS"));

        JMenuItem typesEpreuve = new JMenuItem("Types d'épreuve");
        typesEpreuve.setMnemonic('T');
        typesEpreuve.addActionListener(e -> showPanel("TYPES_EPREUVE"));

        JMenuItem reservations = new JMenuItem("Réservations");
        reservations.setMnemonic('R');
        reservations.addActionListener(e -> showPanel("RESERVATIONS"));

        menuGestion.add(articles);
        menuGestion.add(coureurs);
        menuGestion.add(typesEpreuve);
        menuGestion.add(reservations);
        menuGestion.addSeparator();

        JMenuItem ruptures = new JMenuItem("Articles en rupture");
        ruptures.setMnemonic('U');
        ruptures.addActionListener(e -> showPanel("RUPTURES"));
        menuGestion.add(ruptures);

        // === Menu Approvisionnement ===
        JMenu menuAppro = new JMenu("Approvisionnement");
        menuAppro.setMnemonic('A');

        JMenuItem fournisseurs = new JMenuItem("Fournisseurs");
        fournisseurs.setMnemonic('F');
        fournisseurs.addActionListener(e -> showPanel("FOURNISSEURS"));

        JMenuItem pointsLivraison = new JMenuItem("Points de livraison");
        pointsLivraison.setMnemonic('P');
        pointsLivraison.addActionListener(e -> showPanel("POINTS_LIVRAISON"));

        JMenuItem demandes = new JMenuItem("Demandes de réappro");
        demandes.setMnemonic('D');
        demandes.addActionListener(e -> showPanel("DEMANDES_REAPPRO"));

        menuAppro.add(fournisseurs);
        menuAppro.add(pointsLivraison);
        menuAppro.add(demandes);

        // === Menu Historique ===
        JMenu menuHistorique = new JMenu("Historique");
        menuHistorique.setMnemonic('H');

        JMenuItem historique = new JMenuItem("Consulter l'historique");
        historique.setMnemonic('C');
        historique.addActionListener(e -> showPanel("HISTORIQUE"));
        menuHistorique.add(historique);

        // === Menu Aide ===
        JMenu menuAide = new JMenu("Aide");
        menuAide.setMnemonic('E');

        JMenuItem aPropos = new JMenuItem("À propos");
        aPropos.setMnemonic('P');
        aPropos.addActionListener(e -> 
            JOptionPane.showMessageDialog(this,
                "Java Stocks - Version 1.0\n\n" +
                "Application de gestion de stocks\n" +
                "pour l'association Web Courses\n\n" +
                "PostgreSQL • localhost:5433/javastocks",
                "À propos",
                JOptionPane.INFORMATION_MESSAGE)
        );
        menuAide.add(aPropos);

        // Ajouter tous les menus à la barre
        menuBar.add(menuFichier);
        menuBar.add(menuGestion);
        menuBar.add(menuAppro);
        menuBar.add(menuHistorique);
        menuBar.add(menuAide);

        return menuBar;
    }

    /**
     * Affiche le panel correspondant au nom
     */
    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
        setTitle("Java Stocks - " + getPanelTitle(name));
    }

    /**
     * Retourne le titre du panel
     */
    private String getPanelTitle(String name) {
        switch (name) {
            case "ARTICLES": return "Gestion des articles";
            case "COUREURS": return "Gestion des coureurs";
            case "TYPES_EPREUVE": return "Gestion des types d'épreuve";
            case "RESERVATIONS": return "Gestion des réservations";
            case "RUPTURES": return "Articles en rupture / Réservations en attente";
            case "HISTORIQUE": return "Historique";
            case "FOURNISSEURS": return "Gestion des fournisseurs";
            case "POINTS_LIVRAISON": return "Gestion des points de livraison";
            case "DEMANDES_REAPPRO": return "Gestion des demandes de réappro";
            default: return "Accueil";
        }
    }
}