package layout;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import com.leon.locum.LocationsAdapter;
import com.leon.locum.LocationsDBHelper;
import android.location.LocationListener;
import android.widget.Toast;
import com.leon.locum.R;
import com.leon.locum.WebTask;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import static android.content.Context.LOCATION_SERVICE;


public class FragmentList extends Fragment implements SearchView.OnQueryTextListener, LocationListener, View.OnClickListener, View.OnLongClickListener {
    // declaring on data members
    private LocationsDBHelper helper;
    private LocationsAdapter adapter;
    private SearchView searchView;
    private RecyclerView recyclerView = null;
    private FloatingActionButton fab;
    private FragmentManager myFragMan;
    private float myLAT;
    private float myLON;
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private SharedPreferences coordinates;
    private Timer timer;
    private boolean gotLocation= false;
    private LocationManager locationManager;
    private String providerName, measure;
    private ProgressBar progressBar;
    private String radiusStr;
    private ImageView imageView, imageViewStar, imageViewMyLoc, imageViewPlace;
    private String name;
    private Location myLocation;
    private FragmentListInteraction callback;

    // fragment manager
    public FragmentManager getMyFragMan() {
        return myFragMan;
    }

    public FragmentList() {
    }

    // setting list fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (FragmentListInteraction) context;
    }

    // ask for location service permission
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if no permission to location ask permission from the user (the built in dialog)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
        }

        // initialize location call and set data members fo view
        initLocation();
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView list = (RecyclerView) v.findViewById(R.id.list);
        searchView = (SearchView) v.findViewById(R.id.search_main);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_wait)  ;
        progressBar.setVisibility(View.INVISIBLE);
        imageView = (ImageView) v.findViewById(R.id.imageAntena);
        imageViewStar = (ImageView) v.findViewById(R.id.imageStar);
        imageViewMyLoc = (ImageView) v.findViewById(R.id.imageMyLoc);
        imageViewPlace = (ImageView) v.findViewById(R.id.placePic);
        imageViewStar.setOnClickListener(this);
        imageViewMyLoc.setOnClickListener(this);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        searchView.setOnQueryTextListener(this);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        helper = new LocationsDBHelper(getContext());
        adapter = new LocationsAdapter(getContext(), helper.getAllItems());
        list.setAdapter(adapter);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new FinishedInsert(), new IntentFilter("finished"));

        return v;
    }

    // location initialization method
    public void initLocation(){
        setHasOptionsMenu(true);
        coordinates = PreferenceManager.getDefaultSharedPreferences(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        providerName = LocationManager.GPS_PROVIDER;
        try {
            locationManager.requestLocationUpdates(providerName, 1000, 100, this);
        } catch (SecurityException e) {
            Log.e("Location", e.getMessage());
        }
        // create timer object
        timer = new Timer("provider");
        // create TimerTask implementation - it will run on a new thread!
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // if we do not have a location yet
                if (gotLocation == false) {
                    try {
                        // remove old location provider(gps)
                        locationManager.removeUpdates(FragmentList.this);
                        // change provider name to NETWORK
                        providerName = LocationManager.NETWORK_PROVIDER;

                        // start listening to location again on the main thread
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        locationManager.requestLocationUpdates(providerName, 1000, 100, FragmentList.this);
                                    } catch (SecurityException e) {

                                    }
                                }
                            });
                        }
                    } catch (SecurityException e) {
                        Log.e("Location", e.getMessage());
                    }

                }
            }
        };
        // schedule the timer to run the task after 5 seconds from now
        timer.schedule(task, new Date(System.currentTimeMillis() + 1500));

    }

    public class FinishedInsert extends BroadcastReceiver{

    // receiving results from search
    @Override
    public void onReceive(Context context, Intent intent) {
        adapter.clearList();
        adapter.addToList(helper.getAllItems());
        if (helper.getAllItems().size() == 0){
            Toast.makeText(context, R.string.no_location_found, Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
}

    // passing intent service after entering a query in search bar
    @Override
    public boolean onQueryTextSubmit(String query) {
        progressBar.setVisibility(View.VISIBLE);
        searchView.clearFocus();
        String type = query;
        Intent intent = new Intent(getContext(), WebTask.class);
        intent.putExtra("type", type);
        intent.putExtra("lat", myLAT);
        intent.putExtra("lon", myLON);
        intent.putExtra("url", BASE_URL);
        intent.putExtra("radius", radiusStr);
        intent.putExtra("gotLocation", gotLocation);
        getContext().startService(intent);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return true;
    }

    // as my current location is found it is saved to shared preferences and antenna icon will change from black to green
    @Override
    public void onLocationChanged(Location location) {
// change antenna image background when got location from GPS or Network
       if (gotLocation = true){
           imageView.setImageResource(R.drawable.antena_green);
           imageView.setBackgroundColor(Color.TRANSPARENT);

       }else{
           imageView.setImageResource(R.drawable.antena);
           imageView.setBackgroundColor(Color.TRANSPARENT);
       }
        // cancel the timer
        timer.cancel();

        myLAT = (float ) location.getLatitude();
        myLON = (float ) location.getLongitude();
        coordinates.edit().putFloat("latMe", myLAT).putFloat("lonMe", myLON).commit();
        myLocation = location;
    }

    public Location getMyLocation() {
        return myLocation;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    // click options for buttons
    // FAB - manually enter a search radius
    // STAR - switch to favorites fragment
    // My Location - show all immediate places around me disregarding their type
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                if (coordinates.getFloat("unitsselected", 1) == 1){
                    measure = getString(R.string.kilometers_fab);
                }else{
                    measure = getString(R.string.miles_fab);
                }
                builder.setTitle(getString(R.string.enter_search_radius_fab) +  measure);

                final EditText input = new EditText(getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton(R.string.ok_fab, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        radiusStr = input.getText().toString();
                    }

                });
                builder.show();
                break;

            case R.id.imageStar:
                callback.onFavoriteClicked();
                break;

            case R.id.imageMyLoc:
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getContext(), WebTask.class);
                intent.putExtra("type", "-1");
                intent.putExtra("lat", myLAT);
                intent.putExtra("lon", myLON);
                intent.putExtra("url", BASE_URL);
                intent.putExtra("radius", "500");
                intent.putExtra("gotLocation", gotLocation);
                getContext().startService(intent);
                break;
        }
    }

    // do nothing on long click
    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    // method for showing favorites fragment
    public interface FragmentListInteraction {
    void onFavoriteClicked();
    }

    // method monitoring and reporting if no internet connection
    public class NoInetConnection extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.INVISIBLE);

            Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();

        }
    }
}

