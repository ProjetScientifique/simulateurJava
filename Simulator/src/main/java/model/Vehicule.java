package model;

import java.io.IOException;

import rest.Map;

public abstract class Vehicule {
	public abstract int getYear();
	public abstract Coord getCoord();
	public abstract int getNumbIntMax();
	public abstract int getId();
	public abstract int getIdType();
	public abstract int getIdEmergency();
	public abstract boolean isDone();
	public abstract Coord move(Coord coord, Map clientMap) throws IOException;
	
	@Override
	public String toString() {
		return "Vehicule [getYear()=" + getYear() + ", getCoord()=" + getCoord() + ", getNumbIntMax()="
				+ getNumbIntMax() + ", getId()=" + getId() + ", getIdType()=" + getIdType() + ", getIdEmergency()="
				+ getIdEmergency() + "]";
	}
}
