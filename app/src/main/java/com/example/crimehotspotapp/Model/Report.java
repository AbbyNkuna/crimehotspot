package com.example.crimehotspotapp.Model;

public class Report {
    private String ID,Crime,Lat,Log,City,Province,Code;

    public Report() {
    }

    public Report(String ID, String crime, String lat, String log, String city, String province, String code) {
        this.ID = ID;
        Crime = crime;
        Lat = lat;
        Log = log;
        City = city;
        Province = province;
        Code = code;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCrime() {
        return Crime;
    }

    public void setCrime(String crime) {
        Crime = crime;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLog() {
        return Log;
    }

    public void setLog(String log) {
        Log = log;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
