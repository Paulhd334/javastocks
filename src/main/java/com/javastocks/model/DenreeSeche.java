package com.javastocks.model;

import java.math.BigDecimal;

public class DenreeSeche extends Article {
    private BigDecimal poids;  // C'est bien BigDecimal
    
    public DenreeSeche() {
        super();
        this.seuilReappro = 50;
    }
    
    public DenreeSeche(String libelle, int quantite, BigDecimal poids) {
        super(libelle, quantite);
        this.poids = poids;
        this.seuilReappro = 50;
    }
    
    public DenreeSeche(int id, String libelle, int quantite, BigDecimal poids) {
        super(id, libelle, quantite);
        this.poids = poids;
        this.seuilReappro = 50;
    }
    
    @Override
    public void afficher() {
        System.out.println("Article Denrée Sèche [ID=" + id + ", Libellé=" + libelle + 
                          ", Poids=" + poids + "g, Quantité=" + quantite + "]");
    }
    
    @Override
    public String toString() {
        return super.toString() + " - " + poids + "g";
    }
    
    public BigDecimal getPoids() { return poids; }
    public void setPoids(BigDecimal poids) { this.poids = poids; }
    // Supprime ou commente setPoids(double) pour éviter les confusions
}