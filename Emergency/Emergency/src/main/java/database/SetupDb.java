package database;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import controller.ControllerConfig;
import model.Coord;
import rest.EmergencyApi;

public class SetupDb {
	private EmergencyApi client;

	public SetupDb(EmergencyApi client) {
		super();
		this.client = client;
	}
	
	public ArrayList<String> postDetectors() throws IOException {
		ArrayList<String> arrRes = new ArrayList<String>();
		for (Coord coordForDetector : ControllerConfig.COORDS) {
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
	
	public void resetDatabase() throws IOException {
		client.deleteApi("delete_all", "");
	}
}