package model;

public class EmergencyBuildingFactory {
	public static EmergencyBuilding getEmergencyBuilding(EmergencyBuildingAbstractFactory factory){
		return factory.createEmergencyBuilding();
	}
}
