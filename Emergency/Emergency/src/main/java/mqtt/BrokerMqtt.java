package mqtt;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONObject;

import model.Detector;
import rest.EmergencyApi;

public class BrokerMqtt {
	private MqttClient mqttClient;
	private ArrayList<Detector> arrTriggeredDetectors;

	public BrokerMqtt() throws MqttException {
		super();
		this.mqttClient = new MqttClient("tcp://127.0.0.1:1883", "", new MemoryPersistence());;
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
	
	public void getMessage(String topic, final EmergencyApi emergencyApiClient) throws MqttSecurityException, MqttException, InterruptedException {
		
        final CountDownLatch latch = new CountDownLatch(1);
        mqttClient.setCallback(new MqttCallback() {
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String time = new Timestamp(System.currentTimeMillis()).toString();
                System.out.println("\nReceived a Message!" +
                    "\n\tTime:    " + time +
                    "\n\tTopic:   " + topic +
                    "\n\tMessage: " + new String(message.getPayload()) +
                    "\n\tQoS:     " + message.getQos() + "\n");
                int id = new JSONObject(new String(message.getPayload())).getInt("id");
                JSONArray res = emergencyApiClient.getApi("detecteur/" + id);
                System.out.println(res);
                //arrTriggeredDetectors.add(detector); // ?
                latch.countDown(); // unblock main thread
            }

            public void connectionLost(Throwable cause) {
                System.out.println("Connection to broker lost!" + cause.getMessage());
                latch.countDown(); // unblock main thread
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
        mqttClient.subscribe(topic, 0);	
		latch.await();
	}
	
	public void disconnect() throws MqttException {
		mqttClient.disconnect();
	}

	public ArrayList<Detector> getArrTriggeredDetectors() {
		return arrTriggeredDetectors;
	}
}