package tms.network;

import tms.intersection.Intersection;
import tms.route.Route;
import tms.sensors.Sensor;
import tms.util.DuplicateSensorException;
import tms.util.IntersectionNotFoundException;
import tms.util.InvalidOrderException;
import tms.util.RouteNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a network of intersections connected by routes.
 *
 * <p>
 *     Networks need to keep track of the intersections that form the network.
 * </p>
 */

public class Network {
    /** List of intersections in network. */
    private List<Intersection> intersections;

    /** Network-wide yellow time. */
    private int yellowTime;

    /**
     * Creates a new empty network with no intersections.
     */
    public Network() {
        this.intersections = new ArrayList<>();
    }

    /**
     * Returns the yellow time for all traffic lights in this network.
     * @return traffic light yellow time (in seconds)
     */
    public int getYellowTime() {
        return this.yellowTime;
    }

    /**
     * Sets the time that lights appear yellow between turning from green to
     * red (in seconds) for all new traffic lights added to this network.
     *
     * <p>Existing traffic lights should not have their yellow time changed
     * after this method is called. </p>
     *
     * <p>
     *     The yellow time must be at least one (1) second. If the argument
     *     provided is below 1, throw an exception and do not set the yellow
     *     time.
     * </p>
     *
     * @param yellowTime new yellow time for all new traffic lights in network
     *
     * @throws IllegalArgumentException if yellowTime < 1
     */
    public void setYellowTime(int yellowTime) {
        if (yellowTime < 1) {
            throw new IllegalArgumentException("Yellow time must be at least " +
                    "one second.");
        }
        this.yellowTime = yellowTime;
    }

    /**
     * Creates a new intersection with the given ID and adds it to this network.
     *
     * @param id identifier of the intersection to be created
     * @throws IllegalArgumentException if an intersection already exists
     * with the given ID, or if the given ID contains the colon character (:)
     * , or if the id contains only whitespace (space, newline, tab, etc.)
     * characters
     * @requires id != null
     */
    public void createIntersection(String id) throws IllegalArgumentException {
        Intersection toAdd = new Intersection(id);
        if (this.getIntersections().contains(toAdd)) {
            throw new IllegalArgumentException("ID already exists.");
        }
        if (id.contains(":")) {
            throw new IllegalArgumentException("ID contains invalid character" +
                    " :");
        }
        if (id.isBlank()) {
            throw new IllegalArgumentException("ID only contains whitespace.");
        }
        this.intersections.add(toAdd);
    }

    /**
     * Creates a connecting route between the two intersections with the given
     * IDs.
     *
     * <p>The new route should start at 'from' and end at 'to', and have a
     * default speed of 'defaultSpeed'.</p>
     * @param from ID of origin intersection
     * @param to ID of destination intersection
     * @param defaultSpeed speed limit of the route to create
     * @throws IntersectionNotFoundException if no intersection exists with
     * an ID of 'from' or 'to'
     * @throws IllegalStateException if a route already exists between the
     * given two intersections
     * @throws IllegalArgumentException if defaultSpeed is negative
     */
    public void connectIntersections(String from, String to, int defaultSpeed)
            throws IntersectionNotFoundException, IllegalStateException,
            IllegalArgumentException {

        Intersection destination = this.findIntersection(to);
        Intersection origin = this.findIntersection(from);

        List<Intersection> destinationConnectedIntersections =
                destination.getConnectedIntersections();

        if (destinationConnectedIntersections.contains(origin)) {
            throw new IllegalStateException("Route already exists between " +
                    "these intersections.");
        }
        if (defaultSpeed < 0) {
            throw new IllegalArgumentException("Default speed cannot be " +
                    "negative.");
        }
        destination.addConnection(origin, defaultSpeed);
    }

