package unsw.blackout;

import java.util.HashMap;
import java.util.Map;

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

    public Satellite(String satelliteId, double height, Angle position, String type) {
        this.satelliteId = satelliteId;
        this.height = height;
        this.position = position;
        this.type = type;
        this.isClockwise = true;
    }

    ///////////////////////////////////// edit this function ////////////////////////////////////////////////////
    public void addFile(String filename, String content) {
        boolean hasTransferCompleted = false;
        File file = new File(filename, content, hasTransferCompleted);
        files.put(filename, file);
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
