package unsw.blackout;

import unsw.utils.Angle;

public class DesktopDevice extends Device {
    public static final int DESKTOP_RANGE = 200_000;

    public DesktopDevice(String deviceId, Angle position, String type) {
        super(deviceId, position, DESKTOP_RANGE, type);
    }
}
