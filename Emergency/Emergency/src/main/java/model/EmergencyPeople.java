package model;

import java.sql.Date;

public abstract class EmergencyPeople {
	public abstract String getName();
	public abstract int getNumbMaxIntPerDay();
	public abstract Date getBirthDate();
	public abstract Boolean getDisponibility();

	
	@Override
	public String toString() {
		return "EmergencyPeople [getName()=" + getName() + ", getNumbMaxIntPerDate()=" + getNumbMaxIntPerDay()
				+ ", getBirthDate()=" + getBirthDate() + ", getDisponibility()=" + getDisponibility() + "]";
	}
}
