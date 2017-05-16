package com.leon.locum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationHolder> {
    private SharedPreferences coordinates;
    private float  latMe, lonMe, radius, measureKey;
    private Button button;
    private OnLocationSelection listener;
    private Context context;
    private ArrayList<Locations> locations = new ArrayList<>();
    private String placePicUrl;

    // location adapter constructor
    public LocationsAdapter(Context context, ArrayList<Locations> locations) {
        this.context = context;
        this.locations = locations;
        this.listener = (OnLocationSelection) context;
    }

    // add locations to list
    public void addToList(ArrayList<Locations>locations){
        this.locations.addAll(locations);
        notifyDataSetChanged();
    }

    // clear all locations from list
    public void clearList(){
        locations.clear();
        notifyDataSetChanged();

    }

    // location holder definition
    @Override
    public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
            v = LayoutInflater.from(context).inflate(R.layout.activity_locations_list, parent, false);
        return new LocationHolder(v);
    }

    // binding location by position
    @Override
    public void onBindViewHolder(LocationsAdapter.LocationHolder holder, int position) {
        Locations loc = locations.get(position);
        holder.bind(loc);
    }

    // getting size of list of places
    @Override
    public int getItemCount() {
        return locations.size();
    }

    // definitions for view holder for items in recycler view
    public class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView textTitle, textAddress;
        private ImageView imageView;
        private Locations locations;
        private Double lat, lon;
        private ImageView placePic;

        public LocationHolder(View locationView) {
            super(locationView);
            textTitle = (TextView) locationView.findViewById(R.id.textTitle);
            textAddress = (TextView) locationView.findViewById(R.id.textAddress);
            imageView = (ImageView) locationView.findViewById(R.id.imageView);
            button = (Button) locationView.findViewById(R.id.btn_status);
            placePic = (ImageView) locationView.findViewById(R.id.placePic);
            locationView.setOnClickListener(this);
            locationView.setOnLongClickListener(this);
        }

        // binding location to corresponding data
        public void bind(Locations locations){
            this.locations = locations;
            coordinates = PreferenceManager.getDefaultSharedPreferences(context);
            latMe = coordinates.getFloat("latMe", 0);
            lonMe = coordinates.getFloat("lonMe", 0);
            measureKey = coordinates.getFloat("unitsselected", 1);

            radius = coordinates.getFloat("radius", 500);
            double distance = distance(latMe, locations.getLat(), lonMe, locations.getLon());
            double dist =  distance/measureKey;
            DecimalFormat formater = new DecimalFormat("0.00");

            if (measureKey ==1 ) {
                textTitle.setText(locations.getName() + " - \n" +  formater.format(dist) + context.getString(R.string.km_from_you));
            }else{
                textTitle.setText(locations.getName() + " - \n" + formater.format(dist) + context.getString(R.string.miles_from_you));
            }
            textTitle.setTextColor(Color.parseColor("#0000FF"));
            textAddress.setText(locations.getAddress());
            Picasso.with(context).load(locations.getIcon()).into(imageView);
            placePicUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&photoreference=" + locations.getPhoto_reference() +
                            "&key=AIzaSyBWR3S7bcVnysNY49SXQBBapuFsPD_jALk";
            Picasso.with(context).load(placePicUrl).into(placePic);

            if (locations.isOpenNow() == false){
                button.setBackgroundColor(Color.RED);
            }else{
                button.setBackgroundColor(Color.GREEN);
            }
        }
        @Override
        public void onClick(View view) {
             listener.onLocationSelected(locations);
        }

        @Override
        public boolean onLongClick(View view) {
            Double latLink = locations.getLat();
            Double lonLink = locations.getLon();
            String uri = "http://maps.google.com/maps?daddr=" +latLink+","+lonLink;
            Intent sharedLocation = new Intent(Intent.ACTION_SEND);
            sharedLocation.setType("text/plain");
            sharedLocation.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.recommend_place));
            sharedLocation.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.place_awsome) + "'" + locations.getName().toString() + "' , " + context.getString(R.string.plase_address_share) + locations.getAddress().toString() + " " + uri);
            view.getContext().startActivity(Intent.createChooser(sharedLocation, context.getString(R.string.share_with)));
            return true;
        }
    }





    public class LocationsDetailTask extends AsyncTask<Double, Void, Locations> {
        private static final String MAP_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";

        @Override
        protected Locations doInBackground(Double... doubles) {
            HttpURLConnection connection = null;
            return null;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void insert(int position, Locations data) {
        locations.add(position, data);
        notifyItemInserted(position);
    }


    // Remove a RecyclerView item containing a specified Data object
    public void remove(Locations data) {
        int position = locations.indexOf(data);
        locations.remove(position);
        notifyItemRemoved(position);
    }

    public static double distance(double latMe, double latPlace,
                                  double lonMe, double lonPlace) {

        final int R = 6371; // Radius of the earth
        Double latDistance = Math.toRadians(latPlace - latMe);
        Double lonDistance = Math.toRadians(lonPlace - lonMe);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latMe)) * Math.cos(Math.toRadians(latPlace))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c ; // convert to meters
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }

    public interface OnLocationSelection {
         void onLocationSelected (Locations locations);
    }

}
