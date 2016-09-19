package com.thoughtworks.sts.model;

import com.google.gson.annotations.Expose;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by salonivithalani on 9/3/16.
 */
public class Lane {

    private Logger logger = Logger.getLogger(Signal.class);
    private final String id;

    @Expose
    private final int defaultGreenTime;
    @Expose
    private final String name;
    private final LCD16X2 lcd16X2;

    @Expose
    private final List<UltrasonicSensor> sensors;

    private int greenTime;

    @Expose
    private float thresholdFactor;

    @Expose
    private int roadDistanceVariation;

    private int nextGreenTime;

    private static GpioController gpio;

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public Lane(String id, String name, int defaultGreenTime) {
        this.id = id;
        gpio = GpioFactory.getInstance();
        this.name = name;
        sensors = new ArrayList<>(0);
        lcd16X2 = new LCD16X2();

        this.greenTime = defaultGreenTime;
        this.defaultGreenTime = defaultGreenTime;
        thresholdFactor = 0.5f;
        roadDistanceVariation = 1;
    }

    public Lane(String name, int defaultGreenTime) {
        this(UUID.randomUUID().toString(), name, defaultGreenTime);
    }

    public int getGreenTime() {
        return greenTime;
    }

    public void setGreenTime(int greenTime) {
        this.greenTime = greenTime;
    }

    public float getThresholdFactor() {
        return thresholdFactor;
    }

    public void setThresholdFactor(float thresholdFactor) {
        this.thresholdFactor = thresholdFactor;
    }

    public void addSensor(UltrasonicSensor sensor) {
        sensors.add(sensor);
    }

    public int getNextGreenTime() {
        return nextGreenTime;
    }

    public void setNextGreenTime(int nextGreenTime) {
        this.nextGreenTime = nextGreenTime;
    }

    public boolean isTrafficAtPeak() {

        int sensorAtThreshold = 0;

        for (UltrasonicSensor sensor : sensors) {
            if (getTrafficFactor(sensor) <= thresholdFactor) {
                sensorAtThreshold++;
            }
        }

        if (sensors.isEmpty())
            return false;

        return sensorAtThreshold / sensors.size() >= 0.5;
    }

    private float getTrafficFactor(UltrasonicSensor sensor) {

        List<Float> readings = sensor.getReadings();

        if (readings.isEmpty()) {
            if (sensor.isRunning())
                startSensor(sensor);
            return 1f;
        }

        int roadDistanceFrequency = calculateRoadDistanceFrequency(readings, getApproxRoadDistance(sensor));
        return (roadDistanceFrequency * 1.0f / readings.size());
    }

    private float getApproxRoadDistance(UltrasonicSensor sensor) {
        return sensor.getRoadDistance() - roadDistanceVariation;
    }

    private int calculateRoadDistanceFrequency(List<Float> readings, float approxRoadDistance) {

        int roadDistanceFrequency = 0;
        for (Float reading : readings) {
            if (reading != null && reading >= approxRoadDistance) {
                roadDistanceFrequency++;
            }
        }

        return roadDistanceFrequency;
    }

    public void startSensor() {
        sensors.forEach(sensor -> startSensor(sensor));
    }

    private void startSensor(UltrasonicSensor sensor) {
        Thread thread = new Thread(sensor);
        thread.setDaemon(true);
        threadPool.execute(thread);
    }

    public int getDefaultGreenTime() {
        return defaultGreenTime;
    }

    public void display(String content) {
        lcd16X2.write(name + ": " + content);
    }

    public void setNextGreenTimeAsDefault() {
        this.nextGreenTime = this.defaultGreenTime;
    }

    public void updateGreenTime() {
        this.greenTime = this.nextGreenTime;
    }

    public String getName() {
        return name;
    }

    public void stopSensor() {
        sensors.forEach(sensor -> sensor.stop());
        System.out.println("Lane " + name + " notified all sensors to stop.");
    }
}
