package com.jwebmp.entityassist;

@SuppressWarnings("unused")
public class EntityAssistException
		extends RuntimeException
{
	public EntityAssistException()
	{
		super();
	}

	public EntityAssistException(String message)
	{
		super(message);
	}

	public EntityAssistException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public EntityAssistException(Throwable cause)
	{
		super(cause);
	}

	protected EntityAssistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
