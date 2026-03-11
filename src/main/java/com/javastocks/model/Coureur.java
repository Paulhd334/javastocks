package com.javastocks.model;

public class Coureur {
    private int id;
    private String nom;
    private String prenom;
    
    public Coureur() {}
    
    public Coureur(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }
    
    // Méthodes génériques
    public void creer() {
        System.out.println("Création du coureur : " + this.prenom + " " + this.nom);
    }
    
    public void modifier() {
        System.out.println("Modification du coureur : " + this.id);
    }
    
    public void consulter() {
        System.out.println("Consultation du coureur : " + this.id);
        this.afficher();
    }
    
    public void supprimer() {
        System.out.println("Suppression du coureur : " + this.id);
    }
    
    public void afficher() {
        System.out.println("Coureur [ID=" + id + ", Nom=" + nom + ", Prénom=" + prenom + "]");
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    // Solution pour l'affichage dans les JComboBox
    @Override
    public String toString() {
        return this.prenom + " " + this.nom;
    }
}