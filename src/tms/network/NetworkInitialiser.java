package tms.network;

import tms.util.InvalidNetworkException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class responsible for loading and initialising a saved network from
 * a file.
 */

public class NetworkInitialiser {
    /** Stores file contents. Each element is a line of the file. */
    private static List<String> fileLines;

    /** The network to load. */
    private static Network toLoad;

    /**
     * Delimiter used to separate individual pieces of data on a single line
     * @see "Constant Field Values"
     */
    public static final String LINE_INFO_SEPARATOR = ":";

    /** Delimiter used to separate individual elements in a variable-length
     * list on a single line
     * @see "Constant Field Values"
     */
    public static final String LINE_LIST_SEPARATOR = ",";

    /** Comment in network files */
    private static final char COMMENT = ';';

    /** Only three numeric network details should be present in the file:
     * number of intersections, number of routes, and network yellow time.
     * Add one to denote position of first intersection in file.
     */
    private static final int NUM_NUMERIC_DETAILS = 4;

    /** First numeric network detail should be the number of intersections.
     * So when storing numeric network details in an array, this will denote
     * the zeroth index.
     */
    private static final int NUM_INTERSECTIONS = 0;

    /** Second numeric network detail should be the number of routes. So when
     * storing numeric network details in an array, this will denote the
     * first index.
     */
    private static final int NUM_ROUTES = 1;

    /** Third numeric network detail should be the number of routes. So when
     * storing numeric network details in an array, this will denote the
     * second index.
     */
    private static final int YELLOW_TIME = 2;

    /** After storing numeric network details in array. Provide position of
     * first intersectionId. As there are 3 numeric network details, this
     * will be stored in the third index.
     */
    private static final int FIRST_INTERSECTION = 3;

    /** Each Intersection's toString() representation cannot contain more
     * than 2 colons. Used to differentiate intersections with routes and
     * sensors.
     */
    private static final int MAX_NUM_COLONS_FOR_INTERSECTIONS = 2;

    /** Each Route's toString() representation must contain at least 3 colons.
     * Used to differentiate intersections with routes and sensors.
     */
    private static final int MIN_NUM_COLONS_FOR_ROUTES = 3;

    /**
     * Loads a saved Network from the file with the given filename.
     *
     * <p>Network files have the following structure. Square brackets
     * indicate that the data inside them is optional. For example, a route
     * does not necessarily need a speed sign (speedSignSpeed).</p>
     *
     * <p>See the demo network for an example (demo.txt).</p>
     *
     * <p>; This is a comment. It should be ignored.</p>
     * <p>numIntersections</p>
     * <p>numRoutes</p>
     * <p>yellowTime</p>
     * <p>intersectionId[:duration:sequence,of,intersection,ids]</p>
     * <p>... (more intersections)</p>
     * <p>intersectionFromId:intersectionToId:defaultSpeed:numSensors
     * [:speedSignSpeed]</p>
     * <p>SENSORTYPE:threshold:list,of,data,values</p>
     * <p>... (more routes and sensors)</p>
     *
     * <p>A network file is invalid if any of the following conditions are
     * true:</p>
     *
     * <ul>
     *     <li>The number of intersections specified is not equal to the
     *     number of intersections read from the file.</li>
     *     <li>The number of routes specified does not match the number read
     *     from the file.</li>
     *     <li>The number of sensors specified for a route does not match the
     *     number read from the line below.</li>
     *     <li>An intersection referenced by a route does not exist.</li>
     *     <li>An intersection has an invalid ID according to
     *     <code>Network.createIntersection(String)</code>.</li>
     *     <li>Two or more intersections have the same identifier string.</li>
     *     <li>Two or more routes have the same starting and ending
     *     intersections, e.g. a route X→Y and another route X→Y. A route is
     *     allowed to end at its starting intersection, i.e. X→X is allowed
     *     .</li>
     *     <li>A sensor type that is not one of the three provided demo
     *     sensors.</li>
     *     <li>A route contains sensors of the same type.</li>
     *     <li>The traffic light yellow time is less than one (1).</li>
     *     <li>A traffic light duration is less than the traffic light yellow
     *     time plus one (1).</li>
     *     <li>For intersections with traffic lights:</li>
     *     <ul>
     *         <li>The traffic light order for an intersection is not a
     *         permutation of that intersection's incoming routes.</li>
     *         <li>The traffic light order for an intersection is empty.</li>
     *     </ul>
     *     <li>Any numeric value that should be non-negative is less than
     *     zero. This includes:</li>
     *     <ul>
     *         <li>route speeds</li>
     *         <li>speed sign speeds</li>
     *         <li>sensor thresholds (also, cannot be zero)</li>
     *         <li>sensor data values</li>
     *     </ul>
     *     <li>The colon-delimited format is violated, i.e. there are
     *     more/fewer colons than expected.</li>
     *     <li>Any numeric value fails to be parsed.</li>
     *     <li>An empty line occurs where a non-empty line is expected.</li>
     *     <li>The file contains any more than two (2) newline characters at
     *     the end of the file.</li>
     * </ul>
     * @param filename name of the file from which to load a network
     * @return the Network loaded from file
     * @throws IOException any IOExceptions encountered when reading the file
     * are bubbled up
     * @throws InvalidNetworkException if the file format of the given file
     * is invalid
     */
    public static Network loadNetwork(String filename) throws IOException,
            InvalidNetworkException {
        toLoad = new Network();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        fileLines = new ArrayList<>();

        // Keep reading until EOF (and skip any comments)
        String lineToAdd;
        while ((lineToAdd = reader.readLine()) != null) {

            // Add any blank lines to validate after (to make it easier to
            // differentiate between blank lines in the middle vs end of the
            // file). We include this extra condition to avoid indexing
            // issues with String.charAt();
            if (lineToAdd.isBlank() || lineToAdd.charAt(0) != COMMENT) {
                fileLines.add(lineToAdd);
            }
        }
        reader.close();

        checkEmptyLines();
        checkExtraNewLines();

        // Numeric network details correspond to the number of intersections,
        // the number of routes, and the network yellow time
        int[] networkNumericDetails = getNetworkNumericDetails();

        // Validate and set all network details
        loadYellowTime(networkNumericDetails[YELLOW_TIME]);
        loadIntersections(networkNumericDetails);
        loadRoutesAndSensors(networkNumericDetails);

        return toLoad;
    }

