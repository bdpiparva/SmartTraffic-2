package com.thoughtworks.sts.model;

import com.google.gson.annotations.Expose;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by salonivithalani on 9/3/16.
 */
public class Signal {

    private Logger logger = Logger.getLogger(Signal.class);
    private ExecutorService threadPool = Executors.newFixedThreadPool(30);

    @Expose
    private final String id;

    @Expose
    private String name;

    @Expose
    private final List<Lane> lanes;

    private int totalTime;
    private boolean isProcessing;
    private boolean isRunning;

    @Expose
    private int bufferTime;
    private int shortestLaneDuration;

    @Expose
    private int greenTimeFactor;

    @Expose
    private float adjustmentFactor;
    private int currentLaneIndex;

    @Expose
    private int yellowTime;

    public Signal(String id, String name, int bufferTime) {
        this.id = id;
        this.lanes = new ArrayList<>(4);
        this.isProcessing = true;
        this.isRunning = true;
        this.bufferTime = bufferTime;
        this.name = name;
        this.adjustmentFactor = 15f;
        this.currentLaneIndex = 0;
        this.yellowTime = 3;
    }

    public Signal(String name, int bufferTime) {
        this(UUID.randomUUID().toString(), name, bufferTime);
    }

    public void setAdjustmentFactor(float adjustmentFactor) {
        this.adjustmentFactor = adjustmentFactor;
    }

    public void addLane(Lane lane) {
        lanes.add(lane);
        totalTime += lane.getGreenTime();
        shortestLaneDuration(lane.getGreenTime());
    }

    private void shortestLaneDuration(int greenTime) {
        if (shortestLaneDuration == 0) {
            shortestLaneDuration = greenTime;
        } else if (greenTime < shortestLaneDuration) {
            shortestLaneDuration = greenTime;
        }
    }

    public void start() throws InterruptedException {

        isRunning = true;
        TrafficProcessor trafficProcessor = new TrafficProcessor();
        trafficProcessor.start();
        greenTimeFactor = (int) Math.ceil(shortestLaneDuration / adjustmentFactor);
        System.out.println("SignalModel green time factor: " + greenTimeFactor);

    }

    public void stop() {

        lanes.forEach(lane -> lane.stopSensor());
        System.out.println("Signal " + name + " notified all lanes to stop.");
        isRunning = false;
    }

    public void pause() {
        isProcessing = false;
    }

    public void resume() {
        isProcessing = true;
    }

    private class TrafficProcessor {

        public void start() {
            long start = System.currentTimeMillis();
            boolean isProcessed = false;
            int timer = 0;
            currentLaneIndex = 0;
            Lane currentLane = lanes.get(currentLaneIndex);
            while (true) {

                if (isRunning) {
                    long beforeProcessing = System.currentTimeMillis();

                    lanes.forEach(lane -> lane.startSensor());

                    System.out.println(((System.currentTimeMillis() + 1 - start) / 1000) + ", ");

                    ++timer;

                    if (timer >= currentLane.getGreenTime() - yellowTime) {
                        currentLane.display("Yellow " + timer);
                    } else {
                        currentLane.display("Green " + timer);
                    }

                    if (timer == currentLane.getGreenTime()) {
                        currentLane.display("Red");
                        ++currentLaneIndex;
                        if (lanes.size() > currentLaneIndex)
                            currentLane = lanes.get(currentLaneIndex);
                        timer = 0;
                    }


                    if (System.currentTimeMillis() >= ((totalTime - bufferTime) * 1000) + start && !isProcessed) {
                        adjustLaneTimings();
                        isProcessed = true;
                    }

                    if (System.currentTimeMillis() >= start + (totalTime * 1000)) {
                        start = System.currentTimeMillis();
                        timer = 0;
                        currentLaneIndex = 0;
                        currentLane = lanes.get(0);
                        lanes.forEach(lane -> lane.updateGreenTime());
                        isProcessed = false;
                        System.out.print("\nTimer: ");
                    }
                    try {
                        if (System.currentTimeMillis() - beforeProcessing < 995) {
                            Thread.sleep(1000 - (System.currentTimeMillis() - beforeProcessing));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /*@Override
        public void run() {

            long start = System.currentTimeMillis();
            boolean isProcessed = false;
            int timer = 0;
            currentLaneIndex = 0;
            Lane currentLane = lanes.get(currentLaneIndex);
            while (isRunning) {

                if (isProcessing) {
                    long beforeProcessing = System.currentTimeMillis();
                    System.out.println(((System.currentTimeMillis() + 1 - start) / 1000) + ", ");
                    ++timer;

                    if (timer >= currentLane.getGreenTime() - yellowTime) {
                        currentLane.display("Yellow " + timer);
                    } else {
                        currentLane.display("Green " + timer);
                    }

                    if (timer == currentLane.getGreenTime()) {
                        currentLane.display("Red");
                        ++currentLaneIndex;
                        if (lanes.size() > currentLaneIndex)
                            currentLane = lanes.get(currentLaneIndex);
                        timer = 0;
                    }


                    if (System.currentTimeMillis() >= ((totalTime - bufferTime) * 1000) + start && !isProcessed) {
                        adjustLaneTimings();
                        isProcessed = true;
                    }

                    if (System.currentTimeMillis() >= start + (totalTime * 1000)) {
                        start = System.currentTimeMillis();
                        timer = 0;
                        currentLaneIndex = 0;
                        currentLane = lanes.get(0);
                        lanes.forEach(lane -> lane.updateGreenTime());
                        isProcessed = false;
                        System.out.print("\nTimer: ");
                    }
                    waitTill(beforeProcessing);
                }
            }
        }
*/
        private void adjustLaneTimings() {

            final List<Lane> lanesAtPeak = getLanesAtPeak();
            final int numberOfLanesAtPeak = lanesAtPeak.size();


            if (isTimeAdjustmentRequired(numberOfLanesAtPeak)) {

                final int timeToAdd = (lanes.size() - numberOfLanesAtPeak) * greenTimeFactor;
                final int timeToReduce = numberOfLanesAtPeak * greenTimeFactor;

                lanes.forEach(lane -> {

                    boolean isLaneAtePeak = lanesAtPeak.contains(lane);
                    System.out.println(lane.getName() + " green time before adjusting: " + lane.getGreenTime());
                    System.out.println(lane.getName() + " is at peak: " + isLaneAtePeak);

                    if (isLaneAtePeak) {
                        lane.setNextGreenTime(lane.getDefaultGreenTime() + timeToAdd);
                    } else {
                        lane.setNextGreenTime(lane.getDefaultGreenTime() - timeToReduce);
                    }

                    System.out.println(lane.getName() + " green time after adjusting: " + lane.getGreenTime());
                });

            } else {

                lanes.forEach(lane -> {
                    lane.setNextGreenTimeAsDefault();
                    System.out.println("Adjusted green time " + lane.getGreenTime());
                });

            }
        }
    }

    private List<Lane> getLanesAtPeak() {
        List<Lane> lanesAtPeak = new ArrayList<>();
        lanes.forEach(lane -> {
            if (lane.isTrafficAtPeak()) {
                lanesAtPeak.add(lane);
            }
        });
        return lanesAtPeak;
    }

    private boolean isTimeAdjustmentRequired(int numberOfLanesAtPeak) {
        return numberOfLanesAtPeak != 0 && numberOfLanesAtPeak != lanes.size();
    }

    private void waitTill(long beforeProcessing) {
        try {
            if (System.currentTimeMillis() - beforeProcessing < 995) {
                Thread.sleep(1000 - (System.currentTimeMillis() - beforeProcessing));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
