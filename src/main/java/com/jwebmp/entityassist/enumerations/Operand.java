package com.jwebmp.entityassist.enumerations;

public enum Operand
{
	/**
	 * Where it like, use your own %%
	 */
	Like,
	/**
	 * Where it is not like
	 */
	NotLike,
	/**
	 * Where it is equal to
	 */
	Equals,
	/**
	 * Where it is not equal to
	 */
	NotEquals,
	/**
	 * Where it is null
	 */
	Null,
	/**
	 * Where it is not null
	 */
	NotNull,
	/**
	 * Where it is less than
	 */
	LessThan,
	/**
	 * here it is less than or equal to
	 */
	LessThanEqualTo,
	/**
	 * Greater Than
	 */
	GreaterThan,
	/**
	 * Where it is greater than or equal to
	 */
	GreaterThanEqualTo,
	/**
	 * Where it is in list
	 */
	InList,
	/**
	 * Where it is not in list
	 */
	NotInList,
}
