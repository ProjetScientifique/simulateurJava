package model;

public class Coord {
	private double longitude;
	private double latitude;
	
	public Coord( double latitude, double longitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	@Override
	public String toString() {
		return "Coord [longitude=" + longitude + ", latitude=" + latitude + "]";
	}
}
