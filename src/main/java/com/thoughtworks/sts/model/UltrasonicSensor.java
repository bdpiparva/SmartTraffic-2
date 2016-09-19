package com.thoughtworks.sts.model;

import com.google.gson.annotations.Expose;
import com.pi4j.io.gpio.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UltrasonicSensor implements Runnable {

    private Logger logger = Logger.getLogger(UltrasonicSensor.class);

    private final static float SOUND_SPEED = 340.29f;  // speed of sound in m/s
    private final static int TRIG_DURATION_IN_MICROS = 60; // trigger duration of 10 micro s
    private final static int WAIT_DURATION_IN_MILLIS = 60; // wait 60 milli s
    private final static int TIMEOUT = Integer.MAX_VALUE;
    private static GpioController gpio;

    @Expose
    private final String id;

    @Expose
    private GpioPinDigitalInput echoPin;

    @Expose
    private GpioPinDigitalOutput trigPin;

    private List<Float> readings = new ArrayList<>();
    private boolean isMonitoring = true;
    private boolean isRunning = true;

    @Expose
    private float roadDistance;

    public UltrasonicSensor(String id, Pin echoPin, Pin trigPin) {
        this.id = id;
        gpio = GpioFactory.getInstance();
        this.echoPin = gpio.provisionDigitalInputPin(echoPin);
        this.trigPin = gpio.provisionDigitalOutputPin(trigPin);
        this.trigPin.low();
        this.roadDistance = 16f;
    }

    public UltrasonicSensor(Pin echoPin, Pin trigPin) {
        this(UUID.randomUUID().toString(), echoPin, trigPin);
    }

    public float measureDistance() throws TimeoutException {
        this.triggerSensor();
        this.waitForSignal();
        long duration = this.measureSignal();
        return duration * SOUND_SPEED / (2 * 10000);
    }

    private void triggerSensor() {
        try {
            this.trigPin.high();
            Thread.sleep(0, TRIG_DURATION_IN_MICROS * 1000);
//            Thread.sleep(500);
            this.trigPin.low();
        } catch (InterruptedException ex) {
            logger.error("Interrupt during trigger", ex);
        }

    }

    private void waitForSignal() throws TimeoutException {
        int countdown = TIMEOUT;
        while (this.echoPin.isLow() && countdown > 0) {
            countdown--;
        }

        if (countdown <= 0) {
            throw new TimeoutException("Timeout waiting for signal start");
        }
    }

    private long measureSignal() throws TimeoutException {
        int countdown = TIMEOUT;
        long start = System.nanoTime();
        while (this.echoPin.isHigh() && countdown > 0) {
            countdown--;
        }
        long end = System.nanoTime();

        if (countdown <= 0) {
            throw new TimeoutException("Timeout waiting for signal end");
        }

        return (long) Math.ceil((end - start) / 1000.0);
    }

    @Override
    public void run() {

        try {
            isRunning = true;
            while (isRunning) {
                if (isMonitoring) {
                    try {
                        float distance = measureDistance();
                        readings.add(distance);
                        Thread.sleep(WAIT_DURATION_IN_MILLIS);
                    } catch (InterruptedException ex) {
                        System.out.println("Ultrasonic sensor interrupted" + ex);
                    } catch (TimeoutException e) {
                        System.out.println("Ultrasonic sensor timeout while measuring distance" + e);
                    }
                }
            }
        } finally {
            System.out.println("Ultrasonic with echo pin " + echoPin.toString() + " stopped successfully.");
            if (isRunning)
                run();
        }
    }

    public List<Float> getReadings() {
        List<Float> data = new ArrayList<>(readings);
        System.out.println("Readings size:  " + data.size());
        readings = new ArrayList<>();
        return data;
    }

    public float getRoadDistance() {
        return roadDistance;
    }

    public void setRoadDistance(float roadDistance) {
        this.roadDistance = roadDistance;
    }

    public void stop() {
        gpio.shutdown();
        System.out.println("Ultrasonic with echo pin " + echoPin.toString() + " stop signal sent.");
        isRunning = false;
    }

    private static class TimeoutException extends Exception {

        private final String reason;

        public TimeoutException(String reason) {
            this.reason = reason;
        }

        @Override
        public String toString() {
            return this.reason;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

}