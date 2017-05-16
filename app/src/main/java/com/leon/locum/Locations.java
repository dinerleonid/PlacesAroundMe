package com.leon.locum;

import android.os.Parcel;
import android.os.Parcelable;

public class Locations implements Parcelable {

    // data members declaration
    private Double lat, lon;
    private String icon, name, place_id, photo_reference, address, place_type;
    private boolean isOpenNow;
    private long idInternal;

    // constructor definitions
    public Locations(Double lat, Double lon, String icon, String name, String place_id, String photo_reference, String address, boolean isOpenNow, long idInternal, String place_type) {
        this.lat = lat;
        this.lon = lon;
        this.icon = icon;
        this.name = name;
        this.place_id = place_id;
        this.photo_reference = photo_reference;
        this.address = address;
        this.isOpenNow = isOpenNow;
        this.idInternal = idInternal;
        this.place_type = place_type;
    }

    protected Locations(Parcel in) {

        lat = in.readDouble();
        lon = in.readDouble();
        icon = in.readString();
        name = in.readString();
        place_id = in.readString();
        photo_reference = in.readString();
        address = in.readString();
        isOpenNow = in.readByte() != 0;
        idInternal = in.readLong();
        place_type = in.readString();
    }

    public Double getLat() { return lat; }

    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }

    public void setLon(Double lon) { this.lon = lon; }

    public String getIcon() { return icon; }

    public void setIcon(String icon) { this.icon = icon; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPlace_id() { return place_id; }

    public void setPlace_id(String place_id) { this.place_id = place_id; }

    public String getPhoto_reference() { return photo_reference; }

    public void setPhoto_reference(String photo_reference) { this.photo_reference = photo_reference; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public boolean isOpenNow() { return isOpenNow; }

    public void setOpenNow(boolean openNow) { isOpenNow = openNow; }

    public long getIdInternal() { return idInternal; }

    public void setIdInternal(long idInternal) { this.idInternal = idInternal; }

    public String getPlace_type() { return place_type; }

    public void setPlace_type(String place_type) { this.place_type = place_type; }

    public static final Creator<Locations> CREATOR = new Creator<Locations>() {
        @Override
        public Locations createFromParcel(Parcel in) { return new Locations(in); }

        @Override
        public Locations[] newArray(int size) { return new Locations[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);
        parcel.writeString(icon);
        parcel.writeString(name);
        parcel.writeString(place_id);
        parcel.writeString(photo_reference);
        parcel.writeString(address);
        parcel.writeByte((byte) (isOpenNow ? 1 : 0));
        parcel.writeLong(idInternal);
        parcel.writeString(place_type);
    }
}
