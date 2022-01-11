package launcher;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import controller.ControllerConfig;
import controller.DetectorController;
import controller.EmergencyController;
import controller.EmergencySimulationController;
import controller.FireController;
import controller.SensorController;
import database.SetupDb;
import model.Coord;
import model.Detector;
import model.Emergency;
import model.Fire;
import mqtt.BrokerMqtt;
import rest.EmergencyApi;
import rest.Map;
import rest.SimulatorApi;

public class Launcher {
	public static void main(String[] args) throws IOException, InterruptedException, MqttException {
		//// API & MQTT Clients
		SimulatorApi simulatorApiClient = new SimulatorApi("449928d774153132c2c3509647e3d23f8e168fb50660fa27dd33c8342735b166");
		EmergencyApi emergencyApiClient = new EmergencyApi("CB814D37E278A63D3666B1A1604AD0F5C5FD7E177267F62B8D719F49182F410A");
		Map mapClient = new Map("VybYldGG1GIV15GOG3meIG9QEil7MxfD");
		BrokerMqtt mqttClient = new BrokerMqtt();
		//// Setup DB
		SetupDb setupDb = new SetupDb(simulatorApiClient);
		setupDb.resetDatabase();
		setupDb.postDetectors();
		//// Setup Detectors
		// Get Detectors from DB
		DetectorController sensorController = new SensorController(simulatorApiClient);
		sensorController.populateDetectorArray();
		//// Start Simulation
		// Create fire & Instantiate Simulation Controller for the Emergency stuff
		EmergencyController fireController = new FireController(simulatorApiClient);
		ArrayList<Emergency> arrFire = new ArrayList<Emergency>();
		EmergencySimulationController emergencySimulationController = new EmergencySimulationController(emergencyApiClient, arrFire, mapClient);
		int turn = 0;
		do {
			/*
			if (turn % 5 == 0) { // Generate a fire every five round
				// Generate fire
				boolean newFireCoordinateOk = false;
				Emergency fire = null;
				while(newFireCoordinateOk == false) {
					fire = fireController.generateEmergency();
					boolean allFireCoordinatesOk = true;
					for(Emergency fireSimulated: arrFire) {
						double distBetweenFires = Math.sqrt(Math.pow(fireSimulated.getCoord().getLongitude() - fire.getCoord().getLongitude(), 2) + Math.pow(fireSimulated.getCoord().getLatitude() - fire.getCoord().getLatitude(), 2));
						if (distBetweenFires <= 2*ControllerConfig.RANGE) {
							allFireCoordinatesOk = false;
						}
					}
					if (allFireCoordinatesOk == true) {
						newFireCoordinateOk = true;
					}
				}
				// Post to the API (maybe go through the broker ?)
				fireController.apiPostEmergency(fire);
				System.out.println("Fire generated : " + fire);
				arrFire.add(fire);
			}*/
			Emergency fireTest = new Fire(8.6, LocalDate.now(), new Coord(45.7660346, 4.834665));
			arrFire.add(fireTest);
			for(Emergency fire: arrFire) {
				// Update detectors to know which one detected the incident
				sensorController.updateDetectors(fire.getCoord(), fire.getIntensity());
				// Get triggered detectors
				ArrayList<Detector> arrTriggeredDetectors = sensorController.getTriggeredDetectorArray();
				System.out.println("Triggered detectors : " + arrTriggeredDetectors);
				// Forge the message that will be sent to the broker and post triggered detectors the the API one by one
				String msg = "";
				for(Detector trigDetector: arrTriggeredDetectors){
					sensorController.apiPostTriggeredDetector(fire, trigDetector);
				    msg += new JSONObject()
				    		.put("id", trigDetector.getId())
				    		.put("intensity", trigDetector.getIntensity())
				    		+ "\n";
				}
				// Send message to broker
				String responserino = mqttClient.publishMessage("topic/detectors", msg);
				System.out.println(responserino);
			}
			// Do stuff with emergency data ?
			emergencySimulationController.simulateIntervention();
			turn += 1;
			TimeUnit.SECONDS.sleep(5);
		}while(turn < ControllerConfig.NUMB_TURN);
		System.exit(0);
	}
}
