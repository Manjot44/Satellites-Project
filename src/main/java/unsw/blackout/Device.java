package unsw.blackout;

import java.util.HashMap;
import java.util.Map;

import unsw.utils.Angle;

public abstract class Device {
    private Map<String, File> files = new HashMap<String, File>();
    private String deviceId;
    private Angle position;
    private int range;

    private String type;

    public Device(String deviceId, Angle position, String type) {
        this.deviceId = deviceId;
        this.position = position;
        this.type = type;
    }

    public void addFile(String filename, String content) {
        boolean hasTransferCompleted = true;
        File file = new File(filename, content, hasTransferCompleted);
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

    public String getType() {
        return type;
    }

    public void setRange(int range) {
        this.range = range;
    }
}
