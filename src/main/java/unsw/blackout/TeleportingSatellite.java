package unsw.blackout;

import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {
    public static final int TELE_RANGE = 200_000;
    public static final int TELE_SPEED = 1_000;
    public static final int TELE_SEND = 10;
    public static final int TELE_RECEIVE = 15;
    public static final int TELE_FILE_STORAGE = -1;
    public static final int TELE_BYTE_STORAGE = 200;

    private boolean isTeleported;

    public TeleportingSatellite(String satelliteId, double height, Angle position, String type) {
        super(satelliteId, height, TELE_RANGE, TELE_SPEED, position, type, TELE_SEND, TELE_RECEIVE,
              TELE_FILE_STORAGE, TELE_BYTE_STORAGE);
        this.isTeleported = false;
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
            if (position.compareTo(thresholdAngle) == 1 && getPosition().compareTo(thresholdAngle) <= 0) {
                setPosition(zeroAngle);
                setClockwise(checkDirection());
                isTeleported = true;
            }
        } else {
            setPosition(position.add(radiansMoved));
            if (position.compareTo(fullAngle) == 1) {
                setPosition(position.subtract(fullAngle));
            }
            if (position.compareTo(thresholdAngle) == -1 && getPosition().compareTo(thresholdAngle) >= 0) {
                setPosition(zeroAngle);
                setClockwise(checkDirection());
                isTeleported = true;
            }
        }
    }

    public void teleTransfer(BlackoutController control) {
        for (File file : getFiles().values()) {
            String fromId = file.getFromId();
            String toId = file.getToId();
            if (control.getDevices().containsKey(fromId)) {
                teleFromDevice(file, control.getDevices().get(fromId));
            } else if (control.getDevices().containsKey(toId)) {
                teletoDevice(file, control.getDevices().get(toId));
            } else if (fromId != getSatelliteId() && control.getSatellites().containsKey(fromId)) {
                teleFromSat(file, control.getSatellites().get(fromId));
            } else if (toId != getSatelliteId() && control.getSatellites().containsKey(toId)) {
                teleToSat(file, control.getSatellites().get(toId));
            }
        }
        isTeleported = false;
    }

    public void teleFromDevice(File file, Device device) {
        getFiles().remove(file.getFilename());

        File fromFile = device.getFile(file.getFilename());
        String content = fromFile.getContent();
        content = content.replace("t", "");
        fromFile.setContent(content);
        fromFile.setFullSize(content.length());
        fromFile.setFromId(device.getDeviceId());
    }

    public void teletoDevice(File file, Device device) {
        File toFile = device.getFile(file.getFilename());
        String content = file.getContent();
        String remContent = content.substring(toFile.getCurrentSize());
        remContent = remContent.replace("t", "");

        toFile.setContent(toFile.getContent() + remContent);
        toFile.setFullSize((toFile.getContent() + remContent).length());
        toFile.setHasTransferCompleted(true);
        toFile.setFromId(device.getDeviceId());
        file.setToId(getSatelliteId());
    }

    public void teleFromSat(File file, Satellite satellite) {
        File fromFile = satellite.getFile(file.getFilename());
        String content = fromFile.getContent();
        String remContent = content.substring(file.getCurrentSize());
        remContent = remContent.replace("t", "");

        file.setContent(file.getContent() + remContent);
        file.setFullSize((file.getContent() + remContent).length());
        file.setHasTransferCompleted(true);
        file.setFromId(getSatelliteId());
        fromFile.setToId(satellite.getSatelliteId());
    }

    public void teleToSat(File file, Satellite satellite) {
        File toFile = satellite.getFile(file.getFilename());
        String content = file.getContent();
        String remContent = content.substring(toFile.getCurrentSize());
        remContent = remContent.replace("t", "");

        toFile.setContent(toFile.getContent() + remContent);
        toFile.setFullSize((toFile.getContent() + remContent).length());
        toFile.setHasTransferCompleted(true);
        toFile.setFromId(satellite.getSatelliteId());
        file.setToId(getSatelliteId());
    }

    public boolean isTeleported() {
        return isTeleported;
    }
}
