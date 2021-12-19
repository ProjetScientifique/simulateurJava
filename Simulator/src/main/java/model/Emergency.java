package model;

import java.sql.Timestamp;

public abstract class Emergency {
	public abstract Timestamp getDate();
	public abstract Coord getCoord();
	public abstract double getIntensity();
	
	@Override
	public String toString() {
		return "Emergency [getDate()=" + getDate() + ", getCoord()=" + getCoord() + ", getIntensity()=" + getIntensity()
				+ "]";
	}
}
