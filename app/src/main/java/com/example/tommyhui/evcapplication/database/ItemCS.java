package com.example.tommyhui.evcapplication.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ItemCS implements Parcelable {

    private int id;
    private String address;
    private String district;
    private String description;
    private String type;
    private String socket;
    private int quantity;
    private String latitude;
    private String longitude;

    private String distance;
    private String time;
    private String availability;
    private ArrayList<ItemCS> ItemCSes;

    public ItemCS() {
    }

    /*Getter and Setter*/

    public ItemCS(String address, String district, String description, String type, String socket, int quantity, String latitude, String longitude) {
        super();
        this.address = address;
        this.district = district;
        this.description = description;
        this.type = type;
        this.socket = socket;
        this.quantity = quantity;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<ItemCS> getItemCSes() {
        return ItemCSes;
    }

    public void setItemCSes(ArrayList<ItemCS> itemCSes) {
        ItemCSes = itemCSes;
    }

    @Override
    public String toString() {
        return "Charging Station [id = '" + getId() + "'; " +
                "description = '" + description + "'; " +
                "type = '" + type + "'; " +
                "socket = '" + socket + "'; " +
                "quantity = '" + quantity + "'; " +
                "latitude = '" + latitude + "'; " +
                "longitude = '" + longitude + "']";
    }

    public void readFromParcel(Parcel in) {

        id = in.readInt();
        address = in.readString();
        district = in.readString();
        description = in.readString();
        type = in.readString();
        socket = in.readString();
        quantity = in.readInt();
        longitude = in.readString();
        latitude = in.readString();
    }

    public static final Parcelable.Creator<ItemCS> CREATOR = new Parcelable.Creator<ItemCS>() {

        public ItemCS createFromParcel(Parcel in) {

            ItemCS cs = new ItemCS();
            cs.setId(in.readInt());
            cs.setAddress(in.readString());
            cs.setDistrict(in.readString());
            cs.setDescription(in.readString());
            cs.setType(in.readString());
            cs.setSocket(in.readString());
            cs.setQuantity(in.readInt());
            cs.setLatitude(in.readString());;
            cs.setLongitude(in.readString());;
            return cs;
        }

        public ItemCS[] newArray(int size) {
            return new ItemCS[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(address);
        dest.writeString(district);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeString(socket);
        dest.writeInt(quantity);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }
}
