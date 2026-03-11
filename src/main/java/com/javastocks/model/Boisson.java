package com.javastocks.model;

import java.math.BigDecimal; // Import pour BigDecimal

public class Boisson extends Article {
    private BigDecimal volume; // Changé de int à BigDecimal pour correspondre à numeric(10,2) en base
    
    public Boisson() {
        super();
        this.seuilReappro = 100; // Seuil spécifique boisson
    }
    
    public Boisson(String libelle, int quantite, BigDecimal volume) {
        super(libelle, quantite);
        this.volume = volume;
        this.seuilReappro = 100;
    }
    
    // Constructeur avec id pour la lecture depuis la base
    public Boisson(int id, String libelle, int quantite, BigDecimal volume) {
        super(id, libelle, quantite);
        this.volume = volume;
        this.seuilReappro = 100;
    }
    
    @Override
    public void afficher() {
        System.out.println("Article Boisson [ID=" + id + ", Libellé=" + libelle + 
                          ", Volume=" + volume + "cl, Quantité=" + quantite + "]");
    }
    
    @Override
    public String toString() {
        return super.toString() + " - " + volume + "ml";
    }
    
    // Getters et Setters
    public BigDecimal getVolume() { 
        return volume; 
    }
    
    public void setVolume(BigDecimal volume) { 
        this.volume = volume; 
    }
    
    // Pour faciliter la création avec des doubles (pratique pour les tests)
    public void setVolume(double volume) {
        this.volume = BigDecimal.valueOf(volume);
    }
}