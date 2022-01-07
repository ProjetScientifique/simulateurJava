package model;

import java.sql.Timestamp;

public abstract class Emergency {
	public abstract Timestamp getDate();
	public abstract Coord getCoord();
	public abstract double getIntensity();
	public abstract double getId();
	public abstract void setId(int newId);
	
	@Override
	public String toString() {
		return "Emergency [getDate()=" + getDate() + ", getCoord()=" + getCoord() + ", getIntensity()=" + getIntensity()
				+ ", getId()=" + getId() + "]";
	}
}
