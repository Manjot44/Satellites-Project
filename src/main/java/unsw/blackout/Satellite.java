package unsw.blackout;

import java.util.HashMap;
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

    public Satellite(String satelliteId, double height, int range, int speed, Angle position, String type) {
        this.satelliteId = satelliteId;
        this.height = height;
        this.position = position;
        this.speed = speed;
        this.range = range;
        this.type = type;
        this.isClockwise = true;
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

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setClockwise(boolean isClockwise) {
        this.isClockwise = isClockwise;
    }

    public abstract boolean checkDirection();
    public abstract void move();
}
