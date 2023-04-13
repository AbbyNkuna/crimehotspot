package com.example.crimehotspotapp.Model;

public class User {
    private String Name,Surname,Phone;

    public User(String name, String surname, String phone) {
        Name = name;
        Surname = surname;
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
