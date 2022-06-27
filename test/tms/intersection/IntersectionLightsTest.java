package tms.intersection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tms.route.Route;
import tms.route.TrafficSignal;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class IntersectionLightsTest {
    private IntersectionLights intersectionLights;
    private List<Route> connections;
    private List<Route> connectionsOne;
    private IntersectionLights intersectionLightsOne;
    private IntersectionLights intersectionLightsManyYellow;
    private List<Route> connectionsManyYellow;

    @Before
    public void setUp() {
        this.connectionsOne = new ArrayList<>();
        this.connectionsOne.add(new Route("A:B", new Intersection("A"), 55));
        this.connectionsOne.get(0).addTrafficLight();
        this.connectionsOne.get(0).setSignal(TrafficSignal.RED);
        this.intersectionLightsOne = new IntersectionLights(this.connectionsOne
                , 3, 8);

        this.connections = new ArrayList<>();
        this.connections.add(new Route("C:B", new Intersection("C"), 55));
        this.connections.get(0).addTrafficLight();
        this.connections.get(0).setSignal(TrafficSignal.RED);

        this.connections.add(new Route("A:C", new Intersection("A"), 55));
        this.connections.get(1).addTrafficLight();
        this.connections.get(1).setSignal(TrafficSignal.RED);
        this.intersectionLights = new IntersectionLights(this.connections
                , 4, 9);

        this.connectionsManyYellow = new ArrayList<>();

        this.connectionsManyYellow.add(new Route("C:A", new Intersection("C")
                , 55));
        this.connectionsManyYellow.get(0).addTrafficLight();
        this.connectionsManyYellow.get(0).setSignal(TrafficSignal.GREEN);

        this.connectionsManyYellow.add(new Route("A:C", new Intersection("A")
                , 55));
        this.connectionsManyYellow.get(1).addTrafficLight();
        this.connectionsManyYellow.get(1).setSignal(TrafficSignal.RED);

        this.connectionsManyYellow.add(new Route("B:C", new Intersection("B")
                , 55));
        this.connectionsManyYellow.get(2).addTrafficLight();
        this.connectionsManyYellow.get(2).setSignal(TrafficSignal.RED);

        this.intersectionLightsManyYellow =
                new IntersectionLights(this.connectionsManyYellow, 1, 5);
    }

    @Test
    public void firstLightGreenOne() {
        Assert.assertEquals(TrafficSignal.GREEN,
                this.connectionsOne.get(0).getTrafficLight().getSignal());
    }

    @Test
    public void firstLightGreenMany() {
        Assert.assertEquals(TrafficSignal.GREEN,
                this.connections.get(0).getTrafficLight().getSignal());
    }

    @Test
    public void getYellowTimeOne() {
        Assert.assertEquals(3, this.intersectionLightsOne.getYellowTime());
        this.intersectionLightsOne.setDuration(6);
        Assert.assertEquals(3, this.intersectionLightsOne.getYellowTime());
    }

    @Test
    public void getYellowTimeMany() {
        Assert.assertEquals(4, this.intersectionLights.getYellowTime());
        this.intersectionLights.setDuration(35);
        Assert.assertEquals(4, this.intersectionLights.getYellowTime());
    }

    @Test
    public void setDurationOneGreen() {
        Assert.assertEquals(TrafficSignal.GREEN,
                connectionsOne.get(0).getTrafficLight().getSignal());
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        Assert.assertEquals(TrafficSignal.YELLOW,
                connectionsOne.get(0).getTrafficLight().getSignal());
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        Assert.assertEquals(TrafficSignal.GREEN,
                connectionsOne.get(0).getTrafficLight().getSignal());
        this.intersectionLightsOne.setDuration(4);
        Assert.assertEquals(TrafficSignal.GREEN,
                connectionsOne.get(0).getTrafficLight().getSignal());
        this.intersectionLightsOne.oneSecond();
        Assert.assertEquals(TrafficSignal.YELLOW,
                connectionsOne.get(0).getTrafficLight().getSignal());
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        Assert.assertEquals(TrafficSignal.GREEN,
                connectionsOne.get(0).getTrafficLight().getSignal());
    }

    @Test
    public void setDurationOneYellow() {
        Assert.assertEquals(TrafficSignal.GREEN,
                connectionsOne.get(0).getTrafficLight().getSignal());
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        Assert.assertEquals(TrafficSignal.YELLOW,
                connectionsOne.get(0).getTrafficLight().getSignal());
        this.intersectionLightsOne.setDuration(4);
        Assert.assertEquals(TrafficSignal.GREEN,
                connectionsOne.get(0).getTrafficLight().getSignal());
        this.intersectionLightsOne.oneSecond();
        Assert.assertEquals(TrafficSignal.YELLOW,
                connectionsOne.get(0).getTrafficLight().getSignal());
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        this.intersectionLightsOne.oneSecond();
        Assert.assertEquals(TrafficSignal.GREEN,
                connectionsOne.get(0).getTrafficLight().getSignal());
    }

    @Test
    public void setDurationManyGreen() {
        Assert.assertEquals(TrafficSignal.GREEN,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());

        this.intersectionLightsManyYellow.setDuration(3);

        this.intersectionLightsManyYellow.oneSecond();
        Assert.assertEquals(TrafficSignal.GREEN,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());

        this.intersectionLightsManyYellow.oneSecond();
        Assert.assertEquals(TrafficSignal.YELLOW,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());

        this.intersectionLightsManyYellow.oneSecond();
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.GREEN,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());

        this.intersectionLightsManyYellow.oneSecond();
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.GREEN,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());

        this.intersectionLightsManyYellow.oneSecond();
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.YELLOW,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());
    }

    @Test
    public void setDurationManyYellow() {
        Assert.assertEquals(TrafficSignal.GREEN,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());

        this.connectionsManyYellow.get(0).setSignal(TrafficSignal.YELLOW);

        Assert.assertEquals(TrafficSignal.YELLOW,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());

        this.intersectionLightsManyYellow.setDuration(3);

        this.intersectionLightsManyYellow.oneSecond();
        Assert.assertEquals(TrafficSignal.GREEN,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());

        this.intersectionLightsManyYellow.oneSecond();
        Assert.assertEquals(TrafficSignal.YELLOW,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());

        this.intersectionLightsManyYellow.oneSecond();
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.GREEN,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());

        this.intersectionLightsManyYellow.oneSecond();
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.GREEN,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());

        this.intersectionLightsManyYellow.oneSecond();
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(0).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.YELLOW,
                this.connectionsManyYellow.get(1).getTrafficLight().getSignal());
        Assert.assertEquals(TrafficSignal.RED,
                this.connectionsManyYellow.get(2).getTrafficLight().getSignal());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("9:C,A", intersectionLights.toString());
        Assert.assertEquals("8:A", intersectionLightsOne.toString());
        Assert.assertEquals("5:C,A,B", intersectionLightsManyYellow.toString());
    }

    @Test
    public void testToStringAfterSetDuration() {
        intersectionLights.setDuration(15);
        Assert.assertEquals("15:C,A", intersectionLights.toString());

        intersectionLightsOne.setDuration(5);
        Assert.assertEquals("5:A", intersectionLightsOne.toString());

        intersectionLightsManyYellow.setDuration(300);
        Assert.assertEquals("300:C,A,B",
                intersectionLightsManyYellow.toString());
    }
}