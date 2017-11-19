package za.co.mmagon.entityassist.exceptions;

/**
 * Occurs when the query builder core cannot find a builder from the entity
 */
public class BuilderNotFoundException extends Exception
{
	/**
	 * Occurs when the query builder core cannot find a builder from the entity
	 */
	public BuilderNotFoundException()
	{
		//No config
	}

	/**
	 * Occurs when the query builder core cannot find a builder from the entity
	 *
	 * @param message
	 */
	public BuilderNotFoundException(String message)
	{
		super(message);
	}

	/**
	 * Occurs when the query builder core cannot find a builder from the entity
	 *
	 * @param message
	 * @param cause
	 */
	public BuilderNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Occurs when the query builder core cannot find a builder from the entity
	 *
	 * @param cause
	 */
	public BuilderNotFoundException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Occurs when the query builder core cannot find a builder from the entity
	 *
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public BuilderNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
