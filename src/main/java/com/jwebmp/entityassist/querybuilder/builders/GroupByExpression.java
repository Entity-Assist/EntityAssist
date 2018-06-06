package com.jwebmp.entityassist.querybuilder.builders;

import javax.persistence.metamodel.Attribute;
import java.io.Serializable;

final class GroupByExpression implements Serializable
{
	private static final long serialVersionUID = 1L;

	private transient Attribute attribute;


	GroupByExpression()
	{
	}

	GroupByExpression(Attribute attribute)
	{
		this.attribute = attribute;

	}

	public Attribute getAttribute()
	{
		return attribute;
	}

}
