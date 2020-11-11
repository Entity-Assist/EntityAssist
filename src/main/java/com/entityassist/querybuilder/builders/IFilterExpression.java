package com.entityassist.querybuilder.builders;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.*;
import jakarta.validation.constraints.NotNull;
import java.util.*;

/**
 * Designates a portion that can be used in a where statement or join statement
 */
@FunctionalInterface
public interface IFilterExpression
{
	/**
	 * Returns if the class is a singular attribute
	 *
	 * @param attribute
	 * 		The attribute to check
	 *
	 * @return boolean
	 */
	static boolean isSingularAttribute(Attribute attribute)
	{
		return SingularAttribute.class.isAssignableFrom(attribute.getClass());
	}

	/**
	 * Returns if the attribute is plural or map
	 *
	 * @param attribute
	 * 		The attribute to check
	 *
	 * @return boolean
	 */
	static boolean isPluralOrMapAttribute(Attribute attribute)
	{
		return isPluralAttribute(attribute) || isMapAttribute(attribute);
	}

	/**
	 * Returns if the class is a singular attribute
	 *
	 * @param attribute
	 * 		The attribute to check
	 *
	 * @return boolean
	 */
	static boolean isPluralAttribute(Attribute attribute)
	{
		return PluralAttribute.class.isAssignableFrom(attribute.getClass());
	}

	/**
	 * Returns if the class is a singular attribute
	 *
	 * @param attribute
	 * 		The attribute to check
	 *
	 * @return boolean
	 */
	static boolean isMapAttribute(Attribute attribute)
	{
		return MapAttribute.class.isAssignableFrom(attribute.getClass());
	}

	/**
	 * Returns if the attribute is plural or map
	 *
	 * @param attribute
	 * 		The attribute to check
	 *
	 * @return boolean
	 */
	@SuppressWarnings("unused")
	static boolean isCollectionAttribute(Attribute attribute)
	{
		return CollectionAttribute.class.isAssignableFrom(attribute.getClass());
	}

	/**
	 * Builds the in cluase query
	 *
	 * @param inClause
	 * 		The in clause to add the values to
	 * @param object
	 * 		The object to come in, Not null
	 *
	 * @return The given collection as a set for the in clause
	 */
	@SuppressWarnings({"unchecked", "UnusedReturnValue"})
	@NotNull
	static Set buildInObject(CriteriaBuilder.In<Object> inClause, @NotNull Object object)
	{
		boolean isArray = object.getClass()
		                        .isArray();
		boolean isCollection = Collection.class.isAssignableFrom(object.getClass());
		boolean isMap = Map.class.isAssignableFrom(object.getClass());

		Set output = new LinkedHashSet();

		if (!(isArray || isCollection || isMap))
		{
			output.add(object);
		}
		else if (isArray)
		{
			//noinspection ConstantConditions
			Collections.addAll(output, (Object[]) object);
		}
		else if (isCollection)
		{
			output.addAll((Collection) object);
		}
		for (Object o : output)
		{
			inClause.value(o);
		}
		return output;
	}

	/**
	 * Produces a predicate for the given filter expression
	 *
	 * @return The predicate to apply
	 */
	Optional<Predicate> toPredicate(CriteriaBuilder builder);

}
