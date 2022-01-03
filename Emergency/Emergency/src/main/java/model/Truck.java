package model;

public class Truck extends Vehicule {
	private int year;
	private Coord coord;
	private int numbIntMax;
	
	public Truck(int year, int numbIntMax, Coord coord) {
		super();
		this.year = year;
		this.coord = coord;
		this.numbIntMax = numbIntMax;
	}

	@Override
	public int getYear() {
		return year;
	}

	@Override
	public Coord getCoord() {
		return coord;
	}

	@Override
	public int getNumbIntMax() {
		return numbIntMax;
	}
}
