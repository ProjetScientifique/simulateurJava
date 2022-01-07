package controller;

import java.io.IOException;
import java.util.ArrayList;

import model.Coord;
import model.Detector;
import model.Emergency;

public abstract class DetectorController {
	protected ArrayList<Detector> detectorArray = new ArrayList<>();	
	
	public void updateDetectors(Coord coordEmergency, double intensity) {
		for(Detector detector : detectorArray) {
			if (detector.inRange(coordEmergency)) {
				detector.setIntensity(coordEmergency, intensity);
			}
		}
	}
	
	public abstract void populateDetectorArray() throws IOException;
	
	public abstract String apiPostTriggeredDetector(Emergency emergency, Detector detector) throws IOException;
	
	public ArrayList<Detector> getDetectorArray(){
		return detectorArray;
	}
	
	public ArrayList<Detector> getTriggeredDetectorArray(){
		ArrayList<Detector> arrTriggeredDetectors = new ArrayList<Detector>();
		for(Detector detector : detectorArray) {
			if (detector.getIntensity() != 0) {
				arrTriggeredDetectors.add(detector);
			}
		}
		return arrTriggeredDetectors;
	}
	
	public void resetDetectors() {
		for(Detector detector : detectorArray) {
			detector.resetIntensity();
		}
	}
	
	@Override
	public String toString() {
		return "DetectorController [getDetectorArray()=" + getDetectorArray() + ", getTriggeredDetectorArray()="
				+ getTriggeredDetectorArray() + "]";
	}
}
