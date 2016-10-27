package com.tineye.services;

import java.net.URISyntaxException;

import org.apache.log4j.Logger;

/**
 * Provides methods to call the WineEngine API methods.
 * <p>
 * For a list of available WineEngine API methods, refer
 * to the documentation for your WineEngine API installation.
 * <p>
 * Copyright (C) 2016 Idée Inc. All rights reserved worldwide.
 */
public class WineEngineRequest extends MobileEngineRequest
{
    /**
     * Construct a <code>WineEngineRequest</code> instance to issue
     * HTTP requests to the WineEngineRequest API.
     *
     * @param apiURL The WineEngine API URL
     *
     * @throws NullPointerException   If the apiURL is null
     * @throws URISyntaxException     If the apiURL is not a valid URL
     * @throws TinEyeServiceException If the apiURL does not end with /rest/
     */
    public WineEngineRequest(String apiURL)
        throws NullPointerException, URISyntaxException, TinEyeServiceException
    {
        super(apiURL, null, null);
    }

    /**
     * Construct a <code>WineEngineRequest</code> instance to issue
     * HTTP requests using HTTP basic authentication to the WineEngineRequest API.
     *
     * @param apiURL     The WineEngine API URL
     * @param username   The username for HTTP basic authentication when
     *                   connecting to the WineEngine API
     * @param password   The password for HTTP basic authentication when
     *                   connecting to the WineEngine API
     *
     * @throws NullPointerException   If the apiURL is null
     * @throws URISyntaxException     If the apiURL is not a valid URL
     * @throws TinEyeServiceException If the apiURL does not end with /rest/
     */
    public WineEngineRequest(String apiURL, String username, String password)
        throws NullPointerException, URISyntaxException, TinEyeServiceException
    {
        super(apiURL, username, password);
    }
}
