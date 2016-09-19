package com.thoughtworks.sts.data.modal;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Created by bhupendrakumar on 9/10/16.
 */
public enum PinModel {

    GPIO_01(1),
    GPIO_02(2),
    GPIO_03(3),
    GPIO_04(4),
    GPIO_05(5),
    GPIO_06(6),
    GPIO_07(7),
    GPIO_08(8),
    GPIO_09(9),
    GPIO_10(10),
    GPIO_11(11),
    GPIO_12(12),
    GPIO_13(13),
    GPIO_14(14),
    GPIO_15(15),
    GPIO_16(16),
    GPIO_17(17),
    GPIO_18(18),
    GPIO_19(19),
    GPIO_20(20),
    GPIO_21(21),
    GPIO_22(22),
    GPIO_23(23),
    GPIO_24(24),
    GPIO_25(25),
    GPIO_26(26),
    GPIO_27(27),
    GPIO_28(28),
    GPIO_29(29),
    GPIO_30(30),
    GPIO_31(31), GPIO_00(0);

    final int value;

    PinModel(int value) {
        this.value = value;
    }

    private Pin[] gpioPins = {RaspiPin.GPIO_00, RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_04, RaspiPin.GPIO_05, RaspiPin.GPIO_06, RaspiPin.GPIO_07, RaspiPin.GPIO_08, RaspiPin.GPIO_09,
            RaspiPin.GPIO_10, RaspiPin.GPIO_11, RaspiPin.GPIO_12, RaspiPin.GPIO_13, RaspiPin.GPIO_14, RaspiPin.GPIO_15, RaspiPin.GPIO_16, RaspiPin.GPIO_18, RaspiPin.GPIO_18, RaspiPin.GPIO_19,
            RaspiPin.GPIO_20, RaspiPin.GPIO_21, RaspiPin.GPIO_22, RaspiPin.GPIO_23, RaspiPin.GPIO_24, RaspiPin.GPIO_25, RaspiPin.GPIO_26, RaspiPin.GPIO_27, RaspiPin.GPIO_28, RaspiPin.GPIO_29,
            RaspiPin.GPIO_30, RaspiPin.GPIO_31};

    public Pin getPin() {
        return gpioPins[value];
    }
}
