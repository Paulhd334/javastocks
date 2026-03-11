package com.javastocks.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private int id;
    private Date date;
    private Coureur coureur;
    private TypeEpreuve typeEpreuve;
    private String statut; // "en_attente" ou "validee"
    private List<LigneReservation> lignes;
    
    public Reservation() {
        this.lignes = new ArrayList<>();
        this.statut = "en_attente";
    }
    
    public Reservation(Date date, Coureur coureur, TypeEpreuve typeEpreuve) {
        this.date = date;
        this.coureur = coureur;
        this.typeEpreuve = typeEpreuve;
        this.lignes = new ArrayList<>();
        this.statut = "en_attente";
    }
    
    // Méthodes génériques
    public void creer() {
        System.out.println("Création de la réservation pour " + coureur.getPrenom() + " " + coureur.getNom());
    }
    
    public void modifier() {
        System.out.println("Modification de la réservation : " + this.id);
    }
    
    public void consulter() {
        System.out.println("Consultation de la réservation : " + this.id);
        this.afficher();
    }
    
    public void supprimer() {
        System.out.println("Suppression de la réservation : " + this.id);
    }
    
    public void afficher() {
        System.out.println("Réservation [ID=" + id + ", Date=" + date + 
                          ", Coureur=" + coureur.getNom() + ", Épreuve=" + typeEpreuve.getLibelle() +
                          ", Statut=" + statut + "]");
        for (LigneReservation ligne : lignes) {
            System.out.println("  - " + ligne.getArticle().getLibelle() + " x" + ligne.getQuantite());
        }
    }
    
    public void ajouterLigne(Article article, int quantite) {
        this.lignes.add(new LigneReservation(article, quantite));
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public Coureur getCoureur() { return coureur; }
    public void setCoureur(Coureur coureur) { this.coureur = coureur; }
    
    public TypeEpreuve getTypeEpreuve() { return typeEpreuve; }
    public void setTypeEpreuve(TypeEpreuve typeEpreuve) { this.typeEpreuve = typeEpreuve; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public List<LigneReservation> getLignes() { return lignes; }
    
    // Classe interne pour les lignes de réservation
    public static class LigneReservation {
        private Article article;
        private int quantite;
        
        public LigneReservation(Article article, int quantite) {
            this.article = article;
            this.quantite = quantite;
        }
        
        public Article getArticle() { return article; }
        public int getQuantite() { return quantite; }
    }
}