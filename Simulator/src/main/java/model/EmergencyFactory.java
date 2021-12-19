package model;

public class EmergencyFactory {
	public static Emergency getEmergency(EmergencyAbstractFactory factory){
		return factory.createEmergency();
	}
}
