package unsw.blackout;

import java.util.HashMap;
import java.util.Map;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public abstract class Device {
    private Map<String, File> files = new HashMap<String, File>();
    private String deviceId;
    private Angle position;
    private int range;

    private String type;

    public Device(String deviceId, Angle position, int range, String type) {
        this.deviceId = deviceId;
        this.position = position;
        this.range = range;
        this.type = type;
    }

    public void addFile(String filename, String content) {
        boolean hasTransferCompleted = true;
        File file = new File(filename, content, hasTransferCompleted);
        files.put(filename, file);
    }

    public Map<String, FileInfoResponse> generateFileResponse() {
        Map<String, FileInfoResponse> fileResponse = new HashMap<String, FileInfoResponse>();
        for (File file : files.values()) {
            fileResponse.put(file.getFilename(), new FileInfoResponse(file.getFilename(), file.getContent(),
                                                                      file.getSize(), file.isHasTransferCompleted()));
        }
        return fileResponse;
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
