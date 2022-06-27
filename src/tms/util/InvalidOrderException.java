package tms.util;

/**
 * Exception thrown when traffic lights are created with an invalid ordering
 * of incoming routes, ie. the order is not a permutation of the
 * intersection's list of incoming routes.
 *
 * @see "Serialzed Form"
 */
public class InvalidOrderException extends Exception {

    /**
     * Constructs a normal InvalidOrderException with no error
     * message or cause.
     *
     * @see Exception#Exception()
     */
    public InvalidOrderException() {
        super();
    }

    /**
     * Constructs an InvalidOrderException that contains a helpful
     * message detailing why the exception occurred.
     *
     * <p>
     *     <strong>Note: </strong> implementing this constructor
     *     is <strong>optional</strong>. It has only been included to ensure
     *     your code will compile if you give your exception a message when
     *     throwing it. This practice can be useful for debugging purposes.
     * </p>
     *
     * <p>
     *     <strong>Important: </strong>do not write JUnit tests that expect a
     *     valid implementation of the assignment to have a certain error
     *     message, as the official solution will use different messages to
     *     those you are expecting, if any at all.
     * </p>
     *
     * @param message detail message
     *
     * @see Exception#Exception(String)
     */
    public InvalidOrderException(String message) {
        super(message);
    }

    /**
     * Constructs an InvalidOrderException that contains a helpful
     * message detailing why the exception occurred and a cause of the
     * exception.
     *
     * <p>
     *     <strong>Note: </strong> implementing this constructor
     *     is <strong>optional</strong>. It has only been included to ensure
     *     your code will compile if you give your exception a message when
     *     throwing it. This practice can be useful for debugging purposes.
     * </p>
     *
     * <p>
     *     <strong>Important: </strong>do not write JUnit tests that expect a
     *     valid implementation of the assignment to have a certain error
     *     message, as the official solution will use different messages to
     *     those you are expecting, if any at all.
     * </p>
     *
     * @param message detail message
     * @param err cause of the exception
     * @see Exception#Exception(String, Throwable)
     */
    public InvalidOrderException(String message, Throwable err) {
        super(message, err);
    }
}
