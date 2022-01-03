package model;

import java.sql.Date;

public class PompierFactory implements EmergencyPeopleAbstractFactory {
	private String firstName;
	private String lastName;
	private Date birthDate;
	private int nbMaxIntPerDay;
	private Boolean disponibility;
	
	public PompierFactory(String firstName, String lastName, Date birthDate, int nbMaxIntPerDay, Boolean disponibility) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.nbMaxIntPerDay = nbMaxIntPerDay;
		this.disponibility = disponibility;
	}

	@Override
	public EmergencyPeople createEmergencyPeople() {
		return new Pompier(firstName, lastName, birthDate, nbMaxIntPerDay, disponibility);
	}
}
