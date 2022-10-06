package unsw.utils;

import java.util.HashSet;
import java.util.Set;

import unsw.blackout.BlackoutController;
import unsw.blackout.Device;
import unsw.blackout.Satellite;

public class CommsHelper {
    public static Set<String> addRecursive(BlackoutController control, String id, boolean isSenderDevice) {
        Set<String> inRange = new HashSet<String>();

        if (control.getDevices().containsKey(id)) {
            deviceToSatellite(control, inRange, control.getDevices().get(id), isSenderDevice);
        } else {
            satelliteToSatellite(control, inRange, control.getSatellites().get(id), isSenderDevice);
            if (!isSenderDevice) {
                satelliteToDevice(control, inRange, control.getSatellites().get(id));
            }
        }

        return inRange;
    }

    public static void deviceToSatellite(BlackoutController control, Set<String> inRange, Device dev,
                                         boolean isSenderDevice) {
        for (Satellite sat : control.getSatellites().values()) {
            if (deviceInRange(dev, sat, dev.getRange())) {
                inRange.add(sat.getSatelliteId());
                if (sat.getType() == "RelaySatellite") {
                    inRange.addAll(addRecursive(control, sat.getSatelliteId(), isSenderDevice));
                }
            }
        }
    }

    public static void satelliteToSatellite(BlackoutController control, Set<String> inRange, Satellite sat1,
                                            boolean isSenderDevice) {
        for (Satellite sat2 : control.getSatellites().values()) {
            if (sat1 != sat2 && satelliteInRange(sat1, sat2, sat1.getRange())) {
                inRange.add(sat2.getSatelliteId());
                if (sat2.getType() == "RelaySatellite") {
                    inRange.addAll(addRecursive(control, sat2.getSatelliteId(), isSenderDevice));
                }
            }
        }
    }

    public static void satelliteToDevice(BlackoutController control, Set<String> inRange, Satellite sat1) {
        for (Device dev : control.getDevices().values()) {
            if (deviceInRange(dev, sat1, sat1.getRange())) {
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
}
