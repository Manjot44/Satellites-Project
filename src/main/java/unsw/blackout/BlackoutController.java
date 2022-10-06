package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class BlackoutController {
    private Map<String, Device> devices = new HashMap<String, Device>();
    private Map<String, Satellite> satellites = new HashMap<String, Satellite>();

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
        if (devices.get(id) != null) {
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
        // TODO: Task 2 b)
        return new ArrayList<>();
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        // TODO: Task 2 c)
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
