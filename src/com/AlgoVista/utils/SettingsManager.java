package com.AlgoVista.utils;

import java.util.prefs.Preferences;

public class SettingsManager {
    private static final Preferences prefs = Preferences.userNodeForPackage(SettingsManager.class);
    
    private static final String AUDIO_ENABLED = "AUDIO_ENABLED";
    private static final String ANIMATION_SPEED = "ANIMATION_SPEED"; // 0.25 to 2.0
    
    public static boolean isAudioEnabled() {
        return prefs.getBoolean(AUDIO_ENABLED, true);
    }
    
    public static void setAudioEnabled(boolean val) {
        prefs.putBoolean(AUDIO_ENABLED, val);
    }
    
    public static double getSpeed() {
        return prefs.getDouble(ANIMATION_SPEED, 1.0);
    }
    
    public static void setSpeed(double val) {
        prefs.putDouble(ANIMATION_SPEED, val);
    }
    
    /**
     * Exponential conversion from slider value (0.25-2.0) to actual delay/rate multiplier.
     * Use Base 10 curve: 
     *  Slider 1.0 -> Multiplier 1.0
     *  Slider 2.0 -> Multiplier 10.0
     *  Slider 0.25 -> Multiplier ~0.17 (5.6x slower)
     */
    public static double getMultiplier(double sliderValue) {
        return Math.pow(10.0, sliderValue - 1.0);
    }

    public static double getSleepMultiplier() {
        // Returns the DELAY multiplier (1 / speed multiplier)
        return 1.0 / getMultiplier(getSpeed());
    }

    public static double getTimelineRate(double sliderValue) {
        // Returns the RATE multiplier (speed multiplier)
        return getMultiplier(sliderValue);
    }
}
