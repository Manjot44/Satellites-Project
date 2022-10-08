package unsw.blackout;

import java.util.HashMap;
import java.util.List;
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
        int fullSize = content.length();
        boolean hasTransferCompleted = true;
        String fromId = deviceId;
        String toId = deviceId;
        File file = new File(filename, content, fullSize, hasTransferCompleted, fromId, toId);
        files.put(filename, file);
    }

    public void receiveFile(String filename, int fullSize, String fromId) {
        String content = "";
        boolean hasTransferCompleted = false;
        String toId = deviceId;
        File file = new File(filename, content, fullSize, hasTransferCompleted, fromId, toId);
        files.put(filename, file);
    }

    public void sendFile(String filename, String toId) {
        files.get(filename).setToId(toId);
    }

    public void removeFile(String filename) {
        files.remove(filename);
    }

    public Map<String, FileInfoResponse> generateFileResponse() {
        Map<String, FileInfoResponse> fileResponse = new HashMap<String, FileInfoResponse>();
        for (File file : files.values()) {
            fileResponse.put(file.getFilename(), new FileInfoResponse(file.getFilename(), file.getContent(),
                                                                      file.getFullSize(),
                                                                      file.isHasTransferCompleted()));
        }
        return fileResponse;
    }

    public void transfer(BlackoutController control) {
        for (File file : files.values()) {
            if (file.getToId() == deviceId) continue;

            List<String> entities = control.communicableEntitiesInRange(deviceId);
            String toId = file.getToId();
            Satellite sat = control.getSatellites().get(toId);

            if (!entities.contains(toId)) {
                file.setToId(deviceId);
                if (sat != null) {
                    sat.removeFile(file.getFilename());
                }
            } else {
                sendBytes(sat, file);
            }
        }
    }

    public void sendBytes(Satellite sat, File file) {
        int speed = sat.getRecSpeed();
        File toFile = sat.getFile(file.getFilename());
        if (file.getFullSize() - toFile.getCurrentSize() <= speed) {
            toFile.setContent(file.getContent());
            toFile.setHasTransferCompleted(true);
            toFile.setFromId(sat.getSatelliteId());
            file.setToId(deviceId);
        } else {
            int newLength = toFile.getCurrentSize() + speed;
            String content = file.getContent();
            toFile.setContent(content.substring(0, newLength));
        }
    }

    public int fullFileExists(String filename) {
        if (files.containsKey(filename) && files.get(filename).isHasTransferCompleted()) {
            return files.get(filename).getFullSize();
        } else {
            return -1;
        }
    }

    public boolean partFileExists(String filename) {
        return files.containsKey(filename);
    }

    public File getFile(String filename) {
        return files.get(filename);
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
}
