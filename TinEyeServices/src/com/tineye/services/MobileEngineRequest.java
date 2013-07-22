package com.tineye.services;

import java.net.URISyntaxException;

import org.apache.log4j.Logger;

/**
 * Provides methods to call the MobileEngine API methods.
 * <p>
 * For a list of available MobileEngine API methods, refer
 * to the documentation for your MobileEngine API installation.
 * <p>
 * Copyright (C) 2013 Idee Inc. All rights reserved worldwide.
 */
public class MobileEngineRequest extends MatchEngineRequest
{
    /**
     * Construct a <code>MobileEngineRequest</code> instance to issue
     * HTTP requests to the MobileEngineRequest API.
     *
     * @param apiURL The MobileEngine API URL
     *
     * @throws NullPointerException   If the apiURL is null
     * @throws URISyntaxException     If the apiURL is not a valid URL
     */
    public MobileEngineRequest(String apiURL)
        throws NullPointerException, URISyntaxException
    {
        super(apiURL, null, null);
    }

    /**
     * Construct a <code>MobileEngineRequest</code> instance to issue
     * HTTP requests using HTTP basic authentication to the MobileEngineRequest API.
     *
     * @param apiURL     The MobileEngine API URL
     * @param username   The username for HTTP basic authentication when
     *                   connecting to MobileEngine API
     * @param password   The password for HTTP basic authentication when
     *                   connecting to MobileEngine API
     *
     * @throws NullPointerException   If the apiURL is null
     * @throws URISyntaxException     If the apiURL is not a valid URL
     */
    public MobileEngineRequest(String apiURL, String username, String password)
        throws NullPointerException, URISyntaxException
    {
        super(apiURL, username, password);
    }
}