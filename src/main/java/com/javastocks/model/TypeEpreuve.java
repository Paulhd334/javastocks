package com.javastocks.model;

public class TypeEpreuve {
    private int id;
    private String libelle;
    
    public TypeEpreuve() {}
    
    public TypeEpreuve(String libelle) {
        this.libelle = libelle;
    }
    
    // Méthodes génériques
    public void creer() {
        System.out.println("Création du type d'épreuve : " + this.libelle);
    }
    
    public void modifier() {
        System.out.println("Modification du type d'épreuve : " + this.id);
    }
    
    public void consulter() {
        System.out.println("Consultation du type d'épreuve : " + this.id);
        this.afficher();
    }
    
    public void supprimer() {
        System.out.println("Suppression du type d'épreuve : " + this.id);
    }
    
    public void afficher() {
        System.out.println("Type Épreuve [ID=" + id + ", Libellé=" + libelle + "]");
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    
    @Override
    public String toString() {
        return this.libelle;
    }
}