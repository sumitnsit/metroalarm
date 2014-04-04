package in.appdoor.metroalarm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public class StationListFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_stations, container, false);
            ListView listStations = (ListView) rootView.findViewById(R.id.listStations);

            StationListAdapter adapter = new StationListAdapter(this.getActivity(),
                    android.R.layout.simple_list_item_1, StationRepo.getInstance().getStations());
            
        	// Assign adapter to ListView
            listStations.setAdapter(adapter); 
            
            return rootView;
        }
    }