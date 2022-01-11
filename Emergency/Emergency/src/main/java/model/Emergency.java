package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public abstract class Emergency {
	public abstract LocalDateTime getDate();
	public abstract Coord getCoord();
	public abstract double getIntensity();
	public abstract int getId();
	public abstract int getIdType();
	public abstract int getIdTypeStatus();
	
	@Override
	public String toString() {
		return "Emergency [getDate()=" + getDate() + ", getCoord()=" + getCoord() + ", getIntensity()=" + getIntensity()
				+ ", getId()=" + getId() + ", getIdType()=" + getIdType() + ", getIdTypeStatus()=" + getIdTypeStatus()
				+ "]";
	}
}
