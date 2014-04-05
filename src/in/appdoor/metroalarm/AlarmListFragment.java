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
    public class AlarmListFragment extends Fragment {
    	
    	private AlarmListAdapter listAdapter;
    	
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_main, container, false);
            ListView listStations = (ListView) rootView.findViewById(R.id.listStations);

            listAdapter = new AlarmListAdapter(this.getActivity(),
                    android.R.layout.simple_list_item_1, StationRepo.getInstance().getStations());
            
            //TODO: Refactor this
            StationListAdapter.alarmViewAdapter = listAdapter;
            
            listAdapter.getFilter().filter("abc");
            
        	// Assign adapter to ListView
            listStations.setAdapter(listAdapter); 
            
            return rootView;
        }
        
    }