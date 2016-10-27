package com.tineye.services;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.log4j.Logger;

/**
 * Provides methods to call the TinEye Service API methods that
 * deal with searching and tagging images with metadata.
 * <p>
 * Copyright (C) 2011-2016 Idée Inc. All rights reserved worldwide.
 */
public class MetadataRequest extends TinEyeServiceRequest
{
    private static final Logger logger = Logger.getLogger(MetadataRequest.class);

    /**
     * Construct a <code>MetadataRequest</code> instance to issue
     * HTTP requests to a TinEye Services API that supports image metadata.
     *
     * @param apiURL   The URL to a specific TinEye Services API
     *
     * @throws NullPointerException   If the apiURL is null
     * @throws URISyntaxException     If the apiURL is not a valid URL
     * @throws TinEyeServiceException If the apiURL does not end with /rest/
     */
    public MetadataRequest(String apiURL)
        throws NullPointerException, URISyntaxException, TinEyeServiceException
    {
        super(apiURL, null, null);
    }

    /**
     * Construct a <code>MetadataRequest</code> instance to issue
     * HTTP requests using HTTP basic authentication to a specific
     * TinEye Services API that supports image metadata.
     *
     * @param apiURL     The URL to a specific TinEye Services API
     * @param username   The username for HTTP basic authentication when
     *                   connecting to the TinEye Services API
     * @param password   The password for HTTP basic authentication when
     *                   connecting to the TinEye Services API
     *
     * @throws NullPointerException   If the apiURL is null
     * @throws URISyntaxException     If the apiURL is not a valid URL
     * @throws TinEyeServiceException If the apiURL does not end with /rest/
     */
    public MetadataRequest(String apiURL, String username, String password)
        throws NullPointerException, URISyntaxException, TinEyeServiceException
    {
        super(apiURL, username, password);
    }

    /**
     * Add the images in <code>images</code> to the hosted image collection using the image data.
     * <p>
     * If the images have JSON metadata, add the images to the collection with the metadata.
     * If metadata is included, each image must have metadata or the API will return an error.
     * <p>
     * If the ignoreBackground option is set to true, an image region containing
     * 75% or more of the image's edge pixels is considered a background region
     * and its associated color is disgarded by the add call.
     * <p>
     * Returns the API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>add</code></li>
     *     <li><code>result</code>: Empty array</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param images                     List of images to add to the hosted image collection
     * @param ignoreBackground           If true, ignore the background color of the images
     *                                   If false, include the background color of the images
     * @param ignoreInteriorBackground   If true, ignore regions that have the same color as the
     *                                   background region but that are surrounded by non-background
     *                                   regions.
     *
     * @return The API JSON response with the status of the image addition
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the API <code>add</code>
     *                                  request or parsing the response
     */
    public JSONObject addImage(Image[] images, boolean ignoreBackground, boolean ignoreInteriorBackground)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            int i = 0;
            for(Image image: images)
            {
                ByteArrayBody toAdd = new ByteArrayBody(image.getData(), image.getFilepath());
                postEntity.addPart("images[" + i + "]", toAdd);

                if (image.getFilepath() != null)
                {
                    postEntity.addPart("filepaths[" + i + "]", new StringBody(image.getFilepath()));
                }

                if (image.getMetadata() != null)
                {
                    StringBody metadataContent = new StringBody(image.getMetadata().toString());
                    postEntity.addPart("metadata[" + i + "]", metadataContent);
                }

                i += 1;
            }
            postEntity.addPart("ignore_background",          new StringBody(Boolean.toString(ignoreBackground)));
            postEntity.addPart("ignore_interior_background", new StringBody(Boolean.toString(ignoreInteriorBackground)));

