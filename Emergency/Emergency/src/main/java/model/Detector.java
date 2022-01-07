package model;

public abstract class Detector {
	public abstract String getName();
	public abstract Coord getCoord();
	public abstract double getIntensity();
	public abstract double getRange();
	public abstract int getId();
	public abstract void setIntensity(Coord coord, double intensity);
	public abstract boolean inRange(Coord coord);
	public abstract void resetIntensity();
	
	@Override
	public String toString() {
		return "Detector [getName()=" + getName() + ", getCoord()=" + getCoord() + ", getIntensity()=" + getIntensity()
				+ ", getRange()=" + getRange() + ", getId()=" + getId() + "]";
	}
}
		