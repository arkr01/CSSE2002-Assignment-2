package tms.congestion;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tms.sensors.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AveragingCongestionCalculatorTest {
    private AveragingCongestionCalculator averagingCongestionCalculator;

    private AveragingCongestionCalculator emptyCalc;

    private AveragingCongestionCalculator oneCalc;

    private List<Sensor> sensors;

    private List<Sensor> single;

    private DemoVehicleCount demoVehicleCount;
    private DemoSpeedCamera demoSpeedCamera;
    private DemoPressurePad demoPressurePad;

    @Before
    public void setUp() {
        this.sensors = new ArrayList<>();

        int[] data = new int[] {60, 70, 60, 30, 15, 0};
        int threshold = 60;
        this.demoSpeedCamera = new DemoSpeedCamera(data, threshold);
        sensors.add(this.demoSpeedCamera);

        data = new int[] {0, 1, 2, 3, 4, 5, 6};
        threshold = 4;
        this.demoPressurePad = new DemoPressurePad(data, threshold);
        sensors.add(this.demoPressurePad);

        data = new int[] {15, 8, 4, 1, 0, 9, 20};
        threshold = 10;
        this.demoVehicleCount = new DemoVehicleCount(data, threshold);
        sensors.add(this.demoVehicleCount);

        this.averagingCongestionCalculator =
                new AveragingCongestionCalculator(sensors);

        this.emptyCalc = new AveragingCongestionCalculator(new ArrayList<>());

        this.single = new ArrayList<>();
        single.add(this.demoVehicleCount);
        this.oneCalc =
                new AveragingCongestionCalculator(single);
    }

    @Test
    public void calculateCongestionEmpty() {
        Assert.assertEquals(0, this.emptyCalc.calculateCongestion());
    }

    @Test
    public void calculateCongestionOne() {
        Assert.assertEquals(0, this.oneCalc.calculateCongestion());

        this.demoVehicleCount.oneSecond();
        Assert.assertEquals(20,
                this.oneCalc.calculateCongestion());
    }

    @Test
    public void calculateCongestion() {
        Assert.assertEquals(0,
                this.averagingCongestionCalculator.calculateCongestion());

        this.demoPressurePad.oneSecond();
        this.demoSpeedCamera.oneSecond();
        this.demoVehicleCount.oneSecond();

        Assert.assertEquals(15,
                this.averagingCongestionCalculator.calculateCongestion());

        this.demoSpeedCamera.oneSecond();
        this.demoSpeedCamera.oneSecond();
        Assert.assertEquals(32,
                this.averagingCongestionCalculator.calculateCongestion());
    }
}