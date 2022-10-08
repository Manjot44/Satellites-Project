package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;
import unsw.utils.CommsHelper;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class BlackoutController {
    private Map<String, Device> devices = new HashMap<String, Device>();
    private Map<String, Satellite> satellites = new HashMap<String, Satellite>();

    public Map<String, Satellite> getSatellites() {
        return satellites;
    }
    public Map<String, Device> getDevices() {
        return devices;
    }

    public void createDevice(String deviceId, String type, Angle position) {
        if (type == "HandheldDevice") devices.put(deviceId, new HandheldDevice(deviceId, position, type));
        if (type == "LaptopDevice") devices.put(deviceId, new LaptopDevice(deviceId, position, type));
        if (type == "DesktopDevice") devices.put(deviceId, new DesktopDevice(deviceId, position, type));
    }

    public void removeDevice(String deviceId) {
        devices.remove(deviceId);
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        if (type == "StandardSatellite") {
            satellites.put(satelliteId, new StandardSatellite(satelliteId, height, position, type));
        }
        if (type == "TeleportingSatellite") {
            satellites.put(satelliteId, new TeleportingSatellite(satelliteId, height, position, type));
        }
        if (type == "RelaySatellite") {
            satellites.put(satelliteId, new RelaySatellite(satelliteId, height, position, type));
        }
    }

    public void removeSatellite(String satelliteId) {
        satellites.remove(satelliteId);
    }

    public List<String> listDeviceIds() {
        return new ArrayList<String>(devices.keySet());
    }

    public List<String> listSatelliteIds() {
        return new ArrayList<String>(satellites.keySet());
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        Device device = devices.get(deviceId);
        device.addFile(filename, content);
    }

    public EntityInfoResponse getInfo(String id) {
        if (devices.containsKey(id)) {
            Device device = devices.get(id);
            return new EntityInfoResponse(id, device.getPosition(), RADIUS_OF_JUPITER, device.getType(),
                                          device.generateFileResponse());
        } else {
            Satellite satellite = satellites.get(id);
            return new EntityInfoResponse(id, satellite.getPosition(), satellite.getHeight(), satellite.getType(),
                                          satellite.generateFileResponse());
        }
    }

    public void simulate() {
        for (Satellite satellite : satellites.values()) {
            satellite.move();

            if (satellite.getType() == "TeleportingSatellite") {
                TeleportingSatellite teleSat = (TeleportingSatellite) satellite;
                if (teleSat.isTeleported()) {
                    teleSat.teleTransfer(this);
                }
            }
            satellite.transfer(this);
        }
        for (Device device : devices.values()) {
            device.transfer(this);
        }
    }

    /**
     * Simulate for the specified number of minutes.
     * You shouldn't need to modify this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        List<String> inRange = new ArrayList<String>();
        boolean isSenderDevice;
        if (devices.containsKey(id)) {
            isSenderDevice = true;
            CommsHelper.addRecursive(this, inRange, id, isSenderDevice);
            if (devices.get(id).getType() == "DesktopDevice") {
                inRange.removeIf(x -> satellites.get(x) != null && satellites.get(x).getType() == "StandardSatellite");
            }
            return inRange;
        } else {
            isSenderDevice = false;
            CommsHelper.addRecursive(this, inRange, id, isSenderDevice);
            if (satellites.get(id).getType() == "StandardSatellite") {
                inRange.removeIf(x -> (devices.get(x) != null) && (devices.get(x).getType() == "DesktopDevice"));
            }
            return inRange;
        }
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        if (devices.containsKey(fromId)) {
            CommsHelper.devToSatSend(fileName, devices.get(fromId), satellites.get(toId));
        } else if (devices.containsKey(toId)) {
            CommsHelper.satToDevSend(fileName, satellites.get(fromId), devices.get(toId));
        } else {
            CommsHelper.satToSatSend(fileName, satellites.get(fromId), satellites.get(toId));
        }
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }

}
