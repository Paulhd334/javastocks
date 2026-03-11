package com.javastocks.model;

public class Fournisseur {
    private int id;
    private String nom;
    private String rue;
    private String cp;
    private String ville;
    private String tel;
    private String email;
    
    public Fournisseur() {}
    
    public Fournisseur(String nom, String rue, String cp, String ville, String tel, String email) {
        this.nom = nom;
        this.rue = rue;
        this.cp = cp;
        this.ville = ville;
        this.tel = tel;
        this.email = email;
    }
    
    // Méthodes génériques
    public void creer() {
        System.out.println("Création du fournisseur : " + this.nom);
    }
    
    public void modifier() {
        System.out.println("Modification du fournisseur : " + this.id);
    }
    
    public void consulter() {
        System.out.println("Consultation du fournisseur : " + this.id);
        this.afficher();
    }
    
    public void afficher() {
        System.out.println("Fournisseur [ID=" + id + ", Nom=" + nom + 
                          ", Adresse=" + rue + " " + cp + " " + ville + 
                          ", Tel=" + tel + ", Email=" + email + "]");
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getRue() { return rue; }
    public void setRue(String rue) { this.rue = rue; }
    
    public String getCp() { return cp; }
    public void setCp(String cp) { this.cp = cp; }
    
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    
    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    // Solution pour l'affichage dans les JComboBox
    @Override
    public String toString() {
        return this.nom;
    }
}