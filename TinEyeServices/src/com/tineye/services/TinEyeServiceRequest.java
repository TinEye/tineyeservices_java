package com.tineye.services;

import java.net.URI;
import java.net.URISyntaxException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.log4j.Logger;

// import static java.lang.System.out;

/**
 * Provides methods to call the TinEye Services API methods that
 * are common across all of the TinEye Services APIs (excluding
 * the TinEye Commercial API).
 * <p>
 * Copyright (C) 2011-2013 Idee Inc. All rights reserved worldwide.
 */
public class TinEyeServiceRequest
{
    private static final Logger logger = Logger.getLogger(TinEyeServiceRequest.class);

    private final String apiURL;
    private final String host;

    private final String username;
    private final String password;

    // The TinEye Services APIs will always be on port 80.
    private final int port = 80;

    /**
     * Construct a <code>TinEyeServiceRequest</code> instance to issue
     * HTTP requests to a specific TinEye Services API.
     *
     * @param apiURL   The URL for a specific TinEye Services API.
     *
     * @throws NullPointerException   If the apiURL is null.
     * @throws URISyntaxException     If the apiURL is not a valid URL.
     */
    public TinEyeServiceRequest(String apiURL)
        throws NullPointerException, URISyntaxException
    {
        this(apiURL, null, null);
    }

    /**
     * Construct a <code>TinEyeServiceRequest</code> instance to issue
     * HTTP requests to a specific TinEye Services API using HTTP basic
     * authentication.
     *
     * @param apiURL     The URL for a specific TinEye Services API.
     * @param username   The username for HTTP basic authentication when
     *                   connecting to the TinEye Services API.
     * @param password   The password for HTTP basic authentication when
     *                   connecting to the TinEye Services API.
     *
     * @throws NullPointerException   If the apiURL is null.
     * @throws URISyntaxException     If the apiURL is not a valid URL.
     */
    public TinEyeServiceRequest(String apiURL, String username, String password)
        throws NullPointerException, URISyntaxException
    {
        // All API URLs have to end with /rest/ or else the URL is incorrect.
        // It also helps if we need to add query parameters to the URL for
        // GET requests.
        if (!apiURL.endsWith("/"))
        {
            this.apiURL = apiURL + "/";
        }
        else
        {
            this.apiURL = apiURL;
        }
        this.username = username;
        this.password = password;
        this.host = new URI(apiURL).getHost();
    }

