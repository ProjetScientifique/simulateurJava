package model;

public class Sensor extends Detector {
	private String name;
	private Coord coord;
	private double intensity;
	private double range;
	private int id;
	
	public Sensor(double intensity, String name, Coord coord, double range, int id) {
		super();
		this.name = name;
		this.coord = coord;
		this.intensity = intensity;
		this.range = range;
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Coord getCoord() {
		return coord;
	}

	@Override
	public double getIntensity() {
		return intensity;
	}

	@Override
	public void setIntensity(Coord coord, double intensity) {
		double distToCenter = Math.sqrt(Math.pow(coord.getLongitude() - this.coord.getLongitude(), 2) + Math.pow(coord.getLatitude() - this.coord.getLatitude(), 2));
		this.intensity = (intensity - intensity * distToCenter / this.range);
	}

	@Override
	public double getRange() {
		return range;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public boolean inRange(Coord coord) {
		boolean isIn = false;
		if ( (Math.pow(coord.getLongitude() - this.coord.getLongitude(), 2) +  Math.pow(coord.getLatitude() - this.coord.getLatitude(), 2) ) <= Math.pow(this.range, 2) ) {
			isIn = true;
		}
		return isIn;
	}

	@Override
	public void resetIntensity() {
		this.intensity = 0;
		
	}
}
