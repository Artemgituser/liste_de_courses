package com.example.mescourses.models;

public class ShoppingList {
    private int id;
    private String nom;
    private String dateCreation;
    private int statut; // 0: active, 1: finished
    private String dateFin;

    public ShoppingList(int id, String nom, String dateCreation, int statut, String dateFin) {
        this.id = id;
        this.nom = nom;
        this.dateCreation = dateCreation;
        this.statut = statut;
        this.dateFin = dateFin;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getDateCreation() { return dateCreation; }
    public int getStatut() { return statut; }
    public String getDateFin() { return dateFin; }
}
