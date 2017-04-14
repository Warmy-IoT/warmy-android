package com.sacco.nicola.warmy.control;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by nicola on 23/12/15.
 */
public class WarmyApplication extends Application {


    public static String MQTT_SERVER_URI = "tcp://platform.nextechs.it:7000";

    public static String NEW_MESSAGE_EVENT = "NEW_MESSAGE_EVENT";

    public static String DEFAULT_BASE_TOPIC = "/warmy/#";

    private MqttClient mqttClient;

    public void connectClientIfNot() throws MqttException {
        if (!isConnected()) {
            connectClient();
        }
    }

    public void connectClient() throws MqttException {


        MemoryPersistence memPer = new MemoryPersistence();


        mqttClient = new MqttClient(MQTT_SERVER_URI, String.valueOf(Math.random()), memPer);

        if (!mqttClient.isConnected()) {

            mqttClient.setTimeToWait(3000);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void messageArrived(String arg0, MqttMessage arg1)
                        throws Exception {
                    handleIncomingMqttMessage(arg0, new String(arg1.getPayload(), "UTF-8"));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken arg0) {

                }

                @Override
                public void connectionLost(Throwable arg0) {

                }
            });
            mqttClient.connect();
            mqttClient.subscribe("/warmy/#");
        }

    }

    public boolean isConnected() {
        boolean isConnected;

        try {
            isConnected = mqttClient.isConnected();
        } catch (NullPointerException e) {
            isConnected = false;
        }

        return isConnected;
    }

    public void publish(String topic, String payload) {
        if (mqttClient.isConnected())
            try {
                mqttClient.publish(topic, payload.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
    }

    public void subscribe(String topic) {
        if (mqttClient.isConnected())
            try {
                mqttClient.subscribe(topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
    }


    public void disconnectClient() {
        try {
            if (mqttClient != null)
                if (mqttClient.isConnected())
                    mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void handleIncomingMqttMessage(String topic, String payload) {
        Intent i = new Intent(WarmyApplication.NEW_MESSAGE_EVENT);
        i.putExtra("topic", topic);
        i.putExtra("payload", payload);
        Log.d("MQTT", topic + " : " + payload);
        sendBroadcast(i);
    }

}
