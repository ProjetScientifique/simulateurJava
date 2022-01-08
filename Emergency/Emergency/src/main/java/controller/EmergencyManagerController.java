package controller;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.json.JSONException;
import org.json.JSONObject;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import model.Coord;
import model.Detector;
import model.Emergency;
import mqtt.BrokerMqtt;
import rest.EmergencyApi;

public class EmergencyManagerController {
	private BrokerMqtt clientMqtt;

	public EmergencyManagerController(BrokerMqtt clientMqtt) {
		super();
		this.clientMqtt = clientMqtt;
	}
	
	// Get Detectors that detected the same fire (Requires that only one fire can be set per turn ?)
	private ArrayList<ArrayList<Detector>> getLinkedDetectors() {
		// Clone the Data to prevent deleting data too early
		ArrayList<Detector> arrTriggeredDetectors = clientMqtt.getArrTriggeredDetectors();
		ArrayList<Detector> cloneArrTriggeredDetectors = new ArrayList<Detector>();
		cloneArrTriggeredDetectors.addAll(arrTriggeredDetectors);
		ArrayList<ArrayList<Detector>> arrLinkPerDetector = new ArrayList<ArrayList<Detector>>();
		// For every element, we'll take it and check with every other element of the array if it is linked with it. If it's the case, remove the element from the array and update j accordingly.
		for (int i = 0; i < cloneArrTriggeredDetectors.size(); i++) {
			ArrayList<Detector> arrLinkedDetectors = new ArrayList<Detector>();
			arrLinkedDetectors.add(cloneArrTriggeredDetectors.get(i));
			cloneArrTriggeredDetectors.remove(i);
			for (int j = 0; j < cloneArrTriggeredDetectors.size(); j++) {
				if ( (cloneArrTriggeredDetectors.get(i).getCoord().getLongitude() == cloneArrTriggeredDetectors.get(j).getCoord().getLongitude()) || (cloneArrTriggeredDetectors.get(i).getCoord().getLatitude() == cloneArrTriggeredDetectors.get(j).getCoord().getLatitude())) {
					arrLinkedDetectors.add(cloneArrTriggeredDetectors.get(j));
					cloneArrTriggeredDetectors.remove(j);
					j-=1;
				}
			} 
			arrLinkPerDetector.add(arrLinkedDetectors);
			i-=1;
		}
		return arrLinkPerDetector;
	}
	
	// Check if the linkedDetectors array has enough data to get the position of a potential fire. If it does, detect the fire, post it to the DB, then update the DB to have the detectors linked to the incident we just found instead of a phantom one.
	public void detectPotentialFire(EmergencyApi emergencyApiClient) throws JSONException, IOException {
		if (getLinkedDetectors().size() != 0) {
			for(ArrayList<Detector> arrLinkedDetector: getLinkedDetectors()) {
				if (arrLinkedDetector.size() >= 3) {
					// DETECT FIRE
					double[][] positions = getDetectorsPosition(arrLinkedDetector);
		            double[] distances = getDetectorsDistance(arrLinkedDetector);
		            
		            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
		            Optimum optimum = solver.solve();
		            
		            double[] centroid = optimum.getPoint().toArray();
		            
		            Coord fireCoordinates = new Coord(centroid[1], centroid[0]);
		            System.out.println("Potential fire : " + fireCoordinates);
		            System.out.println(getIntensityFire(arrLinkedDetector, fireCoordinates));
		            // Post located fire to database
		            String fire = emergencyApiClient.postApi("incident", new JSONObject()
		            		.put("id_type_incident", EmergencyApi.idTypeEmergency)
		            		.put("latitude_incident", centroid[0])
		            		.put("longitude_incident", centroid[1])
		            		.put("intensite_incident", 10)
		            		.put("date_incident", java.time.LocalDateTime.now())
		            		.put("id_type_status_incident", EmergencyApi.idTypeStatusEmergency)
		            		.toString());
		            // Move detectors from the fake emergency to the real on and purge the collection
		            for(Detector detector: arrLinkedDetector) {
		            	emergencyApiClient.deleteApi("detecte", new JSONObject()
		            			.put("id_incident", EmergencyApi.idEmergencyFake)
		            			.put("id_detecteur", detector.getId())
		            			.toString());
		            	emergencyApiClient.postApi("detecte", new JSONObject() // Post received detectors to the detecte table linked with a fake emergency.
		                		.put("id_incident", new JSONObject(fire).getInt("id_incident"))
		                		.put("id_detecteur", detector.getId())
		                		.put("date_detecte", java.time.LocalDateTime.now())
		                		.put("intensite_detecte", detector.getIntensity())
		                		.toString());
		            	clientMqtt.getArrTriggeredDetectors().remove(detector);
		            }
				} else {
					System.out.println("Not enough data to triangulate position !");
				}
			}
		}else {
			System.out.println("Not enough data to triangulate position !");
		}
	}
	
	private double[][] getDetectorsPosition(ArrayList<Detector> arrLinkedDetectors) {
        double[][] positions = new double[arrLinkedDetectors.size()][2];

        for(int i = 0; i<arrLinkedDetectors.size(); i++) {
            Detector detector = arrLinkedDetectors.get(i);
            positions[i][0] = detector.getCoord().getLatitude();
            positions[i][1] = detector.getCoord().getLongitude();
        }

        return positions;
    }

    private double[] getDetectorsDistance(ArrayList<Detector> arrLinkedDetectors) {
        double[] positions = new double[arrLinkedDetectors.size()];

        for(int i = 0; i<arrLinkedDetectors.size(); i++) {
            Detector detector = arrLinkedDetectors.get(i);
            positions[i] = detector.getRange() - (detector.getRange() * detector.getIntensity() / 100);
        }

        return positions;
    }
	
    private double getIntensityFire(ArrayList<Detector> arrLinkedDetectors, Coord coordEmergency) {
    	double intensityFire = 0;
    	double maxIntensityFromDetector = 0;
    	for (Detector detector: arrLinkedDetectors) {
    		if (detector.getIntensity() >= maxIntensityFromDetector) {
    			double distToCenter = Math.sqrt(Math.pow(coordEmergency.getLongitude() - detector.getCoord().getLongitude(), 2) + Math.pow(coordEmergency.getLatitude() - detector.getCoord().getLatitude(), 2));
        		double intensityFromDetector = detector.getIntensity()*detector.getRange()/(detector.getRange()-distToCenter);
    			maxIntensityFromDetector = detector.getIntensity();
        		intensityFire = intensityFromDetector;
    		}
    	}
    	return intensityFire;
    }
	
}
