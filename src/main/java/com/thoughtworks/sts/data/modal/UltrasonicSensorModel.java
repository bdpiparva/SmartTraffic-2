package com.thoughtworks.sts.data.modal;

import com.google.gson.annotations.Expose;

import java.util.UUID;

public class UltrasonicSensorModel {

    @Expose
    private String id;

    @Expose
    private PinModel echoPin;

    @Expose
    private PinModel trigPin;

    @Expose
    private float roadDistance;

    public UltrasonicSensorModel(PinModel echoPin, PinModel trigPin) {
        this.echoPin = echoPin;
        this.trigPin = trigPin;
        this.roadDistance = 16f;
        this.id = UUID.randomUUID().toString();
    }

    public PinModel getEchoPin() {
        return echoPin;
    }

    public void setEchoPin(PinModel echoPin) {
        this.echoPin = echoPin;
    }

    public PinModel getTrigPin() {
        return trigPin;
    }

    public void setTrigPin(PinModel trigPin) {
        this.trigPin = trigPin;
    }

    public float getRoadDistance() {
        return roadDistance;
    }

    public void setRoadDistance(float roadDistance) {
        this.roadDistance = roadDistance;
    }

    public String getId() {
        return id;
    }
}