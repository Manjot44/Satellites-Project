package unsw.blackout;

import unsw.utils.Angle;

public class LaptopDevice extends Device {
    public LaptopDevice(String deviceId, Angle position, String type) {
        super(deviceId, position, type);
        setRange(getRange());
    }

    @Override
    public void setRange(int range) {
        range = 100000;
    }
}
