package com.sudicode.fb2gh.common;

/**
 * A <a href="https://en.wikipedia.org/wiki/Builder_pattern">builder</a> for objects of type T.
 *
 * @param <T> The type of object being built
 */
public abstract class AbstractBuilder<T> {

    /**
     * @return An instance of T.
     */
    public abstract T build();

}
