package com.tineye.services;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * Provides methods for issuing HTTP GET and POST requests.
 * <p>
 * HTTP connections can be made with or without HTTP basic authentication
 * crendentials. To use HTTP basic authentication, the host, port, username
 * and password to the website being connected to must be provided.
 * <p>
 * Copyright (C) 2011-2012 Idee Inc. All rights reserved worldwide.
 */
public class HttpUtils
{
    private static final Logger logger = Logger.getLogger(HttpUtils.class);

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    private final boolean useAuth;

    /**
     * Construct an <code>HttpUtils</code> instance to issue HTTP requests.
     */
    public HttpUtils()
    {
        this(null, -1, null, null);
    }

    /**
     * Construct an <code>HttpUtils</code> instance to issue HTTP requests using
     * HTTP basic authentication.
     *
     * @param host       The hostname to use for HTTP basic authentication when
     *                   connecting to website, this should be the domain name
     *                   excluding "http://"
     * @param port       The port for HTTP basic authentication
     * @param username   The username for HTTP basic authentication
     * @param password   The password for HTTP basic authentication
     */
    public HttpUtils(String host, int port, String username, String password)
    {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;

        if (host != null  && port >= 0 && username != null && password != null)
        {
            this.useAuth = true;
        }
        else
        {
            this.useAuth = false;
        }
    }

    /**
     * Do an HTTP POST of the contentBody to the requestURL.
     *
     * @param requestURL    The URL to issue the POST request to
     * @param contentBody   Body of POST request (including any files to upload)
     *
     * @return The server response string.
     *
     * @throws HttpUtilsException   If there's an error issuing the POST request
     *                              or parsing the response
     */
    public String doPost(String requestURL, MultipartEntity contentBody)
        throws HttpUtilsException
    {
        DefaultHttpClient httpClient = setupHttpClient();
        String content = null;
        HttpEntity responseEntity = null;

        try
        {
            HttpPost httpPost = new HttpPost(requestURL);
            httpPost.setEntity(contentBody);

            HttpResponse response = httpClient.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();

            responseEntity = response.getEntity();

            if (responseEntity != null && responseCode == 200)
            {
                content = EntityUtils.toString(responseEntity);
            }
        }
        catch (Exception e)
        {
            logger.error("'doPost' failed: " + e.toString());
            throw new HttpUtilsException("'doPost' failed", e);
        }
        finally
        {
            // Always need to consume responseEntity to free resources
            // and close any open streams. Also always need to shutdown
            // the httpClient to close any open connections.
            try
            {
                EntityUtils.consume(responseEntity);
            }
            catch (IOException e)
            {
                logger.warn("'doPost': Failed to consume response: " + e.toString());
            }
            httpClient.getConnectionManager().shutdown();
        }
        return content;
    }

    /**
     * Do an HTTP GET request to the requestURL.
     *
     * @param requestURL   The URL to issue the GET request to
     *
     * @return The server response string.
     *
     * @throws HttpUtilsException   If there's an error issuing the GET request
     *                              or parsing the response
     */
    public String doGet(String requestURL)
        throws HttpUtilsException
    {
        HttpClient httpClient = setupHttpClient();
        String content = null;
        HttpEntity responseEntity = null;

        try
        {
            HttpGet request = new HttpGet(requestURL);

            HttpResponse response = httpClient.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();

            responseEntity = response.getEntity();

            if (responseEntity != null && responseCode == 200)
            {
                content = EntityUtils.toString(responseEntity);
            }
        }
        catch (Exception e)
        {
            logger.error("'doGET' failed: " + e.toString());
            throw new HttpUtilsException("'doGet' failed", e);
        }
        finally
        {
            // Always need to consume responseEntity to free resources
            // and close any open streams. Also always need to shutdown
            // the httpClient to close any open connections.
            try
            {
                EntityUtils.consume(responseEntity);
            }
            catch (IOException e)
            {
                logger.warn("'doGet': Failed to consume response: " + e.toString());
            }
            httpClient.getConnectionManager().shutdown();
        }
        return content;
    }

    /**
     * Setup HTTP client to use for HTTP requests.
     * <p>
     * Adds credentials to access a website via HTTP basic authentication
     * if all the required authentication parameters were specified
     * when the HttpUtils were constructed.
     * <p>
     * <b>Be sure to shutdown the HTTP client when done with it!</b>
     *
     * @return HTTP client to use for requests
     */
    protected DefaultHttpClient setupHttpClient()
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        if (this.useAuth)
        {
            assert (this.host != null);
            assert (this.port >= 0);
            assert (this.username != null);
            assert (this.password != null);

            httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(this.host, this.port),
                new UsernamePasswordCredentials(this.username, this.password));
        }
        return httpClient;
    }
}