            responseJSON = postAPIRequest("add", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'addImage' failed: " + e.toString());
            throw new TinEyeServiceException("'addImage' failed", e);
        }
        return responseJSON;
    }

    /**
     * Add the images in <code>images</code> to the hosted image collection using their URLs.
     * <p>
     * Each Image in <code>images</code> must have a URL and collection filepath set.
     * <p>
     * If the images have JSON metadata, add the images to the collection with the metadata.
     * If metadata is included, each image must have metadata or the API will return an error.
     * <p>
     * If the ignoreBackground option is set to true, an image region containing
     * 75% or more of the image's edge pixels is considered a background region
     * and its associated color is disgarded by the add call.
     * <p>
     * Returns the API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>add</code></li>
     *     <li><code>result</code>: Empty array</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param images                     List of images with URLs and collection filepaths
     *                                   to add to the hosted image collection
     * @param ignoreBackground           If true, ignore the background color of the images
     *                                   If false, include the background color of the images
     * @param ignoreInteriorBackground   If true, ignore regions that have the same color as the
     *                                   background region but that are surrounded by non-background
     *                                   regions.
     *
     * @return The API JSON response with the status of the image addition
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the API <code>add</code>
     *                                  request or parsing the response
     */
    public JSONObject addURL(Image[] images, boolean ignoreBackground, boolean ignoreInteriorBackground)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            int i = 0;
            for(Image image: images)
            {
                StringBody toAdd = new StringBody(image.getURL().toString());
                postEntity.addPart("urls[" + i + "]", toAdd);

                toAdd = new StringBody(image.getCollectionFilepath());
                postEntity.addPart("filepaths[" + i + "]", toAdd);

                if (images[i].getMetadata() != null)
                {
                    toAdd = new StringBody(image.getMetadata().toString());
                    postEntity.addPart("metadata[" + i + "]", toAdd);
                }

                i += 1;
            }
            postEntity.addPart("ignore_background",          new StringBody(Boolean.toString(ignoreBackground)));
            postEntity.addPart("ignore_interior_background", new StringBody(Boolean.toString(ignoreInteriorBackground)));

            responseJSON = postAPIRequest("add", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'addURL' failed: " + e.toString());
            throw new TinEyeServiceException("'addURL' failed", e);
        }
        return responseJSON;
    }

    /**
     * Get the keywords from the index associated with the images with the given
     * collection image filepaths.
     * <p>
     * Returns the API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>get_metadata</code></li>
     *     <li><code>result</code>: Array of JSON objects for each match with the following fields:
     *         <ul><li><code>filepath</code>: The collection match filepath</li>
     *             <li><code>metadata</code>: JSON object with the keywords
     *                 associated with the image</li></ul>
     *     </li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param filepaths   List of collection image filepaths to retrieve keywords for
     *
     * @return The API JSON response with the image keywords
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the API <code>get_metadata</code>
     *                                  request or parsing the response
     */
    public JSONObject getMetadata(String[] filepaths)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            int i = 0;
            for(String filepath: filepaths)
            {
                postEntity.addPart("filepaths[" + i + "]", new StringBody(filepath));
                i += 1;
            }
            responseJSON = postAPIRequest("get_metadata", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'getMetadata' failed: " + e.toString());
            throw new TinEyeServiceException("'getMetadata' failed", e);
        }
        return responseJSON;
    }

    /**
     * Get the metadata tree structure that can be searched.
     * <p>
     * Returns the API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>get_search_metadata</code></li>
     *     <li><code>result</code>: Array with a single JSON object containing the
     *         metadata tree structure that can be searched also including the keyword
     *         type and number of images in the index associated with that keyword</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @return The API JSON response with the searchable metadata tree structure
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the API <code>get_search_metadata</code>
     *                                  request or parsing the response
     */
    public JSONObject getSearchMetadata()
        throws TinEyeServiceException
    {
        JSONObject responseJSON = null;

        try
        {
            responseJSON = getAPIRequest("get_search_metadata");
        }
        catch (Exception e)
        {
            logger.error("'getSearchMetadata' failed: " + e.toString());
            throw new TinEyeServiceException("'getSearchMetadata' failed", e);
        }
        return responseJSON;
    }

    /**
     * Get the metadata that can be returned by a search method along with each match.
     * <p>
     * Returns the API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>get_return_metadata</code></li>
     *     <li><code>result</code>: Array with a single JSON object containing
     *         the keywords that can be returned along with the data type and
     *         count of images in the index that have that keyword for each keyword</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @return The API JSON response with the metadata available to return
     *         with search results
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the API <code>get_return_metadata</code>
     *                                  request or parsing the response
     */
    public JSONObject getReturnMetadata()
        throws TinEyeServiceException
    {
        JSONObject responseJSON = null;

        try
        {
            responseJSON = getAPIRequest("get_return_metadata");
        }
        catch (Exception e)
        {
            logger.error("'getReturnMetadata' failed: " + e.toString());
            throw new TinEyeServiceException("'getReturnMetadata' failed", e);
        }
        return responseJSON;
    }

    /**
     * Helper method to add common search options to an API POST request.
     *
     * @param postEntity        The POST request to add the search options to
     * @param metadata          Metadata to perform additional filtering on the search results
     * @param returnMetadata    Metadata fields to return with each match,
     *                          which can include sorting options
     * @param sortMetadata      If true, sort results by metadata score instead of by match score
     * @param minScore          Minimum score of search results to return
     * @param offset            Offset from start of search results to return (starting from 0)
     * @param limit             The maximum number of results to return
     *
     * @return The postEntity with added search options
     *
     * @throws UnsupportedEncodingException   If search option encoding is not valid
     */
    protected MultipartEntity addExtraSearchOptions(MultipartEntity postEntity, JSONObject metadata,
                                                    JSONArray returnMetadata, boolean sortMetadata,
                                                    int minScore, int offset, int limit)
        throws UnsupportedEncodingException
    {
        // Handle metadata options
        if (metadata != null)
        {
            postEntity.addPart("metadata",        new StringBody(metadata.toString()));
            postEntity.addPart("return_metadata", new StringBody(returnMetadata.toString()));
            postEntity.addPart("sort_metadata",   new StringBody(Boolean.toString(sortMetadata)));
        }
        postEntity.addPart("min_score", new StringBody(Integer.toString(minScore)));
        postEntity.addPart("offset",    new StringBody(Integer.toString(offset)));
        postEntity.addPart("limit",     new StringBody(Integer.toString(limit)));

        return postEntity;
    }

    /**
     * Update the metadata for a list of images already present in the hosted image collection.
     * <p>
     * Note there must be one metadata entry for each image filepath passed in.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>update_metadata</code></li>
     *     <li><code>result</code>: Empty array.</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param filepaths    Array of hosted image filepaths to update metadata for.
     * @param metadata     The metadata entries to associate with each image filepath, respectively.
     *
     * @return The API JSON response.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the API
     *                                  <code>update_metadata</code> request or parsing
     *                                  the response
     */
    public JSONObject updateMetadata(String[] filepaths, JSONObject[] metadata)
    	throws TinEyeServiceException
    {
    	MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
        	if (filepaths.length != metadata.length)
    			throw new TinEyeServiceException("filepaths and metadata list must have the same number of entries");

            for(int i = 0; i < filepaths.length; i++)
            {
            	postEntity.addPart("filepaths[" + i + "]", new StringBody(filepaths[i]));
                postEntity.addPart("metadata[" + i + "]",  new StringBody(metadata[i].toString()));
            }

            responseJSON = postAPIRequest("update_metadata", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'updateMetadata' failed: " + e.toString());
            throw new TinEyeServiceException("'updateMetadata' failed", e);
        }
        return responseJSON;
    }
}
