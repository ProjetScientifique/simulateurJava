package model;

public class CaserneFactory implements EmergencyBuildingAbstractFactory {
	private String name;
	private Coord coord;
	
	public CaserneFactory(String name, Coord coord) {
		super();
		this.name = name;
		this.coord = coord;
	}

	@Override
	public EmergencyBuilding createEmergencyBuilding() {
		return new Caserne(name, coord);
	}
}
