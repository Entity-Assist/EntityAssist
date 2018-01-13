package za.co.mmagon.entityassist.querybuilder.builders;

import za.co.mmagon.entityassist.enumerations.SelectAggregrate;

import javax.persistence.metamodel.Attribute;
import java.io.Serializable;

final class SelectExpression implements Serializable
{
	private static final long serialVersionUID = 1L;

	private transient Attribute attribute;
	private SelectAggregrate aggregrate;

	SelectExpression()
	{
	}

	SelectExpression(Attribute attribute, SelectAggregrate aggregrate)
	{
		this.attribute = attribute;
		this.aggregrate = aggregrate;
	}

	@Override
	public int hashCode()
	{
		int result = getAttribute() != null ? getAttribute().hashCode() : 0;
		result = 31 * result + (getAggregrate() != null ? getAggregrate().hashCode() : 0);
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

		SelectExpression that = (SelectExpression) o;

		if (getAttribute() != null ? !getAttribute().equals(that.getAttribute()) : that.getAttribute() != null)
		{
			return false;
		}
		return getAggregrate() == that.getAggregrate();
	}

	public Attribute getAttribute()
	{
		return attribute;
	}

	public SelectExpression setAttribute(Attribute attribute)
	{
		this.attribute = attribute;
		return this;
	}

	public SelectAggregrate getAggregrate()
	{
		return aggregrate;
	}

	public void setAggregrate(SelectAggregrate aggregrate)
	{
		this.aggregrate = aggregrate;
	}
}
