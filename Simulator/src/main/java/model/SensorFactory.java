package model;

public class SensorFactory implements DetectorAbstractFactory {
	private String name;
	private Coord coord;
	private double intensity;
	private double range;
	
	public SensorFactory(double intensity, String name, Coord coord, double range) {
		super();
		this.name = name;
		this.coord = coord;
		this.intensity = intensity;
		this.range = range;
	}

	@Override
	public Detector createDetector() {
		return new Sensor(intensity, name, coord, range);
	}
}
