package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

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
        Angle position;
        double height;
        String type;
        Map<String, File> filesMap;

        Device device = devices.get(id);

        if (device != null) {
            position = device.getPosition();
            height = 69911;
            type = device.getType();
            filesMap = device.getFiles();
        } else {
            Satellite satellite = satellites.get(id);
            position = satellite.getPosition();
            height = satellite.getHeight();
            type = satellite.getType();
            filesMap = satellite.getFiles();
        }

        List<String> filenames = new ArrayList<String>(filesMap.keySet());
        List<File> files = new ArrayList<File>(filesMap.values());
        Map<String, FileInfoResponse> fileResponse = new HashMap<String, FileInfoResponse>();

        for (int i = 0; i < files.size(); i++) {
            fileResponse.put(filenames.get(i), new FileInfoResponse(files.get(i).getFilename(),
                                                                    files.get(i).getContent(),
                                                                    files.get(i).getContent().length(),
                                                                    files.get(i).isHasTransferCompleted()));
        }

        return new EntityInfoResponse(id, position, height, type, fileResponse);
    }

    public void simulate() {
        // TODO: Task 2a)
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
