package com.guicedee.entityassist;

@SuppressWarnings("unused")
public class EntityAssistException
		extends RuntimeException
{
	/**
	 * Constructor EntityAssistException creates a new EntityAssistException instance.
	 */
	public EntityAssistException()
	{
		super();
	}

	/**
	 * Constructor EntityAssistException creates a new EntityAssistException instance.
	 *
	 * @param message
	 * 		of type String
	 */
	public EntityAssistException(String message)
	{
		super(message);
	}

	/**
	 * Constructor EntityAssistException creates a new EntityAssistException instance.
	 *
	 * @param message
	 * 		of type String
	 * @param cause
	 * 		of type Throwable
	 */
	public EntityAssistException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Constructor EntityAssistException creates a new EntityAssistException instance.
	 *
	 * @param cause
	 * 		of type Throwable
	 */
	public EntityAssistException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Constructor EntityAssistException creates a new EntityAssistException instance.
	 *
	 * @param message
	 * 		of type String
	 * @param cause
	 * 		of type Throwable
	 * @param enableSuppression
	 * 		of type boolean
	 * @param writableStackTrace
	 * 		of type boolean
	 */
	protected EntityAssistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
