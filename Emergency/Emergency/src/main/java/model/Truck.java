package model;

public class Truck extends Vehicule {
	private int year;
	private Coord coord;
	private int numbIntMax;
	private int id;
	private int idType;
	private int idTypeDisponibility;
	private int idCaserne;
	
	public Truck(int year, int numbIntMax, Coord coord, int id, int idType, int idTypeDisponibility, int idCaserne) {
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

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getIdType() {
		return idType;
	}

	@Override
	public int getIdTypeDisponibility() {
		return idTypeDisponibility;
	}
	
	@Override
	public int getIdCaserne() {
		return idCaserne;
	}
}
