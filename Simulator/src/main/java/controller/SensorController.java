package controller;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Coord;
import model.Detector;
import model.DetectorFactory;
import model.Emergency;
import model.SensorFactory;
import rest.SimulatorApi;

public class SensorController extends DetectorController{
	private SimulatorApi client;
	
	public SensorController(SimulatorApi client) {
		super();
		this.client = client;		
	}
	
	@Override
	public void populateDetectorArray() throws IOException {
		JSONArray response = client.getApi("detecteurs");
		for(Object o: response) {
			JSONObject jsonElem = new JSONObject(o.toString());
			detectorArray.add(DetectorFactory.getDetector(new SensorFactory(0, jsonElem.getString("nom_detecteur"), new Coord(jsonElem.getDouble("latitude_detecteur"), jsonElem.getDouble("longitude_detecteur")), ControllerConfig.RANGE, jsonElem.getInt("id_detecteur"))));
		}
	}

	@Override
	public String apiPostTriggeredDetector(Emergency emergency, Detector detector) throws IOException {
		JSONObject json = new JSONObject()
				.put("id_incident", emergency.getId())
				.put("id_detecteur", detector.getId())
				.put("date_detecte", java.time.LocalDateTime.now())
				.put("intensite_detecte", detector.getIntensity());
		String res = client.postApi("detecte", json.toString());
		return res;
	}
	
	@Override
	public ArrayList<String> apiPatchTriggeredDetector(Emergency emergency, Detector detector) throws IOException {
		ArrayList<String> arrRes = new ArrayList<String>();
		String resDelete = client.deleteDetecte(emergency.getId(), detector.getId()); ///////
		arrRes.add(resDelete);
		String resPost = client.postApi("detecte", new JSONObject()
				.put("id_incident", emergency.getId())
				.put("id_detecteur", detector.getId())
				.put("date_detecte", java.time.LocalDateTime.now())
				.put("intensite_detecte", detector.getIntensity())
				.toString());
		arrRes.add(resPost);
		return arrRes;
	}
}