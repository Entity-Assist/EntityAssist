package com.entityassist.querybuilder.builders;

import com.entityassist.enumerations.GroupedFilterType;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import java.util.*;

/**
 * A grouped filter expression (bla bla bla) and (bla bla group 2)
 */
@SuppressWarnings("WeakerAccess")
public class GroupedExpression
		implements IFilterExpression
{
	/**
	 * The final set of expressions
	 */
	private final Set<IFilterExpression> filterExpressions = new LinkedHashSet<>();

	/**
	 * The type of filter to apply
	 */
	private GroupedFilterType groupedFilterType;

	/**
	 * Returns the grouped filter type
	 *
	 * @return The given type
	 */
	@SuppressWarnings("unused")
	public GroupedFilterType getGroupedFilterType()
	{
		return groupedFilterType;
	}

	/**
	 * Method setGroupedFilterType sets the groupedFilterType of this GroupedExpression object.
	 * <p>
	 * The type of filter to apply
	 *
	 * @param groupedFilterType
	 * 		the groupedFilterType of this GroupedExpression object.
	 *
	 * @return GroupedExpression
	 */
	@SuppressWarnings("UnusedReturnValue")
	public GroupedExpression setGroupedFilterType(GroupedFilterType groupedFilterType)
	{
		this.groupedFilterType = groupedFilterType;
		return this;
	}

	/**
	 * Produces a predicate for the given filter expression
	 *
	 *
	 * @return The predicate to apply
	 */
	@Override
	public Optional<Predicate> toPredicate(CriteriaBuilder builder)
	{
		List<Predicate> wheres = new ArrayList<>();
		for (IFilterExpression filterExpression : getFilterExpressions())
		{
			Optional<Predicate> op = filterExpression.toPredicate(builder);
			op.ifPresent(wheres::add);
		}
		if (wheres.isEmpty())
		{
			return Optional.empty();
		}
		else
		{
			Predicate[] preds = new Predicate[wheres.size()];
			wheres.toArray(preds);
			Predicate groupPredicate = builder.or(preds);
			return Optional.of(groupPredicate);
		}
	}

	/**
	 * Method getFilterExpressions returns the filterExpressions of this GroupedExpression object.
	 * <p>
	 * The final set of expressions
	 *
	 * @return the filterExpressions (type Set IFilterExpression ) of this GroupedExpression object.
	 */
	public Set<IFilterExpression> getFilterExpressions()
	{
		return filterExpressions;
	}
}
