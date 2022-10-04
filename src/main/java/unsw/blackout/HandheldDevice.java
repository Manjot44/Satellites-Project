package unsw.blackout;

import unsw.utils.Angle;

public class HandheldDevice extends Device {
    public HandheldDevice(String deviceId, Angle position) {
        super(deviceId, position);
        setRange(getRange());
    }

    @Override
    public void setRange(int range) {
        range = 50000;
    }
}
