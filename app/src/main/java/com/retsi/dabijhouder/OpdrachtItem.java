package com.retsi.dabijhouder;

public class OpdrachtItem{
    private String typeOpdracht, vakNaam, titel, datum, beschrijving;
    private boolean isExpanded;
    private Integer datumTagSorter;

    public OpdrachtItem(String typeOpdracht, String vakNaam, String titel, String datum, String beschrijving, Integer datumKey) {
        this.typeOpdracht = typeOpdracht;
        this.vakNaam = vakNaam;
        this.titel = titel;
        this.datum = datum;
        this.beschrijving = beschrijving;
        this.datumTagSorter = datumKey;
    }

    public OpdrachtItem(String typeOpdracht, String vakNaam, String titel, String datum, String beschrijving) {
        this.typeOpdracht = typeOpdracht;
        this.vakNaam = vakNaam;
        this.titel = titel;
        this.datum = datum;
        this.beschrijving = beschrijving;
        this.isExpanded = isExpanded;
    }

    public Integer getDatumTagSorter() {
        return datumTagSorter;
    }

    public void setDatumTagSorter(int datumTagSorter) {
        this.datumTagSorter = datumTagSorter;
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
