package controller;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import model.CaserneFactory;
import model.Coord;
import model.Detector;
import model.Emergency;
import model.EmergencyBuilding;
import model.EmergencyBuildingFactory;
import model.EmergencyPeople;
import model.EmergencyPeopleFactory;
import model.Fire;
import model.PompierFactory;
import model.TruckFactory;
import model.Vehicule;
import model.VehiculeFactory;
import mqtt.BrokerMqtt;
import rest.EmergencyApi;

public class EmergencyManagerController {
	private BrokerMqtt clientMqtt;
	private EmergencyApi emergencyApiClient;
	private ArrayList<EmergencyBuilding> arrEmergencyBuilding = new ArrayList<EmergencyBuilding>();
	private ArrayList<EmergencyPeople> arrEmergencyPeople = new ArrayList<EmergencyPeople>();
	private ArrayList<Vehicule> arrVehicule = new ArrayList<Vehicule>();
	private ArrayList<Emergency> arrEmergency = new ArrayList<Emergency>();

	public EmergencyManagerController(BrokerMqtt clientMqtt, EmergencyApi emergencyApiClient) {
		super();
		this.clientMqtt = clientMqtt;
		this.emergencyApiClient = emergencyApiClient;
	}
	
	public void dealWithEmergencies() throws IOException, JSONException, ParseException {
		getEmergencies();
		getRessources(); // Get/Updates ressources
		for (Emergency emergency: arrEmergency) {
			if (emergency.getIdTypeStatus() == EmergencyApi.idTypeStatusEmergencyNotTreated) {
				EmergencyBuilding emergencyBuilding = getClosestEmergencyBuilding(emergency);
				if (emergencyBuilding != null) {
					if (emergency.getIntensity() >= 5) {
						// Send 5 Emergency People
						postIntervientAndUpdateResources(emergencyApiClient, emergencyBuilding, emergency, 5);
						System.out.println("One truck and 5 Firefighters were sent.");
					} else {
						// Send 3 Emergency People
						postIntervientAndUpdateResources(emergencyApiClient, emergencyBuilding, emergency, 3);
						System.out.println("One truck and 3 Firefighters were sent.");
					}
				}
			} else {
				// Maybe method that fetches the intervenes table and recaps the order ?
				System.out.println("Emergency : " + emergency + " is already being taken care of.");
			}
		}
	}
	
	// Same here ? I think I should have three arrays declared as private on this Class, and the getRessources would just populate / update them, much easier ?
	private void getRessources() throws JSONException, IOException, ParseException {
		arrEmergencyBuilding = getEmergencyBuildings();
		arrEmergencyPeople = getEmergencyPeople();
		arrVehicule = getVehicules();
	}
	