    /**
     * Delete images from the hosted image collection.
     * <p>
     * Returns the API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>delete</code></li>
     *     <li><code>result</code>: Empty array</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param filepaths   Filepaths of images to delete as returned by a search or list call.
     *
     * @return The API JSON response with image deletion status.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the API <code>delete</code>
     *                                  request or parsing the response.
     */
    public JSONObject delete(String[] filepaths)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            int i = 0;
            for(String filepath: filepaths)
            {
                StringBody toDelete = new StringBody(filepath);
                postEntity.addPart("filepaths[" + i + "]", toDelete);

                i += 1;
            }
            responseJSON = postAPIRequest("delete", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'delete' failed: " + e.toString());
            throw new TinEyeServiceException("'delete' failed", e);
        }
        return responseJSON;
    }

    /**
     * Get count of all the images in the hosted image collection.
     * <p>
     * Returns the API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>count</code></li>
     *     <li><code>result</code>: Array with one entry which is the image count</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @return The API JSON response with the hosted image collection image count.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the API <code>count</code>
     *                                  request or parsing the response.
     */
    public JSONObject count()
        throws TinEyeServiceException
    {
        try
        {
            return getAPIRequest("count");
        }
        catch (Exception e)
        {
            logger.error("'count' failed: " + e.toString());
            throw new TinEyeServiceException("'count' failed", e);
        }
    }

    /**
     * Get a list of images present in the hosted image collection.
     * <p>
     * Returns the API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>list</code></li>
     *     <li><code>result</code>: Array with list of collection image filepaths</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param offset   Offset from start of search results to return (starting from 0).
     * @param limit    Maximum number of images to list.
     *
     * @return The API JSON response with list of collection images.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the API <code>list</code>
     *                                  request or parsing the response.
     */
    public JSONObject list(int offset, int limit)
        throws TinEyeServiceException
    {
        try
        {
            String queryParams = "offset=" + offset + "&limit=" + limit;

            return getAPIRequest("list", queryParams);
        }
        catch (Exception e)
        {
            logger.error("'list' failed: " + e.toString());
            throw new TinEyeServiceException("'list' failed", e);
        }
    }

    /**
     * Check if the API server is running.
     * <p>
     * Returns the API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>ping</code></li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @return The API JSON response with the server status.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the API <code>ping</code>
     *                                  request or parsing the response.
     */
    public JSONObject ping()
        throws TinEyeServiceException
    {
        try
        {
            return getAPIRequest("ping");
        }
        catch (Exception e)
        {
            logger.error("'ping' failed: " + e.toString());
            throw new TinEyeServiceException("'ping' failed", e);
        }
    }

    /**
     * Helper method to issue an HTTP GET request to the specified API method.
     *
     * @param method   The API method to issue the HTTP GET request to.
     *
     * @return The API JSON response returned by the API server.
     *
     * @throws HttpUtilsException   If an exception occurs calling the API.
     * @throws JSONException        If an exception occurs converting the API response to a JSONObject.
     */
    protected JSONObject getAPIRequest(String method)
        throws HttpUtilsException, JSONException
    {
        return this.getAPIRequest(method, null);
    }

    /**
     * Helper method to issue an HTTP GET request to the specified API method
     * with the given API method query parameters.
     *
     * @param method        The API method to issue the HTTP GET request to
     * @param queryParams   Key-value pairs of request query parameters joined by &,
     *                      or null if there are no query parameters.
     *
     * @return The API JSON response returned by the API server.
     *
     * @throws HttpUtilsException   If an exception occurs calling the API.
     * @throws JSONException        If an exception occurs converting the API response to a JSONObject.
     */
    protected JSONObject getAPIRequest(String method, String queryParams)
        throws HttpUtilsException, JSONException
    {
        if (method == null)
        {
            logger.error("Attempted to call API with 'null' method");
            throw new IllegalArgumentException("Cannot call API with 'null' method");
        }

        HttpUtils httpHelper = new HttpUtils(this.host, this.port, this.username, this.password);
        String requestURL = this.apiURL + method + "/";

        if (queryParams != null)
        {
            requestURL += "?" + queryParams;
        }

        JSONObject responseJSON = null;

        // out.println(requestURL);
        
        try
        {
            String response = httpHelper.doGet(requestURL);
            // out.println(response);
            responseJSON = (JSONObject)JSONSerializer.toJSON(response);
        }
        catch (HttpUtilsException he)
        {
            logger.error("Got exception making GET request to '" + requestURL + "': " + he.toString());
            throw he;
        }
        catch (JSONException je)
        {
            logger.error("Got exception converting response to JSON: " + je.toString());
            throw je;
        }

        return responseJSON;
    }

    /**
     * Helper method to issue an HTTP POST request to the specified API method
     * using the postRequest body.
     *
     * @param method        The API method being called.
     * @param postRequest   The API POST request to send to the API.
     *
     * @return The JSON response returned by the API.
     *
     * @throws HttpUtilsException   If an exception occurs calling the API.
     * @throws JSONException        If an exception occurs converting the API response to a JSONObject.
     */
    protected JSONObject postAPIRequest(String method, MultipartEntity postRequest)
        throws HttpUtilsException, JSONException
    {
        if (method == null)
        {
            logger.error("Attempted to call API with 'null' method");
            throw new IllegalArgumentException("Cannot call API with 'null' method");
        }
        HttpUtils httpHelper = new HttpUtils(this.host, this.port, this.username, this.password);
        String requestURL = this.apiURL + method + "/";

        JSONObject responseJSON = null;

        // try
        // {
        //     postRequest.writeTo(out);
        // }
        // catch (Exception e)
        // {
        //     logger.error("Got exception printing POST request: " + e.toString());
        // }

        try
        {
            String response = httpHelper.doPost(requestURL, postRequest);
            // out.println(response);
            responseJSON = (JSONObject)JSONSerializer.toJSON(response);
        }
        catch (HttpUtilsException he)
        {
            logger.error("Got exception making POST request: " + he.toString());
            throw he;
        }
        catch (JSONException je)
        {
            logger.error("Got exception converting response to JSON: " + je.toString());
            throw je;
        }
        return responseJSON;
    }
}
