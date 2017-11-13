package za.co.mmagon.entityassist.exceptions;

/**
 * Occurs when the query builder core execution has an error
 */
public class ConstraintsNotMetException extends Exception
{
	/**
	 * Occurs when the query builder core execution has an error
	 */
	public ConstraintsNotMetException()
	{
		//No config
	}

	/**
	 * Occurs when the query builder core execution has an error
	 *
	 * @param message
	 */
	public ConstraintsNotMetException(String message)
	{
		super(message);
	}

	/**
	 * Occurs when the query builder core execution has an error
	 *
	 * @param message
	 * @param cause
	 */
	public ConstraintsNotMetException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Occurs when the query builder core execution has an error
	 *
	 * @param cause
	 */
	public ConstraintsNotMetException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Occurs when the query builder core execution has an error
	 *
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ConstraintsNotMetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
