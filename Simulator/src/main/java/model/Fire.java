package model;

import java.time.LocalDate;

public class Fire extends Emergency{
	private double intensity;
	private LocalDate date;
	private Coord coord;
	private int id;
	private int idEmergency;
	
	public Fire(double intensity, LocalDate date, Coord coord) {
		super();
		this.intensity = intensity;
		this.date = date;
		this.coord = coord;
		this.id = 0;
		this.idEmergency = 0;
	}

	@Override
	public LocalDate getDate() {
		return this.date;
	}

	@Override
	public Coord getCoord() {
		return this.coord;
	}
	
	@Override
	public void setIntensity(double newIntensity) {
		intensity = newIntensity;
	}
	
	@Override
	public double getIntensity() {
		return intensity;
	}

	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public int getIdEmergency() {
		return idEmergency;
	}

	@Override
	public void setId(int newId) {
		this.id = newId;
	}
	
	@Override
	public void setIdEmergency(int newId) {
		this.idEmergency = newId;
	}
}
