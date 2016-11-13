package com.sudicode.fb2gh.github;

import com.jcabi.github.mock.MkGithub;

import java.io.IOException;
import java.io.UncheckedIOException;

import static org.mockito.Mockito.*;

/**
 * Mocked GitHub repository.
 */
public class GHRepoMock {

    public static GHRepo get() {
        try {
            GHRepo repo = spy(new GHRepo(new MkGithub().randomRepo()));
            return repo;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
