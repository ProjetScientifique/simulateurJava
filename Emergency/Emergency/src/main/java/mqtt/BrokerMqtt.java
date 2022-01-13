package mqtt;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import controller.ControllerConfig;
import model.Coord;
import model.Detector;
import model.Sensor;
import rest.EmergencyApi;

public class BrokerMqtt {
	private MqttClient mqttClient;
	private ArrayList<Detector> arrTriggeredDetectors = new ArrayList<Detector>();

	public BrokerMqtt() throws MqttException {
		super();
		this.mqttClient = new MqttClient("tcp://127.0.0.1:1883", "", new MemoryPersistence());;
	}
	
	public void setUpBroker() throws MqttSecurityException, MqttException {
		MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        mqttClient.connect(connOpts);
	}
	
	public String publishMessage(String topic, String msg) throws MqttSecurityException, MqttException {
		if (msg.length() != 0) {
	        MqttMessage message = new MqttMessage(msg.getBytes());
	        message.setQos(2);
	        mqttClient.publish(topic, message);
	        return "Sent the following message to broker : \n" + msg;
		} else {
			return "Msg to send was empty";
	    }
	}
	
	// This function will subscribe to a given topic and set a callback that will allow us to get messages sent by the mqtt broker
	public void getMessage(String topic, final EmergencyApi emergencyApiClient) throws MqttSecurityException, MqttException, InterruptedException {
        //final CountDownLatch latch = new CountDownLatch(1);
        mqttClient.setCallback(new MqttCallback() {
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("Received a Message from broker : " + new String(message.getPayload()));
                JSONObject json = new JSONObject(new String(message.getPayload()));
                JSONObject jsonDetector = emergencyApiClient.getApi("detecteur/" + json.getInt("id")).getJSONObject(0);
                emergencyApiClient.postApi("detecte", new JSONObject() // Post received detectors to the detecte table linked with a fake emergency.
                		.put("id_incident", EmergencyApi.ID_EMERGENCY_FAKE)
                		.put("id_detecteur", json.getInt("id"))
                		.put("date_detecte", java.time.LocalDateTime.now())
                		.put("intensite_detecte", json.getDouble("intensity"))
                		.toString());
                Detector detector = new Sensor(json.getDouble("intensity"), "", new Coord(jsonDetector.getDouble("latitude_detecteur"), jsonDetector.getDouble("longitude_detecteur")), ControllerConfig.RANGE, jsonDetector.getInt("id_detecteur"));
                synchronized (arrTriggeredDetectors) {
                    arrTriggeredDetectors.add(detector);
				}
                //latch.countDown(); // unblock main thread
            }

            public void connectionLost(Throwable cause) {
                System.out.println("Connection to broker lost!" + cause.getMessage());
                //latch.countDown(); // unblock main thread
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
        mqttClient.subscribe(topic, 0);	
		//latch.await();
	}
	
	public void disconnect() throws MqttException {
		mqttClient.disconnect();
	}

	public ArrayList<Detector> getArrTriggeredDetectors() {
		return arrTriggeredDetectors;
	}
}