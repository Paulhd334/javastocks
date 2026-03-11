package com.javastocks.model;

public class Textile extends Article {
    private String taille;
    private String couleur;
    
    public Textile() {
        super();
        this.seuilReappro = 10; // Seuil spécifique textile
    }
    
    public Textile(String libelle, int quantite, String taille, String couleur) {
        super(libelle, quantite);
        this.taille = taille;
        this.couleur = couleur;
        this.seuilReappro = 10;
    }
    
    @Override
    public void afficher() {
        System.out.println("Article Textile [ID=" + id + ", Libellé=" + libelle + 
                          ", Taille=" + taille + ", Couleur=" + couleur + 
                          ", Quantité=" + quantite + "]");
    }
    @Override
    public String toString() {
    return super.toString() + " - " + taille + " " + couleur;
}
    // Getters et Setters
    public String getTaille() { return taille; }
    public void setTaille(String taille) { this.taille = taille; }
    
    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
}