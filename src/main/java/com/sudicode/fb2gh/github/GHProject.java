package com.sudicode.fb2gh.github;

import com.google.common.annotations.Beta;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import com.sudicode.fb2gh.FB2GHException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * GitHub project.
 */
@Beta
@EqualsAndHashCode
@ToString(exclude = "baseRequest")
public class GHProject {

    private static final Logger logger = LoggerFactory.getLogger(GHProject.class);

    private final Request baseRequest;
    private final int id;
    private final String name;

    /**
     * Constructor.
     *
     * @param baseRequest Entry point for the projects API
     * @param id          Project ID
     * @param name        Project name
     */
    GHProject(final Request baseRequest, final int id, String name) {
        this.baseRequest = baseRequest;
        this.id = id;
        this.name = name;
    }

    /**
     * Get a column by name.
     *
     * @param name The name of the column
     * @return The column
     * @throws FB2GHException if a GitHub error occurs
     */
    public GHColumn getColumn(final String name) throws FB2GHException {
        try {
            logger.info("Searching for column named '{}'", name);
            JsonArray array = baseRequest.uri().path(String.format("/projects/%d/columns", id)).back()
                    .method(Request.GET)
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .as(JsonResponse.class)
                    .json()
                    .readArray();
            for (JsonObject obj : array.getValuesAs(JsonObject.class)) {
                String columnName = obj.getString("name");
                if (name.equals(columnName)) {
                    GHColumn column = new GHColumn(baseRequest, obj.getInt("id"), columnName);
                    logger.info("Found column: {}", column);
                    return column;
                }
            }
        } catch (AssertionError e) {
            throw GHUtils.rethrow(e);
        } catch (IOException e) {
            throw new FB2GHException(e);
        }
        throw new FB2GHException(String.format("Column '%s' not found.", name));
    }

    /**
     * @return The project ID.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The project name.
     */
    public String getName() {
        return name;
    }

}
