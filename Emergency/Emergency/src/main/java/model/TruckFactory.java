package model;

public class TruckFactory implements VehiculeAbstractFactory {
	private int year;
	private Coord coord;
	private int numbIntMax;
	
	public TruckFactory(int year, int numbIntMax, Coord coord) {
		super();
		this.year = year;
		this.coord = coord;
		this.numbIntMax = numbIntMax;
	}

	@Override
	public Vehicule createVehicule() {
		return new Truck(year, numbIntMax, coord);
	}
}
