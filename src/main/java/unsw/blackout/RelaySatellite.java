package unsw.blackout;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    public static final int RELAY_RANGE = 300_000;
    public static final int RELAY_SPEED = 1_500;

    public RelaySatellite(String satelliteId, double height, Angle position, String type) {
        super(satelliteId, height, position, type);
        setRange(RELAY_RANGE);
        setSpeed(RELAY_SPEED);
        setClockwise(checkDirection());
    }

    @Override
    public boolean checkDirection() {
        Angle position = getPosition();
        Angle leftBound = Angle.fromDegrees(140);
        Angle rightBound = Angle.fromDegrees(190);
        Angle thresholdAngle = Angle.fromDegrees(345);

        if (position.compareTo(rightBound) == 1) {
            return position.compareTo(thresholdAngle) == -1;
        }
        return !(position.compareTo(leftBound) == -1);
    }

    @Override
    public void move() {
        Angle position = getPosition();
        Angle leftBound = Angle.fromDegrees(140);
        Angle rightBound = Angle.fromDegrees(190);

        if (position.compareTo(rightBound) == 1) {
            setClockwise(checkDirection());
        } else if (position.compareTo(leftBound) == -1) {
            setClockwise(checkDirection());
        }

        double angularVelocity = getSpeed() / getHeight();
        Angle radiansMoved = Angle.fromRadians(angularVelocity);

        Angle zeroAngle = Angle.fromDegrees(0);
        Angle fullAngle = Angle.fromDegrees(360);
        if (getClockwise()) {
            setPosition(position.subtract(radiansMoved));
            if (position.compareTo(zeroAngle) == -1) {
                setPosition(position.add(fullAngle));
            }
        } else {
            setPosition(position.add(radiansMoved));
            if (position.compareTo(fullAngle) == 1) {
                setPosition(position.subtract(fullAngle));
            }
        }
    }
}