    /**
     * Adds traffic lights to the intersection with the given ID.
     *
     * <p>The traffic lights will change every <code>duration</code> seconds
     * and will cycle in the order given by <code>intersectionOrder</code>,
     * whereby each element in the list represents the intersection from
     * which each incoming route originates. The yellow time will be the
     * network's yellow time value.</p>
     *
     * <p>If the intersection already has traffic lights, the existing lights
     * should be completely overwritten and reset, and the new duration and
     * order should be set.</p>
     *
     * @param intersectionId ID of intersection to add traffic lights to
     * @param duration number of seconds between traffic light cycles
     * @param intersectionOrder list of origin intersection IDs, traffic
     *                          lights will go green in this order
     * @throws IntersectionNotFoundException if no intersection with the
     * given ID exists
     * @throws InvalidOrderException if the order specified is not a
     * permutation of the intersection's incoming routes; or if order is empty
     * @throws IllegalArgumentException if the given duration is less than
     * the network's yellow time plus one
     *
     * @see Intersection#addTrafficLights(List, int, int)
     */
    public void addLights(String intersectionId, int duration,
            List<String> intersectionOrder) throws
            IntersectionNotFoundException, InvalidOrderException,
            IllegalArgumentException {
        Intersection toAddLightsTo = this.findIntersection(intersectionId);
        
        List<Route> toAddLightsToConnections = toAddLightsTo.getConnections();
        List<Route> greenLightOrder = new ArrayList<>();

        // Extract routes from intersectionOrder and store in greenLightOrder
        for (String id : intersectionOrder) {
            try {
                greenLightOrder.add(this.getConnection(id, intersectionId));
            } catch (RouteNotFoundException rnfe) {
                rnfe.getMessage();
            }
        }
        boolean isAPermutation =
                toAddLightsToConnections.containsAll(greenLightOrder) &&
                greenLightOrder.containsAll(toAddLightsToConnections);

        if (greenLightOrder.size() == 0 || !isAPermutation) {
            throw new InvalidOrderException("Either the order specified is " +
                    "not a permutation of the intersection's incoming routes," +
                    " or the order is empty.");
        }

        if (duration < this.getYellowTime() + 1) {
            throw new IllegalArgumentException("Invalid duration given.");
        }
        toAddLightsTo.addTrafficLights(greenLightOrder,
                this.getYellowTime(), duration);
    }

    /**
     * Adds an electronic speed sign on the route between the two given
     * intersections.
     *
     * <p>The new speed sign should have an initial displayed speed of
     * 'initialSpeed'.</p>
     * @param from ID of origin intersection
     * @param to ID of destination intersection
     * @param initialSpeed initial speed to be displayed on speed sign
     * @throws IntersectionNotFoundException if no intersection exists with
     * an ID given by 'from' or 'to'
     * @throws RouteNotFoundException if no route exists between the two
     * given intersections
     * @throws IllegalArgumentException if the given speed is negative
     */
    public void addSpeedSign(String from, String to, int initialSpeed) throws
            IntersectionNotFoundException, RouteNotFoundException {
        if (initialSpeed < 0) {
            throw new IllegalArgumentException("Given speed is negative.");
        }
        this.getConnection(from, to).addSpeedSign(initialSpeed);
    }

    /**
     * Sets the speed limit on the route between the two given intersections.
     *
     * <p>
     *     Speed limits can only be changed on routes with an electronic speed
     *     sign. Calling this method on a route without an electronic speed
     *     sign should result in an exception.
     * </p>
     * @param from ID of origin intersection
     * @param to ID of destination intersection
     * @param newLimit new speed limit
     * @throws IntersectionNotFoundException if no intersection exists with
     * an ID given by 'from' or 'to'
     * @throws RouteNotFoundException if no route exists between the two
     * given intersections
     * @throws IllegalStateException if the specified route does not have an
     * electronic speed sign
     * @throws IllegalArgumentException if the given speed limit is negative
     */
    public void setSpeedLimit(String from, String to, int newLimit) throws
            IntersectionNotFoundException, RouteNotFoundException {
        Route connection = this.getConnection(from, to);

        if (!connection.hasSpeedSign()) {
            throw new IllegalStateException("Route does not have electronic " +
                    "speed sign.");
        }
        if (newLimit < 0) {
            throw new IllegalArgumentException("Negative speed given.");
        }
        connection.setSpeedLimit(newLimit);
    }

