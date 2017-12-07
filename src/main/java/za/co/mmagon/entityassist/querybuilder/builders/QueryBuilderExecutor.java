package za.co.mmagon.entityassist.querybuilder.builders;

import za.co.mmagon.entityassist.BaseEntity;
import za.co.mmagon.entityassist.CoreEntity;
import za.co.mmagon.entityassist.enumerations.OrderByType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class QueryBuilderExecutor<J extends QueryBuilderExecutor<J, E, I>, E extends BaseEntity<E, J, I>, I extends Serializable>
		extends DefaultQueryBuilder<J, E, I>
{

	private static final Logger log = Logger.getLogger(QueryBuilderExecutor.class.getName());
	/**
	 * Marks if this query is selected
	 */
	private boolean selected;

	@SuppressWarnings("unchecked")
	public Long getCount()
	{
		if (!selected)
		{
			select();
		}

		TypedQuery<Long> query = getEntityManager().createQuery(getCriteriaQuery());

		Long j = null;
		try
		{
			j = query.getSingleResult();
			return j;
		}
		catch (NoResultException nre)
		{
			log.log(Level.WARNING, "Couldn''t find object with name : " + getEntityClass().getName() + "}\n", nre);
			return 0L;
		}
	}

	/**
	 * Prepares the select statement
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	private J select()
	{
		List<Predicate> allWheres = new ArrayList<>();
		allWheres.addAll(getFilters());

		Predicate[] preds = new Predicate[allWheres.size()];
		preds = allWheres.toArray(preds);

		CriteriaQuery<E> cq = getCriteriaQuery();

		getCriteriaQuery().where(preds);

		if (!getGroupBys().isEmpty())
		{
			for (Expression p : getGroupBys())
			{
				cq.groupBy(p);
			}
		}

		if (!getHaving().isEmpty())
		{
			for (Expression expression : getHaving())
			{
				cq.having(expression);
			}
		}

		if (!getOrderBys().isEmpty())
		{
			getOrderBys().forEach((key, value) -> processOrderBys(key, value, cq));
		}
		if (getSelections().isEmpty())
		{
			getCriteriaQuery().select(getRoot());
		}
		else
		{
			getSelections().forEach(a -> getCriteriaQuery().select(a));
		}
		selected = true;
		return (J) this;
	}

	@SuppressWarnings("unchecked")
	private void processOrderBys(Attribute key, OrderByType value, CriteriaQuery cq)
	{
		switch (value)
		{

			case DESC:
			{
				if (isSingularAttribute(key))
				{
					cq.orderBy(getCriteriaBuilder().desc(getRoot().get(SingularAttribute.class.cast(key))));
				}
				else if (isPluralOrMapAttribute(key))
				{
					cq.orderBy(getCriteriaBuilder().desc(getRoot().get(PluralAttribute.class.cast(key))));
				}
				break;
			}
			case ASC:
			default:
			{
				if (isSingularAttribute(key))
				{
					cq.orderBy(getCriteriaBuilder().asc(getRoot().get(SingularAttribute.class.cast(key))));
				}
				else if (isPluralOrMapAttribute(key))
				{
					cq.orderBy(getCriteriaBuilder().asc(getRoot().get(PluralAttribute.class.cast(key))));
				}
				break;
			}
		}
	}

	/**
	 * Returns a non-distinct list and returns an empty optional if a non-unique-result exception is thrown
	 *
	 * @return
	 */
	public Optional<E> get()
	{
		return get(false);
	}

	public abstract EntityManager getEntityManager();

	/**
	 * Returns a list (distinct or not) and returns an empty optional if returns a list, or will simply return the first result found from a list with the same criteria
	 *
	 * @param returnFirst
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Optional<E> get(boolean returnFirst)
	{
		if (!selected)
		{
			select();
		}
		TypedQuery<E> query = getEntityManager().createQuery(getCriteriaQuery());
		E j = null;
		try
		{
			j = query.getSingleResult();
			j.setFake(false);
			return Optional.of(j);
		}
		catch (NoResultException nre)
		{
			log.log(Level.WARNING, "Couldn't find object for class : " + getEntityClass().getName() + "}\n");
			log.log(Level.FINEST, "Couldn't find object : " + getEntityClass().getName() + "}\n", nre);
			return Optional.empty();
		}
		catch (NonUniqueResultException nure)
		{
			log.log(Level.WARNING, "Get didn't return a single result\n");
			log.log(Level.FINEST, "Couldn't find object for class : " + getEntityClass().getName() + "}\n", nure);
			if (returnFirst)
			{
				List<E> returnedList = query.getResultList();
				j = returnedList.get(0);
				j.setFake(false);
				return Optional.of(j);
			}
			else
			{
				return Optional.empty();
			}
		}
	}

	/**
	 * Returns a list of entities from a distinct or non distinct list
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<E> getAll()
	{
		if (!selected)
		{
			select();
		}
		TypedQuery<E> query = getEntityManager().createQuery(getCriteriaQuery());

		if (getMaxResults() != null)
		{
			query.setMaxResults(getMaxResults());
		}
		if (getFirstResults() != null)
		{
			query.setFirstResult(getFirstResults());
		}
		List<E> j;
		j = query.getResultList();
		for (Object j1 : j)
		{
			CoreEntity wct = (CoreEntity) j1;
			wct.setFake(false);
		}
		return j;
	}
}
