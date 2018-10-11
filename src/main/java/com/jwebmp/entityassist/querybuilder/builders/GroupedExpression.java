package com.jwebmp.entityassist.querybuilder.builders;

import com.jwebmp.entityassist.enumerations.GroupedFilterType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.util.*;

public class GroupedExpression
		implements IFilterExpression
{
	private final Set<IFilterExpression> filterExpressions = new LinkedHashSet<>();

	private GroupedFilterType groupedFilterType;

	public GroupedFilterType getGroupedFilterType()
	{
		return groupedFilterType;
	}

	public GroupedExpression setGroupedFilterType(GroupedFilterType groupedFilterType)
	{
		this.groupedFilterType = groupedFilterType;
		return this;
	}

	@Override
	public Optional<Predicate> toPredicate(From entityRoot, CriteriaBuilder builder)
	{
		List<Predicate> wheres = new ArrayList<>();
		for (IFilterExpression filterExpression : getFilterExpressions())
		{
			Optional<Predicate> op = filterExpression.toPredicate(entityRoot, builder);
			if (op.isPresent())
			{
				wheres.add(op.get());
			}
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

	public Set<IFilterExpression> getFilterExpressions()
	{
		return filterExpressions;
	}
}
