package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task2ExampleTests {
    @Test
    public void testEntitiesInRange() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(315));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
        controller.createDevice("DeviceD", "HandheldDevice", Angle.fromDegrees(180));
        controller.createSatellite("Satellite3", "StandardSatellite", 2000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC", "Satellite2"), controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "DeviceC", "Satellite1"), controller.communicableEntitiesInRange("Satellite2"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2"), controller.communicableEntitiesInRange("DeviceB"));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceD"), controller.communicableEntitiesInRange("Satellite3"));
    }

    @Test
    public void testSomeExceptionsForSend() {
        // just some of them... you'll have to test the rest
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertThrows(FileTransferException.VirtualFileNotFoundException.class, () -> controller.sendFile("NonExistentFile", "DeviceC", "Satellite1"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        controller.simulate(msg.length() * 2);
        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class, () -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
    }

    @Test
    public void testMovement() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(340), 100 + RADIUS_OF_JUPITER, "StandardSatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(337.95), 100 + RADIUS_OF_JUPITER, "StandardSatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testExample() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate(msg.length() * 2);
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        controller.simulate(msg.length());
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));
    }

    @Test
    public void testRelayMovement() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER,
                                Angle.fromDegrees(180));

        // moves in negative direction
        assertEquals(
                        new EntityInfoResponse("Satellite1", Angle.fromDegrees(180), 100 + RADIUS_OF_JUPITER,
                                        "RelaySatellite"),
                        controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(178.77), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(177.54), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(176.31), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));

        controller.simulate(5);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(170.18), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(24);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        // edge case
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(139.49), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        // coming back
        controller.simulate(1);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(5);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(146.85), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testTeleportingMovement() {
        // Test for expected teleportation movement behaviour
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(0));

        controller.simulate();
        Angle clockwiseOnFirstMovement = controller.getInfo("Satellite1").getPosition();
        controller.simulate();
        Angle clockwiseOnSecondMovement = controller.getInfo("Satellite1").getPosition();
        assertTrue(clockwiseOnSecondMovement.compareTo(clockwiseOnFirstMovement) == 1);

        // It should take 250 simulations to reach theta = 180.
        // Simulate until Satellite1 reaches theta=180
        controller.simulate(250);

        // Verify that Satellite1 is now at theta=0
        assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() % 360 == 0);

        // Verify that the satellite is now going clockwise
        controller.simulate();
        clockwiseOnFirstMovement = controller.getInfo("Satellite1").getPosition();
        controller.simulate();
        clockwiseOnSecondMovement = controller.getInfo("Satellite1").getPosition();
        assertTrue(clockwiseOnSecondMovement.compareTo(clockwiseOnFirstMovement) == -1);
    }

    @Test
    public void testrelayPositive() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER,
                                Angle.fromDegrees(345));

        // checks if satellites initial movement is positive
        Angle initial = Angle.fromDegrees(345);
        controller.simulate();
        Angle next = controller.getInfo("Satellite1").getPosition();
        assertTrue(next.compareTo(initial) == 1);

        // checks if satellite turns back at 190 degrees
        controller.simulate(166);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(190), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));

        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(188.78), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testRelayNegative() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER,
                                Angle.fromDegrees(344));

        // checks if satellites initial movement is positive
        Angle initial = Angle.fromDegrees(344);
        controller.simulate();
        Angle next = controller.getInfo("Satellite1").getPosition();
        assertTrue(next.compareTo(initial) == -1);
    }

    @Test
    public void testDeviceCompatibilityOnly() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(315));
        controller.createSatellite("Satellite3", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(310));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(315));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(310));

        // testing that desktops and standard satellites can't communicate
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite3", "DeviceA", "DeviceB"), controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite3"), controller.communicableEntitiesInRange("DeviceC"));
    }

    @Test
    public void testInRangeRelay() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(150));
        controller.createSatellite("Satellite1", "StandardSatellite", 500 + RADIUS_OF_JUPITER, Angle.fromDegrees(60));
        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("DeviceA"));

        // creating chain of relays to communicate with satellite
        controller.createSatellite("Satellite2", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(125));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2"), controller.communicableEntitiesInRange("DeviceA"));

        controller.createSatellite("Satellite3", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(90));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite3", "Satellite1"), controller.communicableEntitiesInRange("DeviceA"));
    }

    @Test
    public void testRelayDeviceCompatibility() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(150));
        controller.createSatellite("Satellite1", "StandardSatellite", 500 + RADIUS_OF_JUPITER, Angle.fromDegrees(60));

        // creating chain of relays and seeing that the desktop and the standard satellite still won't communicate
        controller.createSatellite("Satellite2", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(125));
        controller.createSatellite("Satellite3", "RelaySatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(90));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite3"), controller.communicableEntitiesInRange("DeviceA"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite3"), controller.communicableEntitiesInRange("Satellite1"));
    }

    @Test
    public void testOutOfRangeMidTransfer() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(150));
        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(130));

        // sending file and simulating til out of range
        String msg = "Hello StandardSatellite";
        controller.addFileToDevice("DeviceA", "File1", msg);
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));

        controller.simulate(10);
        assertFalse(controller.getInfo("Satellite1").getFiles().containsKey("File1"));
    }

    @Test
    public void testDeleteMidTransfer() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(150));
        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(130));

        // sending file and simulating til out of range
        String msg = "Hello StandardSatellite";
        controller.addFileToDevice("DeviceA", "File1", msg);
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));

        controller.simulate(1);
        controller.removeSatellite("Satellite1");
        controller.simulate(10);
    }
}
