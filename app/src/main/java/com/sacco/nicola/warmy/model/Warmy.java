package com.sacco.nicola.warmy.model;


import java.util.ArrayList;

/**
 * Created by nicolasacco on 28/12/15.
 */
public class Warmy {


    public final static String OFF_MODE = "OFF";

    public final static String MANUAL_MODE = "MANUAL";

    public final static String AUTO_MODE = "AUTO";

    public final static String[] SUPPORTED_MODES = {OFF_MODE, MANUAL_MODE, AUTO_MODE};

    private String actualMode = OFF_MODE;

    private String device_id = "";

    private String name = "";

    private float actualTemperature;

    private float desiredTemperature;

    private boolean isWarming = false;

    public boolean isWarming() {
        return isWarming;
    }

    public void setIsWarming(boolean isWarming) {
        this.isWarming = isWarming;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getActualTemperature() {
        return actualTemperature;
    }

    public void setActualTemperature(float actualTemperature) {
        this.actualTemperature = actualTemperature;
    }

    public String getActualMode() {
        return actualMode;
    }

    public void setActualMode(String actualMode) {

        boolean modeIsSupported = false;
        for (int i = 0; i < SUPPORTED_MODES.length; i++)
            modeIsSupported = modeIsSupported || SUPPORTED_MODES[i] == actualMode;

        if (modeIsSupported)
            this.actualMode = actualMode;
    }

    public float getDesiredTemperature() {
        return desiredTemperature;
    }

    public void setDesiredTemperature(float desiredTemperature) {
        this.desiredTemperature = desiredTemperature;
    }

    public void fromMQTT(String topic,String payload){

        if (topic.equals("/warmy/" + this.getDevice_id() + "/mode")) {
            if (payload.contains(OFF_MODE))
               this.setActualMode(OFF_MODE);
            if (payload.contains(MANUAL_MODE))
                this.setActualMode(MANUAL_MODE);
        }

        if (topic.equals("/warmy/" + this.getDevice_id() + "/actual_temperature")) {
            this.setActualTemperature(Float.parseFloat(payload));
        }

        if (topic.equals("/warmy/" + this.getDevice_id() + "/desired_temperature")) {
            this.setDesiredTemperature(Float.parseFloat(payload));
        }

        if (topic.equals("/warmy/" + this.getDevice_id() + "/warming")) {

            if (payload.equals("1")) {
                this.setIsWarming(true);
            } else {
                this.setIsWarming(false);
            }
        }

        if (topic.equals("/warmy/" + this.getDevice_id() + "/name")) {
            this.setName(payload);
        }
    }


}
