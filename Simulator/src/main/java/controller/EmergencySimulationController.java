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

public class EmergencySimulationController {
	private EmergencyApi emergencyApiClient;
	private ArrayList<Emergency> arrFireSimulated = new ArrayList<Emergency>();
	private ArrayList<Vehicule> arrVehicule = new ArrayList<Vehicule>();
	private ArrayList<EmergencyPeople> arrEmergencyPeople = new ArrayList<EmergencyPeople>();
	
	public EmergencySimulationController(EmergencyApi emergencyApiClient, ArrayList<Emergency> arrFireSimulated) {
		super();
		this.emergencyApiClient = emergencyApiClient;
		this.arrFireSimulated = arrFireSimulated;
	}

	public void simulateIntervention() throws IOException {
		matchSimulatedAndRealFire();
		ArrayList<Integer> arrIdEmergencyReal = instanciateRessourcesByEmergency();
		for(int idEmergency: arrIdEmergencyReal) {
			Emergency simulatedFireMatching = getFireSimulatedMatching(idEmergency);
			System.out.println(simulatedFireMatching);
			ArrayList<Vehicule> arrVehiculeForEmergency = getVehiculesForEmergency(idEmergency);
			System.out.println("VEHICULES : " + arrVehiculeForEmergency);
			ArrayList<EmergencyPeople> arrEmergencyPeopleForEmergency = getEmergencyPeopleForEmergency(idEmergency);
			System.out.println("FIREFIGHTERS : " + arrEmergencyPeopleForEmergency);
			Coord coordForEmergency = getCoord(idEmergency);
			for(Vehicule vehicule: arrVehiculeForEmergency) { // Should not be instantaneous
				vehicule.move(coordForEmergency);
				String resPatch = emergencyApiClient.patchApi("vehicule/" + vehicule.getId(), new JSONObject()
						.put("longitude_vehicule", coordForEmergency.getLongitude())
						.put("latitude_vehicule", coordForEmergency.getLatitude())
						.toString());
				System.out.println("PATCH : " + resPatch);
			}
			for(EmergencyPeople emergencyPeople: arrEmergencyPeopleForEmergency) { // Should do stuff depending on efficacy
				if (simulatedFireMatching.getIntensity() >= 1) {
					simulatedFireMatching.setIntensity(simulatedFireMatching.getIntensity() - 2);
					System.out.println("NEW : " + simulatedFireMatching);
				} else {
					System.out.println("Fire with id : " + idEmergency + " has been dealt with.");
					arrFireSimulated.remove(simulatedFireMatching);
					System.out.println("ARRAY : " + arrFireSimulated);
					ArrayList<String> arrRes = patchEmergencyDataBaseAndUninstanciateRessources(idEmergency, arrVehiculeForEmergency, arrEmergencyPeopleForEmergency);
					System.out.println("PATCH&DELETE : " + arrRes);
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
				arrIdEmergency.add(json.getInt("id_incident"));
			}
		}
				
		for(int id: arrIdEmergency) {
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
		return arrIdEmergency;
	}
	
	private void instanciateVehicule(int idVehicule, int idEmergency) throws IOException {
		JSONArray jsonArrVehicule = emergencyApiClient.getApi("vehicule/" + idVehicule);
		for(Object oBis: jsonArrVehicule) {
			JSONObject jsonVehicule = new JSONObject(oBis.toString());
			arrVehicule.add(new Truck(jsonVehicule.getInt("annee_vehicule"),
									jsonVehicule.getInt("nombre_intervention_maximum_vehicule"), 
									new Coord(jsonVehicule.getDouble("longitude_vehicule"), jsonVehicule.getDouble("latitude_vehicule")), 
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
		Coord coord = new Coord(longitude, latitude);
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
					System.out.println(distBetween);
					System.out.println(ControllerConfig.RANGE);
					if (distBetween <= ControllerConfig.RANGE) {
						fireSimulated.setIdEmergency(json.getInt("id_incident"));
					}else {
						System.out.println("Simulated fire : " + fireSimulated + " was not detected by the Emergency Manager !");
					}
				}
			}
		}
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
