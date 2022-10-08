package unsw.blackout;

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

    public void transfer(BlackoutController control) {
        for (File file : files.values()) {
            if (file.getToId() == satelliteId) continue;

            List<String> entities = control.communicableEntitiesInRange(satelliteId);
            String toId = file.getToId();

            if (control.getDevices().containsKey(toId)) {
                Device dev = control.getDevices().get(toId);
                if (!entities.contains(toId)) {
                    file.setToId(satelliteId);
                    if (dev != null) {
                        dev.removeFile(file.getFilename());
                    }
                } else {
                    sendBytesToDev(dev, file);
                }
            } else {
                Satellite sat = control.getSatellites().get(toId);
                if (!entities.contains(toId)) {
                    file.setToId(satelliteId);
                    if (sat != null) {
                        sat.removeFile(file.getFilename());
                    }
                } else {
                    sendBytesToSat(sat, file);
                }
            }
        }
    }

    public void sendBytesToSat(Satellite sat, File file) {
        int speed;
        if (sat.getRecSpeed() <= sendBandwidth / this.numSend()) {
            speed = sat.getRecSpeed();
        } else {
            speed = this.numReceive() / sendBandwidth;
        }

        File toFile = sat.getFile(file.getFilename());
        if (file.getFullSize() - toFile.getCurrentSize() <= speed) {
            toFile.setContent(file.getContent());
            toFile.setHasTransferCompleted(true);
            toFile.setFromId(sat.getSatelliteId());
            file.setToId(satelliteId);
        } else {
            int newLength = toFile.getCurrentSize() + speed;
            String content = file.getContent();
            toFile.setContent(content.substring(0, newLength));
        }
    }

    public void sendBytesToDev(Device dev, File file) {
        int speed = sendBandwidth / numSend();
        File toFile = dev.getFile(file.getFilename());
        if (file.getFullSize() - toFile.getCurrentSize() <= speed) {
            toFile.setContent(file.getContent());
            toFile.setHasTransferCompleted(true);
            toFile.setFromId(dev.getDeviceId());
            file.setToId(satelliteId);
        } else {
            int newLength = toFile.getCurrentSize() + speed;
            String content = file.getContent();
            toFile.setContent(content.substring(0, newLength));
        }
    }

    public void receiveFile(String filename, int fullSize, String fromId) {
        String content = "";
        boolean hasTransferCompleted = false;
        String toId = satelliteId;
        File file = new File(filename, content, fullSize, hasTransferCompleted, fromId, toId);
        files.put(filename, file);
    }

    public void sendFile(String filename, String toId) {
        files.get(filename).setToId(toId);
    }

    public void removeFile(String filename) {
        files.remove(filename);
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
            if (file.getToId() != satelliteId) {
                numSend++;
            }
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
