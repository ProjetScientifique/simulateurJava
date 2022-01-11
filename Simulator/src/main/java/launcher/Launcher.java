package launcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import controller.ControllerConfig;
import controller.DetectorController;
import controller.EmergencyController;
import controller.FireController;
import controller.SensorController;
import database.SetupDb;
import model.Detector;
import model.Emergency;
import mqtt.BrokerMqtt;
import rest.SimulatorApi;

public class Launcher {
	public static void main(String[] args) throws IOException, InterruptedException, MqttException {
		//// API & MQTT Clients
		SimulatorApi simulatorApiClient = new SimulatorApi("449928d774153132c2c3509647e3d23f8e168fb50660fa27dd33c8342735b166");
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
		// Create fire
		EmergencyController fireController = new FireController(simulatorApiClient);
		int turn = 0;
		do {
			// Generate fire
			Emergency fire = fireController.generateEmergency();
			// Post to the API (maybe go through the broker ?)
			fireController.apiPostEmergency(fire);
			System.out.println("Fire generated : " + fire);
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
			turn += 1;
			TimeUnit.SECONDS.sleep(5);
		}while(turn < ControllerConfig.NUMB_TURN);
		System.exit(0);
	}
}
