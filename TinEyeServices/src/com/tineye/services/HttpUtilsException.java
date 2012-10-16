package com.tineye.services;

/**
 * Signals that an exception occurred while constructing or
 * issuing an HTTP request, or processing the HTTP response
 * using <code>HttpUtils</code> methods.
 * <p>
 * Copyright (C) 2011-2012 Idee Inc. All rights reserved worldwide.
 */
public class HttpUtilsException extends Exception
{
    /**
     * Construct an <code>HttpUtilsException</code> with the
     * specified detail message and cause.
     *
     * @param message   description of HttpUtils error
     * @param cause     of the HttpUtils exception
     */
    public HttpUtilsException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
