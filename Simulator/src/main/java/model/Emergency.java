package model;

import java.time.LocalDate;

public abstract class Emergency {
	public abstract LocalDate getDate();
	public abstract Coord getCoord();
	public abstract void setIntensity(double newIntensity);
	public abstract double getIntensity();
	public abstract int getId();
	public abstract void setId(int newId);
	public abstract int getIdEmergency();
	public abstract void setIdEmergency(int newId);
	
	@Override
	public String toString() {
		return "Emergency [getDate()=" + getDate() + ", getCoord()=" + getCoord() + ", getIntensity()=" + getIntensity()
				+ ", getId()=" + getId() + ", getIdEmergency()=" + getIdEmergency() + "]";
	}
}
