package model;

import java.time.LocalDate;

public class PompierFactory implements EmergencyPeopleAbstractFactory {
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	private int nbMaxIntPerDay;
	private Boolean disponibility;
	private int id;
	private int idType;
	private int idCaserne;
	
	public PompierFactory(String firstName, String lastName, LocalDate birthDate, int nbMaxIntPerDay, Boolean disponibility, int id, int idType, int idCaserne) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.nbMaxIntPerDay = nbMaxIntPerDay;
		this.disponibility = disponibility;
		this.id = id;
		this.idType = idType;
		this.idCaserne = idCaserne;
	}

	@Override
	public EmergencyPeople createEmergencyPeople() {
		return new Pompier(firstName, lastName, birthDate, nbMaxIntPerDay, disponibility, id, idType, idCaserne);
	}
}
