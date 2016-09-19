package com.thoughtworks.sts.services;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.sts.data.modal.LaneModel;
import com.thoughtworks.sts.data.modal.PinModel;
import com.thoughtworks.sts.data.modal.SignalModel;
import com.thoughtworks.sts.data.modal.UltrasonicSensorModel;
import com.thoughtworks.sts.model.Lane;
import com.thoughtworks.sts.model.Signal;
import com.thoughtworks.sts.model.UltrasonicSensor;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhupendrakumar on 9/11/16.
 */
public class SignalService {

    private Logger logger = Logger.getLogger(SignalService.class);

    private static final Map<String, Signal> signals = new HashMap<>();

    public Signal createSignal(String signalId) throws Exception {

        SignalModel signalModel = getSignalModel(signalId);

        Signal signal = new Signal(signalModel.getId(), signalModel.getName(), signalModel.getBufferTime());
        signal.setAdjustmentFactor(signalModel.getAdjustmentFactor());

        for (LaneModel laneModel : signalModel.getLanesModel()) {

            Lane lane = new Lane(laneModel.getId(), laneModel.getName(), laneModel.getDefaultGreenTime());
            lane.setThresholdFactor(laneModel.getThresholdFactor());

            for (UltrasonicSensorModel sensorModel : laneModel.getSensorsModel()) {
                UltrasonicSensor sensor = new UltrasonicSensor(sensorModel.getId(), sensorModel.getEchoPin().getPin(), sensorModel.getTrigPin().getPin());
                sensor.setRoadDistance(sensorModel.getRoadDistance());
                lane.addSensor(sensor);
            }
            signal.addLane(lane);
        }
        return signal;
    }

    public Signal startSignal(String signalId) throws Exception {
        stopSignal(signalId);
        Signal signal = signals.get(signalId);
        if (signal == null) {
            signal = createSignal(signalId);
            signals.put(signalId, signal);
        }
        signal.start();
        return signal;
    }

    private SignalModel getSignalModel(String signalId) throws Exception {

        List<SignalModel> signalModels = getSignalConfig();
        if (signalModels != null && signalModels.isEmpty()) {
            logger.error("No saved signal config.");
            throw new Exception("No saved signal config.");
        }
        for (SignalModel signalModel : signalModels) {
            if (signalModel.getId().equals(signalId)) {
                return signalModel;
            }
        }
        throw new Exception("Signal with id " + signalId + " not available.");
    }

    public List<SignalModel> getSignalConfig() {

        List<SignalModel> signalModels = null;
        try {
            String config = readSignalConfig();
            if (signalModels != null && signalModels.isEmpty()) {
                signalModels = new ArrayList<>();
                return signalModels;
            }
            Type listType = new TypeToken<List<SignalModel>>() {
            }.getType();
            signalModels = new GsonBuilder().create().fromJson(config, listType);
        } catch (Exception e) {
            logger.error("Error while getting signal config from file.", e);
        }
        return signalModels;
    }

    private String readSignalConfig() throws IOException {

        InputStreamReader inputStream = new FileReader(new File("config.json"));
        StringBuilder builder = new StringBuilder();

        char c;
        int i;

        try {
            while ((i = inputStream.read()) != -1) {
                c = (char) i;
                builder.append(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
        return builder.toString();
    }

    public void stopSignal(String signalId) {
        Signal signal = signals.get(signalId);
        if (signal != null) {
            signal.stop();
        }
    }

    public void pauseSignal(String signalId) {
        Signal signal = signals.get(signalId);
        if (signal != null) {
            signal.pause();
        }
    }

    public void resumeSignal(String signalId) {
        Signal signal = signals.get(signalId);
        if (signal != null) {
            signal.resume();
        }
    }

    public List<SignalModel> createDefaultConfig() {

        List<SignalModel> signalModels = new ArrayList<>();

        SignalModel signal = new SignalModel("Golf Course", 5);


        //1st : Echo   2nd: trigger pin
        UltrasonicSensorModel sensorModel0 = new UltrasonicSensorModel(PinModel.GPIO_00, PinModel.GPIO_07);
        UltrasonicSensorModel sensorModel1 = new UltrasonicSensorModel(PinModel.GPIO_02, PinModel.GPIO_03);
        UltrasonicSensorModel sensorModel2 = new UltrasonicSensorModel(PinModel.GPIO_04, PinModel.GPIO_05);
        UltrasonicSensorModel sensorModel3 = new UltrasonicSensorModel(PinModel.GPIO_06, PinModel.GPIO_08);

        LaneModel east = new LaneModel("A", 10);
        east.addSensor(sensorModel0);

        LaneModel west = new LaneModel("B", 10);
//        west.addSensor(sensorModel1);

        LaneModel north = new LaneModel("C", 10);
//        north.addSensor(sensorModel2);

        LaneModel south = new LaneModel("D", 10);
//        south.addSensor(sensorModel3);

        signal.addLane(east);
        signal.addLane(west);
        signal.addLane(north);
        signal.addLane(south);
        signalModels.add(signal);

        saveConfig(signalModels);

        return signalModels;
    }

    private void saveConfig(List<SignalModel> signalModels) {

        String json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(signalModels);
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(new File("config.json")));
            writer.write(json);
        } catch (FileNotFoundException e) {
            logger.error("Config file not found.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(writer);
        }
    }

    private static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
        }
    }
}
