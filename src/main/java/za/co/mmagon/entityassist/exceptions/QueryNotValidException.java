package za.co.mmagon.entityassist.exceptions;

/**
 * Occurs when the query builder core execution has an error
 */
public class QueryNotValidException extends Exception
{
	/**
	 * Occurs when the query builder core execution has an error
	 */
	public QueryNotValidException()
	{
		//No config
	}

	/**
	 * Occurs when the query builder core execution has an error
	 *
	 * @param message
	 */
	public QueryNotValidException(String message)
	{
		super(message);
	}

	/**
	 * Occurs when the query builder core execution has an error
	 *
	 * @param message
	 * @param cause
	 */
	public QueryNotValidException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Occurs when the query builder core execution has an error
	 *
	 * @param cause
	 */
	public QueryNotValidException(Throwable cause)
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
	public QueryNotValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
