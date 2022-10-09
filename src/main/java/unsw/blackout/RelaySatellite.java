package unsw.blackout;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    public static final int RELAY_RANGE = 300_000;
    public static final int RELAY_SPEED = 1_500;
    public static final int RELAY_SEND = -1;
    public static final int RELAY_RECEIVE = -1;
    public static final int RELAY_FILE_STORAGE = 0;
    public static final int RELAY_BYTE_STORAGE = 0;

    public RelaySatellite(String satelliteId, double height, Angle position, String type) {
        super(satelliteId, height, RELAY_RANGE, RELAY_SPEED, position, type, RELAY_SEND, RELAY_RECEIVE,
              RELAY_FILE_STORAGE, RELAY_BYTE_STORAGE);
        setClockwise(checkDirection());
    }

    /**
     * Logic for the direction of movement of the satellite (true = clockwise)
     */
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

    /**
     * Moves the satellite based on the direction
     * and also keeps the angle in bounds
     */
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
            if (getPosition().compareTo(zeroAngle) == -1) {
                setPosition(getPosition().add(fullAngle));
            }
        } else {
            setPosition(position.add(radiansMoved));
            if (getPosition().compareTo(fullAngle) >= 0) {
                setPosition(getPosition().subtract(fullAngle));
            }
        }
    }
}
