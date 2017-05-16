package layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.leon.locum.LocationsAdapter;
import com.leon.locum.LocationsDBHelper;
import com.leon.locum.R;


public class FragmentFavoritesList extends Fragment implements  View.OnClickListener, View.OnLongClickListener {

    private LocationsDBHelper helper;
    private LocationsAdapter adapter;
    private RecyclerView list = null;
    public FragmentFavoritesList() {

    }

    // starting a view for favorites fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favorites_layout, container, false);
        list = (RecyclerView) v.findViewById(R.id.favorites_list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        helper = new LocationsDBHelper(getContext());
        adapter = new LocationsAdapter(getActivity(), helper.getAllFavorites());
        list.setAdapter(adapter);
        return v;
    }

    // send item location to map
    @Override
    public void onClick(View v) {

        }

    // share a selected favorite location
    @Override
    public boolean onLongClick(View v) {

        return false;
    }
}

