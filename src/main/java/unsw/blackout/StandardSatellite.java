package unsw.blackout;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {
    public static final int STANDARD_RANGE = 150_000;
    public static final int STANDARD_SPEED = 2_500;

    public StandardSatellite(String satelliteId, double height, Angle position, String type) {
        super(satelliteId, height, STANDARD_RANGE, STANDARD_SPEED, position, type);
        setClockwise(checkDirection());
    }

    @Override
    public boolean checkDirection() {
        return true;
    }

    @Override
    public void move() {
        Angle position = getPosition();

        double angularVelocity = getSpeed() / getHeight();
        Angle radiansMoved = Angle.fromRadians(angularVelocity);
        setPosition(position.subtract(radiansMoved));

        Angle zeroAngle = Angle.fromDegrees(0);
        if (position.compareTo(zeroAngle) == -1) {
            Angle fullAngle = Angle.fromDegrees(360);
            setPosition(position.add(fullAngle));
        }
    }
}
