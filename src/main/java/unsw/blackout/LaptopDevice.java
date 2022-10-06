package unsw.blackout;

import unsw.utils.Angle;

public class LaptopDevice extends Device {
    public static final int LAPTOP_RANGE = 100_000;

    public LaptopDevice(String deviceId, Angle position, String type) {
        super(deviceId, position, LAPTOP_RANGE, type);
    }
}
