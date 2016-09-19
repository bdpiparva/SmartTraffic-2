package com.thoughtworks.sts.data.modal;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by salonivithalani on 9/3/16.
 */
public class LaneModel {

    @Expose
    private String id;

    @Expose
    private int defaultGreenTime;

    @Expose
    private final String name;

    @Expose
    private List<UltrasonicSensorModel> sensors = new ArrayList<>();

    @Expose
    private float thresholdFactor;

    @Expose
    private int roadDistanceVariation;

    public LaneModel(String name, int defaultGreenTime) {

        this.name = name;
        this.defaultGreenTime = defaultGreenTime;
        thresholdFactor = 0.5f;
        roadDistanceVariation = 1;
        this.id = UUID.randomUUID().toString();

    }

    public int getDefaultGreenTime() {
        return defaultGreenTime;
    }

    public String getName() {
        return name;
    }

    public List<UltrasonicSensorModel> getSensorsModel() {
        return sensors;
    }

    public void addSensor(UltrasonicSensorModel ultrasonicSensorModel) {
        sensors.add(ultrasonicSensorModel);
    }

    public float getThresholdFactor() {
        return thresholdFactor;
    }

    public void setThresholdFactor(float thresholdFactor) {
        this.thresholdFactor = thresholdFactor;
    }

    public int getRoadDistanceVariation() {
        return roadDistanceVariation;
    }

    public void setRoadDistanceVariation(int roadDistanceVariation) {
        this.roadDistanceVariation = roadDistanceVariation;
    }

    public String getId() {
        return id;
    }
}
