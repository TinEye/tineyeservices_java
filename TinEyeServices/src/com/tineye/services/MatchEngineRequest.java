package com.tineye.services;

import java.net.URISyntaxException;

import net.sf.json.JSONObject;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.log4j.Logger;

/**
 * Provides methods to call the MatchEngine API methods.
 * <p>
 * For a list of available MatchEngine API methods, refer
 * to the documentation for your MatchEngine API installation.
 * <p>
 * Copyright (C) 2011-2012 Idee Inc. All rights reserved worldwide.
 */
public class MatchEngineRequest extends TinEyeServiceRequest
{
    private static final Logger logger = Logger.getLogger(MatchEngineRequest.class);

    /**
     * Construct a <code>MatchEngineRequest</code> instance to issue
     * HTTP requests to the MatchEngine API.
     *
     * @param apiURL The MatchEngine API URL
     *
     * @throws NullPointerException   If the apiURL is null
     * @throws URISyntaxException     If the apiURL is not a valid URL
     */
    public MatchEngineRequest(String apiURL)
        throws NullPointerException, URISyntaxException
    {
        super(apiURL, null, null);
    }

    /**
     * Construct a <code>MatchEngineRequest</code> instance to issue
     * HTTP requests using HTTP basic authentication to the MatchEngine API.
     *
     * @param apiURL     The MatchEngine API URL
     * @param username   The username for HTTP basic authentication when
     *                   connecting to MatchEngine API
     * @param password   The password for HTTP basic authentication when
     *                   connecting to MatchEngine API
     *
     * @throws NullPointerException   If the apiURL is null
     * @throws URISyntaxException     If the apiURL is not a valid URL
     */
    public MatchEngineRequest(String apiURL, String username, String password)
        throws NullPointerException, URISyntaxException
    {
        super(apiURL, username, password);
    }

