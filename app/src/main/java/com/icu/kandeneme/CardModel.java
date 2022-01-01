package com.icu.kandeneme;

public class CardModel {
    private String nameSurname;
    private String dateOfSearch;
    private String blood;
    private String city;
    private String hospital;
    private String type;
    private String contact;

    public CardModel(String nameSurname, String dateOfSearch, String blood, String city, String hospital, String type, String contact) {
        this.nameSurname = nameSurname;
        this.dateOfSearch = dateOfSearch;
        this.blood = blood;
        this.city = city;
        this.hospital = hospital;
        this.type = type;
        this.contact = contact;
    }


    public String getNameSurname() {

        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getDateOfSearch() {
        return dateOfSearch;
    }

    public void setDateOfSearch(String dateOfSearch) {
        this.dateOfSearch = dateOfSearch;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getBlood() {
        return blood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
