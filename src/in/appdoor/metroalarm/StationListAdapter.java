package in.appdoor.metroalarm;

import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class StationListAdapter extends ArrayAdapter<Station> implements Observer {

	private List<Station> stationList;
	private Activity activity;
	
	//TODO: Refactor this
	public static AlarmListAdapter alarmViewAdapter;

	public StationListAdapter(Activity activity, int textViewResourceId,
			List<Station> stationList) {
		super(activity, textViewResourceId, stationList);
		this.activity = activity;
		this.stationList = stationList;
		Collections.sort(stationList);
		StationRepo.getInstance().addObserver(this);
	}

	private class ViewHolder {
		TextView stationName;
		CheckBox stationCheckbox;
		CheckBox favouriteCheckbox;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		Log.v("ConvertView", String.valueOf(position));

		if (convertView == null) {

			LayoutInflater vi = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = vi.inflate(R.layout.station_layout, null);

			holder = new ViewHolder();
			holder.stationName = (TextView) convertView
					.findViewById(R.id.stationName);
			holder.stationCheckbox = (CheckBox) convertView
					.findViewById(R.id.stationCheckbox);
			holder.favouriteCheckbox = (CheckBox) convertView.findViewById(R.id.starCheckbox);

			convertView.setTag(holder);

			holder.stationCheckbox.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							CheckBox cb = (CheckBox) v;
							Station _station = (Station) cb.getTag();
							_station.setSelected(cb.isChecked());
							StationRepo.getInstance().notifyChanged();
						}
					});
			holder.favouriteCheckbox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					Station _station = (Station) cb.getTag();
					_station.setFavourite(cb.isChecked());
					StationRepo.getInstance().notifyChanged();
				}
			});

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Station station = stationList.get(position);

		holder.stationName.setText(station.getName());
		holder.stationCheckbox.setChecked(station.isSelected());
		holder.stationCheckbox.setTag(station);

		holder.favouriteCheckbox.setChecked(station.isFavourite());
		holder.favouriteCheckbox.setTag(station);

		return convertView;
	}

	@Override
	public void update(Observable obs, Object data) {
		this.stationList = StationRepo.getInstance().getStations();
		Collections.sort(this.stationList);
		notifyDataSetChanged();
	}

}
