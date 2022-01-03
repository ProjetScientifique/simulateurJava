package model;

public abstract class EmergencyBuilding {
	public abstract String getName();
	public abstract Coord getCoord();
	
	@Override
	public String toString() {
		return "EmergencyBuilding [getName()=" + getName() + ", getCoord()=" + getCoord() + "]";
	}
}
