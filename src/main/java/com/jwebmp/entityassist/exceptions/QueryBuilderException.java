package com.jwebmp.entityassist.exceptions;

/**
 * Occurs when a query builder exception happens
 */
@SuppressWarnings("unused")
public class QueryBuilderException
		extends Exception
{
	/**
	 * Occurs when a query builder exception happens
	 */
	public QueryBuilderException()
	{
	}

	/**
	 * Occurs when a query builder exception happens
	 *
	 * @param message
	 */
	public QueryBuilderException(String message)
	{
		super(message);
	}

	/**
	 * Occurs when a query builder exception happens
	 *
	 * @param message
	 * @param cause
	 */
	public QueryBuilderException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Occurs when a query builder exception happens
	 *
	 * @param cause
	 */
	public QueryBuilderException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Occurs when a query builder exception happens
	 *
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public QueryBuilderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
