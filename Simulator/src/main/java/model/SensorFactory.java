package model;

public class SensorFactory implements DetectorAbstractFactory {
	private String name;
	private Coord coord;
	private double intensity;
	private double range;
	private int id;
	
	public SensorFactory(double intensity, String name, Coord coord, double range, int id) {
		super();
		this.name = name;
		this.coord = coord;
		this.intensity = intensity;
		this.range = range;
		this.id = id;
	}

	@Override
	public Detector createDetector() {
		return new Sensor(intensity, name, coord, range, id);
	}
}