    /**
     * Sets the duration of each green-yellow cycle for the given
     * intersection's traffic lights.
     * @param intersectionId ID of target intersection
     * @param duration new duration of traffic lights
     * @throws IntersectionNotFoundException if no intersection exists with
     * an ID given by 'intersectionId'
     * @throws IllegalStateException if the given intersection has no traffic
     * lights
     * @throws IllegalArgumentException if the given duration is less than
     * the network's yellow time plus one
     *
     * @see Intersection#setLightDuration(int)
     */
    public void changeLightDuration(String intersectionId, int duration) throws
            IntersectionNotFoundException {
        Intersection toAddLightsTo = this.findIntersection(intersectionId);

        if (!toAddLightsTo.hasTrafficLights()) {
            throw new IllegalStateException("This intersection has no traffic" +
                    " lights.");
        }

        if (duration < this.getYellowTime() + 1) {
            throw new IllegalArgumentException("Invalid duration given. Must " +
                    "be at least 1 second greater than network yellow time.");
        }

        toAddLightsTo.setLightDuration(duration);
    }

    /**
     * Returns the route that connects the two given intersections.
     * @param from ID of origin intersection
     * @param to ID of destination intersection
     * @return Route that connects these intersections
     * @throws IntersectionNotFoundException if no intersection exists with
     * an ID given by 'to' or 'from'
     * @throws RouteNotFoundException if no route exists between the two
     * given intersections
     */
    public Route getConnection(String from, String to) throws
            IntersectionNotFoundException, RouteNotFoundException {
        Intersection origin = this.findIntersection(from);
        Intersection destination = this.findIntersection(to);

        for (Route connection : destination.getConnections()) {
            if (connection.getFrom().equals(origin)) {
                return connection;
            }
        }
        throw new RouteNotFoundException("No route exists from the " +
                "intersection with ID 'from' to the intersection with ID 'to'");
    }

    /**
     * Adds a sensor to the route between the two intersections with the
     * given IDs.
     * @param from ID of intersection at which the route originates
     * @param to ID of intersection at which the route ends
     * @param sensor sensor instance to add to the route
     * @throws DuplicateSensorException if a sensor already exists on the route
     * with the same type
     * @throws IntersectionNotFoundException if no intersection exists with
     * an ID given by 'from' or 'to'
     * @throws RouteNotFoundException if no route exists between the given
     * to/from intersections
     */
    public void addSensor(String from, String to, Sensor sensor) throws
            DuplicateSensorException, IntersectionNotFoundException,
            RouteNotFoundException {
        Route connection = this.getConnection(from, to);

        for (Sensor connectionSensor : connection.getSensors()) {
            if (connectionSensor.getClass() == sensor.getClass()) {
                throw new DuplicateSensorException("Sensor of same type " +
                        "already exists on route.");
            }
        }
        connection.addSensor(sensor);
    }

    /**
     * Returns the congestion level on the route between the two given
     * intersections.
     * @param from ID of origin intersection
     * @param to ID of destination intersection
     * @return congestion level (integer between 0 and 100) of connecting route
     * @throws IntersectionNotFoundException if no intersection exists with
     * an ID given by 'from' or 'to'
     * @throws RouteNotFoundException if no connecting route exists between
     * the given two intersections
     *
     * @see Route#getCongestion()
     */
    public int getCongestion(String from, String to) throws
            IntersectionNotFoundException, RouteNotFoundException {
        Route connection = this.getConnection(from, to);
        return connection.getCongestion();
    }

    /**
     * Attempts to find an Intersection instance in this network with the
     * same identifier as the given 'id' string.
     * @param id intersection identifier to search for
     * @return the intersection that was found (if one was found)
     * @throws IntersectionNotFoundException if no intersection could be
     * found with the given identifier
     */
    public Intersection findIntersection(String id) throws
            IntersectionNotFoundException {
        Intersection toFind = new Intersection(id);
        for (Intersection intersection : this.getIntersections()) {
            if (intersection.equals(toFind)) {
                return intersection;
            }
        }
        throw new IntersectionNotFoundException("No such intersection exists " +
                "in this network.");
    }

