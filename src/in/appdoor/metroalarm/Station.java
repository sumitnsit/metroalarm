package in.appdoor.metroalarm;

public class Station implements Comparable<Station>{

	private String name;
	private double lat;
	private double lng;
	private boolean selected;
	private boolean favourite;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isFavourite() {
		return favourite;
	}

	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}

	@Override
	public int compareTo(Station other) {
//		int thisSelected = this.isSelected() ? 0 : 1;
//		int thisFavourite = this.isFavourite() ? 0 : 1;
//		int otherSelected = other.isSelected() ? 0 : 1;
//		int otherFavourite = other.isFavourite() ? 0 : 1;
//		
//		if(this.selected && other.selected) {
//			if(this.favourite && other.favourite) {
//				return this.name.compareTo(other.name);
//			} else {
//				return thisFavourite - otherFavourite;
//			}
//		} else if(this.favourite && other.favourite) {
//			return thisSelected - otherSelected;
//		} else {
//			return this.name.compareTo(other.name);
//		}
		
		if(this.getWeightage() == other.getWeightage()) {
			return this.name.compareTo(other.getName());
		} else {
			return this.getWeightage() - other.getWeightage();
		}
	}
	
	private int getWeightage() {
		int weight = 0;
		if(this.selected) weight -= 10;
		if(this.favourite) weight -= 1;
		return weight;
	}
}
