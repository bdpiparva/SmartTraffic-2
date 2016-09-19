package com.thoughtworks.sts;

import com.thoughtworks.sts.data.modal.SignalModel;
import com.thoughtworks.sts.services.SignalService;

import java.util.List;

/**
 * Created by bhupendrakumar on 9/19/16.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        SignalService signalService = new SignalService();
        List<SignalModel> signalModels = signalService.createDefaultConfig();
        if (signalModels == null || signalModels.isEmpty()) {
            System.out.println("Empty signal");
        } else {
            SignalModel signalModel = signalModels.get(0);
            signalService.startSignal(signalModel.getId());
        }
    }
}
