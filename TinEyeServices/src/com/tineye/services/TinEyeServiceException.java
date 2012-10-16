package com.tineye.services;

/**
 * Signals that an exception occurred issuing a request to a
 * TinEye Services API or parsing a TinEye Services API response.
 * <p>
 * Copyright (C) 2011-2012 Idee Inc. All rights reserved worldwide.
 */
public class TinEyeServiceException extends Exception
{
    /**
     * Construct a <code>TinEyeServiceException</code> with the
     * specified detail message.
     *
     * @param message   The details of the exception. The detail message is
     *                  saved for later retrieval by the Throwable.getMessage() method.
     */
    public TinEyeServiceException(String message)
    {
        super(message);
    }

    /**
     * Construct a <code>TinEyeServiceException</code> with the
     * specified detail message and cause.
     *
     * @param message   The details of the exception. The detail message is
     *                  saved for later retrieval by the Throwable.getMessage() method.
     * @param cause     The cause of the exception.
     */
    public TinEyeServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }
}