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

    public Satellite(String satelliteId, double height, Angle position, String type) {
        this.satelliteId = satelliteId;
        this.height = height;
        this.position = position;
        this.type = type;
    }

    public void addFile(String filename, String content) {
        File file = new File(filename, content);
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

    public abstract void setSpeed(int speed);

    public abstract void setRange(int range);
}
