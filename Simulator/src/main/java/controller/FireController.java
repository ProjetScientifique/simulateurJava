package controller;

import java.io.IOException;
import java.sql.Timestamp;

import org.apache.commons.math3.util.Precision;
import org.json.JSONObject;

import model.Coord;
import model.Emergency;
import model.EmergencyFactory;
import model.FireFactory;
import rest.SimulatorApi;

public class FireController extends EmergencyController {
	private SimulatorApi client;
	
	public FireController(SimulatorApi client) {
		super();
		this.client = client;
	}
	
	@Override
	public Emergency generateEmergency() {
		double longitude = Precision.round(ControllerConfig.LONGITUDE_MIN + (Math.random() * (ControllerConfig.LONGITUDE_MAX - ControllerConfig.LONGITUDE_MIN)), 7);
		double latitude = Precision.round(ControllerConfig.LATITUDE_MIN + (Math.random() * (ControllerConfig.LATITUDE_MAX - ControllerConfig.LATITUDE_MIN)), 7);
		Coord coordEmergency = new Coord(longitude, latitude);
		return EmergencyFactory.getEmergency(new FireFactory(Precision.round(Math.random() * 10, 2), new Timestamp(System.currentTimeMillis()), coordEmergency));
	}

	@Override
	public String apiPostEmergency(Emergency emergency) throws IOException {
		String res = client.postApi("incident", "{\r\n"
			+ "	  \"id_type_incident\": " + 1 + ",\r\n"
			+ "	  \"latitude_incident\": " + emergency.getCoord().getLatitude() + ",\r\n"
			+ "	  \"longitude_incident\": " + emergency.getCoord().getLongitude() + ",\r\n"
			+ "	  \"intensite_incident\": " + emergency.getIntensity() + ",\r\n"
			+ "	  \"date_incident\": \"" + java.time.LocalDateTime.now() + "\"\r\n"
			+ "}");
		System.out.println(res);
		emergency.setId(new JSONObject(res).getInt("id_incident"));
		return res;
	}
}