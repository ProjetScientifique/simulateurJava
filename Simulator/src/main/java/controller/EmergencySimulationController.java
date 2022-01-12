package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.Coord;
import model.Emergency;
import model.EmergencyPeople;
import model.Pompier;
import model.Truck;
import model.Vehicule;
import rest.EmergencyApi;
import rest.Map;

// En gros, jpense qu'il me fait les coord des casernes pr les véhicules, puis jfais bouger le truc, chaque tr je prend la postion actuelle du camion et jle fais avancer au prochaint point
// UNE FOIS Sur place ça taf sur le feu
// une fois feu finit, retour ac la même logique qu'a l'aller
// une fois le chemin fini, on patch tt, véhicule dispo, pompier dispo etc...

public class EmergencySimulationController {
	private Map mapClient;
	private EmergencyApi emergencyApiClient;
	private ArrayList<Emergency> arrFireSimulated = new ArrayList<Emergency>();
	private ArrayList<Vehicule> arrVehicule = new ArrayList<Vehicule>();
	private ArrayList<EmergencyPeople> arrEmergencyPeople = new ArrayList<EmergencyPeople>();
	private ArrayList<Integer> arrIdEmergencySimulationStarted = new ArrayList<Integer>();
	
	public EmergencySimulationController(EmergencyApi emergencyApiClient, ArrayList<Emergency> arrFireSimulated, Map mapClient) {
		super();
		this.emergencyApiClient = emergencyApiClient;
		this.arrFireSimulated = arrFireSimulated;
		this.mapClient = mapClient;
	}

	public void simulateIntervention() throws IOException {
		matchSimulatedAndRealFire();
		ArrayList<Integer> arrIdEmergencyReal = instanciateRessourcesByEmergency();
		for(Emergency fireSimulated: arrFireSimulated) { // If fire isn't being taken care of, increase intensity.
			if (!arrIdEmergencyReal.contains(fireSimulated.getIdEmergency())){
				if ((fireSimulated.getIntensity() + 1) >= 10) {
					fireSimulated.setIntensity(10);
				} else {
					fireSimulated.setIntensity(fireSimulated.getIntensity() + 1);
				}
			}
		}
		for(int idEmergency: arrIdEmergencyReal) {
			Emergency simulatedFireMatching = getFireSimulatedMatching(idEmergency);
			ArrayList<Vehicule> arrVehiculeForEmergency = getVehiculesForEmergency(idEmergency);
			ArrayList<EmergencyPeople> arrEmergencyPeopleForEmergency = getEmergencyPeopleForEmergency(idEmergency);
			Coord coordForEmergency = getCoord(idEmergency);
			for(Vehicule vehicule: arrVehiculeForEmergency) {
				if (! vehicule.isDone()) {
					Coord nextCoord = vehicule.move(coordForEmergency, mapClient);
					emergencyApiClient.patchApi("vehicule/" + vehicule.getId(), new JSONObject()
							.put("longitude_vehicule", nextCoord.getLongitude())
							.put("latitude_vehicule", nextCoord.getLatitude())
							.toString());
					if (vehicule.getCoord().equal(coordForEmergency)) {
						for(EmergencyPeople emergencyPeople: arrEmergencyPeopleForEmergency) { // Should do stuff depending on efficacy
							if (simulatedFireMatching.getIntensity() >= 1) {
								simulatedFireMatching.setIntensity(simulatedFireMatching.getIntensity() - 1);
							} else {
								System.out.println("Fire with id : " + idEmergency + " has been dealt with, vehicule is returning to the caserne.");
								arrFireSimulated.remove(simulatedFireMatching);							
							}
						}
					} else {
						System.out.println("Vehicule : " + vehicule + " hasn't arrived yet.");
					}
				} else {
					Coord coordCaserne = getCoordCaserneForVehicule(vehicule.getId());
					Coord nextCoord = vehicule.move(coordCaserne, mapClient);
					String resPatch = emergencyApiClient.patchApi("vehicule/" + vehicule.getId(), new JSONObject()
							.put("longitude_vehicule", nextCoord.getLongitude())
							.put("latitude_vehicule", nextCoord.getLatitude())
							.toString());
					System.out.println("PATCHBIS : " + resPatch);
					if (vehicule.getCoord().equal(coordCaserne)) {
						ArrayList<String> arrRes = patchEmergencyDataBaseAndUninstanciateRessources(idEmergency, arrVehiculeForEmergency, arrEmergencyPeopleForEmergency);
						System.out.println("PATCH&DELETE : " + arrRes);
					} else {
						System.out.println("Vehicule : " + vehicule + " is still returning to it's caserne");
					}
				}
			}
					
		}
	}
	
