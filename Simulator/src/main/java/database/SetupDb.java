package database;

import java.io.IOException;
import java.util.ArrayList;

import controller.ControllerConfig;
import model.Coord;
import rest.SimulatorApi;

public class SetupDb {
	private SimulatorApi client;

	public SetupDb(SimulatorApi client) {
		super();
		this.client = client;
	}
	
	public ArrayList<String> postDetectors() throws IOException {
		ArrayList<String> arrRes = new ArrayList<String>();
		int i = 1;
		for (Coord coordForDetector : ControllerConfig.COORDS) {
			String res = client.postApi("detecteur", "{\r\n"
					+ "	  \"id_detecteur\": " + i + ",\r\n"
					+ "	  \"id_type_detecteur\": " + 1 + ",\r\n"
					+ "	  \"latitude_detecteur\": " + coordForDetector.getLatitude() + ",\r\n"
					+ "	  \"longitude_detecteur\": "+ coordForDetector.getLongitude() + ",\r\n"
					+ "	  \"nom_detecteur\": \"Sensor_" + i + "\"\r\n"
					+ "	}");
			arrRes.add(res);
			i += 1;
		}
		return arrRes;
	}
	
	public void resetDatabase() throws IOException {
		client.deleteApi("delete_all");
	}
}
