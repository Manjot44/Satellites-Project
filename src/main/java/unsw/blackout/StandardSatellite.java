package unsw.blackout;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {

    public StandardSatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position);
        setRange(getRange());
        setSpeed(getSpeed());
    }

    @Override
    public void setRange(int range) {
        range = 150000;
    }

    @Override
    public void setSpeed(int speed) {
        speed = 2500;
    }
}
