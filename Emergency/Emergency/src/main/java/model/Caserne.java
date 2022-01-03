package model;

public class Caserne extends EmergencyBuilding {
	private String name;
	private Coord coord;
	
	public Caserne(String name, Coord coord) {
		super();
		this.name = name;
		this.coord = coord;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Coord getCoord() {
		return coord;
	}
}
