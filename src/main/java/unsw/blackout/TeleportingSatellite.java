package unsw.blackout;

import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {
    public static final int TELEPORTING_RANGE = 200_000;
    public static final int TELEPORTING_SPEED = 1_000;

    public TeleportingSatellite(String satelliteId, double height, Angle position, String type) {
        super(satelliteId, height, position, type);
        setRange(TELEPORTING_RANGE);
        setSpeed(TELEPORTING_SPEED);
        setClockwise(checkDirection());
    }

    @Override
    public boolean checkDirection() {
        return !getClockwise();
    }

    @Override
    public void move() {
        Angle position = getPosition();

        double angularVelocity = getSpeed() / getHeight();
        Angle radiansMoved = Angle.fromRadians(angularVelocity);

        Angle thresholdAngle = Angle.fromDegrees(180);
        Angle zeroAngle = Angle.fromDegrees(0);
        Angle fullAngle = Angle.fromDegrees(360);
        if (getClockwise()) {
            setPosition(position.subtract(radiansMoved));
            if (position.compareTo(zeroAngle) == -1) {
                setPosition(position.add(fullAngle));
            }
            if (position.compareTo(thresholdAngle) == 1 && getPosition().compareTo(thresholdAngle) == -1) {
                setPosition(Angle.fromDegrees(0));
                setClockwise(checkDirection());
            }
        } else {
            setPosition(position.add(radiansMoved));
            if (position.compareTo(fullAngle) == 1) {
                setPosition(position.subtract(fullAngle));
            }
            if (position.compareTo(thresholdAngle) == -1 && getPosition().compareTo(thresholdAngle) == 1) {
                setPosition(Angle.fromDegrees(0));
                setClockwise(checkDirection());
            }
        }
    }
}
