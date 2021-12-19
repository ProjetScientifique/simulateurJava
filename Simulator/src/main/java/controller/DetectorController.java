package controller;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Coord;
import model.Detector;

public abstract class DetectorController {
	protected ArrayList<Detector> detectorArray = new ArrayList<>();

	
	public void updateDetectors(Coord coordEmergency, double intensity) {
		for(Detector detector : detectorArray) {
			if (detector.inRange(coordEmergency)) {
				detector.setIntensity(coordEmergency, intensity);
			}
		}
	}
	
	public ArrayList<Detector> getDetectorArray(){
		return detectorArray;
	}
	
	public JSONObject getTriggeredDetectorArray(){
		JSONArray jsonArr = new JSONArray();
		JSONObject jsonString = new JSONObject()
				.put("Detectors", jsonArr);
		for(Detector detector : detectorArray) {
			if (detector.getIntensity() != 0) {
				jsonArr.put(new JSONObject()
						.put("longitude", detector.getCoord().getLongitude())
						.put("latitude", detector.getCoord().getLatitude())
						.put("intensity", detector.getIntensity())
				);
			}
		}
		return jsonString;
	}
	
	public void resetDetectors() {
		for(Detector detector : detectorArray) {
			detector.resetIntensity();
		}
	}
	
	@Override
	public String toString() {
		return "DetectorController [getDetectorArray()=" + getDetectorArray() + "]";
	}
}