    /**
     * Add the images in <code>images</code> to the hosted image collection.
     * <p>
     * If the images have their collection filepaths set, add the images to the
     * hosted image collection using those paths. Otherwise, use the local image filepaths
     * to specify the image filepath in the hosted image collection.
     * <p>
     * If hosted image collection filepaths are used, each image must have a
     * collection filepath specified or the API will return an error.
     * <p>
     * Returns the API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>add</code></li>
     *     <li><code>result</code>: Empty array</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param images   List of images to add to the hosted collection
     *
     * @return The API JSON response with the image addition status
     *
     * @throws TinEyeServiceException   If exception occurs issuing the API <code>add</code>
     *                                  request or parsing the response
     */
    public JSONObject addImage(Image[] images)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < images.length; i++)
            {
                ByteArrayBody imageToAdd = new ByteArrayBody(images[i].getData(), images[i].getFilepath());
                postEntity.addPart("images[" + i + "]", imageToAdd);

                if (images[i].getCollectionFilepath() != null)
                {
                    StringBody filepathToAdd = new StringBody(images[i].getCollectionFilepath());
                    postEntity.addPart("filepaths[" + i + "]", filepathToAdd);
                }
            }
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
     * Each Image in <code>images</code> must have a URL and their collection filepath set.
     * <p>
     * Returns the API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>add</code></li>
     *     <li><code>result</code>: Empty array</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param images   List of images with URLs and collection filepaths to add
     *                 to the hosted image collection.
     *
     * @return The API JSON response with the status of the image addition
     *
     * @throws TinEyeServiceException   If exception occurs issuing the API <code>add</code>
     *                                  request or parsing the response
     */
    public JSONObject addURL(Image[] images)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < images.length; i++)
            {
                StringBody toAdd = new StringBody(images[i].getURL().toString());
                postEntity.addPart("urls[" + i + "]", toAdd);

                toAdd = new StringBody(images[i].getCollectionFilepath());
                postEntity.addPart("filepaths[" + i + "]", toAdd);
            }
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
     * Search the hosted image collection using an image and return any matches
     * with corresponding scores.
     * <p>
     * Returns the MatchEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>search</code></li>
     *     <li><code>result</code>: Array of JSON objects for each match with the following fields:
     *         <ul><li><code>score</code>: Relevance score of the match</li>
     *             <li><code>overlay</code>: URL pointing to the match overlay image on the API server</li>
     *             <li><code>filepath</code>: The collection match filepath</li></ul>
     *     </li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param image      The image to search for
     * @param minScore   Minimum score to return for results
     * @param offset     Offset to start returning results from (starting from 0)
     * @param limit      Maximum number of results to return
     * @param checkHorizontalFlip    If true, also search for horizontally flipped image in collection
     *
     * @return The MatchEngine API JSON response with search results
     *
     * @throws TinEyeServiceException   If exception occurs issuing the MatchEngine API
     *                                  <code>search</code> request or parsing the response
     */
    public JSONObject searchImage(Image image, int minScore, int offset,
                                  int limit, boolean checkHorizontalFlip)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            postEntity.addPart("image",     new ByteArrayBody(image.getData(), image.getFilepath()));
            postEntity.addPart("min_score", new StringBody(Integer.toString(minScore)));
            postEntity.addPart("offset",    new StringBody(Integer.toString(offset)));
            postEntity.addPart("limit",     new StringBody(Integer.toString(limit)));
            postEntity.addPart("check_horizontal_flip", new StringBody(Boolean.toString(checkHorizontalFlip)));

            responseJSON = postAPIRequest("search", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'searchImage' failed: " + e.toString());
            throw new TinEyeServiceException("'searchImage' failed", e);
        }
        return responseJSON;
    }

    /**
     * Search the hosted image collection using the filepath of an image in the
     * hosted image collection and return any matches with corresponding scores.
     * <p>
     * Returns the MatchEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>search</code></li>
     *     <li><code>result</code>: Array of JSON objects for each match with the following fields:
     *         <ul><li><code>score</code>: Relevance score of the match</li>
     *             <li><code>overlay</code>: URL pointing to the match overlay image on the API server</li>
     *             <li><code>filepath</code>: The collection match filepath</li></ul>
     *     </li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param filepath    A filepath of an image already in the collection as returned by a
     *                    <code>search</code> or <code>list</code> operation
     * @param minScore    Minimum score to return for results
     * @param offset      Offset to start returning results from (starting from 0)
     * @param limit       Maximum number of results to return
     * @param checkHorizontalFlip   If true, also search for horizontally flipped image in the
     *                              collection
     *
     * @return The MatchEngine API JSON response with search results
     *
     * @throws TinEyeServiceException   If exception occurs issuing the MatchEngine API
     *                                  <code>search</code> request or parsing the response
     */
    public JSONObject searchFilepath(String filepath, int minScore, int offset,
                                     int limit, boolean checkHorizontalFlip)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            postEntity.addPart("filepath",  new StringBody(filepath));
            postEntity.addPart("min_score", new StringBody(Integer.toString(minScore)));
            postEntity.addPart("offset",    new StringBody(Integer.toString(offset)));
            postEntity.addPart("limit",     new StringBody(Integer.toString(limit)));
            postEntity.addPart("check_horizontal_flip", new StringBody(Boolean.toString(checkHorizontalFlip)));

            responseJSON = postAPIRequest("search", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'searchFilepath' failed: " + e.toString());
            throw new TinEyeServiceException("'searchFilepath' failed", e);
        }
        return responseJSON;
    }

    /**
     * Search the hosted image collection using an image URL and return any matches with
     * corresponding scores.
     * <p>
     * Returns the MatchEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>search</code></li>
     *     <li><code>result</code>: Array of JSON objects for each match with the following fields:
     *         <ul><li><code>score</code>: Relevance score of the match</li>
     *             <li><code>overlay</code>: URL pointing to the match overlay image on the API server</li>
     *             <li><code>filepath</code>: The collection match filepath</li></ul>
     *     </li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param url        The URL to the image to search for against the hosted image collection
     * @param minScore   Minimum score to return for results
     * @param offset     Offset to start returning results from (starting from 0)
     * @param limit      Maximum number of results to return
     * @param checkHorizontalFlip   If true, also search for horizontally flipped image in collection
     *
     * @return The MatchEngine API JSON response with search results
     *
     * @throws TinEyeServiceException   If exception occurs issuing the MatchEngine API
     *                                  <code>search</code> request or parsing the response
     */
    public JSONObject searchURL(String url, int minScore, int offset,
                                int limit, boolean checkHorizontalFlip)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            postEntity.addPart("url",       new StringBody(url));
            postEntity.addPart("min_score", new StringBody(Integer.toString(minScore)));
            postEntity.addPart("offset",    new StringBody(Integer.toString(offset)));
            postEntity.addPart("limit",     new StringBody(Integer.toString(limit)));
            postEntity.addPart("check_horizontal_flip", new StringBody(Boolean.toString(checkHorizontalFlip)));

            responseJSON = postAPIRequest("search", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'searchURL' failed: " + e.toString());
            throw new TinEyeServiceException("'searchURL' failed", e);
        }
        return responseJSON;
    }

    /**
     * Compare <code>image1</code> to <code>image2</code> and return the match score.
     * <p>
     * Returns the MatchEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>compare</code></li>
     *     <li><code>result</code>: Array with a single JSON object match with the following fields:
     *         <ul><li><code>score</code>: Relevance score of the match</li>
     *             <li><code>match_percent</code>: Percentage of images that matched</li></ul>
     *     </li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param image1      The first image to compare using its data
     * @param image2      The second image to compare using its data
     * @param minScore    The minimum score of the result to return
     * @param checkHorizontalFlip   If true, also check if <code>image2</code> is the
     *                              horizontally flipped version of <code>image1</code>
     *
     * @return The MatchEngine API JSON response with compare results
     *
     * @throws TinEyeServiceException   If exception occurs issuing the MatchEngine API
     *                                  <code>compare</code> request or parsing the response
     */
    public JSONObject compareImage(Image image1, Image image2, int minScore, boolean checkHorizontalFlip)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            postEntity.addPart("image1",    new ByteArrayBody(image1.getData(), image1.getFilepath()));
            postEntity.addPart("image2",    new ByteArrayBody(image2.getData(), image2.getFilepath()));
            postEntity.addPart("min_score", new StringBody(Integer.toString(minScore)));
            postEntity.addPart("check_horizontal_flip", new StringBody(Boolean.toString(checkHorizontalFlip)));

            responseJSON = postAPIRequest("compare", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'compareImage' failed: " + e.toString());
            throw new TinEyeServiceException("'compareImage' failed", e);
        }
        return responseJSON;
    }

    /**
     * Compare the image at <code>url1</code> to the image at <code>url2</code>
     * and return the match score.
     * <p>
     * Returns the MatchEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>compare</code></li>
     *     <li><code>result</code>: Array with a single JSON object match with the following fields:
     *         <ul><li><code>score</code>: Relevance score of the match</li>
     *             <li><code>match_percent</code>: Percentage of images that matched</li></ul>
     *     </li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param url1       URL to the first image to compare
     * @param url2       URL to the second image to compare
     * @param minScore   The minimum score of the result to return
     * @param checkHorizontalFlip   If true, also check if image at <code>url2</code> is the horizontally
     *                              flipped version of image at <code>url1</code>
     *
     * @return The MatchEngine API JSON response with the compare results
     *
     * @throws TinEyeServiceException   If exception occurs issuing the MatchEngine API
     *                                  <code>compare</code> request or parsing the response
     */
    public JSONObject compareURL(String url1, String url2, int minScore, boolean checkHorizontalFlip)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            postEntity.addPart("url1",      new StringBody(url1));
            postEntity.addPart("url2",      new StringBody(url2));
            postEntity.addPart("min_score", new StringBody(Integer.toString(minScore)));
            postEntity.addPart("check_horizontal_flip", new StringBody(Boolean.toString(checkHorizontalFlip)));

            responseJSON = postAPIRequest("compare", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'compareURL' failed: " + e.toString());
            throw new TinEyeServiceException("'compareURL' failed", e);
        }
        return responseJSON;
    }
}
