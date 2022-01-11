package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Fire extends Emergency{
	private double intensity;
	private LocalDateTime date;
	private Coord coord;
	private int id;
	private int idType;
	private int idTypeStatus;
	
	public Fire(double intensity, LocalDateTime date, Coord coord, int id, int idType, int idTypeStatus) {
		super();
		this.intensity = intensity;
		this.date = date;
		this.coord = coord;
		this.id = id;
		this.idType = idType;
		this.idTypeStatus = idTypeStatus;
	}

	@Override
	public LocalDateTime getDate() {
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
	public int getId() {
		return id;
	}

	@Override
	public int getIdType() {
		return idType;
	}
	
	@Override
	public int getIdTypeStatus() {
		return idTypeStatus;
	}
}
