package tms.congestion;

import java.util.List;

import tms.sensors.Sensor;

/**
 * An implementation of a congestion calculator that calculates the average
 * congestion value from all of its sensors.
 */

public class AveragingCongestionCalculator implements CongestionCalculator {
    /** List of sensors on this route to calculate congestion values. */
    private List<Sensor> sensors;

    /**
     * Creates a new averaging congestion calculator for a given list of
     * sensors on a route.
     * @param sensors list of sensors to use in congestion calculation
     */
    public AveragingCongestionCalculator(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    /**
     * Calculates the average congestion level, as returned by
     * <code>Sensor.getCongestion()</code>, of all the sensors stored by this
     * calculator.
     *
     * <p>If there are no sensors stored, return 0.</p>
     *
     * <p>If the computed average is not an integer, it should be rounded to
     * the nearest integer before being returned.</p>
     * @return the average congestion
     */
    public int calculateCongestion() {
        int totalCongestion = 0;
        for (Sensor sensor : this.sensors) {
            totalCongestion += sensor.getCongestion();
        }
        return (this.sensors.size() == 0) ? 0 :
                Math.round((float) totalCongestion / this.sensors.size());
    }
}
