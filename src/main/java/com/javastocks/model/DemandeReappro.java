package com.javastocks.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class DemandeReappro {
    private int id;
    private String motif; // R, NP, UR
    private Date date;
    private String numeroCommande;
    private Fournisseur fournisseur;
    private PointLivraison pointLivraison;
    private List<LigneDemande> lignes;
    
    public DemandeReappro() {
        this.lignes = new ArrayList<>();
    }
    
    public DemandeReappro(String motif, Date date, Fournisseur fournisseur, PointLivraison pointLivraison) {
        this.motif = motif;
        this.date = date;
        this.fournisseur = fournisseur;
        this.pointLivraison = pointLivraison;
        this.lignes = new ArrayList<>();
    }
    
    // Méthodes génériques
    public void creer() {
        System.out.println("Création de la demande de réapprovisionnement");
    }
    
    public void modifier() {
        System.out.println("Modification de la demande : " + this.id);
    }
    
    public void consulter() {
        System.out.println("Consultation de la demande : " + this.id);
        this.afficher();
    }
    
    public void afficher() {
        System.out.println("Demande Réappro [ID=" + id + ", Motif=" + motif + 
                          ", Date=" + date + ", N° Commande=" + numeroCommande + 
                          ", Fournisseur=" + fournisseur.getNom() + 
                          ", Point Livraison=" + pointLivraison.getNom() + "]");
        for (LigneDemande ligne : lignes) {
            System.out.println("  - " + ligne.getArticle().getLibelle() + " x" + ligne.getQuantite());
        }
    }
    
    public void ajouterLigne(Article article, int quantite) {
        this.lignes.add(new LigneDemande(article, quantite));
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public String getNumeroCommande() { return numeroCommande; }
    public void setNumeroCommande(String numeroCommande) { this.numeroCommande = numeroCommande; }
    
    public Fournisseur getFournisseur() { return fournisseur; }
    public void setFournisseur(Fournisseur fournisseur) { this.fournisseur = fournisseur; }
    
    public PointLivraison getPointLivraison() { return pointLivraison; }
    public void setPointLivraison(PointLivraison pointLivraison) { this.pointLivraison = pointLivraison; }
    
    public List<LigneDemande> getLignes() { return lignes; }
    
    // Classe interne pour les lignes de demande
    public static class LigneDemande {
        private Article article;
        private int quantite;
        
        public LigneDemande(Article article, int quantite) {
            this.article = article;
            this.quantite = quantite;
        }
        
        public Article getArticle() { return article; }
        public int getQuantite() { return quantite; }
    }
}