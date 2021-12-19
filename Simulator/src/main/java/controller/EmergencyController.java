package controller;

import model.Emergency;

public abstract class EmergencyController {
	public abstract Emergency generateEmergency();

	@Override
	public String toString() {
		return "EmergencyController [generateEmergency()=" + generateEmergency() + "]";
	}
}
