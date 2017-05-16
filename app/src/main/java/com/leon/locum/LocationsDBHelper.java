package com.leon.locum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LocationsDBHelper extends SQLiteOpenHelper {

    private static final String LOCATION_TABLE_TITLE = "locations";
    private static final String LOCATION_TABLE_FAVORITES = "favorites";
    private static final String LOCATION_ID = "idIntenal";
    private static final String LOCATION_NAME = "locationname";
    private static final String LOCATION_ICON = "icon";
    private static final String LOCATION_LAT = "lat";
    private static final String LOCATION_LON = "lon";
    private static final String LOCATION_IMAGES = "imagespath";
    private static final String LOCATION_ADDRESS = "address";
    private static final String LOCATION_PID = "idmap";
    private static final String LOCATION_REFERENCE = "reference";
    private static final String LOCATION_TYPE = "type";
    private static final String LOCATION_ISOPEN = "isOpenNow";


    public LocationsDBHelper(Context context) {
        super(context, "searchedPlaces.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT,  %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER)",
                LOCATION_TABLE_TITLE, LOCATION_ID, LOCATION_NAME, LOCATION_ICON, LOCATION_LAT, LOCATION_LON, LOCATION_IMAGES, LOCATION_ADDRESS, LOCATION_PID, LOCATION_REFERENCE, LOCATION_TYPE, LOCATION_ISOPEN);
        sqLiteDatabase.execSQL(sql);

        String sqlFav = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT,  %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER)",
                LOCATION_TABLE_FAVORITES, LOCATION_ID, LOCATION_NAME, LOCATION_ICON, LOCATION_LAT, LOCATION_LON, LOCATION_IMAGES, LOCATION_ADDRESS, LOCATION_PID, LOCATION_REFERENCE, LOCATION_TYPE, LOCATION_ISOPEN);
        sqLiteDatabase.execSQL(sqlFav);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // inserting values to item = movie
    public long insertLocation(Locations location){
        ContentValues values = new ContentValues();
        values.put(LOCATION_NAME, location.getName());
        values.put(LOCATION_ICON, location.getIcon());
        values.put(LOCATION_LAT, location.getLat());
        values.put(LOCATION_LON, location.getLon());
        values.put(LOCATION_IMAGES, location.getPhoto_reference());
        values.put(LOCATION_ADDRESS, location.getAddress());
        values.put(LOCATION_PID, location.getPlace_id());
        values.put(LOCATION_TYPE, location.getPlace_type());
        values.put(LOCATION_ISOPEN, location.isOpenNow());
        SQLiteDatabase db = getWritableDatabase();
        long newID = db.insert(LOCATION_TABLE_TITLE, null, values);
        db.close();
        return newID;
    }

    public long insertFavorite(Locations favorite){
        ContentValues values = new ContentValues();
        values.put(LOCATION_NAME, favorite.getName());
        values.put(LOCATION_ICON, favorite.getIcon());
        values.put(LOCATION_LAT, favorite.getLat());
        values.put(LOCATION_LON, favorite.getLon());
        values.put(LOCATION_IMAGES, favorite.getPhoto_reference());
        values.put(LOCATION_ADDRESS, favorite.getAddress());
        values.put(LOCATION_PID, favorite.getPlace_id());
        values.put(LOCATION_TYPE, favorite.getPlace_type());
        values.put(LOCATION_ISOPEN, favorite.isOpenNow());
        SQLiteDatabase db = getWritableDatabase();
        long newID = db.insert(LOCATION_TABLE_FAVORITES, null, values);
        db.close();
        return newID;
    }


    // inserting values to array = movies array
    public void insertLocationsList(List<Locations> location){
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < location.size() ; i++) {
            ContentValues values = new ContentValues();
            values.put(LOCATION_NAME, location.get(i).getName());
            values.put(LOCATION_ICON, location.get(i).getIcon());
            values.put(LOCATION_LAT, location.get(i).getLat());
            values.put(LOCATION_LON, location.get(i).getLon());
            values.put(LOCATION_ADDRESS, location.get(i).getAddress());
            values.put(LOCATION_IMAGES, location.get(i).getPhoto_reference());
            values.put(LOCATION_PID, location.get(i).getPlace_id());
            values.put(LOCATION_TYPE, location.get(i).getPlace_type());
            values.put(LOCATION_ISOPEN, location.get(i).isOpenNow());
            db.insert(LOCATION_TABLE_TITLE, null, values);
        }
        db.close();
    }

    public void insertFavoritesList(List<Locations> favorite){
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < favorite.size() ; i++) {
            ContentValues values = new ContentValues();
            values.put(LOCATION_NAME, favorite.get(i).getName());
            values.put(LOCATION_ICON, favorite.get(i).getIcon());
            values.put(LOCATION_LAT, favorite.get(i).getLat());
            values.put(LOCATION_LON, favorite.get(i).getLon());
            values.put(LOCATION_ADDRESS, favorite.get(i).getAddress());
            values.put(LOCATION_IMAGES, favorite.get(i).getPhoto_reference());
            values.put(LOCATION_PID, favorite.get(i).getPlace_id());
            values.put(LOCATION_TYPE, favorite.get(i).getPlace_type());
            values.put(LOCATION_ISOPEN, favorite.get(i).isOpenNow());
            db.insert(LOCATION_TABLE_FAVORITES, null, values);
        }
        db.close();
    }

    // get movie from its place and values from their columns
    public ArrayList<Locations> getAllItems(){
        ArrayList<Locations> locations = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(LOCATION_TABLE_TITLE, null, null, null, null,null, LOCATION_NAME);
        while(c.moveToNext()){
            Long idlnternal = c.getLong( c.getColumnIndex(LOCATION_ID) );
            Double lat = c.getDouble( c.getColumnIndex(LOCATION_LAT) );
            Double lon = c.getDouble( c.getColumnIndex(LOCATION_LON) );
            String icon = c.getString( c.getColumnIndex(LOCATION_ICON) );
            String name = c.getString(c.getColumnIndex(LOCATION_NAME));
            String place_id = c.getString(c.getColumnIndex(LOCATION_PID));
            String photo_reference = c.getString(c.getColumnIndex(LOCATION_IMAGES));
            String address = c.getString(c.getColumnIndex(LOCATION_ADDRESS));
            Integer isOpenNow = c.getInt(c.getColumnIndex(LOCATION_ISOPEN));
            String place_type = c.getString(c.getColumnIndex(LOCATION_TYPE));
            locations.add(new Locations(lat, lon, icon, name, place_id, photo_reference, address, isOpenNow==0, idlnternal, place_type));
        }
        db.close();
        return locations;
    }

    public ArrayList<Locations> getAllFavorites(){
        ArrayList<Locations> favorites = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(LOCATION_TABLE_FAVORITES, null, null, null, null,null, LOCATION_NAME);
        while(c.moveToNext()){
            Long idlnternal = c.getLong( c.getColumnIndex(LOCATION_ID) );
            Double lat = c.getDouble( c.getColumnIndex(LOCATION_LAT) );
            Double lon = c.getDouble( c.getColumnIndex(LOCATION_LON) );
            String icon = c.getString( c.getColumnIndex(LOCATION_ICON) );
            String name = c.getString(c.getColumnIndex(LOCATION_NAME));
            String place_id = c.getString(c.getColumnIndex(LOCATION_PID));
            String photo_reference = c.getString(c.getColumnIndex(LOCATION_IMAGES));
            String address = c.getString(c.getColumnIndex(LOCATION_ADDRESS));
            Integer isOpenNow = c.getInt(c.getColumnIndex(LOCATION_ISOPEN));
            String place_type = c.getString(c.getColumnIndex(LOCATION_TYPE));
            favorites.add(new Locations(lat, lon, icon, name, place_id, photo_reference, address, isOpenNow==0, idlnternal, place_type));
        }
        db.close();
        return favorites;
    }

    // delete a location
    public void deleteLocations(long idInternal){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(LOCATION_TABLE_TITLE, LOCATION_ID + "=" + idInternal, null);
        db.close();
    }

    // delete a favorite
    public void deleteFavorites(long idInternal){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(LOCATION_TABLE_FAVORITES, LOCATION_ID + "=" + idInternal, null);
        db.close();
    }

    // delete all locations
    public void deleteAllLocations(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(LOCATION_TABLE_TITLE, null, null);
        db.close();
    }

    // delete all favorites
    public void deleteAllFavorites(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(LOCATION_TABLE_FAVORITES, null, null);
        db.close();
    }
}
