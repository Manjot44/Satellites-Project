package unsw.blackout;

import unsw.utils.Angle;

public class HandheldDevice extends Device {
    public static final int HANDHELD_RANGE = 50_000;

    public HandheldDevice(String deviceId, Angle position, String type) {
        super(deviceId, position, HANDHELD_RANGE, type);
    }
}