	private ArrayList<Integer> instanciateRessourcesByEmergency() throws IOException { // POUR SAVOIR CE QUI EST PAS ENCORE FINI, FAUT FETCH L INCIDENT ET VOIR SON TYPE SI C EST 4 BA OSEF
		JSONArray jsonArr = emergencyApiClient.getApi("intervients");
		ArrayList<Integer> arrIdEmergency = new ArrayList<Integer>();
		ArrayList<Integer> arrIdVehicule = new ArrayList<Integer>();
		
		for(Object o: jsonArr) {
			JSONObject json = new JSONObject(o.toString());
			if (!arrIdEmergency.contains(json.getInt("id_incident"))) {
				JSONArray jsonArrBis = emergencyApiClient.getApi("incident/" + json.getInt("id_incident"));
				if (!(jsonArrBis.getJSONObject(0).getInt("id_type_status_incident") == EmergencyApi.idTypeStatusEmergencyExtinguished)) {
					arrIdEmergency.add(json.getInt("id_incident"));
				}
			}
		}
				
		for(int id: arrIdEmergency) {
			if (! arrIdEmergencySimulationStarted.contains(id)) { // Only instantiate if the ressources haven't been initialized yet for the emergency concerned
				arrIdEmergencySimulationStarted.add(id);
				for(Object o: jsonArr) {
					JSONObject json = new JSONObject(o.toString());
					if (json.getInt("id_incident") == id) {
						instanciateEmergencyPeople(json.getInt("id_pompier"), id);
						if (!arrIdVehicule.contains(json.getInt("id_vehicule"))) {
							instanciateVehicule(json.getInt("id_vehicule"), id);
							arrIdVehicule.add(json.getInt("id_vehicule"));
						}
					}
				}
			}
		}
		return arrIdEmergency;
	}
	
	private void instanciateVehicule(int idVehicule, int idEmergency) throws IOException {
		JSONArray jsonArrVehicule = emergencyApiClient.getApi("vehicule/" + idVehicule);
		for(Object oBis: jsonArrVehicule) {
			JSONObject jsonVehicule = new JSONObject(oBis.toString());
			arrVehicule.add(new Truck(jsonVehicule.getInt("annee_vehicule"),
									jsonVehicule.getInt("nombre_intervention_maximum_vehicule"), 
									new Coord(jsonVehicule.getDouble("latitude_vehicule"), jsonVehicule.getDouble("longitude_vehicule")), 
									jsonVehicule.getInt("id_vehicule"),
									jsonVehicule.getInt("id_type_vehicule"),
									idEmergency));
		}
	}
	
	private void instanciateEmergencyPeople(int idPompier, int idEmergency) throws IOException {
		JSONArray jsonArrEmergencyPeople = emergencyApiClient.getApi("pompier/" + idPompier);
		for(Object oBis: jsonArrEmergencyPeople) {
			JSONObject jsonEmergencyPeople = new JSONObject(oBis.toString());
			arrEmergencyPeople.add(new Pompier(jsonEmergencyPeople.getString("prenom_pompier"),
									jsonEmergencyPeople.getString("nom_pompier"), 
									LocalDate.parse(jsonEmergencyPeople.getString("date_naissance_pompier")), 
									jsonEmergencyPeople.getInt("nombre_intervention_jour_maximum_pompier"),
									jsonEmergencyPeople.getBoolean("disponibilite_pompier"),
									jsonEmergencyPeople.getInt("id_pompier"),
									jsonEmergencyPeople.getInt("id_type_pompier"),
									idEmergency));
		}
	}
	
	private ArrayList<Vehicule> getVehiculesForEmergency(int idEmergency) {
		ArrayList<Vehicule> arrVehiculeForEmergency = new ArrayList<Vehicule>();
		for(Vehicule vehicule: arrVehicule) {
			if (vehicule.getIdEmergency() == idEmergency) {
				arrVehiculeForEmergency.add(vehicule);
			}
		}
		return arrVehiculeForEmergency;
	}
	
