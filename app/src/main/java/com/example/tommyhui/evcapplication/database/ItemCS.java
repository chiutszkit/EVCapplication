package com.example.tommyhui.evcapplication.database;

import java.util.ArrayList;

public class ItemCS {
    private int id;
    private String address;
    private String district;
    private String chargingStation;
    private String type;
    private String socket;
    private int quantity;

    private ArrayList<ItemCS> itemList;

    public ItemCS(){}

    public ItemCS(String address, String district, String chargingStation, String type, String socket, int quantity) {
        super();
        this.address = address;
        this.district = district;
        this.chargingStation = chargingStation;
        this.type = type;
        this.socket = socket;
        this.quantity = quantity;
    }

    /*Getter and Setter*/

    public ArrayList<ItemCS> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<ItemCS> itemList) {
        this.itemList = itemList;
    }

    public int getId() {
        return id;
    }

    public String getSocket() {
        return socket;
    }

    public String getChargingStation() {
        return chargingStation;
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

    public void setChargingStation(String chargingStation) {
        this.chargingStation = chargingStation;
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

    @Override
    public String toString() {
        return "Charging Station [address=" + address + ", " +
                                    "chargingStation=" + chargingStation + "," +
                                    "type=" + type + "," +
                                    "socket=" + socket + "," +
                                    "quantity=" + quantity + "]";
    }
}
