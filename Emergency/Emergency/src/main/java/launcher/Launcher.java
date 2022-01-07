package launcher;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttException;

import controller.EmergencyManagerController;
import database.SetupDb;
import mqtt.BrokerMqtt;
import rest.EmergencyApi;
import rest.SimulatorApi;

public class Launcher {
	public static void main(String[] args) throws MqttException, IOException, InterruptedException {
		//// API & MQTT Clients
		SimulatorApi simulatorApiClient = new SimulatorApi("449928d774153132c2c3509647e3d23f8e168fb50660fa27dd33c8342735b166");
		EmergencyApi emergencyApiClient = new EmergencyApi("CB814D37E278A63D3666B1A1604AD0F5C5FD7E177267F62B8D719F49182F410A");
		BrokerMqtt mqttClient = new BrokerMqtt();
		//// Setup DB
		//SetupDb setupDb = new SetupDb(emergencyApiClient);
		//setupDb.resetDatabase();
		//setupDb.postDetectors();
		//// Start localization of fire
		System.out.println("Starting !");
		//EmergencyManagerController fireManagerController = new EmergencyManagerController(mqttClient.getArrTriggeredDetectors());
		while (true) {
			mqttClient.getMessage("python/test", emergencyApiClient);	
			System.out.println("hello");
		}
	}
}
