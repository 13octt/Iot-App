package com.application.myapplication.Call;

import com.google.gson.annotations.SerializedName;

public class LedState {
    private boolean led_state;

    public LedState(boolean led_state) {
        this.led_state = led_state;
    }

    public boolean isLed_state() {
        return led_state;
    }

    public void setLed_state(boolean led_state) {
        this.led_state = led_state;
    }
}