	private ArrayList<EmergencyPeople> getEmergencyPeopleForEmergency(int idEmergency) {
		ArrayList<EmergencyPeople> arrEmergencyPeopleForEmergency = new ArrayList<EmergencyPeople>();
		for(EmergencyPeople emergencyPeople: arrEmergencyPeople) {
			if (emergencyPeople.getIdEmergency() == idEmergency) {
				arrEmergencyPeopleForEmergency.add(emergencyPeople);
			}
		}
		return arrEmergencyPeopleForEmergency;
	}
	
	private Coord getCoord(int idEmergency) throws IOException {
		JSONArray jsonArr = emergencyApiClient.getApi("incident/" + idEmergency);
		double longitude = 0;
		double latitude = 0;
		for(Object o: jsonArr) {
			JSONObject json = new JSONObject(o.toString());
			longitude = json.getDouble("longitude_incident");
			latitude = json.getDouble("latitude_incident");
		}
		Coord coord = new Coord(latitude, longitude);
		return coord;
	}
	
	private Emergency getFireSimulatedMatching(int idEmergency) {
		for (Emergency fireSimulated: arrFireSimulated) {
			if (fireSimulated.getIdEmergency() == idEmergency) {
				return fireSimulated;
			}
		}
		return null;
	}
	
	private void matchSimulatedAndRealFire() throws IOException {
		JSONArray jsonArr = emergencyApiClient.getApi("incidents");
		for(Object o: jsonArr) {
			JSONObject json = new JSONObject(o.toString());
			if (json.getInt("id_type_status_incident") != EmergencyApi.idTypeStatusEmergencyForDetected) {
				for(Emergency fireSimulated: arrFireSimulated) {
					double distBetween = Math.sqrt(Math.pow(fireSimulated.getCoord().getLongitude() - json.getDouble("longitude_incident"), 2) + Math.pow(fireSimulated.getCoord().getLatitude() - json.getDouble("latitude_incident"), 2));
					if (distBetween <= ControllerConfig.RANGE) {
						fireSimulated.setIdEmergency(json.getInt("id_incident"));
					}else {
						System.out.println("Simulated fire : " + fireSimulated + " was not detected by the Emergency Manager !");
					}
				}
			}
		}
	}
	
	private Coord getCoordCaserneForVehicule(int idVehicule) throws JSONException, IOException {
		JSONObject jsonCaserne = emergencyApiClient.getApi("caserne/" + emergencyApiClient.getApi("vehicule/" + idVehicule).getJSONObject(0).getInt("id_caserne")).getJSONObject(0);
		Coord coordCaserne = new Coord(jsonCaserne.getDouble("latitude_caserne"), jsonCaserne.getDouble("longitude_caserne"));
		return coordCaserne;
	}
	
	private ArrayList<String> patchEmergencyDataBaseAndUninstanciateRessources(int idEmergency, ArrayList<Vehicule> arrVehicule, ArrayList<EmergencyPeople> arrEmergencyPeople) throws JSONException, IOException {
		ArrayList<String> arrRes = new ArrayList<String>();
		String resEmergency = emergencyApiClient.patchApi("incident/" + idEmergency, new JSONObject() //Emergency
				.put("id_type_status_incident", EmergencyApi.idTypeStatusEmergencyExtinguished)
				.toString());
		arrRes.add(resEmergency);

		for(Vehicule vehicule: arrVehicule) {
			String resVehicule = emergencyApiClient.patchApi("vehicule/" + vehicule.getId(), new JSONObject() //Emergency
					.put("id_type_disponibilite_vehicule", EmergencyApi.idTypeDispoVehiculeAvailable)
					.toString());
			vehicule = null;
			arrRes.add(resVehicule);
		}
		
		for(EmergencyPeople emergencyPeople: arrEmergencyPeople) {
			String resEmergencyPeople = emergencyApiClient.patchApi("pompier/" + emergencyPeople.getId(), new JSONObject() //Emergency
					.put("disponibilite_pompier", true)
					.toString());
			emergencyPeople = null;
			arrRes.add(resEmergencyPeople);
		}
		
		return arrRes;
	}
}
