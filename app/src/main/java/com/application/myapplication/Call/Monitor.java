package com.application.myapplication.Call;

public class Monitor {
    private boolean led_state;

    public Monitor(boolean led_state) {
        this.led_state = led_state;
    }

    public boolean isLed_state() {
        return led_state;
    }

    public void setLed_state(boolean led_state) {
        this.led_state = led_state;
    }
}
