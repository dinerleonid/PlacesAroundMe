package com.leon.locum;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import layout.FragmentFavoritesList;
import layout.FragmentList;
import layout.FragmentMap;



public class MainActivity extends AppCompatActivity implements LocationsAdapter.OnLocationSelection, FragmentList.FragmentListInteraction {

    // fragments and SP declaration
    private SharedPreferences coordinates;
    private FragmentFavoritesList fragFav;
    private FragmentMap fragMap;
    private FragmentList fragList;
    private AlertDialog dialogExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing fragmentList, fragment Map , taking in count whether will be on smartPhone or tablet
        fragList = new FragmentList();
        if (getSupportFragmentManager().findFragmentByTag("FragmentMap") == null &&
                getSupportFragmentManager().findFragmentByTag("FragmentList") == null) {
            if (!getResources().getBoolean(R.bool.isTablet)) {
                getSupportFragmentManager().beginTransaction().add(R.id.activity_main_smart, fragList, "FragmentList").commit();
            }else{
                fragMap = new FragmentMap();
                getSupportFragmentManager().beginTransaction().add(R.id.listFrag_tab, fragList, "FragmentList").commit();
                getSupportFragmentManager().beginTransaction().add(R.id.mapFrag_tab, fragMap, "FragmentMap").commit();

            }
        }
    }
    // populating menu with contents and options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu,menu);
        return true;
    }

    // options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //on exit aap with cinfirmation dialog
            case R.id.exit_app:
                dialogExit = new AlertDialog.Builder(this).
                        setMessage(R.string.sure_to_exit).
                        setTitle(R.string.quit_confitmation_title).
                        setPositiveButton(R.string.quit_confirm_yes, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                finish();}
                        }).
                        setNegativeButton(R.string.quit_confirm_yno, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                dialog.cancel();}
                        }).
                        setCancelable(false).
                        create();
                dialogExit.show();
                break;
            //on settings click
            case R.id.settings:
                Intent in = new Intent(this, SettingsMenu.class);
                startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }

    // location selected and displayed on map
    @Override
    public void onLocationSelected(Locations locations) {
        coordinates = PreferenceManager.getDefaultSharedPreferences(this);
        coordinates.edit().putFloat("latP",Float.parseFloat(locations.getLat().toString())).commit();
        coordinates.edit().putFloat("lonP",Float.parseFloat(locations.getLon().toString())).commit();

        Location myLocation = fragList.getMyLocation();
        if (fragMap == null) {
            fragMap = new FragmentMap();

            Bundle bundle = new Bundle();
            bundle.putParcelable("locations", locations);
            fragMap.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.add(R.id.activity_main_smart, fragMap, "FragmentMap");
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();
        }else{
            fragMap.setLocation(locations);

        }
    }

    // request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 99 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            fragList.initLocation();
        }
    }

    // handle on favorite click event
    @Override
    public void onFavoriteClicked() {
        fragFav = new FragmentFavoritesList();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        if (getResources().getBoolean(R.bool.isTablet)) {
            transaction.add(R.id.listFrag_tab,fragFav, "FragmentFavoritesList");
        }else {
            transaction.add(R.id.activity_main_smart, fragFav, "FragmentFavoritesList");
        }
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    // null map fragment on back pressed from map
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!getResources().getBoolean(R.bool.isTablet)) {
            fragMap = null;
        }
    }
}