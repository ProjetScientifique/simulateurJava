package model;

public abstract class Vehicule {
	public abstract int getYear();
	public abstract Coord getCoord();
	public abstract int getNumbIntMax();
	public abstract int getId();
	public abstract int getIdType();
	public abstract int getIdTypeDisponibility();
	public abstract int getIdCaserne();
	
	@Override
	public String toString() {
		return "Vehicule [getYear()=" + getYear() + ", getCoord()=" + getCoord() + ", getNumbIntMax()="
				+ getNumbIntMax() + ", getId()=" + getId() + ", getIdType()=" + getIdType()
				+ ", getIdTypeDisponibility()=" + getIdTypeDisponibility() + ", getIdCaserne()=" + getIdCaserne() + "]";
	}
}
