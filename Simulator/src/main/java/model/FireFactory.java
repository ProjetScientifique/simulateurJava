package model;

import java.time.LocalDate;

public class FireFactory implements EmergencyAbstractFactory {
	private double intensity;
	private LocalDate date;
	private Coord coord;
	private int id;
	private int idEmergency;
	
	public FireFactory(double intensity, LocalDate date, Coord coord) {
		super();
		this.intensity = intensity;
		this.date = date;
		this.coord = coord;
		this.id = 0;
		this.idEmergency = 0;
	}

	@Override
	public Emergency createEmergency() {
		return new Fire(intensity, date, coord);
	}
}
