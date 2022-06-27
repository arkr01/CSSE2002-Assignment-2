package tms.intersection;

import tms.network.NetworkInitialiser;
import tms.route.Route;
import tms.route.TrafficSignal;
import tms.util.TimedItem;

import java.util.List;

/**
 * Represents a set of traffic lights at an intersection.
 *
 * <p>For simplicity, traffic lights only allow one incoming route to be
 * green at any given time, with incoming traffic allowed to exit via any
 * outbound route.</p>
 */

public class IntersectionLights implements TimedItem {
    /** (Non-empty) list of incoming routes */
    private List<Route> connections;

    /** (Non-zero) duration of time in which traffic lights appear yellow */
    private int yellowTime;

    /** (Non-zero) duration of time where traffic lights appear yellow and
     * green */
    private int duration;

    /** Tracks how much time has passed (in seconds) */
    private int secondsPassed;

    /**
     * Creates a new set of traffic lights at an intersection.
     *
     * <p>The first route in the given list of incoming routes should have
     * its TrafficLight signal set to <code>TrafficSignal.GREEN</code>.</p>
     * @param connections a list of incoming routes, the list cannot be empty
     * @param yellowTime time in seconds for which lights will appear yellow
     * @param duration time in seconds for which lights will appear yellow
     *                 and green
     * @requires connections.size() > 0 && yellowTime ≥ 1 && duration >
     * yellowTime
     */
    public IntersectionLights(List<Route> connections, int yellowTime,
            int duration) {
        this.connections = connections;

        // Set the traffic signal of the first route in the given list to be
        // green
        this.connections.get(0).setSignal(TrafficSignal.GREEN);

        this.yellowTime = yellowTime;
        this.duration = duration;

        // No time has passed initially
        this.secondsPassed = 0;
    }

    /**
     * Returns the time in seconds for which a traffic light will appear
     * yellow when transitioning from green to red.
     * @return yellow time in seconds for this set of traffic lights
     */
    public int getYellowTime() {
        return this.yellowTime;
    }

    /**
     * Sets a new duration of each green-yellow cycle.
     *
     * <p>The current progress of the lights cycle should be reset, such that
     * on the next call to <code>oneSecond()</code>, only one second of the new
     * duration has been elapsed for the incoming route that currently has a
     * green light.</p>
     * @param duration the new light signal duration
     *
     * @requires duration > getYellowTime()
     */
    public void setDuration(int duration) {
        this.duration = duration;
        for (Route route : this.connections) {
            // Reset the traffic light that is not red
            if (route.getTrafficLight().getSignal() != TrafficSignal.RED) {
                route.setSignal(TrafficSignal.GREEN);
                this.secondsPassed = 0;
            }
        }
    }

    /**
     * Simulates one second passing and updates the state of this set of
     * traffic lights.
     *
     * <p>If enough time has passed such that a full green-yellow duration
     * has elapsed, or such that the current green light should now be
     * yellow, the appropriate light signals should be changed:</p>
     *
     * <ul>
     *     <li>When a traffic light signal has been green for 'duration -
     *     yellowTime' seconds, it should be changed from green to yellow.</li>
     *
     *     <li>When a traffic light signal has been yellow for 'yellowTime'
     *     seconds, it should be changed from yellow to red, and the next
     *     incoming route in the order passed to <code>IntersectionLights(List,
     *     int, int)</code> should be given a green light. If the end of the
     *     list of routes has been reached, simply wrap around to the start
     *     of the list and repeat.</li>
     * </ul>
     *
     * <p>If no routes are connected to the intersection, the duration shall
     * not elapse and the call should simply return without changing anything.
     * </p>
     */
    public void oneSecond() {
        // Ensure the duration shall not elapse if no routes are connected
        if (this.connections.size() != 0) {
            this.secondsPassed++;
        }
        for (Route route : this.connections) {
            TrafficSignal routesSignal = route.getTrafficLight().getSignal();

            if (routesSignal == TrafficSignal.GREEN && this.secondsPassed ==
                    this.duration - this.getYellowTime()) {
                route.setSignal(TrafficSignal.YELLOW);

                // Must reset seconds passed to ensure tracking of yellowTime
                // is accurate
                this.secondsPassed = 0;
            } else if (routesSignal == TrafficSignal.YELLOW &&
                    this.secondsPassed == this.getYellowTime()) {
                route.setSignal(TrafficSignal.RED);

                // Set next route to green light, get the next route's index
                // (mod number of connections). Use modulo to ensure that if
                // the end of connections has been reached, that we simply
                // wrap around to the start of the list
                int nextRoute = (this.connections.indexOf(route) + 1) %
                        this.connections.size();
                this.connections.get(nextRoute).setSignal(TrafficSignal.GREEN);

                // Must reset to ensure tracking of new signal duration is
                // accurate
                this.secondsPassed = 0;
            }
        }
    }

    /**
     * Returns the string representation of this set of IntersectionLights.
     *
     * <p>The format to return is "duration:list,of,intersection,ids" where
     * 'duration' is our current duration and 'list,of,intersection,ids' is a
     * comma-separated list of the IDs of all intersections that have an
     * incoming route to this set of traffic lights, in order given to
     * IntersectionLights' constructor. </p>
     *
     * <p>For example, for a set of traffic lights with inbound routes from
     * three intersections - A, C and B - in that order, and a duration of 8
     * seconds, return the string "8:A,C,B".</p>
     * @return formatted string representation
     */
    @Override
    public String toString() {
        StringBuilder intersectionLightDetails = new StringBuilder();
        intersectionLightDetails.append(this.duration);
        intersectionLightDetails.append(NetworkInitialiser.LINE_INFO_SEPARATOR);
        for (Route route : this.connections) {
            intersectionLightDetails.append(route.getFrom().getId());

            // Ensure not to add a comma after the last intersection ID
            if (this.connections.indexOf(route) !=
                    this.connections.size() - 1) {
                intersectionLightDetails.append(",");
            }
        }
        return intersectionLightDetails.toString();
    }
}
