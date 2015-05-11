package com.example.tommyhui.evcapplication.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ItemCS implements Parcelable{
    private int id;
    private String address;
    private String district;
    private String description;
    private String type;
    private String socket;
    private int quantity;

    private ArrayList<ItemCS> ItemCSes;

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

    public ArrayList<ItemCS> getItemCSes() {
        return ItemCSes;
    }

    public void setItemCSes(ArrayList<ItemCS> itemCSes) {
        ItemCSes = itemCSes;
    }

    @Override
    public String toString() {
        return "Charging Station [address=" + address + ", " +
                                    "description=" + description + "," +
                                    "type=" + type + "," +
                                    "socket=" + socket + "," +
                                    "quantity=" + quantity + "]";
    }

    public ItemCS(Parcel in){

        id = in.readInt();
        address = in.readString();
        district = in.readString();
        description = in.readString();
        type = in.readString();
        socket = in.readString();
        quantity = in.readInt();

        ItemCSes = (ArrayList<ItemCS>) in.readArrayList(ItemCS.class.getClassLoader());
    }

    public static final Parcelable.Creator<ItemCS> CREATOR = new Parcelable.Creator<ItemCS>() {

        public ItemCS createFromParcel(Parcel in) {
            return new ItemCS(in);
        }

        public ItemCS[] newArray(int size) {
            return new ItemCS[size];
        }
    };

    public void readFromParcel(Parcel in) {

        id = in.readInt();
        address = in.readString();
        district = in.readString();
        description = in.readString();
        type = in.readString();
        socket = in.readString();
        quantity = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeSerializable(id);
        dest.writeSerializable(address);
        dest.writeSerializable(district);
        dest.writeSerializable(description);
        dest.writeSerializable(type);
        dest.writeSerializable(socket);
        dest.writeSerializable(quantity);
    }
}
