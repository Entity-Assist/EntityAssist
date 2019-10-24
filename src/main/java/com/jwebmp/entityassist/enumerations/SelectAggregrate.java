package com.guicedee.entityassist.enumerations;

/**
 * A select aggregate that can be applied to a given column
 */
public enum SelectAggregrate
{
	/**
	 * Just select the thing
	 */
	None,
	/**
	 * Minimum
	 */
	Min,
	/**
	 * Maximum
	 */
	Max,
	/**
	 * Count
	 */
	Count,
	/**
	 * Count Distinct
	 */
	CountDistinct,
	/**
	 * Sum (whatever the db returns)
	 */
	Sum,
	/**
	 * Sum returned as a long
	 */
	SumLong,
	/**
	 * Sum Double
	 */
	SumDouble,
	/**
	 * Average
	 */
	Avg,

}
