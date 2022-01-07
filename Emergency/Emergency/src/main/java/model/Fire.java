package model;

import java.sql.Timestamp;

public class Fire extends Emergency{
	private double intensity;
	private Timestamp date;
	private Coord coord;
	private int id;
	
	public Fire(double intensity, Timestamp date, Coord coord) {
		super();
		this.intensity = intensity;
		this.date = date;
		this.coord = coord;
		this.id = 0;
	}

	@Override
	public Timestamp getDate() {
		return this.date;
	}

	@Override
	public Coord getCoord() {
		return this.coord;
	}
	
	@Override
	public double getIntensity() {
		return intensity;
	}

	@Override
	public double getId() {
		return id;
	}

	@Override
	public void setId(int newId) {
		this.id = newId;
	}
}
