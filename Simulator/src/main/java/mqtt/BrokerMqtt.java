package mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class BrokerMqtt {
	private MqttClient mqttClient;

	public BrokerMqtt() throws MqttException {
		super();
		this.mqttClient = new MqttClient("tcp://mosquitto-simulation:1883", "", new MemoryPersistence());;
	}
	
	public String publishMessage(String topic, String msg) throws MqttSecurityException, MqttException {
		if (msg.length() != 0) {
			MqttConnectOptions connOpts = new MqttConnectOptions();
	        connOpts.setCleanSession(true);
	        mqttClient.connect(connOpts);
	        MqttMessage message = new MqttMessage(msg.getBytes());
	        message.setQos(2);
	        mqttClient.publish(topic, message);
	        mqttClient.disconnect();
	        return "Sent the following message to broker : \n" + msg;
		} else {
			return "Msg to send was empty";
	    }
	}
}