	// MAY RETURN NULL IF NO RESSOURCES ARE AVAILABLE IN ANY EMERGENCY BUILDINGS !
	private EmergencyBuilding getClosestEmergencyBuilding(Emergency emergency) {
		JSONArray jsonArr = new JSONArray();
		for(EmergencyBuilding emergencyBuilding: arrEmergencyBuilding) {
			if(emergencyBuilding.getAvailableVehicules().size() == 0) {
				System.out.println("No vehicules available for the Emergency Building : " + emergencyBuilding);
			} else {
				if (emergencyBuilding.getAvailableEmergencyPeople().size() == 0) {
					System.out.println("No emergency people available for the Emergency Building : " + emergencyBuilding);
				} else {
					double distBetween = Math.sqrt(Math.pow(emergencyBuilding.getCoord().getLongitude() - emergency.getCoord().getLongitude(), 2) + Math.pow(emergencyBuilding.getCoord().getLatitude() - emergency.getCoord().getLatitude(), 2));
					jsonArr.put(new JSONObject()
							.put("index", arrEmergencyBuilding.indexOf(emergencyBuilding))
							.put("dist_between", distBetween));
				}
			}
		}
		if (jsonArr.length() != 0) {	
			EmergencyBuilding closestEmergencyBuilding = arrEmergencyBuilding.get(jsonArr.getJSONObject(0).getInt("index"));
			double closestDistBetween = jsonArr.getJSONObject(0).getDouble("dist_between");
			for(Object o: jsonArr) {
				JSONObject json = new JSONObject(o.toString());
				if (json.getDouble("dist_between") < closestDistBetween) {
					closestDistBetween = json.getDouble("dist_between");
					closestEmergencyBuilding = arrEmergencyBuilding.get(json.getInt("index"));
				}
			}
			return closestEmergencyBuilding;
		}
		return null;
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
	public void detectPotentialFire() throws JSONException, IOException, ParseException {
		getEmergencies();
		if (getLinkedDetectors().size() != 0) {
			for(ArrayList<Detector> arrLinkedDetector: getLinkedDetectors()) {
				if (arrLinkedDetector.size() >= 3) {
					boolean isNewFire = true;
					// DETECT FIRE
					double[][] positions = getDetectorsPosition(arrLinkedDetector);
		            double[] distances = getDetectorsDistance(arrLinkedDetector);
		            
		            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
		            Optimum optimum = solver.solve();
		            
		            double[] centroid = optimum.getPoint().toArray();
		            
		            Coord fireCoordinates = new Coord(centroid[0], centroid[1]);
		            System.out.println("Potential fire : " + fireCoordinates + " | Intensity : " + getIntensityFire(arrLinkedDetector, fireCoordinates));
		            for(Emergency emergency: arrEmergency) {
						double distBetweenFires = Math.sqrt(Math.pow(emergency.getCoord().getLongitude() - fireCoordinates.getLongitude(), 2) + Math.pow(emergency.getCoord().getLatitude() - fireCoordinates.getLatitude(), 2));
						if (distBetweenFires <= 2*ControllerConfig.RANGE) {
							System.out.println("Fire at coordinates : " + fireCoordinates + " is already known. Updating it." );
							isNewFire = false;
							// Update values from existing detectors
			            	for(Detector detector: arrLinkedDetector) {
			            		emergencyApiClient.deleteDetecte(EmergencyApi.idEmergencyFake, detector.getId());
			            		// Patch equivalent
			            		String resDelete = emergencyApiClient.deleteDetecte(emergency.getId(), detector.getId());
			            		System.out.println("AAAAAA : " + resDelete);
			            		emergencyApiClient.postApi("detecte", new JSONObject()
			            				.put("id_incident", emergency.getId())
				            			.put("id_detecteur", detector.getId())
				            			.put("date_detecte", java.time.LocalDateTime.now())
				            			.put("intensite_detecte", detector.getIntensity())
				            			.toString());
			            		emergencyApiClient.patchApi("incident/" + emergency.getId(), new JSONObject()
			            				.put("intensite_incident", getIntensityFire(arrLinkedDetector, fireCoordinates))
			            				.toString());
				            	clientMqtt.getArrTriggeredDetectors().remove(detector);
			            	}
						}
		            }
		            if (isNewFire == true) {
		            	// Post located fire to database
			            String fire = emergencyApiClient.postApi("incident", new JSONObject()
			            		.put("id_type_incident", EmergencyApi.idTypeEmergency)
			            		.put("latitude_incident", centroid[0])
			            		.put("longitude_incident", centroid[1])
			            		.put("intensite_incident", getIntensityFire(arrLinkedDetector, fireCoordinates))
			            		.put("date_incident", java.time.LocalDateTime.now())
			            		.put("id_type_status_incident", EmergencyApi.idTypeStatusEmergencyNotTreated)
			            		.toString());
			            // Move detectors from the fake emergency to the real on and purge the collection
			            for(Detector detector: arrLinkedDetector) {
			            	emergencyApiClient.deleteDetecte(EmergencyApi.idEmergencyFake, detector.getId());
			            	emergencyApiClient.postApi("detecte", new JSONObject() // Post received detectors to the detecte table linked with a fake emergency.
			                		.put("id_incident", new JSONObject(fire).getInt("id_incident"))
			                		.put("id_detecteur", detector.getId())
			                		.put("date_detecte", java.time.LocalDateTime.now())
			                		.put("intensite_detecte", detector.getIntensity())
			                		.toString());
			            	clientMqtt.getArrTriggeredDetectors().remove(detector);
			            }
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
    	if (intensityFire <= 10 && intensityFire >= 0) { // Prevents random data ?
        	return intensityFire;
    	}else {
    		return maxIntensityFromDetector;
    	}
    }
	
    public ArrayList<EmergencyBuilding> getEmergencyBuildings() throws IOException, JSONException, ParseException {
    	ArrayList<EmergencyBuilding> arrEmergencyBuilding = new ArrayList<EmergencyBuilding>();
    	JSONArray jsonArr = emergencyApiClient.getApi("casernes");
    	for(Object o: jsonArr) {
    		JSONObject json = new JSONObject(o.toString());
    		ArrayList<EmergencyPeople> arrEmergencyPeopleLocal = new ArrayList<EmergencyPeople>();
        	ArrayList<Vehicule> arrVehiculeLocal = new ArrayList<Vehicule>();
        	for(EmergencyPeople emergencyPeople: arrEmergencyPeople) {
    			if (emergencyPeople.getIdCaserne() == json.getInt("id_caserne")) {
    				arrEmergencyPeopleLocal.add(emergencyPeople);
    			}
    		}
    		for(Vehicule vehicule: arrVehicule) {
    			if (vehicule.getIdCaserne() == json.getInt("id_caserne")) {
    				arrVehiculeLocal.add(vehicule);
    			}
    		}
    		arrEmergencyBuilding.add(EmergencyBuildingFactory.getEmergencyBuilding(new CaserneFactory(json.getString("nom_caserne"), 
    																				new Coord(json.getDouble("latitude_caserne"), json.getDouble("longitude_caserne")), 
    																				json.getInt("id_caserne"),
    																				arrEmergencyPeople,
    																				arrVehicule)));
    		
    	}
    	return arrEmergencyBuilding;
    }
    
    private ArrayList<EmergencyPeople> getEmergencyPeople() throws IOException, JSONException, ParseException {
    	ArrayList<EmergencyPeople> arrEmergencyPeopleLocal = new ArrayList<EmergencyPeople>();
    	JSONArray jsonArr = emergencyApiClient.getApi("pompiers");
    	for(Object o: jsonArr) {
    		JSONObject json = new JSONObject(o.toString());
    		arrEmergencyPeopleLocal.add(EmergencyPeopleFactory.getEmergencyPeople(new PompierFactory(json.getString("prenom_pompier"), 
    																		json.getString("nom_pompier"), 
    																		LocalDate.parse(json.getString("date_naissance_pompier")), 
    																		json.getInt("nombre_intervention_jour_maximum_pompier"), 
    																		json.getBoolean("disponibilite_pompier"), 
    																		json.getInt("id_pompier"), 
    																		json.getInt("id_type_pompier"),
    																		json.getInt("id_caserne"))));
    	}
    	return arrEmergencyPeopleLocal;
    }
    
    private ArrayList<Vehicule> getVehicules() throws IOException, JSONException, ParseException {
    	ArrayList<Vehicule> arrVehiculeLocal = new ArrayList<Vehicule>();
    	JSONArray jsonArr = emergencyApiClient.getApi("vehicules");
    	for(Object o: jsonArr) {
    		JSONObject json = new JSONObject(o.toString());
    		arrVehiculeLocal.add(VehiculeFactory.getVehicule(new TruckFactory(json.getInt("annee_vehicule"), 
    													json.getInt("nombre_intervention_maximum_vehicule"), 
    													new Coord(json.getDouble("latitude_vehicule"), json.getDouble("longitude_vehicule")), 
    													json.getInt("id_vehicule"), 
    													json.getInt("id_type_vehicule"), 
    													json.getInt("id_type_disponibilite_vehicule"),
    													json.getInt("id_caserne"))));
    		
    	}
    	return arrVehiculeLocal;
    }
    
    private void getEmergencies() throws IOException, JSONException, ParseException {
    	ArrayList<Emergency> arrEmergencyLocal = new ArrayList<Emergency>();
    	JSONArray jsonArr = emergencyApiClient.getApi("incidents");
    	for(Object o: jsonArr) {
    		JSONObject json = new JSONObject(o.toString());
    		if (json.getInt("id_type_status_incident") != EmergencyApi.idTypeStatusEmergencyForDetected) {
    			arrEmergencyLocal.add(new Fire(json.getDouble("intensite_incident"),
	    				LocalDateTime.parse(json.getString("date_incident")),
	    				new Coord(json.getDouble("latitude_incident"), json.getDouble("longitude_incident")),
	    				json.getInt("id_incident"),
	    				json.getInt("id_type_incident"),
	    				json.getInt("id_type_status_incident")));
    	
    		}
    	}
    	arrEmergency = arrEmergencyLocal;
    }
    
    
    
	
	private ArrayList<String> postIntervientAndUpdateResources(EmergencyApi emergencyApiClient, EmergencyBuilding emergencyBuilding, Emergency emergency, int nb) throws IOException {
		ArrayList<String> arrRes = new ArrayList<String>();
		ArrayList<EmergencyPeople> arrAvailableEmergencyPeople = emergencyBuilding.getAvailableEmergencyPeople();
		Vehicule vehicule = emergencyBuilding.getAvailableVehicules().get(0);
		while (nb > arrAvailableEmergencyPeople.size()) {
			nb = nb - 1;
		}
		for (int i = 0; i < nb; i++) {
			String res = emergencyApiClient.postApi("intervient", new JSONObject()
					.put("id_pompier", arrAvailableEmergencyPeople.get(i).getId())
					.put("id_vehicule", vehicule.getId())
					.put("id_incident", emergency.getId())
					.put("date_intervient", java.time.LocalDateTime.now())
					.toString());
			arrRes.add(res);
			
			emergencyApiClient.patchApi("pompier/" + arrAvailableEmergencyPeople.get(i).getId(), new JSONObject()
					.put("disponibilite_pompier", false)
					.toString());
		}
		emergencyApiClient.patchApi("incident/" + emergency.getId(), new JSONObject()
				.put("id_type_status_incident", EmergencyApi.idTypeStatusEmergencyBeingTreated)
				.toString());
		emergencyApiClient.patchApi("vehicule/" + vehicule.getId(), new JSONObject()
				.put("id_type_disponibilite_vehicule", EmergencyApi.idTypeDispoVehiculeNotAvailable)
				.toString());
		return arrRes;
	}
}
