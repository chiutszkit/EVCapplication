package com.example.tommyhui.evcapplication.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ItemCS implements Parcelable {
    public static final Parcelable.Creator<ItemCS> CREATOR = new Parcelable.Creator<ItemCS>() {

        public ItemCS createFromParcel(Parcel in) {
            return new ItemCS(in);
        }

        public ItemCS[] newArray(int size) {
            return new ItemCS[size];
        }
    };
    private int id;
    private String address;
    private String district;
    private String description;
    private String type;
    private String socket;
    private int quantity;
    private ArrayList<ItemCS> ItemCSes;

    public ItemCS() {
    }

    /*Getter and Setter*/

    public ItemCS(String address, String district, String description, String type, String socket, int quantity) {
        super();
        this.address = address;
        this.district = district;
        this.description = description;
        this.type = type;
        this.socket = socket;
        this.quantity = quantity;
    }

    public ItemCS(Parcel in) {

        ItemCSes = new ArrayList<ItemCS>();

        id = in.readInt();
        address = in.readString();
        district = in.readString();
        description = in.readString();
        type = in.readString();
        socket = in.readString();
        quantity = in.readInt();

        ItemCSes = in.readArrayList(ItemCS.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
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
