package fb2gh;

/**
 * Unchecked exception which can be used to wrap a checked exception.
 */
public class FB2GHException extends RuntimeException {

	private static final long serialVersionUID = 5229374306832599751L;

	/**
	 * Constructs a new runtime exception with the specified cause and a detail
	 * message of <tt>(cause==null ? null : cause.toString())</tt> (which
	 * typically contains the class and detail message of <tt>cause</tt>). This
	 * constructor is useful for runtime exceptions that are little more than
	 * wrappers for other throwables.
	 *
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public FB2GHException(Throwable cause) {
		super(cause);
	}

}
