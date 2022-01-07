package controller;

import java.io.IOException;

import model.Emergency;

public abstract class EmergencyController {
	public abstract Emergency generateEmergency();

	public abstract String apiPostEmergency(Emergency emergency) throws IOException;
	
	@Override
	public String toString() {
		return "EmergencyController [generateEmergency()=" + generateEmergency() + "]";
	}
}
