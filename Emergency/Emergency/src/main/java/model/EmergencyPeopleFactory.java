package model;

public class EmergencyPeopleFactory {
	public static EmergencyPeople getEmergencyPeople(EmergencyPeopleAbstractFactory factory){
		return factory.createEmergencyPeople();
	}
}
