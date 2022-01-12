package launcher;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;

import controller.EmergencyManagerController;
import database.SetupDb;
import model.EmergencyBuilding;
import mqtt.BrokerMqtt;
import rest.EmergencyApi;

public class LauncherEmergency {
	public static void main(String[] args) throws MqttException, IOException, InterruptedException, JSONException, ParseException {
		//// API & MQTT Clients & Setup
		EmergencyApi emergencyApiClient = new EmergencyApi("CB814D37E278A63D3666B1A1604AD0F5C5FD7E177267F62B8D719F49182F410A");
		BrokerMqtt mqttClient = new BrokerMqtt();
		mqttClient.setUpBroker();
		EmergencyManagerController fireManagerController = new EmergencyManagerController(mqttClient, emergencyApiClient);
		//// Setup DB
		SetupDb setupDb = new SetupDb(emergencyApiClient);
		setupDb.resetDatabase();
		setupDb.postDetectors();
		setupDb.postFakeEmergency(); // Emergency that will regroup every detector sent by the IoT chain, until an actual emergency is located. When located, concerned detectors will be moved from this fake emrgency to the real one
		setupDb.postEmergencyBuildings();
		for(EmergencyBuilding emergencyBuilding: fireManagerController.getEmergencyBuildings()) {
			setupDb.postCamionCiterne(emergencyBuilding, 2);
			setupDb.postCaporal(emergencyBuilding, 10);
			setupDb.postCaporalChef(emergencyBuilding, 2);
		}
		//// Start localization of fire
		System.out.println("Starting !");
		mqttClient.getMessage("python/test", emergencyApiClient);
		while (true) {
			fireManagerController.detectPotentialFire();
			fireManagerController.dealWithEmergencies();
			// Maybe method to send stuff to grafana ?
			// Maybe sync grafana directly to postgresql ?
			TimeUnit.SECONDS.sleep(5);
		}
	}
}
