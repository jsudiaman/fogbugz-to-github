package com.sudicode.fb2gh.github;

import com.google.common.annotations.Beta;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import com.sudicode.fb2gh.FB2GHException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * GitHub project column.
 */
@Beta
@EqualsAndHashCode
@ToString(exclude = "baseRequest")
public class GHColumn {

    private final Request baseRequest;
    private final int id;
    private final String name;

    /**
     * Constructor.
     *
     * @param baseRequest Entry point for the projects API
     * @param id          Column ID
     * @param name        Column name
     */
    GHColumn(final Request baseRequest, final int id, final String name) {
        this.baseRequest = baseRequest;
        this.id = id;
        this.name = name;
    }

    /**
     * Create a project card.
     *
     * @param issue The issue to associate with this card
     * @return The created {@link GHCard}
     * @throws FB2GHException if a GitHub error occurs
     */
    public GHCard createCard(final GHIssue issue) throws FB2GHException {
        try {
            int cardId = baseRequest.uri().path(String.format("/projects/columns/%d/cards", id)).back()
                    .method(Request.POST)
                    .body()
                    .set(Json.createObjectBuilder().add("content_id", issue.getId()).add("content_type", "Issue").build())
                    .back()
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_CREATED)
                    .as(JsonResponse.class)
                    .json()
                    .readObject()
                    .getInt("id");
            return new GHCard(baseRequest, cardId);
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * List project cards.
     *
     * @return The project cards
     * @throws FB2GHException if a GitHub error occurs
     */
    public List<GHCard> listCards() throws FB2GHException {
        try {
            List<GHCard> cards = new ArrayList<>();
            JsonArray array = baseRequest.uri().path(String.format("/projects/columns/%d/cards", id)).back()
                    .method(Request.GET)
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .as(JsonResponse.class)
                    .json()
                    .readArray();
            for (JsonObject obj : array.getValuesAs(JsonObject.class)) {
                cards.add(new GHCard(baseRequest, obj.getInt("id")));
            }
            return cards;
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
    }

    /**
     * @return The column ID.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The column name.
     */
    public String getName() {
        return name;
    }

}
