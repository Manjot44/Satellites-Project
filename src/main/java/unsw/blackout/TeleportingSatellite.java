package unsw.blackout;

import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {

    public TeleportingSatellite(String satelliteId, double height, Angle position, String type) {
        super(satelliteId, height, position, type);
        setRange(getRange());
        setSpeed(getSpeed());
    }

    @Override
    public void setRange(int range) {
        range = 200000;
    }

    @Override
    public void setSpeed(int speed) {
        speed = 1000;
    }
}
