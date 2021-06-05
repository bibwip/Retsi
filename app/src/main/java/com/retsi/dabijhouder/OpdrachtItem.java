package com.retsi.dabijhouder;

public class OpdrachtItem{
    private String typeOpdracht, vakNaam, titel, datum, beschrijving, typeOpdracht_key;
    private boolean isExpanded;
    private Integer datumTagSorter;

    public OpdrachtItem(String typeOpdracht, String vakNaam, String titel, String datum, String beschrijving, Integer datumKey, String OpKey) {
        this.typeOpdracht = typeOpdracht;
        this.vakNaam = vakNaam;
        this.titel = titel;
        this.datum = datum;
        this.beschrijving = beschrijving;
        this.datumTagSorter = datumKey;
        this.typeOpdracht_key = OpKey;
    }

    public OpdrachtItem(String typeOpdracht, String vakNaam, String titel, String datum, String beschrijving) {
        this.typeOpdracht = typeOpdracht;
        this.vakNaam = vakNaam;
        this.titel = titel;
        this.datum = datum;
        this.beschrijving = beschrijving;
        this.isExpanded = isExpanded;
    }

    public String getTypeOpdracht_key() {
        return typeOpdracht_key;
    }

    public void setTypeOpdracht_key(String typeOpdracht_key) {
        this.typeOpdracht_key = typeOpdracht_key;
    }

    public Integer getDatumTagSorter() {
        return datumTagSorter;
    }

    public String getTypeOpdracht() {
        return typeOpdracht;
    }

    public String getVakNaam() {
        return vakNaam;
    }

    public String getTitel() {
        return titel;
    }

    public String getDatum() {
        return datum;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }



}
