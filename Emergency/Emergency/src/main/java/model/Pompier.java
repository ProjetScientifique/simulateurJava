package model;

import java.sql.Date;

public class Pompier extends EmergencyPeople {
	private String firstName;
	private String lastName;
	private int nbMaxIntPerDay;
	private Date birthDate;
	private Boolean disbonibility;
	
	public Pompier(String firstName, String lastName, Date birthDate, int nbMaxIntPerDay, Boolean disponibilty) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.nbMaxIntPerDay = nbMaxIntPerDay;
		this.disbonibility = disponibilty;
	}

	@Override
	public String getName() {
		return firstName + lastName;
	}
	
	@Override
	public int getNumbMaxIntPerDay() {
		return nbMaxIntPerDay;
	}

	@Override
	public Date getBirthDate() {
		return birthDate;
	}
	
	@Override
	public Boolean getDisponibility() {
		return disbonibility;
	}
}
