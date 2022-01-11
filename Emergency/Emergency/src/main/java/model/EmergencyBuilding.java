package model;

import java.util.ArrayList;

public abstract class EmergencyBuilding {
	public abstract String getName();
	public abstract Coord getCoord();
	public abstract int getId();
	public abstract ArrayList<EmergencyPeople> getEmergencyPeople();
	public abstract ArrayList<Vehicule> getArrVehicule();
	public abstract ArrayList<EmergencyPeople> getAvailableEmergencyPeople();
	public abstract ArrayList<Vehicule> getAvailableVehicules();
	public abstract ArrayList<EmergencyPeople> getAvailableEmergencyPeopleByType(int idType);
	
	@Override
	public String toString() {
		return "EmergencyBuilding [getName()=" + getName() + ", getCoord()=" + getCoord() + ", getId()=" + getId()
				+ ", getEmergencyPeople()=" + getEmergencyPeople() + ", getArrVehicule()=" + getArrVehicule()
				+ ", getAvailableEmergencyPeople()=" + getAvailableEmergencyPeople() + ", getAvailableVehicules()="
				+ getAvailableVehicules() + "]";
	}
}
