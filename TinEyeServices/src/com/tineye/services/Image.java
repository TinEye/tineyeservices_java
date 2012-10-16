package com.tineye.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;


/**
 * Class representing images used by the TinEye Services APIs.
 * <p>
 * Copyright (C) 2011-2012 Idee Inc. All rights reserved worldwide.
 */
public class Image
{
    private String filepath;
    private final String collectionFilepath;
    private URI imageURL;
    private final JSONObject metadata;

    private byte[] data;

    /**
     * Construct an <code>Image</code> by reading the image data at the given filepath.
     *
     * @param filepath   Where the image to be read is located
     *
     * @throws NullPointerException   If image filepath is null
     * @throws IOException            If image cannot be read
     */
    public Image(String filepath)
        throws NullPointerException, IOException
    {
        this(filepath, null, null);
    }

    /**
     * Construct an <code>Image</code> by reading the image data at the given filepath,
     * to associate with the image collectionFilepath in the API.
     *
     * @param filepath             Path on disk to image to be read
     * @param collectionFilepath   Filepath of the image in the API image collection
     *
     * @throws NullPointerException   If image filepath is null
     * @throws IOException            If image cannot be read
     */
    public Image(String filepath, String collectionFilepath)
        throws NullPointerException, IOException
    {
        this(filepath, collectionFilepath, null);
    }

    /**
     * Construct an <code>Image</code> by reading the image data at the specified filepath,
     * to associate with the JSON metadata in the API.
     *
     * @param filepath   Path on disk to image to be read
     * @param metadata   Metadata to associate with the image in the API
     *
     * @throws NullPointerException   If image filepath is null
     * @throws IOException            If image cannot be read
     */
    public Image(String filepath, JSONObject metadata)
        throws NullPointerException, IOException
    {
        this(filepath, null, metadata);
    }

    /**
     * Construct an <code>Image</code> by reading the image data at the specified filepath,
     * to associate with the JSON metadata and image collectionFilepath in the API.
     *
     * @param filepath             Path on disk to image to be read
     * @param collectionFilepath   Filepath of the image in the API image collection
     * @param metadata             Metadata to associate with the image in the API
     *
     * @throws NullPointerException   If image filepath is null
     * @throws IOException            If image cannot be read
     */
    public Image(String filepath, String collectionFilepath, JSONObject metadata)
        throws NullPointerException, IOException
    {
        this.filepath = filepath;
        this.readData();

        this.collectionFilepath = collectionFilepath;
        this.metadata = metadata;
    }

    /**
     * Construct an <code>Image</code> with the specified image URL.
     *
     * @param url The Image URL
     */
    public Image(URI url)
    {
        this(url, null, null);
    }

    /**
     * Construct an <code>Image</code> with the specified image URL,
     * to associate with the image collectionFilepath in the API.
     *
     * @param url   The Image URL
     * @param collectionFilepath   Filepath of the image in the API image collection
     */
    public Image(URI url, String collectionFilepath)
    {
        this(url, collectionFilepath, null);
    }

    /**
     * Construct an <code>Image</code> with the specified image URL
     * to associate with the JSON metadata in the API.
     *
     * @param url        The Image URL
     * @param metadata   Metadata to associate with the image the API
     */
    public Image(URI url, JSONObject metadata)
    {
        this(url, null, metadata);
    }

    /**
     * Construct an <code>Image</code> with the specified image URL to associate
     * with the JSON metadata and image colletionFilepath in the API.
     *
     * @param url                  The Image URL
     * @param collectionFilepath   Filepath of the image in the API image collection
     * @param metadata             Metadata to associate with the image the API
     */
    public Image(URI url, String collectionFilepath, JSONObject metadata)
    {
        this.imageURL = url;
        this.collectionFilepath = collectionFilepath;
        this.metadata = metadata;
    }

    /**
     * Get the Image filepath if set.
     *
     * @return The Image filepath or null if not set
     */
    public String getFilepath()
    {
        return this.filepath;
    }

    /**
     * Get the API image collection filepath if set.
     *
     * @return The Image collectionFilepath or null if not set
     */
    public String getCollectionFilepath()
    {
        return this.collectionFilepath;
    }

    /**
     * Get the Image URL if set.
     *
     * @return The Image URL or null if not set
     */
    public URI getURL()
    {
        return this.imageURL;
    }

    /**
     * Get the JSON metadata associated with this Image in the API.
     * <p>
     * Note only the MulticolorEngine API supports metadata at the moment. 
     *
     * @return Metadata associated with this Image in the API or null if not set
     */
    public JSONObject getMetadata()
    {
        return this.metadata;
    }

    /**
     * Get the Image data if set.
     *
     * @return The Image data or null if not set
     */
    public byte[] getData()
    {
        return this.data;
    }

    /**
     * Read and store the image into a byte array.
     * Store null if the image can't be found or read for some reason.
     *
     * @throws NullPointerException   If image filepath is null
     * @throws IOException            If image cannot be read
     */
    protected void readData()
        throws NullPointerException, IOException
    {
        this.data = FileUtils.readFileToByteArray(new File(this.filepath));
    }
}
