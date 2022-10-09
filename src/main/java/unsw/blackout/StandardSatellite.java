package unsw.blackout;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {
    public static final int STANDARD_RANGE = 150_000;
    public static final int STANDARD_SPEED = 2_500;
    public static final int STANDARD_SEND = 1;
    public static final int STANDARD_RECEIVE = 1;
    public static final int STANDARD_FILE_STORAGE = 3;
    public static final int STANDARD_BYTE_STORAGE = 80;


    public StandardSatellite(String satelliteId, double height, Angle position, String type) {
        super(satelliteId, height, STANDARD_RANGE, STANDARD_SPEED, position, type, STANDARD_SEND, STANDARD_RECEIVE,
              STANDARD_FILE_STORAGE, STANDARD_BYTE_STORAGE);
        setClockwise(checkDirection());
    }

    @Override
    public boolean checkDirection() {
        return true;
    }

    /**
     * Moves the satellite clockwise and keeps angle in bounds
     */
    @Override
    public void move() {
        Angle position = getPosition();

        double angularVelocity = getSpeed() / getHeight();
        Angle radiansMoved = Angle.fromRadians(angularVelocity);
        setPosition(position.subtract(radiansMoved));

        Angle zeroAngle = Angle.fromDegrees(0);
        if (getPosition().compareTo(zeroAngle) == -1) {
            Angle fullAngle = Angle.fromDegrees(360);
            setPosition(getPosition().add(fullAngle));
        }
    }
}