    /**
     * Gets the number of intersections from the file contents.
     *
     * @return the (apparent) number of intersections in the network, the
     * (apparent) number of routes in the network, and the network yellow time.
     * @throws InvalidNetworkException If the line containing the  number of
     * intersections does not contain a parsable int
     */
    private static int[] getNetworkNumericDetails() throws
            InvalidNetworkException {
        int[] networkNumericDetails = new int[NUM_NUMERIC_DETAILS];

        // Counter to obtain each data value in correct order (i.e. first
        // number of intersections, then number of routes, then yellow time)
        int whichDetail = 0;

        for (String fileLine : fileLines) {
            try {
                networkNumericDetails[whichDetail] =
                        Integer.parseInt(fileLine);
                whichDetail++;

                // Everything has been added, add fileLine position of
                // next line (i.e. first intersection)
                if (whichDetail == NUM_NUMERIC_DETAILS - 1) {
                    networkNumericDetails[whichDetail] =
                            fileLines.indexOf(fileLine) + 1;
                    return networkNumericDetails;
                }
            } catch (NumberFormatException nfe) {
                throw new InvalidNetworkException("Invalid numeric " +
                        "details");
            }
        }
        throw new InvalidNetworkException("This file is empty.");
    }

    /**
     * Checks the file contents for empty lines where non-empty lines are
     * expected.
     *
     * @throws InvalidNetworkException If empty lines are found when
     * expecting non-empty lines
     */
    private static void checkEmptyLines() throws InvalidNetworkException {
        // Last element of fileLines is either a route or a blank line (based
        // on how many newline characters are present)
        for (int line = 0; line < fileLines.size(); line++) {
            if (line != fileLines.size() - 1 && fileLines.get(line).isBlank()) {
                throw new InvalidNetworkException("Empty line occurs where a " +
                        "non-empty line is expected.");
            }
        }
    }

    /**
     * Sets the network yellow time. Ensures yellowTime is at least one (1)
     * second.
     *
     * @param yellowTime Yellow time from file
     * @throws InvalidNetworkException if yellowTime < 1
     */
    private static void loadYellowTime(int yellowTime) throws
            InvalidNetworkException {
        if (yellowTime >= 1) {
            toLoad.setYellowTime(yellowTime);
        } else {
            throw new InvalidNetworkException("Invalid yellow time");
        }
    }

