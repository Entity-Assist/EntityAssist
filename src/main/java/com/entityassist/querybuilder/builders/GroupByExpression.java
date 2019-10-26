package com.entityassist.querybuilder.builders;

import javax.persistence.metamodel.Attribute;

/**
 * Desginates a group by expression for a query
 */
@SuppressWarnings("unused")
final class GroupByExpression
{
	/**
	 * The attribute to apply
	 */
	private Attribute groupByAttribute;

	/**
	 * A new blank instance
	 */
	GroupByExpression()
	{
		//No config required
	}

	/**
	 * A new group by expression with the given attribute
	 *
	 * @param groupByAttribute
	 * 		The attribute to use
	 */
	GroupByExpression(Attribute groupByAttribute)
	{
		this.groupByAttribute = groupByAttribute;

	}

	/**
	 * Returns the attribute applied for the group bys
	 *
	 * @return The attribute
	 */
	public Attribute getGroupByAttribute()
	{
		return groupByAttribute;
	}

}
