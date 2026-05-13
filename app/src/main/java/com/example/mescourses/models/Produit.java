package com.example.mescourses.models;

public class Produit {
    private int id;
    private String nom;
    private int quantite;
    private String rayon;
    // 0 = base produits, 1 = liste courses en cours, 2 = archivage
    private int statut;
    private int categoryId = -1;
    private boolean isChecked = false;

    public Produit() {}

    public Produit(String nom, int quantite, String rayon, int statut) {
        this.nom = nom;
        this.quantite = quantite;
        this.rayon = rayon;
        this.statut = statut;
    }

    public Produit(int id, String nom, int quantite, String rayon, int statut) {
        this.id = id;
        this.nom = nom;
        this.quantite = quantite;
        this.rayon = rayon;
        this.statut = statut;
    }

    public Produit(int id, String nom, int quantite, String rayon, int statut, int categoryId) {
        this.id = id;
        this.nom = nom;
        this.quantite = quantite;
        this.rayon = rayon;
        this.statut = statut;
        this.categoryId = categoryId;
    }

    // Getters
    public int getId()        { return id; }
    public String getNom()    { return nom; }
    public int getQuantite()  { return quantite; }
    public String getRayon()  { return rayon; }
    public int getStatut()    { return statut; }
    public int getCategoryId() { return categoryId; }
    public boolean isChecked() { return isChecked; }

    // Setters
    public void setId(int id)           { this.id = id; }
    public void setNom(String nom)      { this.nom = nom; }
    public void setQuantite(int q)      { this.quantite = q; }
    public void setRayon(String rayon)  { this.rayon = rayon; }
    public void setStatut(int statut)   { this.statut = statut; }
    public void setCategoryId(int id)   { this.categoryId = id; }
    public void setChecked(boolean b)   { this.isChecked = b; }

    // Statut constants
    public static final int STATUT_BASE     = 0;
    public static final int STATUT_COURSES  = 1;
    public static final int STATUT_ARCHIVE  = 2;
}
