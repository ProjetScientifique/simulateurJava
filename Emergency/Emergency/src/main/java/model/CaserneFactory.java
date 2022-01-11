package model;

import java.util.ArrayList;

public class CaserneFactory implements EmergencyBuildingAbstractFactory {
	private String name;
	private Coord coord;
	private int id;
	private ArrayList<EmergencyPeople> arrEmergencyPeople;
	private ArrayList<Vehicule> arrVehicule;
	
	public CaserneFactory(String name, Coord coord, int id, ArrayList<EmergencyPeople> arrEmergencyPeople, ArrayList<Vehicule> arrVehicule) {
		super();
		this.name = name;
		this.coord = coord;
		this.id = id;
		this.arrEmergencyPeople = arrEmergencyPeople;
		this.arrVehicule = arrVehicule;
	}

	@Override
	public EmergencyBuilding createEmergencyBuilding() {
		return new Caserne(name, coord, id, arrEmergencyPeople, arrVehicule);
	}
}
