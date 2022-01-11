package model;

import java.time.LocalDate;

public class Pompier extends EmergencyPeople {
	private String firstName;
	private String lastName;
	private int nbMaxIntPerDay;
	private LocalDate birthDate;
	private Boolean disbonibility;
	private int id;
	private int idType;
	private int idCaserne;
	
	public Pompier(String firstName, String lastName, LocalDate birthDate, int nbMaxIntPerDay, Boolean disponibilty, int id, int idType, int idCaserne) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.nbMaxIntPerDay = nbMaxIntPerDay;
		this.disbonibility = disponibilty;
		this.id = id;
		this.idType = idType;
		this.idCaserne = idCaserne;
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
	public LocalDate getBirthDate() {
		return birthDate;
	}
	
	@Override
	public Boolean getDisponibility() {
		return disbonibility;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getIdType() {
		return idType;
	}
	
	@Override
	public int getIdCaserne() {
		return idCaserne;
	}
}