    /**
     * Entry point for intersection file content validation and network loading.
     *
     * @param networkNumericDetails the numeric details of the network
     * @throws InvalidNetworkException if any issues with file contents
     * relating to the intersections are found
     */
    private static void loadIntersections(int[] networkNumericDetails) throws
            InvalidNetworkException {
        List<String> intersectionToStrings = new ArrayList<>();

        for (int line = 0; line < networkNumericDetails[NUM_INTERSECTIONS];
             line++) {
            // Ignore lines before intersections
            String currentLine = fileLines.get(line +
                    networkNumericDetails[FIRST_INTERSECTION]);

            validateIntersections(networkNumericDetails,
                    intersectionToStrings, currentLine);
            intersectionToStrings.add(currentLine);
        }
        addIntersections(intersectionToStrings);
        // REMAINDER UNIMPLEMENTED
    }

    /**
     * Validates the Intersection.toString() representations.
     *
     * @param networkNumericDetails the numeric details of the network
     * @param intersectionToStrings the Intersection.toString() representations
     * @param currentLine the current representation being observed
     * @throws InvalidNetworkException if any issues with the
     * Intersection.toString() representations are found (e.g. duplicate
     * intersection IDs, incorrect number of colons, invalid duration of
     * green-yellow IntersectionLights cycle, etc.)
     */
    private static void validateIntersections(int[] networkNumericDetails,
            List<String> intersectionToStrings, String currentLine) throws
            InvalidNetworkException {
        String[] intersectionComponents =
                currentLine.split(LINE_INFO_SEPARATOR);

        // We have reached the routes...or the intersection is invalid.
        // More than 2 colons implies more than 3 intersection toString
        // components
        if (intersectionComponents.length >
                MAX_NUM_COLONS_FOR_INTERSECTIONS + 1) {
            throw new InvalidNetworkException("Incorrect number of " +
                    "intersections given in file.");
        }
        if (intersectionToStrings.contains(intersectionComponents[0])) {
            throw new InvalidNetworkException("Two or more intersections " +
                    "have same identifier string");
        }
        try {
            if (intersectionComponents.length > 1 &&
                    Integer.parseInt(intersectionComponents[1]) <
                            networkNumericDetails[YELLOW_TIME] + 1) {
                throw new InvalidNetworkException("Invalid duration");
            }
        } catch (NumberFormatException nfe) {
            throw new InvalidNetworkException("Invalid " +
                    "intersection details");
        }
    }

    /**
     * Adds the intersections to the network. Checks validity of Intersection
     * IDs based on Network.createIntersection().
     *
     * @see Network#createIntersection(String)
     * @param intersectionToStrings The toString() representations of all
     *                              intersections
     * @throws InvalidNetworkException if an Intersection ID is invalid
     * according to Network.createIntersection()
     */
    private static void addIntersections(List<String> intersectionToStrings)
            throws InvalidNetworkException {
        for (String intersectionToString : intersectionToStrings) {
            String[] intersectionComponents =
                    intersectionToString.split(LINE_INFO_SEPARATOR);
            try {
                toLoad.createIntersection(intersectionComponents[0]);
            } catch (IllegalArgumentException iae) {
                throw new InvalidNetworkException("Invalid intersection");
            }
        }
    }

    /**
     * Entry point for route and sensor file content validation and network
     * loading.
     *
     * @param networkNumericDetails the numeric details of the network
     */
    private static void loadRoutesAndSensors(int[] networkNumericDetails) {
        List<String> routesAndSensors = new ArrayList<>();

        // Position of first route in fileLines (since numIntersections has
        // been validated) is position of first intersection + numIntersections
        int firstRoute = networkNumericDetails[FIRST_INTERSECTION] +
                networkNumericDetails[NUM_INTERSECTIONS];

        for (int line = 0; line < networkNumericDetails[NUM_ROUTES];
             line++) {
            String currentLine = fileLines.get(line + firstRoute);
            String[] routeComponents = currentLine.split(LINE_INFO_SEPARATOR);

        }
        // REMAINDER UNIMPLEMENTED
    }

    /**
     * Checks if more than 2 newline characters are found at the end of the
     * file.
     *
     * @throws InvalidNetworkException If more than 2 newline characters are
     * found at the end of the file
     */
    private static void checkExtraNewLines() throws
            InvalidNetworkException {
        // If more than 2 newline characters are at the end of the file,
        // BufferedReader will read at least 2 blank lines at the end
        String lastLine = fileLines.get(fileLines.size() - 1);
        String secondLastLine = fileLines.get(fileLines.size() - 2);

        // Check if the last 2 lines are blank. This corresponds to extra
        // newlines
        if (lastLine.equals(secondLastLine) && lastLine.isBlank()) {
            throw new InvalidNetworkException("More than 2 newline characters" +
                    " were found at the end of the file.");
        }
    }
}
