package layout;


import android.Manifest;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leon.locum.Locations;
import com.leon.locum.LocationsDBHelper;
import com.leon.locum.R;
import java.util.ArrayList;


public class FragmentMap extends Fragment implements OnMapReadyCallback, View.OnClickListener  {
    // declaring on data members
    private String KEY = "&key=AIzaSyBWR3S7bcVnysNY49SXQBBapuFsPD_jALk";
    private GoogleMap mMap;
    private float latMe,lonMe, latP, lonP, radius, radiusCircle, measureKey ;
    private String name;
    private SharedPreferences coordinates;
    private Marker marker;
    private Circle circle;
    private ImageView imageAddToFav;
    private Locations locations;
    private LocationsDBHelper helper;
    public FragmentMap() {
    }

    // get arguments for locations from Locations
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new LocationsDBHelper(getContext());
        if(getArguments() !=  null) {
            locations = getArguments().getParcelable("locations");

        }
    }

    // creating view for map fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        IntentFilter filter = new IntentFilter("latLonRadiusNamereceiver");
        View v = inflater.inflate(R.layout.activity_maps, container, false);
        imageAddToFav = (ImageView) v.findViewById(R.id.imageStarAddFav);
        imageAddToFav.setOnClickListener(this);
        initLocationParams();
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        return v;
    }

    // setting my location and a place coordinates and other data
    public void initLocationParams(){
        coordinates = PreferenceManager.getDefaultSharedPreferences(getActivity());
        latP = coordinates.getFloat("latP", 0);
        lonP = coordinates.getFloat("lonP", 0);
        latMe = coordinates.getFloat("latMe", 0);
        lonMe = coordinates.getFloat("lonMe", 0);
        radius = coordinates.getFloat("radius", 500);
        measureKey = coordinates.getFloat("unitsselected", 1);

    }

    // setting map ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initMap();
    }

    // clicking on a star on a map adds it to favorites
    @Override
    public void onClick(View v) {
        if(R.id.imageStarAddFav == v.getId()){
            //if exists in list it will not be added and Toast will pop
            ArrayList<Locations> allFavorites= helper.getAllFavorites();
            boolean isLocationExists = false;
            for (int i=0; i < allFavorites.size(); i++){
                if (allFavorites.get(i).getPlace_id().equals(locations.getPlace_id())){
                    isLocationExists = true;
                    break;
                }
            }

            if (!isLocationExists){
                helper.insertFavorite(locations);
                Toast.makeText(getActivity(), locations.getName() + getString(R.string.add_to_favorites), Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getActivity(), locations.getName() + getString(R.string.exists_in_favorites), Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void initMap(){

    if (ActivityCompat.checkSelfPermission(getContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        return;
    }

    LatLng locP = new LatLng(latP, lonP);
    LatLng locMe = new LatLng(latMe, lonMe);

    if(marker == null) {
        marker = mMap.addMarker(new MarkerOptions().position(locP).title(locations.getName()));
    } else {
        marker.remove();
        marker = mMap.addMarker(new MarkerOptions().position(locP).title(locations.getName()));
    }

    mMap.setMyLocationEnabled(true);
    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    // Add a marker in Sydney and move the camera
    mMap.moveCamera(CameraUpdateFactory.newLatLng(locP));
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locP, 16));

    // add circle

        if(measureKey == 1){
            radiusCircle = radius*1000;
        }else{
            radiusCircle = radius*1600;
        }
    circle = mMap.addCircle(new CircleOptions()
            .center(locMe)
           // .radius(Float.parseFloat(radius)).fillColor(Color.parseColor("#644FC4F6"))
            .radius(radiusCircle).fillColor(Color.parseColor("#644FC4F6"))

            .strokeColor(Color.TRANSPARENT));
    // animate the camera
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locP, 15));
    }

    public void setLocation(Locations locations){
        this.locations = locations;
        mMap.clear();

        initLocationParams();
        initMap();
    }

}