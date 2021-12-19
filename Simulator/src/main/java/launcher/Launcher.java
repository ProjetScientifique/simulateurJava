package launcher;

import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import controller.DetectorController;
import controller.EmergencyController;
import controller.FireController;
import controller.SensorController;
import model.Emergency;

public class Launcher {
	public static void main(String[] args) {
		try {    
			// Setup MQTT
			MqttClient sampleClient = new MqttClient("tcp://127.0.0.1:1883", "", new MemoryPersistence());
			MqttConnectOptions connOpts = new MqttConnectOptions();
	        connOpts.setCleanSession(true);
	        sampleClient.connect(connOpts);
			// Instanciate controllers
			DetectorController sensorController = new SensorController();
			EmergencyController fireController = new FireController();
			int turn = 0;
			do {
				Emergency fire = fireController.generateEmergency();
				sensorController.updateDetectors(fire.getCoord(), fire.getIntensity());
				JSONObject trigDetectors = sensorController.getTriggeredDetectorArray();
				String msg = "";
				for(Object o: trigDetectors.getJSONArray("Detectors")){
				    if ( trigDetectors instanceof JSONObject ) {
				    	msg += o + "\n";
				    }
				}
				System.out.println(msg);
				MqttMessage message = new MqttMessage(msg.getBytes());
	            message.setQos(2);
	            sampleClient.publish("topic/detectors", message);
	            turn += 1;
	            sensorController.resetDetectors();
	            TimeUnit.SECONDS.sleep(5);
			}while(turn <= 0);
            System.exit(0);
            System.out.println("?");
		} catch (MqttException e) {
            e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
