package unsw.blackout;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {

    public RelaySatellite(String satelliteId, double height, Angle position, String type) {
        super(satelliteId, height, position, type);
        setRange(getRange());
        setSpeed(getSpeed());
    }

    @Override
    public void setRange(int range) {
        range = 300000;
    }

    @Override
    public void setSpeed(int speed) {
        speed = 1500;
    }
}
