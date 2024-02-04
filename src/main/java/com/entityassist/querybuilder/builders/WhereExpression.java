package com.entityassist.querybuilder.builders;

import com.entityassist.EntityAssistException;
import com.entityassist.enumerations.Operand;


import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Attribute;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

final class WhereExpression<X, Y>
		implements IFilterExpression
{
	private Expression<X> expressionAttribute;
	private Attribute attribute;

	private Operand operand;
	private Object expressionValue;

	private CriteriaBuilder criteriaBuilder;

	WhereExpression()
	{
	}

	WhereExpression(Attribute attribute, Expression<X> expressionAttribute, Operand operand, Object expressionValue)
	{
		this.expressionAttribute = expressionAttribute;
		this.attribute = attribute;
		this.operand = operand;
		this.expressionValue = expressionValue;
	}

	public WhereExpression switchRoot(From root)
	{
		Path attr = (Path) expressionAttribute;
		expressionAttribute = root.get(attribute.getName());
		return this;
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
	public Optional<Predicate> toPredicate(CriteriaBuilder builder)
	{
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
			Logger.getLogger(getClass().getName()).severe("Unable to generate a where clause for the given expression");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Optional<Predicate> processWhereNulls(WhereExpression whereExpression)
	{
		switch (whereExpression.getOperand())
		{
			case Null:
			{
				return Optional.of(getCriteriaBuilder().isNull(expressionAttribute));
			}
			case NotNull:
			{
				return Optional.of(getCriteriaBuilder().isNotNull(expressionAttribute));
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
		Object value = whereExpression.getExpressionValue();
		switch (whereExpression.getOperand())
		{
			case Equals:
			{
				return Optional.of(getCriteriaBuilder().equal(expressionAttribute, value));
			}

			case NotEquals:
			{
				return Optional.of(getCriteriaBuilder().notEqual(expressionAttribute, value));
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
		Object value = whereExpression.getExpressionValue();
		switch (whereExpression.getOperand())
		{
			case Like:
			{
				return Optional.of(getCriteriaBuilder().like((Expression<String>) expressionAttribute, value.toString()));
			}
			case NotLike:
			{
				return Optional.of(getCriteriaBuilder().notLike((Expression<String>) expressionAttribute, value.toString()));
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
		Object value = whereExpression.getExpressionValue();
		switch (whereExpression.getOperand())
		{
			case InList:
			{
				Expression<Object> path;
				path = (Expression<Object>) expressionAttribute;
				CriteriaBuilder.In<Object> in = getCriteriaBuilder().in(path);
				IFilterExpression.buildInObject(in, value);
				return Optional.of(in);
			}
			case NotInList:
			{
				Expression<Object> path = null;
				path = (Expression<Object>) expressionAttribute;
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
		T value = (T) whereExpression.getExpressionValue();
		switch (whereExpression.getOperand())
		{
			case LessThan:
			{
				return Optional.of(getCriteriaBuilder().lessThan((Expression) expressionAttribute, value));
			}
			case LessThanEqualTo:
			{
				return Optional.of(getCriteriaBuilder().lessThanOrEqualTo((Expression) expressionAttribute, value));
			}
			case GreaterThan:
			{
				return Optional.of(getCriteriaBuilder().greaterThan((Expression) expressionAttribute, value));

			}
			case GreaterThanEqualTo:
			{

				return Optional.of(getCriteriaBuilder().greaterThanOrEqualTo((Expression) expressionAttribute, value));
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
	public Expression<X> getExpressionAttribute()
	{
		return expressionAttribute;
	}

	/**
	 * Sets the attribute
	 *
	 * @param expressionAttribute
	 * 		The attribute
	 *
	 * @return This
	 */
	public WhereExpression setExpressionAttribute(Expression<X> expressionAttribute)
	{
		this.expressionAttribute = expressionAttribute;
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
	 * Whatever the value object is
	 *
	 * @return The given value, nullable for isNull
	 */
	public Object getExpressionValue()
	{
		return expressionValue;
	}

	/**
	 * Whatever the value object is
	 *
	 * @param expressionValue
	 * 		The value, null for not and is null
	 *
	 * @return This
	 */
	public WhereExpression setExpressionValue(Y expressionValue)
	{
		this.expressionValue = expressionValue;
		return this;
	}

	/**
	 * Whatever the value object is
	 *
	 * @param expressionValue
	 * 		The value, null for not and is null
	 *
	 * @return This
	 */
	public WhereExpression setExpressionValue(Collection<Y> expressionValue)
	{
		this.expressionValue = expressionValue;
		return this;
	}

	/**
	 * Whatever the value object is
	 *
	 * @param expressionValue
	 * 		The value, null for not and is null
	 *
	 * @return This
	 */
	public WhereExpression setExpressionValue(Y...expressionValue)
	{
		this.expressionValue = expressionValue;
		return this;
	}
}
