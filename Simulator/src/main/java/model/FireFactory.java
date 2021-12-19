package model;

import java.sql.Timestamp;

public class FireFactory implements EmergencyAbstractFactory {
	private double intensity;
	private Timestamp date;
	private Coord coord;
	
	public FireFactory(double intensity, Timestamp date, Coord coord) {
		super();
		this.intensity = intensity;
		this.date = date;
		this.coord = coord;
	}

	@Override
	public Emergency createEmergency() {
		return new Fire(intensity, date, coord);
	}
}
