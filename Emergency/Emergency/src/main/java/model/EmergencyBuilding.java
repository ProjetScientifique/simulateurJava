package model;

public abstract class EmergencyBuilding {
	public abstract String getName();
	public abstract Coord getCoord();
	public abstract int getId();
	
	@Override
	public String toString() {
		return "EmergencyBuilding [getName()=" + getName() + ", getCoord()=" + getCoord() + ", getId()=" + getId()
				+ "]";
	}
}
