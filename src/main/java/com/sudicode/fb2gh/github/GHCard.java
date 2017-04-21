package com.sudicode.fb2gh.github;

import com.google.common.annotations.Beta;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import com.sudicode.fb2gh.FB2GHException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * GitHub project card.
 */
@Beta
@EqualsAndHashCode
@ToString(exclude = "baseRequest")
public class GHCard {

    private final Request baseRequest;
    private final int id;

    /**
     * Constructor.
     *
     * @param baseRequest Entry point for the projects API
     * @param id          Card ID
     */
    GHCard(final Request baseRequest, final int id) {
        this.baseRequest = baseRequest;
        this.id = id;
    }

    /**
     * @return The card ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Move this card to a new {@link GHColumn}.
     *
     * @param column The {@link GHColumn}
     * @throws FB2GHException if a GitHub error occurs
     */
    public void moveTo(final GHColumn column) throws FB2GHException {
        try {
            baseRequest.uri().path(String.format("/projects/columns/cards/%d/moves", id)).back()
                    .method(Request.POST)
                    .body()
                    .set(Json.createObjectBuilder().add("position", "top").add("column_id", column.getId()).build())
                    .back()
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_CREATED);
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

}
