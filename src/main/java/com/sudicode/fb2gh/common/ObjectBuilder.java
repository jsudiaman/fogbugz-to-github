package com.sudicode.fb2gh.common;

/**
 * A <a href="https://en.wikipedia.org/wiki/Builder_pattern">builder</a> for objects of type T.
 */
public interface ObjectBuilder<T> {

    /**
     * @return An instance of T.
     */
    T build();

}
