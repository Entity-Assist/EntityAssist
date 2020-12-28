package com.entityassist.enumerations;

/**
 * A select aggregate that can be applied to a given column
 */
public enum SelectAggregrate
{
	/**
	 * Just select the thing
	 */
	None(""),
	/**
	 * Minimum
	 */
	Min("MIN"),
	/**
	 * Maximum
	 */
	Max("MAX"),
	/**
	 * Count
	 */
	Count("COUNT"),
	/**
	 * Count Distinct
	 */
	CountDistinct("COUNT(DISTINCT"),
	/**
	 * Sum (whatever the db returns)
	 */
	Sum("SUM"),
	/**
	 * Sum returned as a long
	 */
	SumLong("SUM"),
	/**
	 * Sum Double
	 */
	SumDouble("SUM"),
	/**
	 * Average
	 */
	Avg("AVG"),
;
	private String selectString;
	
	SelectAggregrate(String selectString)
	{
		this.selectString = selectString;
	}
	
	public String getSelectString()
	{
		return selectString;
	}
}
