package model;

public class Caserne extends EmergencyBuilding {
	private String name;
	private Coord coord;
	private int id;
	
	public Caserne(String name, Coord coord, int id) {
		super();
		this.name = name;
		this.coord = coord;
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Coord getCoord() {
		return coord;
	}

	@Override
	public int getId() {
		return id;
	}
}
