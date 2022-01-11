package model;

import java.time.LocalDate;

public abstract class EmergencyPeople {
	public abstract String getName();
	public abstract int getNumbMaxIntPerDay();
	public abstract LocalDate getBirthDate();
	public abstract Boolean getDisponibility();
	public abstract int getId();
	public abstract int getIdType();
	public abstract int getIdEmergency();
	
	@Override
	public String toString() {
		return "EmergencyPeople [getName()=" + getName() + ", getNumbMaxIntPerDay()=" + getNumbMaxIntPerDay()
				+ ", getBirthDate()=" + getBirthDate() + ", getDisponibility()=" + getDisponibility() + ", getId()="
				+ getId() + ", getIdType()=" + getIdType() + ", getIdEmergency()=" + getIdEmergency() + "]";
	}
}
