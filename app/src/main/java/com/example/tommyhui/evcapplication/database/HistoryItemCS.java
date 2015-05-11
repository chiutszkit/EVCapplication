package com.example.tommyhui.evcapplication.database;

public class HistoryItemCS {

    private int id;
    private String address;
    private String district;
    private String description;
    private String type;
    private String socket;
    private int quantity;

    public HistoryItemCS(){

    }
    public HistoryItemCS(String address, String district, String description, String type, String socket, int quantity){
        this.address = address;
        this.district = district;
        this.description = description;
        this.type = type;
        this.socket = socket;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getSocket() {
        return socket;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}