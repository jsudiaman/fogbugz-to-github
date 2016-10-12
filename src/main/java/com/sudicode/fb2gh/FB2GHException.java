package com.sudicode.fb2gh;

/**
 * Exception thrown by FB2GH.
 */
public class FB2GHException extends RuntimeException {

    /**
     * Construct a new <code>FB2GHException</code> with the specified message.
     *
     * @param message The message
     */
    public FB2GHException(final String message) {
        super(message);
    }

    /**
     * Construct a new <code>FB2GHException</code> with the specified cause.
     *
     * @param cause The cause
     */
    public FB2GHException(final Throwable cause) {
        super(cause);
    }

}
