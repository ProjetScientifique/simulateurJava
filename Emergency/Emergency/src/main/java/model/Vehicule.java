package model;

public abstract class Vehicule {
	public abstract int getYear();
	public abstract Coord getCoord();
	public abstract int getNumbIntMax();
	
	@Override
	public String toString() {
		return "Vehicule [getYear()=" + getYear() + ", getCoord()=" + getCoord() + ", getNumbIntMax()="
				+ getNumbIntMax() + "]";
	}
}
