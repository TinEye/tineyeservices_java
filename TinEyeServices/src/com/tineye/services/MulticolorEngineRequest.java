package com.tineye.services;

import java.awt.Color;
import java.net.URISyntaxException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.log4j.Logger;

/**
 * Provides methods to call the TinEye Service MulticolorEngine API methods.
 * <p>
 * Copyright (C) 2011-2012 Idee Inc. All rights reserved worldwide.
 */
public class MulticolorEngineRequest extends MetadataRequest
{
    private static final Logger logger = Logger.getLogger(MulticolorEngineRequest.class);

    /**
     * Construct a <code>MulticolorEngineRequest</code> instance to issue
     * HTTP requests to the MulticolorEngine API.
     *
     * @param apiURL   The MulticolorEngine API URL
     *
     * @throws NullPointerException   If the apiURL is null
     * @throws URISyntaxException     If the apiURL is not a valid URL
     */
    public MulticolorEngineRequest(String apiURL)
        throws NullPointerException, URISyntaxException
    {
        super(apiURL, null, null);
    }

    /**
     * Construct a <code>MulticolorEngineRequest</code> instance to issue
     * HTTP requests using HTTP basic authentication to the MulticolorEngine API.
     *
     * @param apiURL     The MulticolorEngine API URL
     * @param username   The username for HTTP basic authentication when
     *                   connecting to the TinEye Service API
     * @param password   The password for HTTP basic authentication when
     *                   connecting to the TinEye Service API
     *
     * @throws NullPointerException   If the apiURL is null
     * @throws URISyntaxException     If the apiURL is not a valid URL
     */
    public MulticolorEngineRequest(String apiURL, String username, String password)
        throws NullPointerException, URISyntaxException
    {
        super(apiURL, username, password);
    }

    /**
     * Do a color search against the hosted image collection using an image and return matches
     * with corresponding scores.
     * <p>
     * If the ignoreBackground option is set to true, an image region containing
     * 75% or more of the image's edge pixels is considered a background region
     * and its associated color is not included in the search.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>count</code>: The number of search results</li>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>color_search</code></li>
     *     <li><code>result</code>: Array of JSON objects for each match with the following fields:
     *         <ul><li><code>score</code>: Relevance score of match</li>
     *             <li><code>filepath</code>: The collection match filepath</li></ul>
     *     </li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param image              Image to search the hosted image collection using colors from that image
     * @param metadata           Metadata to perform additional filtering on the search results
     * @param returnMetadata     Metadata fields to return with each match,
     *                           which can include sorting options
     * @param sortMetadata       If true, sort results by metadata score instead of by match score
     * @param ignoreBackground   If true, ignore the background color of the images
     *                           If false, include the background color of the images
     * @param ignoreInteriorBackground   If true, ignore regions that have the same color as the
     *                                   background region but that are surrounded by non-background
     *                                   regions.
     * @param minScore           Minimum score of search results to return
     * @param offset             Offset from start of search results to return (starting from 0)
     * @param limit              The maximum number of results to return
     *
     * @return The MulticolorEngine API JSON response with the color search results
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>color_search</code> request or parsing the response
     */
    public JSONObject searchImage(Image image, JSONObject metadata,
                                  JSONArray returnMetadata, boolean sortMetadata,
                                  boolean ignoreBackground, boolean ignoreInteriorBackground,
                                  int minScore, int offset, int limit)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            postEntity.addPart("image",                      new ByteArrayBody(image.getData(), image.getFilepath()));
            postEntity.addPart("ignore_background",          new StringBody(Boolean.toString(ignoreBackground)));
            postEntity.addPart("ignore_interior_background", new StringBody(Boolean.toString(ignoreInteriorBackground)));

            postEntity = addExtraSearchOptions(postEntity, metadata, returnMetadata,
                                               sortMetadata, minScore, offset, limit);

