package in.appdoor.metroalarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

public class AlarmListAdapter extends ArrayAdapter<Station> implements Filterable {

	private List<Station> stationList;
	private List<Station> filteredData;
	private Activity activity;
	private CheckedStationsFilter mFilter = new CheckedStationsFilter();
	
	public AlarmListAdapter(Activity activity, int textViewResourceId,
			List<Station> stationList) {
		super(activity, textViewResourceId, stationList);
		this.activity = activity;
		this.stationList = stationList;
		this.filteredData = stationList;
	}

	private class ViewHolder {
		TextView stationName;
		CheckBox stationCheckbox;
	}
	
	@Override
	public int getCount() {
		return filteredData.size();
	}
	
	@Override
	public Station getItem(int position) {
		return filteredData.get(position);
	}
 
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public Filter getFilter() {
		return mFilter;
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

			convertView.setTag(holder);

			holder.stationCheckbox.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							CheckBox cb = (CheckBox) v;
							Station _station = (Station) cb.getTag();
							_station.setSelected(cb.isChecked());
						}
					});

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Station station = filteredData.get(position);

		holder.stationName.setText(station.getName());
		// holder.stationCheckbox.setText("");
		holder.stationCheckbox.setChecked(station.isSelected());

		holder.stationCheckbox.setTag(station);

		return convertView;
	}

	private class CheckedStationsFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			final List<Station> list = stationList;
			final List<Station> nlist = new LinkedList<Station>();
 
			for (Station s : list) {
				if (s.isSelected()) {
					nlist.add(s);
				}
			}
			
			results.values = nlist;
			results.count = nlist.size();
 
			return results;
		}
 
		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			filteredData = (List<Station>) results.values;
			notifyDataSetChanged();
		}
 
	}
}
