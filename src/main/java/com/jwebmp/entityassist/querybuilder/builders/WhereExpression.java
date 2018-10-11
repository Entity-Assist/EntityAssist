package com.jwebmp.entityassist.querybuilder.builders;

import com.jwebmp.entityassist.enumerations.Operand;
import com.jwebmp.logger.LogFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.logging.Logger;

final class WhereExpression<X, Y>
		implements IFilterExpression
{
	private static final Logger log = LogFactory.getLog("WhereExpression");

	private transient Attribute<X, Y> attribute;

	private Operand operand;
	private transient Object value;

	private transient From root;
	private transient CriteriaBuilder criteriaBuilder;

	WhereExpression()
	{
	}

	WhereExpression(Attribute<X, Y> attribute, Operand operand, Object value)
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

	@Override
	public Optional<Predicate> toPredicate(From entityRoot, CriteriaBuilder builder)
	{
		root = entityRoot;
		criteriaBuilder = builder;
		return processWhereExpression(this);
	}

	private Optional<Predicate> processWhereExpression(WhereExpression whereExpression)
	{
		Optional<Predicate> result;
		result = processWhereNulls(whereExpression);
		if (!result.isPresent())
		{
			result = processWhereEquals(whereExpression);
		}
		if (!result.isPresent())
		{
			result = processWhereLike(whereExpression);
		}
		if (!result.isPresent())
		{
			result = processWhereLists(whereExpression);
		}
		if (!result.isPresent())
		{
			result = processWhereCompare(whereExpression);
		}
		if (!result.isPresent())
		{
			log.severe("Unable to generate a where clause for the given expression");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Optional<Predicate> processWhereNulls(WhereExpression whereExpression)
	{
		Attribute attribute = whereExpression.getAttribute();
		switch (whereExpression.getOperand())
		{
			case Null:
			{
				if (IFilterExpression.isSingularAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().isNull(getRoot().get(SingularAttribute.class.cast(attribute))));
				}
				else if (IFilterExpression.isPluralOrMapAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().isNull(getRoot().get(PluralAttribute.class.cast(attribute))));
				}
			}
			case NotNull:
			{
				if (IFilterExpression.isSingularAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().isNotNull(getRoot().get(SingularAttribute.class.cast(attribute))));
				}
				else if (IFilterExpression.isPluralOrMapAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().isNotNull(getRoot().get(PluralAttribute.class.cast(attribute))));
				}
			}
			default:
			{
				return Optional.empty();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Optional<Predicate> processWhereEquals(WhereExpression whereExpression)
	{
		Attribute attribute = whereExpression.getAttribute();
		Object value = whereExpression.getValue();
		switch (whereExpression.getOperand())
		{
			case Equals:
			{
				if (IFilterExpression.isSingularAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().equal(getRoot().get(SingularAttribute.class.cast(attribute)), value));
				}
				else if (IFilterExpression.isPluralOrMapAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().equal(getRoot().get(PluralAttribute.class.cast(attribute)), value));
				}
			}

			case NotEquals:
			{
				if (IFilterExpression.isSingularAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().notEqual(getRoot().get(SingularAttribute.class.cast(attribute)), value));
				}
				else if (IFilterExpression.isPluralOrMapAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().notEqual(getRoot().get(PluralAttribute.class.cast(attribute)), value));
				}
			}
			default:
			{
				return Optional.empty();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Optional<Predicate> processWhereLike(WhereExpression whereExpression)
	{
		Attribute attribute = whereExpression.getAttribute();
		Object value = whereExpression.getValue();
		switch (whereExpression.getOperand())
		{
			case Like:
			{
				if (IFilterExpression.isSingularAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().like(getRoot().get(SingularAttribute.class.cast(attribute)), value.toString()));
				}
				else if (IFilterExpression.isPluralOrMapAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().like(getRoot().get(PluralAttribute.class.cast(attribute)), value.toString()));
				}
			}
			case NotLike:
			{
				if (IFilterExpression.isSingularAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().notLike(getRoot().get(SingularAttribute.class.cast(attribute)), value.toString()));
				}
				else if (IFilterExpression.isPluralOrMapAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().notLike(getRoot().get(PluralAttribute.class.cast(attribute)), value.toString()));
				}
			}
			default:
			{
				return Optional.empty();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Optional<Predicate> processWhereLists(WhereExpression whereExpression)
	{
		Attribute attribute = whereExpression.getAttribute();
		Object value = whereExpression.getValue();
		switch (whereExpression.getOperand())
		{
			case InList:
			{
				Expression<Object> path = null;
				if (IFilterExpression.isSingularAttribute(attribute))
				{
					path = getRoot().get(SingularAttribute.class.cast(attribute));
				}
				else if (IFilterExpression.isPluralOrMapAttribute(attribute))
				{
					path = getRoot().get(PluralAttribute.class.cast(attribute));
				}
				CriteriaBuilder.In<Object> in = getCriteriaBuilder().in(path);
				IFilterExpression.buildInObject(in, value);
				return Optional.of(in);
			}
			case NotInList:
			{
				Expression<Object> path = null;
				if (IFilterExpression.isSingularAttribute(attribute))
				{
					path = getRoot().get(SingularAttribute.class.cast(attribute));
				}
				else if (IFilterExpression.isPluralOrMapAttribute(attribute))
				{
					path = getRoot().get(PluralAttribute.class.cast(attribute));
				}
				CriteriaBuilder.In<Object> in = getCriteriaBuilder().in(path);
				IFilterExpression.buildInObject(in, value);
				return Optional.of(getCriteriaBuilder().not(in));
			}
			default:
			{
				return Optional.empty();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private <T extends Comparable<T>> Optional<Predicate> processWhereCompare(WhereExpression whereExpression)
	{
		Attribute attribute = whereExpression.getAttribute();
		T value = (T) whereExpression.getValue();
		switch (whereExpression.getOperand())
		{
			case LessThan:
			{
				if (IFilterExpression.isSingularAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().lessThan(getRoot().get((SingularAttribute<X, T>) attribute), value));
				}
			}
			case LessThanEqualTo:
			{
				if (IFilterExpression.isSingularAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().lessThanOrEqualTo(getRoot().get((SingularAttribute<X, T>) attribute), value));
				}
			}
			case GreaterThan:
			{
				if (IFilterExpression.isSingularAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().greaterThan(getRoot().get((SingularAttribute<X, T>) attribute), value));
				}
			}
			case GreaterThanEqualTo:
			{
				if (IFilterExpression.isSingularAttribute(attribute))
				{
					return Optional.of(getCriteriaBuilder().greaterThanOrEqualTo(getRoot().get((SingularAttribute<X, T>) attribute), value));
				}
			}
			default:
			{
				return Optional.empty();
			}
		}
	}

	/**
	 * Returns the attribute associated
	 *
	 * @return The attribute
	 */
	public Attribute<X, Y> getAttribute()
	{
		return attribute;
	}

	/**
	 * Sets the attribute
	 *
	 * @param attribute
	 * 		The attribute
	 *
	 * @return This
	 */
	public WhereExpression setAttribute(Attribute<X, Y> attribute)
	{
		this.attribute = attribute;
		return this;
	}

	/**
	 * The applicable operand to apply
	 *
	 * @return The operand
	 */
	public Operand getOperand()
	{
		return operand;
	}

	/**
	 * Sets the operand to apply
	 *
	 * @param operand
	 * 		The operand
	 *
	 * @return This
	 */
	public WhereExpression setOperand(Operand operand)
	{
		this.operand = operand;
		return this;
	}

	/**
	 * Returns the sent in criteria builder set on toPredicate
	 *
	 * @return The builder
	 */
	private CriteriaBuilder getCriteriaBuilder()
	{
		return criteriaBuilder;
	}

	/**
	 * Gets the assigned root - only set on toPredicate
	 *
	 * @return The root or null
	 */
	private From getRoot()
	{
		return root;
	}

	/**
	 * Whatever the value object is
	 *
	 * @return The given value, nullable for isNull
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Whatever the value object is
	 *
	 * @param value
	 * 		The value, null for not and is null
	 *
	 * @return This
	 */
	public WhereExpression setValue(Object value)
	{
		this.value = value;
		return this;
	}
}
