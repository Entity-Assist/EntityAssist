package za.co.mmagon.entityassist.querybuilder.builders;

import za.co.mmagon.entityassist.enumerations.Operand;

import javax.persistence.metamodel.Attribute;
import java.io.Serializable;

final class WhereExpression implements Serializable
{
	private static final long serialVersionUID = 1L;

	private transient Attribute attribute;
	private Operand operand;
	private Object value;

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
		int result = getAttribute() != null ? getAttribute().hashCode() : 0;
		result = 31 * result + (getOperand() != null ? getOperand().hashCode() : 0);
		result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		WhereExpression that = (WhereExpression) o;

		if (getAttribute() != null ? !getAttribute().equals(that.getAttribute()) : that.getAttribute() != null)
		{
			return false;
		}
		if (getOperand() != that.getOperand())
		{
			return false;
		}
		return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
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
