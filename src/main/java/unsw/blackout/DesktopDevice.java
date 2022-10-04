package unsw.blackout;

import unsw.utils.Angle;

public class DesktopDevice extends Device {
    public DesktopDevice(String deviceId, Angle position) {
        super(deviceId, position);
        setRange(getRange());
    }

    @Override
    public void setRange(int range) {
        range = 200000;
    }
}
