package model;

public class CaserneFactory implements EmergencyBuildingAbstractFactory {
	private String name;
	private Coord coord;
	private int id;
	
	public CaserneFactory(String name, Coord coord, int id) {
		super();
		this.name = name;
		this.coord = coord;
		this.id = id;
	}

	@Override
	public EmergencyBuilding createEmergencyBuilding() {
		return new Caserne(name, coord, id);
	}
}
