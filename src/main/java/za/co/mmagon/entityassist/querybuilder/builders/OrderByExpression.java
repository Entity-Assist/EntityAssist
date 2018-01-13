package za.co.mmagon.entityassist.querybuilder.builders;

import za.co.mmagon.entityassist.enumerations.OrderByType;

import javax.persistence.metamodel.Attribute;
import java.io.Serializable;

final class OrderByExpression implements Serializable
{
	private static final long serialVersionUID = 1L;

	private transient Attribute attribute;
	private OrderByType orderByType;

	OrderByExpression()
	{
	}

	OrderByExpression(Attribute attribute, OrderByType orderByType)
	{
		this.attribute = attribute;
		this.orderByType = orderByType;
	}

	public Attribute getAttribute()
	{
		return attribute;
	}

	public OrderByExpression setAttribute(Attribute attribute)
	{
		this.attribute = attribute;
		return this;
	}

	public OrderByType getOrderByType()
	{
		return orderByType;
	}

	public OrderByExpression setOrderByType(OrderByType orderByType)
	{
		this.orderByType = orderByType;
		return this;
	}

}