            responseJSON = postAPIRequest("color_search", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'searchImage' failed: " + e.toString());
            throw new TinEyeServiceException("'searchImage' failed", e);
        }
        return responseJSON;
    }

    /**
     * Do a color search against the hosted image collection using the filepath of an image
     * in the hosted image collection and return matches with corresponding scores.
     * <p>
     * If the ignoreBackground option is set to true, an image region containing
     * 75% or more of the image's edge pixels is considered a background region
     * and its associated color is not included in the search.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     * 	   <li><code>count</code>: The number of search results</li>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>color_search</code></li>
     *     <li><code>result</code>: Array of JSON objects for each match with the following fields:
     *         <ul><li><code>score</code>: Relevance score of match</li>
     *             <li><code>filepath</code>: The collection match filepath</li></ul>
     *     </li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param filepath           The collection filepath to the image whose colors to use for searching
     * @param metadata           Metadata to perform additional filtering on the search results
     * @param returnMetadata     Metadata fields to return with each match,
     *                           which can include sorting options
     * @param sortMetadata       If true, sort results by metadata score instead of by match score
     * @param ignoreBackground   If true, ignore the background color of the images
     *                           If false, include the background color of the images
     * @param ignoreInteriorBackground   If true, ignore regions that have the same color as the
     *                                   background region but that are surrounded by non-background
     *                                   regions.
     * @param minScore           Minimum score of search results to return
     * @param offset             Offset from start of search results to return (starting from 0)
     * @param limit              The maximum number of results to return
     *
     * @return The MulticolorEngine API JSON response with the color search results
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>color_search</code> request or parsing the response
     */
    public JSONObject searchFilepath(String filepath, JSONObject metadata,
                                     JSONArray returnMetadata, boolean sortMetadata,
                                     boolean ignoreBackground, boolean ignoreInteriorBackground, 
                                          int minScore, int offset, int limit)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            postEntity.addPart("filepath",                   new StringBody(filepath));
            postEntity.addPart("ignore_background",          new StringBody(Boolean.toString(ignoreBackground)));
            postEntity.addPart("ignore_interior_background", new StringBody(Boolean.toString(ignoreInteriorBackground)));

            postEntity = addExtraSearchOptions(postEntity, metadata, returnMetadata,
                                               sortMetadata, minScore, offset, limit);

            responseJSON = postAPIRequest("color_search", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'searchFilepath' failed: " + e.toString());
            throw new TinEyeServiceException("'searchFilepath' failed", e);
        }
        return responseJSON;
        
    }

    /**
     * Do a color search against the hosted image collection using an image URL and
     * return matches with corresponding scores.
     * <p>
     * If the ignoreBackground option is set to true, an image region containing
     * 75% or more of the image's edge pixels is considered a background region
     * and its associated color is not included in the search.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>count</code>: The number of search results</li>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>color_search</code></li>
     *     <li><code>result</code>: Array of JSON objects for each match with the following fields:
     *         <ul><li><code>score</code>: Relevance score of match</li>
     *             <li><code>filepath</code>: The collection match filepath</li></ul>
     *     </li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param imageURL           URL to image whose colors to use for searching
     * @param metadata           Metadata to perform additional filtering on the search results
     * @param returnMetadata     Metadata fields to return with each match,
     *                           which can include sorting options
     * @param sortMetadata       If true, sort results by metadata score instead of by match score
     * @param ignoreBackground   If true, ignore the background color of the images
     *                           If false, include the background color of the images
     * @param ignoreInteriorBackground   If true, ignore regions that have the same color as the
     *                                   background region but that are surrounded by non-background
     *                                   regions.
     * @param minScore           Minimum score of search results to return
     * @param offset             Offset from start of search results to return (starting from 0)
     * @param limit              The maximum number of results to return
     *
     * @return The MulticolorEngine API JSON response with the color search results
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>color_search</code> request or parsing the response
     */
    public JSONObject searchURL(String imageURL, JSONObject metadata,
                                JSONArray returnMetadata, boolean sortMetadata,
                                boolean ignoreBackground, boolean ignoreInteriorBackground,
                                int minScore, int offset, int limit)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            postEntity.addPart("url",                        new StringBody(imageURL));
            postEntity.addPart("ignore_background",          new StringBody(Boolean.toString(ignoreBackground)));
            postEntity.addPart("ignore_interior_background", new StringBody(Boolean.toString(ignoreInteriorBackground)));

            postEntity = addExtraSearchOptions(postEntity, metadata, returnMetadata,
                                               sortMetadata, minScore, offset, limit);

            responseJSON = postAPIRequest("color_search", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'searchURL' failed: " + e.toString());
            throw new TinEyeServiceException("'searchURL' failed", e);
        }
        return responseJSON;
    }

    /**
     * Do a color search against the hosted image collection using specified colors and
     * return matches with corresponding scores.
     * <p>
     * Each color may have an associated weight, indicating how for much of that color
     * should appear in a search result. If weights are used, there must be a
     * weight for each color.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>count</code>: The number of search results</li>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>color_search</code></li>
     *     <li><code>result</code>: Array of JSON objects for each match with the following fields:
     *         <ul><li><code>score</code>: Relevance score of match</li>
     *             <li><code>filepath</code>: The collection match filepath</li></ul>
     *     </li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param colors             List of colors for searching thfor e collection
     * @param weights            List of weights corresponding to the colors, or empty list
     * @param metadata           Metadata to perform additional filtering on the search results
     * @param returnMetadata     Metadata fields to return with each match,
     *                           which can include sorting options
     * @param sortMetadata       If true, sort results by metadata score instead of by match score
     * @param minScore           Minimum score of search results to return
     * @param offset             Offset from start of search results to return (starting from 0)
     * @param limit              The maximum number of results to return
     *
     * @return The MulticolorEngine API JSON response with the color search results
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>color_search</code> request or parsing the response
     */
    public JSONObject searchColor(Color[] colors, float[] weights, JSONObject metadata,
                                  JSONArray returnMetadata, boolean sortMetadata,
                                  int minScore, int offset, int limit)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            // Store list of colors in hex format, and disgard
            // the alpha component from each color (which by default is white)
            for(int i = 0; i < colors.length; i++)
            {
                String hexColor = Integer.toHexString(colors[i].getRGB()).substring(2);
                postEntity.addPart("colors[" + i + "]", new StringBody(hexColor));
            }

            // weights may be empty since they're optional
            for(int j = 0; j < weights.length; j++)
            {
                postEntity.addPart("weights[" + j + "]", new StringBody(Float.toString(weights[j])));
            }

            postEntity = addExtraSearchOptions(postEntity, metadata, returnMetadata,
                                               sortMetadata, minScore, offset, limit);

            responseJSON = postAPIRequest("color_search", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'searchColor' failed: " + e.toString());
            throw new TinEyeServiceException("'searchColor' failed", e);
        }
        return responseJSON;
    }

    /**
     * Search against the hosted image collection using metadata and
     * return matches with corresponding scores.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>count</code>: The number of search results</li>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>color_search</code></li>
     *     <li><code>result</code>: Array of JSON objects for each match with the following fields:
     *         <ul><li><code>metadata_score</code>: Relevance score of match based on metadata</li>
     *             <li><code>filepath</code>: The collection match filepath</li></ul>
     *     </li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param metadata           Metadata to perform additional filtering on the search results
     * @param returnMetadata     Metadata fields to return with each match,
     *                           which can include sorting options
     * @param sortMetadata       If true, sort results by metadata score instead of by match score
     * @param minScore           Minimum score of search results to return
     * @param offset             Offset from start of search results to return (starting from 0)
     * @param limit              The maximum number of results to return
     *
     * @return The MulticolorEngine API JSON response with the search results
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>color_search</code> request or parsing the response
     */
    public JSONObject searchMetadata(JSONObject metadata, JSONArray returnMetadata, 
    		                         boolean sortMetadata, int minScore, int offset, int limit)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
			postEntity = addExtraSearchOptions(postEntity, metadata, returnMetadata,
			                                   sortMetadata, minScore, offset, limit);
			
			responseJSON = postAPIRequest("color_search", postEntity);
		}
		catch (Exception e)
		{
			logger.error("'searchMetadata' failed: " + e.toString());
			throw new TinEyeServiceException("'searchMetadata' failed", e);
		}
        return responseJSON;
    }
    
    /**
     * Extract the dominant colors from the images passed in.
     * <p>
     * Color dominance is returned as a weight between 1 and 100 showing
     * how much of the color associated with the weight appears in the images.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>extract_image_colors</code></li>
     *     <li><code>result</code>: Array with a JSON objects with the following fields:
     *         <ul><li><code>color</code>: The extracted color in hex format, or an
     *             array with that color's 3 RGB values</li>
     *             <li><code>rank</code>: Integer value used to group images with
     *             similar color palettes together. Results are sorted by rank.</li>
     *             <li><code>weight</code>: Float value between 1 and 100 indicating
     *             how much of that color is in the images</li></ul>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param images                     The images to extract colors from
     * @param limit                      The maximum number of colors to be extracted
     * @param ignoreBackground           If true, ignore the background color of the images
     *                                   If false, include the background color of the images
     * @param ignoreInteriorBackground   If true, ignore regions that have the same color as the
     *                                   background region but that are surrounded by non-background
     *                                   regions.
     * @param colorFormat                To be returned, must be either rgb or hex
     *
     * @return The MulticolorEngine API JSON response with the extracted colors
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>extract_image_colors</code> request or parsing the response
     */
    public JSONObject extractImageColorsImage(Image[] images, int limit, boolean ignoreBackground, 
    		                                  boolean ignoreInteriorBackground, String colorFormat)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < images.length; i++)
            {
                postEntity.addPart("images[" + i + "]", new ByteArrayBody(images[i].getData(), images[i].getFilepath()));
            }
            postEntity.addPart("limit",                      new StringBody(Integer.toString(limit)));
            postEntity.addPart("ignore_background",          new StringBody(Boolean.toString(ignoreBackground)));
            postEntity.addPart("ignore_interior_background", new StringBody(Boolean.toString(ignoreInteriorBackground)));
            postEntity.addPart("color_format",               new StringBody(colorFormat));

            responseJSON = postAPIRequest("extract_image_colors", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'extractImageColorsImage' failed: " + e.toString());
            throw new TinEyeServiceException("'extractImageColorsImage' failed", e);
        }
        return responseJSON;
    }
    
    /**
     * Extract the dominant colors from images at the given URLs.
     * <p>
     * Color dominance is returned as a weight between 1 and 100 showing
     * how much of the color associated with the weight appears in the images.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>extract_image_colors</code></li>
     *     <li><code>result</code>: Array with a JSON objects with the following fields:
     *         <ul><li><code>color</code>: The extracted color in hex format, or an
     *             array with that color's 3 RGB values</li>
     *             <li><code>rank</code>: Integer value used to group images with
     *             similar color palettes together. Results are sorted by rank.</li>
     *             <li><code>weight</code>: Float value between 1 and 100 indicating
     *             how much of that color is in the images</li></ul>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param imageURLs                  URLs to images to extract colors from
     * @param limit                      The maximum number of colors to be extracted
     * @param ignoreBackground           If true, ignore the background color of the images
     *                                   If false, include the background color of the images
     * @param ignoreInteriorBackground   If true, ignore regions that have the same color as the
     *                                   background region but that are surrounded by non-background
     *                                   regions.
     * @param colorFormat                To be returned, must be either rgb or hex
     *
     * @return The MulticolorEngine API JSON response with the extracted colors
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>extract_image_colors</code> request or parsing the response
     */
    public JSONObject extractImageColorsURL(String[] imageURLs, int limit, boolean ignoreBackground, 
    		                                boolean ignoreInteriorBackground, String colorFormat)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < imageURLs.length; i++)
            {
                postEntity.addPart("urls[" + i + "]", new StringBody(imageURLs[i]));
            }
            postEntity.addPart("limit",                      new StringBody(Integer.toString(limit)));
            postEntity.addPart("ignore_background",          new StringBody(Boolean.toString(ignoreBackground)));
            postEntity.addPart("ignore_interior_background", new StringBody(Boolean.toString(ignoreInteriorBackground)));
            postEntity.addPart("color_format",               new StringBody(colorFormat));

            responseJSON = postAPIRequest("extract_image_colors", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'extractImageColorsURL' failed: " + e.toString());
            throw new TinEyeServiceException("'extractImageColorsURL' failed", e);
        }
        return responseJSON;
    }

    /**
     * Extract the dominant colors given the filepaths of images in the hosted
     * image collection.
     * <p>
     * Color dominance is returned as a weight between 1 and 100 showing
     * how much of the color associated with the weight appears in the image.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>extract_collection_colors</code></li>
     *     <li><code>result</code>: Array with a JSON objects with the following fields:
     *         <ul><li><code>color</code>: The extracted color in hex format, or an
     *             array with that color's 3 RGB values</li>
     *             <li><code>rank</code>: Integer value used to group images with
     *             similar color palettes together. Results are sorted by rank.</li>
     *             <li><code>weight</code>: Float value between 1 and 100 indicating
     *             how much of that color is in the image</li></ul>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param filepaths          Filepaths to images in the hosted image collection to
     *                           extract colors from
     * @param limit              The maximum number of colors to be extracted
     * @param colorFormat        To be returned, must be either rgb or hex
     *
     * @return The MulticolorEngine API JSON response with the extracted colors
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>extract_collection_colors</code> request or parsing
     *                                  the response
     */
    public JSONObject extractCollectionColorsFilepath(String[] filepaths, int limit, String colorFormat)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < filepaths.length; i++)
            {
                postEntity.addPart("filepaths[" + i + "]", new StringBody(filepaths[i]));
            }
            postEntity.addPart("limit",        new StringBody(Integer.toString(limit)));
            postEntity.addPart("color_format", new StringBody(colorFormat));

            responseJSON = postAPIRequest("extract_collection_colors", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'extractCollectionColorsFilepath' failed: " + e.toString());
            throw new TinEyeServiceException("'extractCollectionColorsFilepath' failed", e);
        }
        return responseJSON;
    }

    /**
     * Extract the dominant colors in the hosted image collection filtered by
     * image metadata. Metadata can be set to null to extract colors from all
     * images in the collection.
     * <p>
     * Color dominance is returned as a weight between 1 and 100 showing
     * how much of the color associated with the weight appears in the collection.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>extract_collection_colors</code></li>
     *     <li><code>result</code>: Array with a JSON objects with the following fields:
     *         <ul><li><code>color</code>: The extracted color in hex format, or an
     *             array with that color's 3 RGB values</li>
     *             <li><code>rank</code>: Integer value used to group images with
     *             similar color palettes together. Results are sorted by rank.</li>
     *             <li><code>weight</code>: Float value between 1 and 100 indicating
     *             how much of that color is in the collection</li></ul>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param metadata       Metadata to use to filter the results, can be null
     * @param limit          The maximum number of colors to be extracted
     * @param colorFormat    To be returned, must be either rgb or hex
     *
     * @return The MulticolorEngine API JSON response with the extracted colors
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>extract_collection_colors</code> request or parsing
     *                                  the response
     */
    public JSONObject extractCollectionColorsMetadata(JSONObject metadata, int limit,
                                                      String colorFormat)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            if (metadata != null)
            {
                postEntity.addPart("metadata", new StringBody(metadata.toString()));
            }
            postEntity.addPart("limit",        new StringBody(Integer.toString(limit)));
            postEntity.addPart("color_format", new StringBody(colorFormat));

            responseJSON = postAPIRequest("extract_collection_colors", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'extractCollectionColorsMetadata' failed: " + e.toString());
            throw new TinEyeServiceException("'extractCollectionColorsMetadata' failed", e);
        }
        return responseJSON;
    }

    /**
     * Extract the dominant colors in the hosted image collection.
     * <p>
     * Color dominance is returned as a weight between 1 and 100 showing
     * how much of the color associated with the weight appears in the collection.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>extract_collection_colors</code></li>
     *     <li><code>result</code>: Array with a JSON objects with the following fields:
     *         <ul><li><code>color</code>: The extracted color in hex format, or an
     *             array with that color's 3 RGB values</li>
     *             <li><code>rank</code>: Integer value used to group images with
     *             similar color palettes together. Results are sorted by rank.</li>
     *             <li><code>weight</code>: Float value between 1 and 100 indicating
     *             how much of that color is in the collection</li></ul>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param limit          The maximum number of colors to be extracted
     * @param colorFormat    To be returned, must be either rgb or hex
     *
     * @return The MulticolorEngine API JSON response with the extracted colors
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>extract_collection_colors</code> request or parsing
     *                                  the response
     */
    public JSONObject extractCollectionColors(int limit, String colorFormat)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            postEntity.addPart("limit",        new StringBody(Integer.toString(limit)));
            postEntity.addPart("color_format", new StringBody(colorFormat));

            responseJSON = postAPIRequest("extract_collection_colors", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'extractCollectionColors' failed: " + e.toString());
            throw new TinEyeServiceException("'extractCollectionColors' failed", e);
        }
        return responseJSON;
    }
    
    /**
     * Extract the dominant colors in the hosted image collection filtered by colors.
     * The colors passed in are used to get a set of images that have those colors, and
     * then colors are extracted from that set of images and returned.
     * <p>
     * Color dominance is returned as a weight between 1 and 100 showing
     * how much of the color associated with the weight appears in the collection.
     * <p>
     * Each passed in color may have an associated weight to indicate how much of a color
     * should be in the set of images to extract colors from. If weights are included then
     * there must be one weight for each passed in color, each weight must be between
     * 1 and 100 and all weights must add to 100.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>extract_collection_colors</code></li>
     *     <li><code>result</code>: Array with a JSON objects with the following fields:
     *         <ul><li><code>color</code>: The extracted color in hex format, or an
     *             array with that color's 3 RGB values</li>
     *             <li><code>rank</code>: Integer value used to group images with
     *             similar color palettes together. Results are sorted by rank.</li>
     *             <li><code>weight</code>: Float value between 1 and 100 indicating
     *             how much of that color is in the collection</li></ul>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param colors         Array of colors used to filter the results.
     * @param weights        Array of color weights used to filter the results.
     * @param limit          The maximum number of colors to be extracted
     * @param colorFormat    To be returned, must be either rgb or hex
     *
     * @return The MulticolorEngine API JSON response with the extracted colors
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>extract_collection_colors</code> request or parsing
     *                                  the response
     */
    public JSONObject extractCollectionColorsColors(Color[] colors, float[] weights, int limit, String colorFormat)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < colors.length; i++)
            {
                String hexColor = Integer.toHexString(colors[i].getRGB()).substring(2);
                postEntity.addPart("colors[" + i + "]", new StringBody(hexColor));

                // weights must be the same length as the colors if the weights list is not empty.
                if (weights.length > 0)
                {
                    postEntity.addPart("weights[" + i + "]", new StringBody(Float.toString(weights[i])));
                }
            }
            postEntity.addPart("limit",        new StringBody(Integer.toString(limit)));
            postEntity.addPart("color_format", new StringBody(colorFormat));

            responseJSON = postAPIRequest("extract_collection_colors", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'extractCollectionColorsColors' failed: " + e.toString());
            throw new TinEyeServiceException("'extractCollectionColorsColors' failed", e);
        }
        return responseJSON;
    }

    /**
     * Upload a list of images to the API and a color palette to get a count for
     * each color specifying how many of the input images contain that color.
     * <p>
     * If the ignoreBackground option is set to true, an image region containing
     * 75% or more of the image's edge pixels is considered a background region
     * and its associated color is not counted.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>count_image_colors</code></li>
     *     <li><code>result</code>: Array with a JSON objects with the following fields:
     *         <ul><li><code>color</code>: A color in hex format, or an
     *             array with that color's 3 RGB values</li>
     *             <li><code>num_images_partial_area</code>: The number of input images
     *             that contain the palette color in any percentage</li>
     *             <li><code>num_images_full_area</code>: The number of input images
     *             that contain the palette color in a very large percentage</li></ul>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param images             Array of images to count the colors from
     * @param countColors        Array of colors to get image counts for
     * @param ignoreBackground   If true, ignore the background color of the images
     *                           If false, include the background color of the images
     * @param ignoreInteriorBackground   If true, ignore regions that have the same color as the
     *                                   background region but that are surrounded by non-background
     *                                   regions.
     *
     * @return The MulticolorEngine API JSON response with the count of the palette colors in the
     *         passed in images.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>count_image_colors</code> request or parsing
     *                                  the response
     */
    public JSONObject countImageColorsImage(Image[] images, Color[] countColors, 
    		                                boolean ignoreBackground, boolean ignoreInteriorBackground)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < images.length; i++)
            {
                postEntity.addPart("images[" + i + "]", new ByteArrayBody(images[i].getData(), images[i].getFilepath()));
            }

            for(int j = 0; j < countColors.length; j++)
            {
                String hexColor = Integer.toHexString(countColors[j].getRGB()).substring(2);
                postEntity.addPart("count_colors[" + j + "]", new StringBody(hexColor));
            }
            postEntity.addPart("ignore_background",          new StringBody(Boolean.toString(ignoreBackground)));
            postEntity.addPart("ignore_interior_background", new StringBody(Boolean.toString(ignoreInteriorBackground)));

            responseJSON = postAPIRequest("count_image_colors", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'countImageColorsImage' failed: " + e.toString());
            throw new TinEyeServiceException("'countImageColorsImage' failed", e);
        }
        return responseJSON;
    }

    /**
     * Given a list of image URLs and a color palette, get a count for each color
     * specifying how many of the images at the given URLs contain that color.
     * <p>
     * If the ignoreBackground option is set to true, an image region containing
     * 75% or more of the image's edge pixels is consider and its associated color is not counted.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>count_image_colors</code></li>
     *     <li><code>result</code>: Array with a JSON objects with the following fields:
     *         <ul><li><code>color</code>: A color in hex format, or an
     *             array with that color's 3 RGB values</li>
     *             <li><code>num_images_partial_area</code>: The number of input images
     *             that contain the palette color in any percentage</li>
     *             <li><code>num_images_full_area</code>: The number of input images
     *             that contain the palette color in a very large percentage</li></ul>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param imageURLs          Array of URLs to images to count the colors from
     * @param countColors        Array of colors to get image counts for
     * @param ignoreBackground   If true, ignore the background color of the images
     *                           If false, include the background color of the images
     * @param ignoreInteriorBackground   If true, ignore regions that have the same color as the
     *                                   background region but that are surrounded by non-background
     *                                   regions.
     *
     * @return The MulticolorEngine API JSON response with the count of the palette colors in the
     *         passed in images.
     *
     * @throws TinEyeServiceException   If an exceptiCollectionon occurs issuing the MulticolorEngine API
     *                                  <code>count_image_colors</code> request or parsing
     *                                  the response
     */
    public JSONObject countImageColorsURL(String[] imageURLs, Color[] countColors, 
    		                              boolean ignoreBackground, boolean ignoreInteriorBackground)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < imageURLs.length; i++)
            {
                postEntity.addPart("urls[" + i + "]", new StringBody(imageURLs[i]));
            }

            for(int j = 0; j < countColors.length; j++)
            {
                String hexColor = Integer.toHexString(countColors[j].getRGB()).substring(2);
                postEntity.addPart("count_colors[" + j + "]", new StringBody(hexColor));
            }
            postEntity.addPart("ignore_background",          new StringBody(Boolean.toString(ignoreBackground)));
            postEntity.addPart("ignore_interior_background", new StringBody(Boolean.toString(ignoreInteriorBackground)));

            responseJSON = postAPIRequest("count_image_colors", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'countImageColorsURL' failed: " + e.toString());
            throw new TinEyeServiceException("'countImageColorsURL' failed", e);
        }
        return responseJSON;
    }

    /**
     * Given a list of filepaths in the hosted image collection, and a color palette (list of colors),
     * get a count for each color specifying the number of the images contain that color.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>count_collection_colors</code></li>
     *     <li><code>result</code>: Array with a JSON objects with the following fields:
     *         <ul><li><code>color</code>: A color in hex format, or an
     *             array with that color's 3 RGB values</li>
     *             <li><code>num_images_partial_area</code>: The number of images
     *             in the hosted image collection that contain the palette color in
     *             any percentage</li>
     *             <li><code>num_images_full_area</code>: The number of images
     *             in the hosted image collection that contain the palette color in
     *             a very large percentage</li></ul>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param filepaths      Filepaths to images in the hosted image collection to get color counts for.
     * @param countColors    Array of colors to get image counts for
     *
     * @return The MulticolorEngine API JSON response with the count of the palette colors in the
     *         specified hosted image collection images.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>count_collection_colors</code> request or parsing
     *                                  the response
     */
    public JSONObject countCollectionColorsFilepath(String[] filepaths, Color[] countColors)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < filepaths.length; i++)
            {
                postEntity.addPart("filepaths[" + i + "]", new StringBody(filepaths[i]));
            }

            for(int j = 0; j < countColors.length; j++)
            {
                String hexColor = Integer.toHexString(countColors[j].getRGB()).substring(2);
                postEntity.addPart("count_colors[" + j + "]", new StringBody(hexColor));
            }

            responseJSON = postAPIRequest("count_collection_colors", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'countCollectionColorsFilepath' failed: " + e.toString());
            throw new TinEyeServiceException("'countCollectionColorsFilepath' failed", e);
        }
        return responseJSON;
    }

    /**
     * Given a color palette (list of colors) and metadata, get a count for each color specifying
     * the number of the hosted image collection images that contain that color, filtered by the
     * metadata. Metadata can be set to null to count colors from all images in the collection.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>count_collection_colors</code></li>
     *     <li><code>result</code>: Array with a JSON objects with the following fields:
     *         <ul><li><code>color</code>: A color in hex format, or an
     *             array with that color's 3 RGB values</li>
     *             <li><code>num_images_partial_area</code>: The number of images
     *             in the hosted image collection that contain the palette color in
     *             any percentage</li>
     *             <li><code>num_images_full_area</code>: The number of images
     *             in the hosted image collection that contain the palette color in
     *             a very large percentage</li></ul>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param metadata       Metadata to filter the hosted collection images returned that
     *                       contain the given colors
     * @param countColors    Array of colors to get image counts for
     *
     * @return The MulticolorEngine API JSON response with the count of the palette colors in the
     *         hosted image collection image filtered by metadata.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>count_collection_colors</code> request or parsing
     *                                  the response
     */
    public JSONObject countCollectionColorsMetadata(JSONObject metadata, Color[] countColors)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            if (metadata != null)
            {
                postEntity.addPart("metadata", new StringBody(metadata.toString()));
            }

            for(int i = 0; i < countColors.length; i++)
            {
                String hexColor = Integer.toHexString(countColors[i].getRGB()).substring(2);
                postEntity.addPart("count_colors[" + i + "]", new StringBody(hexColor));
            }

            responseJSON = postAPIRequest("count_collection_colors", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'countCollectionColorsMetadata' failed: " + e.toString());
            throw new TinEyeServiceException("'countCollectionColorsMetadata' failed", e);
        }
        return responseJSON;
    }

    /**
     * Get counts for each color specified in a color palette (list of colors) from the 
     * hosted image collection.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>count_collection_colors</code></li>
     *     <li><code>result</code>: Array with a JSON objects with the following fields:
     *         <ul><li><code>color</code>: A color in hex format, or an
     *             array with that color's 3 RGB values</li>
     *             <li><code>num_images_partial_area</code>: The number of images
     *             in the hosted image collection that contain the palette color in
     *             any percentage</li>
     *             <li><code>num_images_full_area</code>: The number of images
     *             in the hosted image collection that contain the palette color in
     *             a very large percentage</li></ul>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param countColors   The palette of colors to count in the filtered image collection
     *
     * @return The MulticolorEngine API JSON response with the count of the palette colors in the
     *         hosted image collection.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>count_collection_colors</code> request or parsing
     *                                  the response
     */
    public JSONObject countCollectionColors(Color[] countColors)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int j = 0; j < countColors.length; j++)
            {
                String hexColor = Integer.toHexString(countColors[j].getRGB()).substring(2);
                postEntity.addPart("count_colors[" + j + "]", new StringBody(hexColor));
            }

            responseJSON = postAPIRequest("count_collection_colors", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'countCollectionColors' failed: " + e.toString());
            throw new TinEyeServiceException("'countCollectionColors' failed", e);
        }
        return responseJSON;
    }
    
    /**
     * Filter images in the hosted image collection by color and then get counts for
     * each color specified in a color palette (list of colors) from the filtered image list.
     * <p>
     * Each passed in filter color may have an associated weight to indicate how much of a
     * color should be in the set of images to count colors from. If weights are included
     * then there must be one weight for each passed in color, each weight must be between
     * 1 and 100 and all weights must add to 100.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>count_collection_colors</code></li>
     *     <li><code>result</code>: Array with a JSON objects with the following fields:
     *         <ul><li><code>color</code>: A color in hex format, or an
     *             array with that color's 3 RGB values</li>
     *             <li><code>num_images_partial_area</code>: The number of images
     *             in the hosted image collection that contain the palette color in
     *             any percentage</li>
     *             <li><code>num_images_full_area</code>: The number of images
     *             in the hosted image collection that contain the palette color in
     *             a very large percentage</li></ul>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param colors        Array of colors to filter image collection
     * @param weights       Array of color weights to filter image collection
     * @param countColors   The palette of colors to count in the filtered image collection
     *
     * @return The MulticolorEngine API JSON response with the count of the palette colors in the
     *         hosted image collection images filtered by color.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>count_collection_colors</code> request or parsing
     *                                  the response
     */
    public JSONObject countCollectionColorsColors(Color[] colors, float[] weights, Color[] countColors)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < colors.length; i++)
            {
                String hexColor = Integer.toHexString(colors[i].getRGB()).substring(2);
                postEntity.addPart("colors[" + i + "]", new StringBody(hexColor));

                // weights must be the same length as the colors if the weights list is not empty.
                if (weights.length > 0)
                {
                    postEntity.addPart("weights[" + i + "]", new StringBody(Float.toString(weights[i])));
                }
            }

            for(int j = 0; j < countColors.length; j++)
            {
                String hexColor = Integer.toHexString(countColors[j].getRGB()).substring(2);
                postEntity.addPart("count_colors[" + j + "]", new StringBody(hexColor));
            }

            responseJSON = postAPIRequest("count_collection_colors", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'countCollectionColorsColors' failed: " + e.toString());
            throw new TinEyeServiceException("'countCollectionColorsColors' failed", e);
        }
        return responseJSON;
    }

    /**
     * Given one of more metadata queries, get a counter for each query specifying how many
     * of the collection images match the query.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>count_metadata</code></li>
     *     <li><code>result</code>: Array with a single JSON object which has a metadata field
     *         set to a list of JSON objects with the counts for each metadata query passed in,
     *         along with the original metadaColorsta passed in.</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param countMetadata    Array of metadata queries to get image counts for.
     *
     * @return The MulticolorEngine API JSON response with the count of images matching
     *         each metadata query passed in.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>count_metadata</code> request or parsing
     *                                  the response
     */
    public JSONObject countMetadata(JSONObject[] countMetadata)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < countMetadata.length; i++)
            {
                postEntity.addPart("count_metadata[" + i + "]", new StringBody(countMetadata[i].toString()));
            }
            responseJSON = postAPIRequest("count_metadata", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'countMetadata' failed: " + e.toString());
            throw new TinEyeServiceException("'countMetadata' failed", e);
        }
        return responseJSON;
    }

    /**
     * Given one of more metadata queries, get a counter for each query specifying how many
     * of the collection images match the query. The images counted in the results may
     * be filtered using json metadata.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>count_metadata</code></li>
     *     <li><code>result</code>: Array with a single JSON object which has a metadata field
     *         set to a list of JSON objects with the counts for each metadata query passed in,
     *         along with the original metadaColorsta passed in.</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param countMetadata    Array of metadata queries to get image counts for.
     * @param metadata         Metadata to filter the images counted.
     *
     * @return The MulticolorEngine API JSON response with the count of images matching
     *         each metadata query passed in.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>count_metadata</code> request or parsing
     *                                  the response
     */
    public JSONObject countMetadataMetadata(JSONObject[] countMetadata, JSONObject metadata)
        throws TinEyeServiceException
    {
        if (metadata == null)
        	return this.countMetadata(countMetadata);
        
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;       

        try
        {
            for(int i = 0; i < countMetadata.length; i++)
            {
                postEntity.addPart("count_metadata[" + i + "]", new StringBody(countMetadata[i].toString()));
            }
            postEntity.addPart("metadata", new StringBody(metadata.toString()));

            responseJSON = postAPIRequest("count_metadata", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'countMetadataMetadata' failed: " + e.toString());
            throw new TinEyeServiceException("'countMetadataMetadata' failed", e);
        }
        return responseJSON;
    }

    /**
     * Given one of more colors and metadata queries, get a counter specifying
     * how many of the collection images with the given colors match each query.
     * <p>
     * A weight may be given for each color indicating how much of a color should
     * appear in a matching image. Weights must be given for each color if weights
     * are included, and each weight must be between 1 and 100 and they all must
     * add to 100.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>count_metadata</code></li>
     *     <li><code>result</code>: Array with a single JSON object which has a metadata field
     *         set to a list of JSON objects with the counts for each image with matching colors,
     *         for each metadata query passed in.</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param countMetadata    Array of metadata queries to get image counts for.
     * @param colors           Array of colors in the images to count.
     * @param weights          Array of color weights for each color included the
     *                         images to count (optional).
     *
     * @return The MulticolorEngine API JSON response with the count of images matching
     *         the colors and each metadata query passed in.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>count_metadata</code> request or parsing
     *                                  the response
     */
    public JSONObject countMetadataColors(JSONObject[] countMetadata, Color[] colors, float[] weights)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < countMetadata.length; i++)
            {
                postEntity.addPart("count_metadata[" + i + "]", new StringBody(countMetadata[i].toString()));
            }

            // Store list of colors in hex format, and disgard
            // the alpha component from each color (which by default is white)
            for(int j = 0; j < colors.length; j++)
            {
                String hexColor = Integer.toHexString(colors[j].getRGB()).substring(2);
                postEntity.addPart("colors[" + j + "]", new StringBody(hexColor));

                // weights must be the same length as the colors if the weights list is not empty.
                if (weights.length > 0)
                {
                    postEntity.addPart("weights[" + j + "]", new StringBody(Float.toString(weights[j])));
                }
            }

            responseJSON = postAPIRequest("count_metadata", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'countMetadataColors' failed: " + e.toString());
            throw new TinEyeServiceException("'countMetadataColors' failed", e);
        }
        return responseJSON;
    }

    /**
     * Given one or more hosted image filepaths and metadata queries, get a counter
     * specifying how many of the images from the specified filepaths match each query.
     * <p>
     * Returns the MulticolorEngine API JSON response with the following fields:
     * <ul>
     *     <li><code>status</code>: One of <code>ok</code>, <code>warn</code>, or <code>fail</code></li>
     *     <li><code>method</code>: <code>count_metadata</code></li>
     *     <li><code>result</code>: Array with a single JSON object which has a metadata field
     *         set to a list of JSON objects with the counts for each image from the given filepaths
     *         that match each metadata query passed in.</li>
     *     <li><code>error</code>: Array of error messages if status is not <code>ok</code></li>
     * </ul>
     *
     * @param countMetadata   Array of metadata queries to get image counts for.
     * @param filepaths       Array of hosted image filepaths to get metadata counts for.
     *
     * @return The MulticolorEngine API JSON response with the count of images matching
     *         each metadata query passed in.
     *
     * @throws TinEyeServiceException   If an exception occurs issuing the MulticolorEngine API
     *                                  <code>count_metadata</code> request or parsing
     *                                  the response
     */
    public JSONObject countMetadataFilepaths(JSONObject[] countMetadata, String[] filepaths)
        throws TinEyeServiceException
    {
        MultipartEntity postEntity = new MultipartEntity();
        JSONObject responseJSON = null;

        try
        {
            for(int i = 0; i < countMetadata.length; i++)
            {
                postEntity.addPart("count_metadata[" + i + "]", new StringBody(countMetadata[i].toString()));
            }

            for(int j = 0; j < filepaths.length; j++)
            {
                postEntity.addPart("filepaths[" + j + "]", new StringBody(filepaths[j]));
            }

            responseJSON = postAPIRequest("count_metadata", postEntity);
        }
        catch (Exception e)
        {
            logger.error("'countMetadataFilepaths' failed: " + e.toString());
            throw new TinEyeServiceException("'countMetadataFilepaths' failed", e);
        }
        return responseJSON;
    }
}
