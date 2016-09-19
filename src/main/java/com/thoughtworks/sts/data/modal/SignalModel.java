package com.thoughtworks.sts.data.modal;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by salonivithalani on 9/3/16.
 */
public class SignalModel {

    @Expose
    private String id;

    @Expose
    private String name;

    @Expose
    private List<LaneModel> lanes = new ArrayList<>();

    @Expose
    private int bufferTime;

    @Expose
    private int greenTimeFactor;

    @Expose
    private float adjustmentFactor;

    @Expose
    private int yellowTime;

    public SignalModel(String name, int bufferTime) {

        this.id = UUID.randomUUID().toString();
        this.bufferTime = bufferTime;
        this.name = name;
        this.adjustmentFactor = 15f;
        this.yellowTime = 3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LaneModel> getLanesModel() {
        return lanes;
    }

    public void addLane(LaneModel laneModel) {
        lanes.add(laneModel);
    }

    public int getBufferTime() {
        return bufferTime;
    }

    public void setBufferTime(int bufferTime) {
        this.bufferTime = bufferTime;
    }

    public int getGreenTimeFactor() {
        return greenTimeFactor;
    }

    public void setGreenTimeFactor(int greenTimeFactor) {
        this.greenTimeFactor = greenTimeFactor;
    }

    public float getAdjustmentFactor() {
        return adjustmentFactor;
    }

    public void setAdjustmentFactor(float adjustmentFactor) {
        this.adjustmentFactor = adjustmentFactor;
    }

    public int getYellowTime() {
        return yellowTime;
    }

    public void setYellowTime(int yellowTime) {
        this.yellowTime = yellowTime;
    }

    public String getId() {
        return id;
    }
}
