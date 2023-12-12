package com.example.tutorial.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Bebida implements Parcelable {
    private int id;
    private String name;
    private int size_ml;
    private double cost;
    private String image_base64;

    // Construtor
    public Bebida(int id, String name, int size_ml, double cost, String image_base64) {
        this.id = id;
        this.name = name;
        this.size_ml = size_ml;
        this.cost = cost;
        this.image_base64 = image_base64;
    }

    public Bebida() {}

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSize_ml() {
        return size_ml;
    }

    public double getCost() {
        return cost;
    }

    public String getImage_base64() {
        return image_base64;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(size_ml);
        dest.writeDouble(cost);
        dest.writeString(image_base64);
    }

    protected Bebida(Parcel in) {
        id = in.readInt();
        name = in.readString();
        size_ml = in.readInt();
        cost = in.readDouble();
        image_base64 = in.readString();
    }

    public static final Creator<Bebida> CREATOR = new Creator<Bebida>() {
        @Override
        public Bebida createFromParcel(Parcel in) {
            return new Bebida(in);
        }

        @Override
        public Bebida[] newArray(int size) {
            return new Bebida[size];
        }
    };
}
