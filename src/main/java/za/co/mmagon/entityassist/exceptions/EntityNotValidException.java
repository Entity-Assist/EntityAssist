package za.co.mmagon.entityassist.exceptions;

/**
 * Occurs when the query builder core cannot store this entity because of the state
 */
public class EntityNotValidException extends Exception
{
	/**
	 * Occurs when the query builder core cannot store this entity because of the state
	 */
	public EntityNotValidException()
	{
		//No config
	}

	/**
	 * Occurs when the query builder core cannot store this entity because of the state
	 *
	 * @param message
	 */
	public EntityNotValidException(String message)
	{
		super(message);
	}

	/**
	 * Occurs when the query builder core cannot store this entity because of the state
	 *
	 * @param message
	 * @param cause
	 */
	public EntityNotValidException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Occurs when the query builder core cannot store this entity because of the state
	 *
	 * @param cause
	 */
	public EntityNotValidException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Occurs when the query builder core cannot store this entity because of the state
	 *
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public EntityNotValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
