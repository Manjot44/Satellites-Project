package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public abstract class Satellite {
    private Map<String, File> files = new HashMap<String, File>();
    private String satelliteId;
    private double height;
    private Angle position;
    private int speed;
    private int range;
    private String type;
    private boolean isClockwise;
    private int sendBandwidth;
    private int receiveBandwidth;
    private int fileStorage;
    private int byteStorage;

    public Satellite(String satelliteId, double height, int range, int speed, Angle position, String type,
                     int sendBandwidth, int receiveBandwidth, int fileStorage, int byteStorage) {
        this.satelliteId = satelliteId;
        this.height = height;
        this.position = position;
        this.speed = speed;
        this.range = range;
        this.type = type;
        this.isClockwise = true;
        this.sendBandwidth = sendBandwidth;
        this.receiveBandwidth = receiveBandwidth;
        this.fileStorage = fileStorage;
        this.byteStorage = byteStorage;
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
                handleTransfer(control, file, toId, removeToIds);
            }
            file.getToIds().removeAll(removeToIds);
        }
    }

    /**
     * Handles the transfer based on whether it is to a device or satellite
     * @param control
     * @param file
     * @param toId
     * @param removeToIds
     */
    public void handleTransfer(BlackoutController control, File file, String toId, List<String> removeToIds) {
        List<String> entities = control.communicableEntitiesInRange(satelliteId);
        if (control.getDevices().containsKey(toId)) {
            Device dev = control.getDevices().get(toId);
            if (!entities.contains(toId)) {
                removeToIds.add(toId);
                if (dev != null) {
                    dev.removeFile(file.getFilename());
                }
            } else {
                sendBytesToDev(dev, file, removeToIds);
            }
        } else {
            Satellite sat = control.getSatellites().get(toId);
            if (!entities.contains(toId)) {
                removeToIds.add(toId);
                if (sat != null) {
                    sat.removeFile(file.getFilename());
                }
            } else {
                sendBytesToSat(sat, file, removeToIds);
            }
        }
    }

    /**
     * Sends the number of bytes throttling to the minimum send/receive speed
     * @param sat
     * @param file
     * @param removeToIds
     */
    public void sendBytesToSat(Satellite sat, File file, List<String> removeToIds) {
        int speed;
        if (sat.getRecSpeed() <= sendBandwidth / this.numSend()) {
            speed = sat.getRecSpeed();
        } else {
            speed = sendBandwidth / this.numSend();
        }

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
     * Sends the number of bytes depending on the send speed of the satellite
     * @param dev
     * @param file
     * @param removeToIds
     */
    public void sendBytesToDev(Device dev, File file, List<String> removeToIds) {
        int speed = sendBandwidth / numSend();
        File toFile = dev.getFile(file.getFilename());
        if (file.getFullSize() - toFile.getCurrentSize() <= speed) {
            toFile.setContent(file.getContent());
            toFile.setHasTransferCompleted(true);
            toFile.setFromId(dev.getDeviceId());
            removeToIds.add(dev.getDeviceId());
        } else {
            int newLength = toFile.getCurrentSize() + speed;
            String content = file.getContent();
            toFile.setContent(content.substring(0, newLength));
        }
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

    public int numReceive() {
        int numReceive = 0;
        for (File file : files.values()) {
            if (file.getFromId() != satelliteId) {
                numReceive++;
            }
        }
        return numReceive;
    }

    public boolean recBandFull() {
        return numReceive() == receiveBandwidth;
    }

    public int getRecSpeed() {
        return receiveBandwidth / numReceive();
    }

    public int numSend() {
        int numSend = 0;
        for (File file : files.values()) {
            numSend += file.getToIds().size();
        }
        return numSend;
    }
    public boolean sendBandFull() {
        return numSend() == sendBandwidth;
    }

    public boolean fileFull() {
        return fileStorage == files.size();
    }

    public boolean storageFull(int bytes) {
        int totalSize = bytes;
        for (File file : files.values()) {
            totalSize += file.getFullSize();
        }
        return totalSize > byteStorage;
    }

    public File getFile(String filename) {
        return files.get(filename);
    }

    public Map<String, File> getFiles() {
        return files;
    }

    public String getSatelliteId() {
        return satelliteId;
    }

    public double getHeight() {
        return height;
    }

    public Angle getPosition() {
        return position;
    }

    public int getSpeed() {
        return speed;
    }

    public int getRange() {
        return range;
    }

    public String getType() {
        return type;
    }

    public boolean getClockwise() {
        return isClockwise;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public void setClockwise(boolean isClockwise) {
        this.isClockwise = isClockwise;
    }

    public abstract boolean checkDirection();
    public abstract void move();
}
