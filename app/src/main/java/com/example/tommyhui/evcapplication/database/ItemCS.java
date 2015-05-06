package com.example.tommyhui.evcapplication.database;

public class ItemCS {
    private int id;
    private String address;
    private String district;
    private String description;
    private String type;
    private String socket;
    private int quantity;

    public ItemCS(){}

    public ItemCS(String address, String district, String description, String type, String socket, int quantity) {
        super();
        this.address = address;
        this.district = district;
        this.description = description;
        this.type = type;
        this.socket = socket;
        this.quantity = quantity;
    }

    /*Getter and Setter*/

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

    @Override
    public String toString() {
        return "Charging Station [address=" + address + ", " +
                                    "description=" + description + "," +
                                    "type=" + type + "," +
                                    "socket=" + socket + "," +
                                    "quantity=" + quantity + "]";
    }
}
