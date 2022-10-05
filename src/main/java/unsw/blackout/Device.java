package unsw.blackout;

import java.util.HashMap;
import java.util.Map;

import unsw.utils.Angle;

public abstract class Device {
    private Map<String, File> files = new HashMap<String, File>();
    private String deviceId;
    private Angle position;
    private int range;

    public Device(String deviceId, Angle position) {
        this.deviceId = deviceId;
        this.position = position;
    }

    public void addFile(String filename, String content) {
        File file = new File(filename, content);
        files.put(filename, file);
    }

    public Map<String, File> getFiles() {
        return files;
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
