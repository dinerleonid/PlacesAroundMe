package layout;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import com.leon.locum.LocationsDBHelper;
import com.leon.locum.R;


public class FragmentSettingsMenu extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {


    public FragmentSettingsMenu() {
        // Required empty public constructor
    }


    private ListPreference measurement_units;
    private ListPreference search_radius;
    private Preference favoritesDelete;
    private String spSelectedUnits = "1";
    private Float spSelectedRadius = 0.5f;
    private LocationsDBHelper helper;
    private AlertDialog dialogDelete;
    SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        measurement_units = (ListPreference) findPreference("measure_key");
        search_radius = (ListPreference) findPreference("radius_key");
        String radius = sp.getString("radius_key", "0.5");
        String units = sp.getString("measure_key", "Kilometer");

        if ( units == "Kilometers"){
            measurement_units.setSummary(getString(R.string.kilometers_in_settings));
        }else{
            measurement_units.setSummary(getString(R.string.miles_in_settings));
        }
        search_radius.setSummary(getString(R.string.chosen_radius_in_settings) + sp.getFloat("radius",Float.parseFloat(radius)) + " " + measurement_units.getSummary());

        search_radius.setOnPreferenceChangeListener(this);
        measurement_units.setOnPreferenceChangeListener(this);




        favoritesDelete = findPreference("favorites_key");
        favoritesDelete.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object val) {

        switch (preference.getKey()){
            case "measure_key":
                if (Float.parseFloat(val.toString()) == 1) {
                    preference.setSummary("Kilometers");
                    search_radius.setSummary(getString(R.string.chosen_radius_in_summary) + sp.getFloat("radius",Float.parseFloat(val.toString())) + " " + measurement_units.getSummary());

                }else{
                    preference.setSummary("Miles");
                    search_radius.setSummary(getString(R.string.chosen_radius_in_summary2) + sp.getFloat("radius",Float.parseFloat(val.toString())) + " " + measurement_units.getSummary());
                }
                spSelectedUnits = val.toString();
                sp.edit().putFloat("unitsselected", Float.parseFloat(val.toString())).commit();
                break;

            case "radius_key":
                preference.setSummary(getString(R.string.chosen_radius_in_summary3) + val  + " " + measurement_units.getSummary());
                spSelectedRadius =  Float.parseFloat(val.toString());
                sp.edit().putFloat("radius", Float.parseFloat(val.toString())).commit();


                break;

        }

        Toast.makeText(getActivity(), getString(R.string.search_will_be_in_radius) + sp.getFloat("radius",Float.parseFloat(val.toString())) + " " + measurement_units.getSummary(), Toast.LENGTH_SHORT).show();
        return true;

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()){
            case "favorites_key":
               dialogDelete = new AlertDialog.Builder(getActivity()).
                        setMessage(R.string.sure_delete_favorite_places).
                        setTitle(R.string.delete_favorites_confirm).
                        setPositiveButton(R.string.delete_favorites_yes, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                helper = new LocationsDBHelper(getActivity());
                                helper.deleteAllFavorites();}
                        }).
                        setNegativeButton(R.string.delete_favorites_no, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                dialog.cancel();}
                        }).
                        setCancelable(false).
                        create();
                dialogDelete.show();
                break;
        }
        return true;
    }
}