    /**
     * Creates a new connecting route in the opposite direction to an
     * existing route.
     *
     * <p>The newly created route should start at the intersection with the
     * ID given by 'to' and end at the intersection with the ID given
     * by 'from'. It should have the same default speed limit as the current
     * speed limit of the existing route, as returned by <code>Route.getSpeed
     * ()</code>.
     * </p>
     *
     * <p>If the existing route has an electronic speed sign, then a new
     * electronic speed sign should be added to the new route with the same
     * displayed speed as the existing speed sign.</p>
     * @param from ID of intersection that the existing route starts at
     * @param to ID of intersection that the existing route ends at
     * @throws IntersectionNotFoundException if no intersection exists with
     * the ID given by 'from' or 'to'
     * @throws RouteNotFoundException if no route currently exists between
     * given two intersections
     * @throws IllegalStateException if a route already exists in the
     * opposite direction to the existing route
     */
    public void makeTwoWay(String from, String to) throws
            IntersectionNotFoundException, RouteNotFoundException {
        Route oneWay = this.getConnection(from, to);

        Intersection destination = this.findIntersection(to);
        Intersection origin = this.findIntersection(from);

        List<Intersection> originConnectedIntersections =
                origin.getConnectedIntersections();

        if (originConnectedIntersections.contains(destination)) {
            throw new IllegalStateException("Route already exists in other " +
                    "direction.");
        }

        origin.addConnection(destination, oneWay.getSpeed());
        Route otherWay = this.getConnection(to, from);

        if (oneWay.hasSpeedSign()) {
            otherWay.addSpeedSign(oneWay.getSpeed());
        }
    }
    /**
     * Returns true if and only if this network is equal to the other given
     * network.
     *
     * <p>For two networks to be equal, they must have the same number of
     * intersections, and all intersections in the first network must be
     * contained in the second network, and vice versa.</p>
     *
     * @param obj other object to compare equality
     * @return true if equal, false otherwise.
     */
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == this.getClass()) {
            Network objNetwork = (Network) obj;
            if (objNetwork.getIntersections().size() ==
                    this.getIntersections().size()) {
                List<Intersection> objIntersections =
                        objNetwork.getIntersections();
                return objIntersections.containsAll(this.getIntersections()) &&
                        this.getIntersections().containsAll(objIntersections);
            }
        }
        return false;
    }

    /**
     * Returns the hash code of this network.
     *
     * <p>Two networks that are equal must have the same hash code.</p>
     * @return hash code of the network
     */
    public int hashCode() {
        return Objects.hash(this.getIntersections());
    }

    /**
     * Returns the string representation of this network.
     *
     * <p>The format of the string to return is identical to that described
     * in <code>NetworkInitialiser.loadNetwork(String)</code>. All
     * intersections in the network, including all connecting routes with
     * their respective sensors, should be included in the returned string.</p>
     *
     * <p>Intersections and routes should be listed in alphabetical order,
     * similar to the way in which sensors are sorted alphabetically in
     * <code>Route.toString()</code>.</p>
     *
     * <p>Comments (lines beginning with a semicolon character ";") are not
     * added to the string representation of a network.</p>
     *
     * <p>See the example network save file (demo.txt) for an example of the
     * string representation of a network.</p>
     * @return string representation of this network
     *
     * @see NetworkInitialiser#loadNetwork(String)
     */
    @Override
    public String toString() {
        StringBuilder networkInformation = new StringBuilder();
        networkInformation.append(this.getIntersections().size());
        networkInformation.append(System.lineSeparator());

        // Get all routes in network
        List<Route> networkRoutes = this.getRoutes();
        networkInformation.append(networkRoutes.size());
        networkInformation.append(System.lineSeparator());

        networkInformation.append(this.getYellowTime());

        // Get Intersection.toString() representations of all intersections
        // into a String[] array, and sort in alphabetical order
        String[] intersectionLines = this.getIntersections().stream()
                .map(Object::toString).sorted().toArray(String[]::new);

        for (String intersectionLine : intersectionLines) {
            networkInformation.append(System.lineSeparator());
            networkInformation.append(intersectionLine);
        }

        // Repeat process used for intersectionLines for all network routes
        String[] routeLines = networkRoutes.stream()
                .map(Object::toString).sorted().toArray(String[]::new);

        for (String routeLine : routeLines) {
            networkInformation.append(System.lineSeparator());
            networkInformation.append(routeLine);
        }
        return networkInformation.toString();
    }

    /**
     * Returns all the routes in the network.
     *
     * @return all routes in the network
     */
    private List<Route> getRoutes() {
        List<Route> routes = new ArrayList<>();
        for (Intersection intersection : this.getIntersections()) {
            routes.addAll(intersection.getConnections());
        }
        return routes;
    }

    /**
     * Returns a new list containing all the intersections in this network.
     *
     * <p>Adding/removing intersections from this list should not affect the
     * network's internal list of intersections.</p>
     * @return list of all intersections in this network
     */
    public List<Intersection> getIntersections() {
        return new ArrayList<>(this.intersections);
    }
}
