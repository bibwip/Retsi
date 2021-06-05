package com.retsi.dabijhouder;

public class VakItem {
    private String naam, color;

    public VakItem(String naam, String color) {
        this.naam = naam;
        this.color = color;
    }

    public String getVaknaam() {
        return naam;
    }

    public String getVakColor() {
        return color;
    }
}
