package com.icu.kandeneme;

public class CardModel2 {
    private String nameSurname;
    private String dateOfSearch;
    private String age;
    private String city;
    private String hospital;
    private String diseases;
    private String contact;

    public CardModel2(String nameSurname, String dateOfSearch, String age, String city, String hospital, String diseases, String contact) {
        this.nameSurname = nameSurname;
        this.dateOfSearch = dateOfSearch;
        this.age = age;
        this.city = city;
        this.hospital = hospital;
        this.diseases = diseases;
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

    public void setAge(String age) {
        this.age = age;
    }

    public String getAge() {
        return age;
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

    public String getDiseases() {
        return diseases;
    }

    public void setDiseases(String diseases) {
        this.diseases = diseases;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
