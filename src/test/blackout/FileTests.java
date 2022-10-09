package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class FileTests {
    @Test
    public void testReceiveBandwidthExcpetion() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(310));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(300));

        // adding 2 files to device and trying to send both to the satellite at the same time
        String msg1 = "Hey Satellite 1";
        String msg2 = "Can I join?";
        controller.addFileToDevice("DeviceA", "File1", msg1);
        controller.addFileToDevice("DeviceA", "File2", msg2);

        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class, () -> controller.sendFile("File2", "DeviceA", "Satellite1"));

        // testing order of exceptions --> should throw no bandwidth before file already exists exception
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class, () -> controller.sendFile("File1", "DeviceA", "Satellite1"));
    }

    @Test
    public void testSendBandwidthException() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(310));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(300));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(305));

        // adding 2 files to device and sending to the satellite
        String msg1 = "1";
        String msg2 = "2";
        controller.addFileToDevice("DeviceA", "File1", msg1);
        controller.addFileToDevice("DeviceA", "File2", msg2);

        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        controller.simulate();
        assertDoesNotThrow(() -> controller.sendFile("File2", "DeviceA", "Satellite1"));
        controller.simulate();

        // attempt to send both files at the same time to another device
        assertDoesNotThrow(() -> controller.sendFile("File1", "Satellite1", "DeviceB"));
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class, () -> controller.sendFile("File2", "Satellite1", "DeviceB"));

        // testing order of exceptions --> should throw no bandwidth before file already exists exception
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class, () -> controller.sendFile("File2", "Satellite1", "DeviceB"));
    }

    @Test
    public void testFileFullException() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(310));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(300));

        // trying to send 4 files to a standard satellite
        String msg1 = "1";
        String msg2 = "2";
        String msg3 = "3";
        String msg4 = "4";
        controller.addFileToDevice("DeviceA", "File1", msg1);
        controller.addFileToDevice("DeviceA", "File2", msg2);
        controller.addFileToDevice("DeviceA", "File3", msg3);
        controller.addFileToDevice("DeviceA", "File4", msg4);

        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        controller.simulate();
        assertDoesNotThrow(() -> controller.sendFile("File2", "DeviceA", "Satellite1"));
        controller.simulate();
        assertDoesNotThrow(() -> controller.sendFile("File3", "DeviceA", "Satellite1"));
        controller.simulate();
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class, () -> controller.sendFile("File4", "DeviceA", "Satellite1"));
    }

    @Test
    public void testStorageFullException() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(135));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(100));

        // trying to send 2 large files (41 bytes each) to a standard satellite
        String msg = "01234567890123456789012345678901234567890";
        controller.addFileToDevice("DeviceA", "File1", msg);
        controller.addFileToDevice("DeviceA", "File2", msg);

        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        controller.simulate(msg.length());

        controller.simulate(185);
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class, () -> controller.sendFile("File2", "DeviceA", "Satellite1"));
    }

    @Test
    public void testThrottleBandwidth() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "TeleportingSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(135));
        controller.createSatellite("Satellite2", "TeleportingSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(115));
        controller.createSatellite("Satellite3", "StandardSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(75));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(100));

        // send a file to satellite 1
        String msg = "Hello TeleportingSatellite";
        controller.addFileToDevice("DeviceA", "File1", msg);

        // checking mid transfer progress
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        controller.simulate();
        assertEquals(new FileInfoResponse("File1", "Hello Teleporti", msg.length(), false), controller.getInfo("Satellite1").getFiles().get("File1"));
        controller.simulate();

        // seeing if the message gets appropriately throttled when sent from satellite 1 to satellite 2 and 3
        assertDoesNotThrow(() -> controller.sendFile("File1", "Satellite1", "Satellite2"));
        assertDoesNotThrow(() -> controller.sendFile("File1", "Satellite1", "Satellite3"));
        controller.simulate();

        assertEquals(new FileInfoResponse("File1", "Hello", msg.length(), false), controller.getInfo("Satellite2").getFiles().get("File1"));
        assertEquals(new FileInfoResponse("File1", "H", msg.length(), false), controller.getInfo("Satellite3").getFiles().get("File1"));
    }

    @Test
    public void testFromDeviceTeleportTransfer() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "TeleportingSatellite", 15000 + RADIUS_OF_JUPITER, Angle.fromDegrees(179));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(175));

        // starting transfer from device to teleporting satellite (50 bytes)
        String msg1 = "lottolottolottolottolottolottolottolottolottolotto";
        controller.addFileToDevice("DeviceA", "File1", msg1);
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));

        // checking if file removed from satellite after teleport and t's are removed from the device
        String msg2 = "looloolooloolooloolooloolooloo";
        controller.simulate(2);

        assertFalse(controller.getInfo("Satellite1").getFiles().containsKey("File1"));
        assertEquals(new FileInfoResponse("File1", msg2, msg2.length(), true), controller.getInfo("DeviceA").getFiles().get("File1"));
    }

    @Test
    public void testToDeviceTeleportTransfer() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "TeleportingSatellite", 15000 + RADIUS_OF_JUPITER, Angle.fromDegrees(176));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(155));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(185));

        // send message from device A to teleporting satellite
        String msg1 = "lottolottolottolottolottolottolottolottolottolotto";
        controller.addFileToDevice("DeviceA", "File1", msg1);
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        controller.simulate(4);

        // send message from teleporting satellite to device B and check if remaining t's are removed after teleport
        assertDoesNotThrow(() -> controller.sendFile("File1", "Satellite1", "DeviceB"));

        String msg2 = "lottolottolooloolooloolooloolooloo";
        controller.simulate(2);
        assertEquals(new FileInfoResponse("File1", msg2, msg2.length(), true), controller.getInfo("DeviceB").getFiles().get("File1"));
    }

    @Test
    public void testDoubleTeleportTransfer() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "TeleportingSatellite", 15000 + RADIUS_OF_JUPITER, Angle.fromDegrees(176));
        controller.createSatellite("Satellite2", "TeleportingSatellite", 15000 + RADIUS_OF_JUPITER, Angle.fromDegrees(176));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(155));

        // adding 2 files to the device and sending it one each to the satellites
        String msg1 = "lottolottolottolottolottolottolottolottolottolotto";
        String msg2 = "lotlotlotlotlotlotlotlotlotlotlotlotlotlotlotlotlot";
        controller.addFileToDevice("DeviceA", "File1", msg1);
        controller.addFileToDevice("DeviceA", "File2", msg2);

        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        assertDoesNotThrow(() -> controller.sendFile("File2", "DeviceA", "Satellite2"));
        controller.simulate(4);

        // now sending files from satellite to satellite at the same time then seeing the result after teleport
        assertDoesNotThrow(() -> controller.sendFile("File1", "Satellite1", "Satellite2"));
        assertDoesNotThrow(() -> controller.sendFile("File2", "Satellite2", "Satellite1"));

        String msg3 = "lottolottolooloolooloolooloolooloo";
        String msg4 = "lotlotlotlolololololololololololololo";
        controller.simulate(2);
        assertEquals(new FileInfoResponse("File2", msg4, msg4.length(), true), controller.getInfo("Satellite1").getFiles().get("File2"));
        assertEquals(new FileInfoResponse("File1", msg3, msg3.length(), true), controller.getInfo("Satellite2").getFiles().get("File1"));
    }
}
