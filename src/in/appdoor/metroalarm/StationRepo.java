package in.appdoor.metroalarm;

import java.util.LinkedList;
import java.util.List;

public class StationRepo {
	private List<Station> stations;
	
	private static final StationRepo INSTANCE = new StationRepo();
	
	private StationRepo() {}
	
	public static StationRepo getInstance() {
		return INSTANCE;
	}
	
	public static void init(List<Station> stations) {
		if(INSTANCE.stations != null) { 
			throw new IllegalStateException("init() shouldn't be called multiple times.");
		}
		INSTANCE.stations = stations;
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
}
