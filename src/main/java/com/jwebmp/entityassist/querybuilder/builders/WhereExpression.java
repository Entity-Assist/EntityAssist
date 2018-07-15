package com.jwebmp.entityassist.querybuilder.builders;

import com.jwebmp.entityassist.enumerations.Operand;

import javax.persistence.metamodel.Attribute;
import java.io.Serializable;

final class WhereExpression implements Serializable
{
	private static final long serialVersionUID = 1L;

	private transient Attribute attribute;
	private Operand operand;
	private transient Object value;

	WhereExpression()
	{
	}

	WhereExpression(Attribute attribute, Operand operand, Object value)
	{
		this.attribute = attribute;
		this.operand = operand;
		this.value = value;
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}

	public Attribute getAttribute()
	{
		return attribute;
	}

	public WhereExpression setAttribute(Attribute attribute)
	{
		this.attribute = attribute;
		return this;
	}

	public Operand getOperand()
	{
		return operand;
	}

	public WhereExpression setOperand(Operand operand)
	{
		this.operand = operand;
		return this;
	}

	public Object getValue()
	{
		return value;
	}

	public WhereExpression setValue(Object value)
	{
		this.value = value;
		return this;
	}
}
