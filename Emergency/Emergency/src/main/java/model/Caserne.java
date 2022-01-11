package model;

import java.util.ArrayList;

public class Caserne extends EmergencyBuilding {
	private String name;
	private Coord coord;
	private int id;
	private ArrayList<EmergencyPeople> arrEmergencyPeople;
	private ArrayList<Vehicule> arrVehicule;
	
	public Caserne(String name, Coord coord, int id, ArrayList<EmergencyPeople> arrEmergencyPeople, ArrayList<Vehicule> arrVehicule) {
		super();
		this.name = name;
		this.coord = coord;
		this.id = id;
		this.arrEmergencyPeople = arrEmergencyPeople;
		this.arrVehicule = arrVehicule;
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

	@Override
	public ArrayList<EmergencyPeople> getEmergencyPeople() {
		return arrEmergencyPeople;
	}

	@Override
	public ArrayList<Vehicule> getArrVehicule() {
		return arrVehicule;
	}

	@Override
	public ArrayList<EmergencyPeople> getAvailableEmergencyPeople() {
		ArrayList<EmergencyPeople> arrAvailable = new ArrayList<EmergencyPeople>();
		for(EmergencyPeople emergencyPeople: arrEmergencyPeople) {
			if (emergencyPeople.getDisponibility() == true) {
				arrAvailable.add(emergencyPeople);
			}
		}
		return arrAvailable;
	}

	@Override
	public ArrayList<Vehicule> getAvailableVehicules() {
		ArrayList<Vehicule> arrAvailable = new ArrayList<Vehicule>();
		for(Vehicule vehicule: arrVehicule) {
			if (vehicule.getIdTypeDisponibility() == 1) { // 2 = Indisponible & 1 = Diponible
				arrAvailable.add(vehicule);
			}
		}
		return arrAvailable;
	}

	@Override
	public ArrayList<EmergencyPeople> getAvailableEmergencyPeopleByType(int idType) { // 1 = Caporal-Chef & 2 = Caporal
		ArrayList<EmergencyPeople> arrAvailabeOfType = new ArrayList<EmergencyPeople>();
		for(EmergencyPeople emergencyPeople: arrEmergencyPeople) {
			if (emergencyPeople.getIdType() == idType && emergencyPeople.getDisponibility()==true) {
				arrAvailabeOfType.add(emergencyPeople);
			}
		}
		return arrAvailabeOfType;
	}
}
