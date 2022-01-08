package launcher;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttException;

import controller.EmergencyManagerController;
import database.SetupDb;
import mqtt.BrokerMqtt;
import rest.EmergencyApi;

public class Launcher {
	public static void main(String[] args) throws MqttException, IOException, InterruptedException {
		//// API & MQTT Clients
		EmergencyApi emergencyApiClient = new EmergencyApi("CB814D37E278A63D3666B1A1604AD0F5C5FD7E177267F62B8D719F49182F410A");
		BrokerMqtt mqttClient = new BrokerMqtt();
		mqttClient.setUpBroker();
		//// Setup DB
		SetupDb setupDb = new SetupDb(emergencyApiClient);
		setupDb.resetDatabase();
		setupDb.postDetectors();
		setupDb.postFakeEmergency(); // Emergency that will regroup every detector sent by the IoT chain, until an actual emergency is located. When located, concerned detectors will be moved from this fake emrgency to the real one
		//// Start localization of fire
		EmergencyManagerController fireManagerController = new EmergencyManagerController(mqttClient);
		System.out.println("Starting !");
		mqttClient.getMessage("python/test", emergencyApiClient);
		while (true) {
			fireManagerController.detectPotentialFire(emergencyApiClient);
			TimeUnit.SECONDS.sleep(1);
		}
	}
}
