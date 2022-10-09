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

    /**
     * Moves the satellite based on the direction while keeping angle in bounds
     * Also checks if satellite has teleported and adjusts
     * direction and position accordingly
     */
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
            if (getPosition().compareTo(zeroAngle) == -1) {
                setPosition(getPosition().add(fullAngle));
            }
            if (position.compareTo(thresholdAngle) == 1 && getPosition().compareTo(thresholdAngle) <= 0) {
                setPosition(zeroAngle);
                setClockwise(checkDirection());
                isTeleported = true;
            }
        } else {
            setPosition(position.add(radiansMoved));
            if (getPosition().compareTo(fullAngle) >= 0) {
                setPosition(getPosition().subtract(fullAngle));
            }
            if (position.compareTo(thresholdAngle) == -1 && getPosition().compareTo(thresholdAngle) >= 0) {
                setPosition(zeroAngle);
                setClockwise(checkDirection());
                isTeleported = true;
            }
        }
    }

    /**
     * Transfers all files based on specified teleporting behaviour
     * (split into 4 helper functions)
     * @param control
     */
    public void teleTransfer(BlackoutController control) {
        for (File file : getFiles().values()) {
            String fromId = file.getFromId();
            if (control.getDevices().containsKey(fromId)) {
                teleFromDevice(file, control.getDevices().get(fromId));
            } else if (fromId != getSatelliteId() && control.getSatellites().containsKey(fromId)) {
                teleFromSat(file, control.getSatellites().get(fromId));
            }

            for (String toId : file.getToIds()) {
                if (control.getDevices().containsKey(toId)) {
                    teletoDevice(file, control.getDevices().get(toId));
                }  else if (control.getSatellites().containsKey(toId)) {
                    teleToSat(file, control.getSatellites().get(toId));
                }
            }
            file.getToIds().clear();
        }
        isTeleported = false;
    }

    /**
     * Applies teleporting behaviour on file transferring from device
     * @param file
     * @param device
     */
    public void teleFromDevice(File file, Device device) {
        getFiles().remove(file.getFilename());

        File fromFile = device.getFile(file.getFilename());
        String content = fromFile.getContent();
        content = content.replace("t", "");
        fromFile.setContent(content);
        fromFile.setFullSize(content.length());
        fromFile.removeToIds(getSatelliteId());
    }

    /**
     * Applies teleporting behaviour on file transferring to device
     * @param file
     * @param device
     */
    public void teletoDevice(File file, Device device) {
        File toFile = device.getFile(file.getFilename());
        String content = file.getContent();
        String remContent = content.substring(toFile.getCurrentSize());
        remContent = remContent.replace("t", "");

        toFile.setContent(toFile.getContent() + remContent);
        toFile.setFullSize(toFile.getContent().length());
        toFile.setHasTransferCompleted(true);
        toFile.setFromId(device.getDeviceId());
    }

    /**
     * Applies teleporting behaviour on file transferring from satellite
     * @param file
     * @param satellite
     */
    public void teleFromSat(File file, Satellite satellite) {
        File fromFile = satellite.getFile(file.getFilename());
        String content = fromFile.getContent();
        String remContent = content.substring(file.getCurrentSize());
        remContent = remContent.replace("t", "");

        file.setContent(file.getContent() + remContent);
        file.setFullSize(file.getContent().length());
        file.setHasTransferCompleted(true);
        file.setFromId(getSatelliteId());
        fromFile.removeToIds(getSatelliteId());
    }

    /**
     * Applies teleporting behaviour on file transferring to satellite
     * @param file
     * @param satellite
     */
    public void teleToSat(File file, Satellite satellite) {
        File toFile = satellite.getFile(file.getFilename());
        String content = file.getContent();
        String remContent = content.substring(toFile.getCurrentSize());
        remContent = remContent.replace("t", "");

        toFile.setContent(toFile.getContent() + remContent);
        toFile.setFullSize(toFile.getContent().length());
        toFile.setHasTransferCompleted(true);
        toFile.setFromId(satellite.getSatelliteId());
    }

    public boolean isTeleported() {
        return isTeleported;
    }
}
