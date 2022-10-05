package unsw.blackout;

import unsw.utils.Angle;

public class HandheldDevice extends Device {
    public HandheldDevice(String deviceId, Angle position, String type) {
        super(deviceId, position, type);
        setRange(getRange());
    }

    @Override
    public void setRange(int range) {
        range = 50000;
    }
}
