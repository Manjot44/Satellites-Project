package unsw.utils;

import java.util.List;

import unsw.blackout.BlackoutController;
import unsw.blackout.Device;
import unsw.blackout.FileTransferException;
import unsw.blackout.Satellite;

public abstract class CommsHelper {
    public static void addRecursive(BlackoutController control, List<String> inRange,
                                    String id, boolean isSenderDevice) {
        if (control.getDevices().containsKey(id)) {
            deviceToSatellite(control, inRange, control.getDevices().get(id), isSenderDevice);
        } else {
            satelliteToSatellite(control, inRange, control.getSatellites().get(id), isSenderDevice);
            if (!isSenderDevice) {
                satelliteToDevice(control, inRange, control.getSatellites().get(id));
            }
        }
    }

    public static void deviceToSatellite(BlackoutController control, List<String> inRange, Device dev,
                                         boolean isSenderDevice) {
        for (Satellite sat : control.getSatellites().values()) {
            if (deviceInRange(dev, sat, dev.getRange()) && !inRange.contains(sat.getSatelliteId())) {
                inRange.add(sat.getSatelliteId());
                if (sat.getType() == "RelaySatellite") {
                    addRecursive(control, inRange, sat.getSatelliteId(), isSenderDevice);
                }
            }
        }
    }

    public static void satelliteToSatellite(BlackoutController control, List<String> inRange, Satellite sat1,
                                            boolean isSenderDevice) {
        for (Satellite sat2 : control.getSatellites().values()) {
            if (sat1 != sat2 && satelliteInRange(sat1, sat2, sat1.getRange())
                && !inRange.contains(sat2.getSatelliteId())) {
                inRange.add(sat2.getSatelliteId());
                if (sat2.getType() == "RelaySatellite") {
                    addRecursive(control, inRange, sat2.getSatelliteId(), isSenderDevice);
                }
            }
        }
    }

    public static void satelliteToDevice(BlackoutController control, List<String> inRange, Satellite sat1) {
        for (Device dev : control.getDevices().values()) {
            if (deviceInRange(dev, sat1, sat1.getRange()) && !inRange.contains(dev.getDeviceId())) {
                inRange.add(dev.getDeviceId());
            }
        }
    }

    public static boolean deviceInRange(Device dev, Satellite sat, int range) {
        double distance = MathsHelper.getDistance(sat.getHeight(), sat.getPosition(), dev.getPosition());
        boolean visible = MathsHelper.isVisible(sat.getHeight(), sat.getPosition(), dev.getPosition());
        return distance <= range && visible;
    }

    public static boolean satelliteInRange(Satellite sat1, Satellite sat2, int range) {
        double distance = MathsHelper.getDistance(sat1.getHeight(), sat1.getPosition(),
                                                  sat2.getHeight(), sat2.getPosition());
        boolean visible = MathsHelper.isVisible(sat1.getHeight(), sat1.getPosition(),
                                                sat2.getHeight(), sat2.getPosition());
        return distance <= range && visible;
    }

    public static void devToSatSend(String filename, Device dev, Satellite sat) throws FileTransferException {
        int bytes = dev.fullFileExists(filename);
        if (bytes == -1) {
            throw new FileTransferException.VirtualFileNotFoundException(filename);
        }
        satReceiveErrors(filename, sat, bytes);
        dev.sendFile(filename, sat.getSatelliteId());
        sat.receiveFile(filename, bytes, dev.getDeviceId());
    }

    public static void satToSatSend(String filename, Satellite sat1, Satellite sat2) throws FileTransferException {
        int bytes = sat1.fullFileExists(filename);
        if (bytes == -1) {
            throw new FileTransferException.VirtualFileNotFoundException(filename);
        } else if (sat1.sendBandFull()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(sat1.getSatelliteId());
        }
        satReceiveErrors(filename, sat2, bytes);
        sat1.sendFile(filename, sat2.getSatelliteId());
        sat2.receiveFile(filename, bytes, sat1.getSatelliteId());
    }

    public static void satToDevSend(String filename, Satellite sat, Device dev) throws FileTransferException {
        int bytes = sat.fullFileExists(filename);
        if (bytes == -1) {
            throw new FileTransferException.VirtualFileNotFoundException(filename);
        } else if (sat.sendBandFull()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(sat.getSatelliteId());
        } else if (dev.partFileExists(filename)) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(filename);
        }
        sat.sendFile(filename, dev.getDeviceId());
        dev.receiveFile(filename, bytes, sat.getSatelliteId());
    }

    public static void satReceiveErrors(String filename, Satellite sat, int bytes) throws FileTransferException {
        if (sat.recBandFull()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(sat.getSatelliteId());
        } else if (sat.partFileExists(filename)) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(filename);
        } else if (sat.fileFull()) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Files Reached");
        } else if (sat.storageFull(bytes)) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Storage Reached");
        }
    }
}
