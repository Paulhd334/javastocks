package com.javastocks.model;

public abstract class Article {
    protected int id;
    protected String libelle;
    protected int quantite;
    protected boolean indicateurSL;
    protected int seuilReappro;
    
    public Article() {
        this.seuilReappro = 0;
    }
    
    public Article(String libelle, int quantite) {
        this.libelle = libelle;
        this.quantite = quantite;
        this.seuilReappro = 0;
    }
    
    public Article(int id, String libelle, int quantite) {
        this.id = id;
        this.libelle = libelle;
        this.quantite = quantite;
        this.seuilReappro = 0;
    }
    
    // Méthodes abstraites
    public abstract void afficher();
    
    // Méthodes génériques
    public void creer() {
        System.out.println("Création de l'article: " + libelle);
    }
    
    public void consulter() {
        System.out.println("Consultation de l'article: " + libelle);
    }
    
    public void modifier() {
        System.out.println("Modification de l'article: " + libelle);
    }
    
    public void supprimer() {
        System.out.println("Suppression de l'article: " + libelle);
    }
    
    @Override
    public String toString() {
        return libelle + " (ID: " + id + ")";
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    
    public boolean isIndicateurSL() { return indicateurSL; }
    public void setIndicateurSL(boolean indicateurSL) { this.indicateurSL = indicateurSL; }
    
    public int getSeuilReappro() { return seuilReappro; }
    public void setSeuilReappro(int seuilReappro) { this.seuilReappro = seuilReappro; }
}