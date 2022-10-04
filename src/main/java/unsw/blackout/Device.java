package unsw.blackout;

import unsw.utils.Angle;

public abstract class Device {
    private String deviceId;
    private Angle position;
    private int range;

    public Device(String deviceId, Angle position) {
        this.deviceId = deviceId;
        this.position = position;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Angle getPosition() {
        return position;
    }

    public int getRange() {
        return range;
    }

    public abstract void setRange(int range);
}
