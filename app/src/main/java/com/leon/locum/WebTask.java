package com.leon.locum;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import layout.FragmentList;

public class WebTask extends IntentService {
    // API KEY IN Manifest
    private static final String KEY = "&key=AIzaSyBWR3S7bcVnysNY49SXQBBapuFsPD_jALk";
    private ProgressBar progressBar;
    private int radiusManual = 0;
    private String radiusStr = null;
    private float radiusPrefs;
    private ArrayList<Locations> place = new ArrayList<>();
    private long idInternal;


    public WebTask() {
        super(""); }

    // handling intent service for results search
    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences spSelectedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String BASE_URL = intent.getStringExtra("url");
        double myLAT = intent.getFloatExtra("lat", 0);
        double myLON = intent.getFloatExtra("lon", 0);
        String type = intent.getStringExtra("type");
        radiusStr = intent.getStringExtra("radius");
        radiusPrefs = Float.parseFloat(spSelectedPrefs.getString("radius_key", "0"));
        float radius;

        if (radiusStr != null){
            radiusManual = Integer.parseInt(radiusStr);
            radius = Float.parseFloat(radiusStr)+1;
        }else {
            if (radiusPrefs != -1) {

                radius = radiusPrefs*1000;
            } else {
                radius = 1;
            }
        }

        String url_builder;
        URL url_item;
        HttpURLConnection connection_item;
        BufferedReader reader_item;
        StringBuilder builder_item = new StringBuilder();
        JSONObject location;
        try {
            // create a URL object
        if (!type.equals("-1")) {
           url_builder = BASE_URL + myLAT + "," + myLON + "&radius=" + radius + "&keyword=" + type + KEY;
        }else{
           url_builder = BASE_URL + myLAT + "," + myLON + "&radius=500" + KEY;
        }
            url_item = new URL(url_builder);
            // open connection to the server
            connection_item = (HttpURLConnection) url_item.openConnection();
            // check if server response is valid and ok
            if (connection_item.getResponseCode() != HttpURLConnection.HTTP_OK) {
            }
            // create reader objects to read data from server. InputStreamReader can read bytes, BufferedReader can read Strings!
            reader_item = new BufferedReader(new InputStreamReader(connection_item.getInputStream()));
            // read first line from stream
            String line_item = reader_item.readLine();
            // loop while there is data in the string (successfully read a line of text)
            while (line_item != null) {
                builder_item.append(line_item);
                // try to read next line
                line_item = reader_item.readLine();
            }

            // starting to parse JSON
            JSONObject found_list = new JSONObject(builder_item.toString());
            JSONArray found_locations = found_list.getJSONArray("results");
            for (int j = 0; j < found_locations.length(); j++) {
                location = found_locations.getJSONObject(j);
                //update the list with all the items on page

                double location_lat = location.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                double location_lon = location.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                String icon = location.getString("icon");
                String name = location.getString("name");
                String place_id = location.getString("place_id");

                // getting photo reference from an internal array
                String photo_reference;
                if (location.has("photos")) {
                    JSONArray found_reference = location.getJSONArray("photos");
                    JSONObject loc_photo_reference = found_reference.getJSONObject(0);
                    photo_reference = loc_photo_reference.getString("photo_reference");
                } else {
                    photo_reference = "";
                }

                String address = location.getString("vicinity");
                boolean is_open=false;


                if (location.has("opening_hours")) {
                    if (String.valueOf(location.getJSONObject("opening_hours").getBoolean("open_now")).equals("true")) {
                        is_open = true;
                    } else if (String.valueOf(location.getJSONObject("opening_hours").getBoolean("open_now")).equals("false")) {
                        is_open = false;
                    } else {
                        is_open = false;
                    }
                }

                // getting place type from an internal array
                JSONArray found_type = location.getJSONArray("types");
                String place_type, place_type1 = null;

                for (int i = 0 ; i < found_type.length() ; i++){
                place_type = found_type.getString(i);
                    place_type1 = place_type1 + ", " + place_type;
                }
                place.add(new Locations(location_lat, location_lon, icon, name, place_id, photo_reference, address, is_open, idInternal, place_type1));

            }

            LocationsDBHelper helper = new LocationsDBHelper(this);
            helper.deleteAllLocations();
            helper.insertLocationsList(place);

            Intent finishedInsert = new Intent("finished");
            LocalBroadcastManager.getInstance(this).sendBroadcast(finishedInsert);

        }catch(MalformedURLException e){
            e.printStackTrace();

        }catch(IOException e){
            Intent noInet = new Intent("noconnection");
            LocalBroadcastManager.getInstance(this).sendBroadcast(noInet);

        }catch(JSONException e){
            throw new RuntimeException(e);

        }
    }
}


