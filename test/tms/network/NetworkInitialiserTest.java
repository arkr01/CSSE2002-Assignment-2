package tms.network;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tms.intersection.Intersection;
import tms.route.Route;
import tms.util.InvalidNetworkException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class NetworkInitialiserTest {
    private Network testDemo;
    private Network testDemoToString;

    @Before
    public void setUp() throws Exception {
        this.testDemo =
                NetworkInitialiser.loadNetwork("networks" + File.separator +
                "demo.txt");
        this.testDemoToString =
                NetworkInitialiser.loadNetwork("networks" + File.separator +
                        "toStringDemo.txt");
    }

    @Test
    public void loadNetworkNumIntersections() {
        Assert.assertEquals(4, this.testDemo.getIntersections().size());
    }
    
    @Test
    public void loadNetworkNumRoutes() {
        int numRoutes = 0;
        for (Intersection intersection : this.testDemo.getIntersections()) {
            numRoutes += intersection.getConnections().size();
        }
        Assert.assertEquals(5, numRoutes);
    }

    @Test
    public void loadNetworkYellowTime() {
        Assert.assertEquals(1, this.testDemo.getYellowTime());
    }

    @Test
    public void loadNetworkIntersections() {
        String[] intersectionLines = this.testDemo.getIntersections().stream()
                .map(Object::toString).sorted().toArray(String[]::new);
        Assert.assertEquals("W", intersectionLines[0]);
        Assert.assertEquals("X", intersectionLines[1]);
        Assert.assertEquals("Y:3:Z,X", intersectionLines[2]);
        Assert.assertEquals("Z", intersectionLines[3]);
    }

    @Test
    public void loadNetworkRoutesAndSensors() {
        List<Route> routes = new ArrayList<>();
        for (Intersection intersection : this.testDemo.getIntersections()) {
            routes.addAll(intersection.getConnections());
        }
        String[] routeLines = routes.stream()
                .map(Object::toString).sorted().toArray(String[]::new);

        Assert.assertEquals("X:Y:60:0", routeLines[0]);
        Assert.assertEquals("Y:X:60:1" + System.lineSeparator() +
                "PP:5:5,2,4,4,1,5,2,7,3,5,6,5,8,5,4,2,3,3,2,5", routeLines[1]);
        Assert.assertEquals("Y:Z:100:2" + System.lineSeparator() +
                "PP:8:1,3,2,1,1,3,4,7,4,7,9,7,8,4,8,8,5,3,2,2" + System.lineSeparator() +
                "VC:50:42,40,37,34,35,31,36,41,41,47,48,50,53,48,54,58,52,52,61,55", routeLines[2]);
        Assert.assertEquals("Z:X:40:1" + System.lineSeparator() +
                "SC:40:39,40,40,40,36,32,25,28,31,39,40,40,40,40,40,40,36,35,39,40", routeLines[3]);
        Assert.assertEquals("Z:Y:100:0:80", routeLines[4]);
    }

    @Test
    public void loadNetworkToStringDemo() {

        String test = 4 +
                System.lineSeparator() +
                5 +
                System.lineSeparator() +
                1 +
                System.lineSeparator() +
                "W" +
                System.lineSeparator() +
                "X" +
                System.lineSeparator() +
                "Y:3:Z,X" +
                System.lineSeparator() +
                "Z" +
                System.lineSeparator() +
                "X:Y:60:0" +
                System.lineSeparator() +
                "Y:X:60:1" +
                System.lineSeparator() +
                "PP:5:5,2,4,4,1,5,2,7,3,5,6,5,8,5,4,2,3,3,2,5" +
                System.lineSeparator() +
                "Y:Z:100:2" +
                System.lineSeparator() +
                "PP:8:1,3,2,1,1,3,4,7,4,7,9,7,8,4,8,8,5,3,2,2" +
                System.lineSeparator() +
                "VC:50:42,40,37,34,35,31,36,41,41,47,48,50,53,48,54,58,52,52,61,55" +
                System.lineSeparator() +
                "Z:X:40:1" +
                System.lineSeparator() +
                "SC:40:39,40,40,40,36,32,25,28,31,39,40,40,40,40,40,40,36,35,39,40" +
                System.lineSeparator() +
                "Z:Y:100:0:80";
        Assert.assertEquals(test, this.testDemoToString.toString());
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkLittleIntersectionsTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "littleIntersections.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkManyIntersectionsTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "manyIntersections.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkLittleRoutesTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "littleRoutes.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkManyRoutesTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "manyRoutes.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkLittleSensorsTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "littleSensors.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkLittleSensorsEmptyTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "littleSensorsEmpty.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkManySensorsTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "manySensors.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkIntersectionNotExistTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "intersectionFake.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkIntersectionInvalidTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "intersectionInvalid.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkIntersectionDuplicateTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "intersectionDuplicate.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkIntersectionDuplicatesTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "intersectionDuplicates.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkIntersectionDuplicatesLightsTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "intersectionDuplicatesLights.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkRouteDuplicateTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "routeDuplicate.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkRouteDuplicatesTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "routeDuplicates.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test
    public void loadNetworkRouteItselfTest() {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "routeItself.txt");
        } catch (InvalidNetworkException ine) {
            Assert.fail();
        } catch (IOException ine) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkInvalidSensorTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "invalidSensor.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkDuplicateSensorTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "duplicateSensor.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkYellowTimeSmallTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "yellowTimeSmall.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkYellowTimeNegativeTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "yellowTimeNegative.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkDurationYellowTimeTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "durationYellowTime.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkPermutationTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "permutation.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkLightsEmptyTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "lightsEmpty.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkNegativeSpeedTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "negativeSpeed.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkNegativeSpeedSignTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "negativeSpeedSign.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkNegativeThresholdTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "negativeThreshold.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkNegativeDataTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "negativeData.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkColonsTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "colons.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkLackOfColonsTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "lackOfColons.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkParseTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "parse.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkEmptyTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "empty.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkExtraLinesTest() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "extraLines.txt");
        } catch (IOException ioe) {
            // squash
        }
    }

    @Test (expected = InvalidNetworkException.class)
    public void loadNetworkEmptyFile() throws InvalidNetworkException {
        try {
            Network t =
                    NetworkInitialiser.loadNetwork("networks" + File.separator +
                            "emptyFile.txt");
        } catch (IOException ioe) {
            // squash
        }
    }
}