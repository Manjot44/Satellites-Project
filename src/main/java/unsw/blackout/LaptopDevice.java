package unsw.blackout;

import unsw.utils.Angle;

public class LaptopDevice extends Device {
    public LaptopDevice(String deviceId, Angle position) {
        super(deviceId, position);
        setRange(getRange());
    }

    @Override
    public void setRange(int range) {
        range = 100000;
    }
}
