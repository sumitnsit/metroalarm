package in.appdoor.metroalarm;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class StationRepo extends Observable {
	private List<Station> stations;
	private Activity activity;
	private SharedPreferences kvStore;
	
	private static final StationRepo INSTANCE = new StationRepo();
	
	private StationRepo() {}
	
	public static StationRepo getInstance() {
		return INSTANCE;
	}
	
	public static void init(Activity activity) {
		if(INSTANCE.stations != null) { 
			throw new IllegalStateException("init() shouldn't be called multiple times.");
		}
		
		INSTANCE.activity = activity;
		
		// Read station list from sharedpreferences if available, else read it from the default JSON file.
		INSTANCE.kvStore = activity.getSharedPreferences(
		        activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		String jsonString = INSTANCE.kvStore.getString("STATION_LIST", null); 
		
		// Initialize station data
        ObjectMapper mapper = new ObjectMapper();
        try {
        	Station[] values = new Station[0];
        	
        	if(jsonString == null) {
        		values = mapper.readValue(INSTANCE.activity.getAssets().open("stations.json"), Station[].class);
        	} else {
        		values = mapper.readValue(jsonString, Station[].class);
        	}
			
        	INSTANCE.stations = Arrays.asList(values);
//        	if(StationRepo.getInstance().getStations() == null) {
//				StationRepo.init(Arrays.asList(values));
//			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public List<Station> getStations() {
		return this.stations;
	}
	
	public List<Station> getStationsWithAlarm() {
		List<Station> result = new LinkedList<Station>();
		for(Station s : this.stations) {
			if(s.isSelected()) result.add(s);
		}
		return result;
	}
	
	public void deactivateStation(String name) {
		for(Station s : this.stations) {
			if(s.getName().equalsIgnoreCase(name)) {
				s.setSelected(false);
				notifyChanged();
				break;
			}
		}
	}
	
	public void notifyChanged() {
		persistStationData();
		setChanged();
		notifyObservers();
	}
	
	private void persistStationData() {
		SharedPreferences.Editor editor = kvStore.edit();
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json;
		try {
			json = ow.writeValueAsString(INSTANCE.stations);
			editor.putString("STATION_LIST", json);
			editor.commit();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
