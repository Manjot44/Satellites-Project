package unsw.blackout;

import java.util.ArrayList;
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
        File file = new File(filename, content, fullSize, hasTransferCompleted, fromId);
        files.put(filename, file);
    }

    public void receiveFile(String filename, int fullSize, String fromId) {
        String content = "";
        boolean hasTransferCompleted = false;
        File file = new File(filename, content, fullSize, hasTransferCompleted, fromId);
        files.put(filename, file);
    }

    public void sendFile(String filename, String toId) {
        files.get(filename).addToIds(toId);
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

    /**
     * Transfers all the files queued to be sent in one tick
     * if entity is still in range
     * @param control
     */
    public void transfer(BlackoutController control) {
        for (File file : files.values()) {
            List<String> removeToIds = new ArrayList<String>();

            for (String toId : file.getToIds()) {
                List<String> entities = control.communicableEntitiesInRange(deviceId);
                Satellite sat = control.getSatellites().get(toId);

                if (!entities.contains(toId)) {
                    removeToIds.add(toId);
                    if (sat != null) {
                        sat.removeFile(file.getFilename());
                    }
                } else {
                    sendBytes(sat, file, removeToIds);
                }
            }
            file.getToIds().removeAll(removeToIds);
        }
    }

    /**
     * Sends the number of bytes based on the receive speed of the satellite
     * @param sat
     * @param file
     * @param removeToIds
     */
    public void sendBytes(Satellite sat, File file, List<String> removeToIds) {
        int speed = sat.getRecSpeed();
        File toFile = sat.getFile(file.getFilename());
        if (file.getFullSize() - toFile.getCurrentSize() <= speed) {
            toFile.setContent(file.getContent());
            toFile.setHasTransferCompleted(true);
            toFile.setFromId(sat.getSatelliteId());
            removeToIds.add(sat.getSatelliteId());
        } else {
            int newLength = toFile.getCurrentSize() + speed;
            String content = file.getContent();
            toFile.setContent(content.substring(0, newLength));
        }
    }

    /**
     * Returns the number of bytes if the full file exists, otherwise -1
     * @param filename
     * @return
     */
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
