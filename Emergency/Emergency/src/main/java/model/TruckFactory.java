package model;

public class TruckFactory implements VehiculeAbstractFactory {
	private int year;
	private Coord coord;
	private int numbIntMax;
	private int id;
	private int idType;
	private int idTypeDisponibility;
	private int idCaserne;
	
	public TruckFactory(int year, int numbIntMax, Coord coord, int id, int idType, int idTypeDisponibility, int idCaserne) {
		super();
		this.year = year;
		this.coord = coord;
		this.numbIntMax = numbIntMax;
		this.id = id;
		this.idType = idType;
		this.idTypeDisponibility = idTypeDisponibility;
		this.idCaserne = idCaserne;
	}

	@Override
	public Vehicule createVehicule() {
		return new Truck(year, numbIntMax, coord, id, idType, idTypeDisponibility, idCaserne);
	}
}
