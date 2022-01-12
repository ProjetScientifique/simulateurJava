package model;

public class Coord {
	private double longitude;
	private double latitude;
	
	public Coord(double latitude, double longitude) {
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

	public String toApi() {
        return this.latitude + ", " + this.longitude;
    }
	
	public boolean equal(Coord coord) {
		if ((coord.getLongitude() == longitude) && (coord.getLatitude() == latitude)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Coord [longitude=" + longitude + ", latitude=" + latitude + "]";
	}
}
