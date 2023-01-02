package com.entityassist.querybuilder.builders;

import com.entityassist.enumerations.*;
import jakarta.persistence.metamodel.*;

/**
 * An order by expression
 */
@SuppressWarnings("unused")
final class OrderByExpression
{
	/**
	 * The attribute to render
	 */
	private Attribute attribute;
	/**
	 * The order by type
	 */
	private OrderByType orderByType;

	/**
	 * A new order by expression
	 */
	OrderByExpression()
	{
		//No config required
	}

	/**
	 * A new order by expression
	 *
	 * @param attribute
	 * 		The attribute to apply on
	 * @param orderByType
	 * 		Order by type
	 */
	OrderByExpression(Attribute attribute, OrderByType orderByType)
	{
		this.attribute = attribute;
		this.orderByType = orderByType;
	}

	/**
	 * Method getAttribute returns the attribute of this OrderByExpression object.
	 * <p>
	 * The attribute to render
	 *
	 * @return the attribute (type Attribute) of this OrderByExpression object.
	 */
	public Attribute getAttribute()
	{
		return attribute;
	}

	/**
	 * Method setAttribute sets the attribute of this OrderByExpression object.
	 * <p>
	 * The attribute to render
	 *
	 * @param attribute
	 * 		the attribute of this OrderByExpression object.
	 *
	 * @return OrderByExpression
	 */
	public OrderByExpression setAttribute(Attribute attribute)
	{
		this.attribute = attribute;
		return this;
	}

	/**
	 * Method getOrderByType returns the orderByType of this OrderByExpression object.
	 * <p>
	 * The order by type
	 *
	 * @return the orderByType (type OrderByType) of this OrderByExpression object.
	 */
	public OrderByType getOrderByType()
	{
		return orderByType;
	}

	/**
	 * Method setOrderByType sets the orderByType of this OrderByExpression object.
	 * <p>
	 * The order by type
	 *
	 * @param orderByType
	 * 		the orderByType of this OrderByExpression object.
	 *
	 * @return OrderByExpression
	 */
	public OrderByExpression setOrderByType(OrderByType orderByType)
	{
		this.orderByType = orderByType;
		return this;
	}

}
