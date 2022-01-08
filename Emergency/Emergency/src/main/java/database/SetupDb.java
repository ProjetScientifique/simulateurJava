package database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.javafaker.Faker;

import controller.ControllerConfig;
import model.Coord;
import model.EmergencyBuilding;
import rest.EmergencyApi;

public class SetupDb {
	private EmergencyApi client;
	private Faker faker = new Faker(new Locale("fr"));
	private Calendar cal = Calendar.getInstance();

	public SetupDb(EmergencyApi client) {
		super();
		this.client = client;
	}
	
	public ArrayList<String> postDetectors() throws IOException {
		ArrayList<String> arrRes = new ArrayList<String>();
		for (Coord coordForDetector : ControllerConfig.COORDS_DETECTORS) {
			String res = client.postApi("detecteur", "{\r\n"
					+ "	  \"id_type_detecteur\": " + 1 + ",\r\n"
					+ "	  \"latitude_detecteur\": " + coordForDetector.getLatitude() + ",\r\n"
					+ "	  \"longitude_detecteur\": " + coordForDetector.getLongitude() + "\r\n"
					+ "	}");
			arrRes.add(res);
		}
		return arrRes;
	}
	
	public String postFakeEmergency() throws JSONException, IOException {
		String res = client.postApi("incident", new JSONObject()
				.put("id_type_incident", EmergencyApi.idTypeEmergencyPotential)
				.put("latitude_incident", 0)
				.put("longitude_incident", 0)
				.put("intensite_incident", 10)
				.put("date_incident", "1999-01-01T00:00:00.000Z")
				.put("id_type_status_incident", EmergencyApi.idTypeStatusEmergency)
				.toString());
		System.out.println(res);
		return res;
	}
	
	public ArrayList<String> postEmergencyBuildings() throws JSONException, IOException {
		ArrayList<String> arrRes = new ArrayList<String>();
		for (Coord coordForEmergencyBuilding : ControllerConfig.COORDS_DETECTORS) {
			String res = client.postApi("caserne", new JSONObject()
					.put("nom_caserne", faker.name().lastName())
					.put("latitude_caserne", coordForEmergencyBuilding.getLatitude())
					.put("longitude_caserne", coordForEmergencyBuilding.getLongitude())
					.toString());
			arrRes.add(res);
		}
		return arrRes;
	}
	
	public ArrayList<String> postCaporalChef(EmergencyBuilding emergencyBuilding, int nbToCreate) throws JSONException, IllegalArgumentException, IOException {
		ArrayList<String> arrRes = new ArrayList<String>();
		cal.set(1999, Calendar.JANUARY, 1);
		Date firstDate = cal.getTime();
		cal.set(2000, Calendar.JANUARY, 1);
		Date lastDate = cal.getTime();
		for (int i = 0; i < nbToCreate; i++) {
			String res = client.postApi("pompier", new JSONObject()
					.put("id_caserne", emergencyBuilding.getId())
					.put("id_type_pompier", 1)
					.put("nom_pompier", faker.name().lastName())
					.put("prenom_pompier", faker.name().firstName())
					.put("date_naissance_pompier", faker.date().between(firstDate, lastDate))
					.put("nombre_intervention_jour_maximum_pompier", ControllerConfig.NB_MAX_INTERV_PER_DAY_EMERGENCYPEOPLE)
					.put("disponibilite_pompier", true)
					.toString());
			arrRes.add(res);
		}
		return arrRes;
	}
	
	public ArrayList<String> postCaporal(EmergencyBuilding emergencyBuilding, int nbToCreate) throws JSONException, IllegalArgumentException, IOException {
		ArrayList<String> arrRes = new ArrayList<String>();
		cal.set(1999, Calendar.JANUARY, 1);
		Date firstDate = cal.getTime();
		cal.set(2000, Calendar.JANUARY, 1);
		Date lastDate = cal.getTime();
		for (int i = 0; i < nbToCreate; i++) {
			String res = client.postApi("pompier", new JSONObject()
					.put("id_caserne", emergencyBuilding.getId())
					.put("id_type_pompier", 2)
					.put("nom_pompier", faker.name().lastName())
					.put("prenom_pompier", faker.name().firstName())
					.put("date_naissance_pompier", faker.date().between(firstDate, lastDate))
					.put("nombre_intervention_jour_maximum_pompier", ControllerConfig.NB_MAX_INTERV_PER_DAY_EMERGENCYPEOPLE)
					.put("disponibilite_pompier", true)
					.toString());
			arrRes.add(res);
		}
		return arrRes;
	}
	
	public ArrayList<String> postCamionCiterne(EmergencyBuilding emergencyBuilding, int nbToCreate) throws JSONException, IllegalArgumentException, IOException {
		ArrayList<String> arrRes = new ArrayList<String>();
		for (int i = 0; i < 5; i++) {
			String res = client.postApi("pompier", new JSONObject()
					.put("id_caserne", emergencyBuilding.getId())
					.put("id_type_vehicule", 1)
					.put("id_type_disponibilite_vehicule", 1) // 1 = Disponible
					.put("annee_vehicule", 5)
					.put("nombre_intervention_maximum_vehicule", ControllerConfig.NB_MAX_INTERV_PER_DAY_VEHICULE)
					.put("latitude_vehicule", emergencyBuilding.getCoord().getLatitude())
					.put("longitude_vehicule", emergencyBuilding.getCoord().getLongitude())
					.toString());
			arrRes.add(res);
		}
		return arrRes;
	}
	
	public void resetDatabase() throws IOException {
		client.deleteApi("delete_all", "");
	}
}