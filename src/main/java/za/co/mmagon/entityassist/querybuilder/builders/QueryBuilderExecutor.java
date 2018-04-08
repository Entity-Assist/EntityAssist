package za.co.mmagon.entityassist.querybuilder.builders;

import za.co.mmagon.entityassist.BaseEntity;
import za.co.mmagon.entityassist.enumerations.OrderByType;
import za.co.mmagon.guiceinjection.Pair;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
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
	/**
	 * Whether or not to detach after select
	 */
	private boolean detach;
	/**
	 * Force no lock on the query built
	 */
	private boolean noLock;
	/**
	 * If the first result must be returned from a list
	 */
	private boolean returnFirst;
	/**
	 * Specifies the query cache to be used.
	 * Cache Region/
	 */
	private Pair<String, String> queryCache;
	/**
	 * The actual cache name
	 */
	private String cacheName;
	/**
	 * If this query must refresh the cache
	 */
	private boolean refreshCache;

	@SuppressWarnings("unchecked")
	public Long getCount()
	{
		if (!selected)
		{
			selectCount();
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
		if (!selected)
		{
			getJoins().forEach(this::processJoins);
			if (getCriteriaDelete() == null)
			{
				processCriteriaQuery();
			}
			else if (getCriteriaDelete() != null && getCriteriaUpdate() == null)
			{
				CriteriaDelete cq = getCriteriaDelete();
				List<Predicate> allWheres = new ArrayList<>(getFilters());
				Predicate[] preds = new Predicate[allWheres.size()];
				preds = allWheres.toArray(preds);
				cq.where(preds);
			}
			else if (getCriteriaUpdate() != null)
			{
				CriteriaUpdate cq = getCriteriaUpdate();
				List<Predicate> allWheres = new ArrayList<>(getFilters());
				Predicate[] preds = new Predicate[allWheres.size()];
				preds = allWheres.toArray(preds);
				cq.where(preds);
			}
		}
		selected = true;
		return (J) this;
	}

	@Override
	public abstract EntityManager getEntityManager();

	@SuppressWarnings("unchecked")
	private void processCriteriaQuery()
	{
		CriteriaQuery<E> cq = getCriteriaQuery();
		List<Predicate> allWheres = new ArrayList<>(getFilters());
		Predicate[] preds = new Predicate[allWheres.size()];
		preds = allWheres.toArray(preds);
		getCriteriaQuery().where(preds);

		for (Expression p : getGroupBys())
		{
			cq.groupBy(p);
		}

		for (Expression expression : getHaving())
		{
			cq.having(expression);
		}

		if (!getOrderBys().isEmpty())
		{
			getOrderBys().forEach((key, value) -> processOrderBys(key, value, cq));
		}

		if (getSelections().isEmpty())
		{
			getCriteriaQuery().select(getRoot());
		}
		else if (getSelections().size() > 1)
		{
			if (getConstruct() != null)
			{
				ArrayList<Selection> aS = new ArrayList(getSelections());
				Selection[] selections = aS.toArray(new Selection[0]);
				CompoundSelection cs = getCriteriaBuilder().construct(getConstruct(), selections);
				getCriteriaQuery().select(cs);
			}
			else
			{
				getCriteriaQuery().multiselect(new ArrayList(getSelections()));
			}
		}
		else
		{
			getSelections().forEach(a -> getCriteriaQuery().select(a));
		}
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
	 * Processors the join section
	 *
	 * @param executor
	 */
	private void processJoins(JoinExpression executor)
	{
		Attribute value = executor.getAttribute();
		JoinType jt = executor.getJoinType();
		Join join = getRoot().join((SingularAttribute) value, jt);

		QueryBuilderExecutor key = executor.getExecutor();
		if (key != null)
		{
			key.reset(join);
			key.select();
			getSelections().addAll(key.getSelections());
			getFilters().addAll(key.getFilters());
			getOrderBys().putAll(key.getOrderBys());
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

	/**
	 * Returns the first result returned
	 *
	 * @param returnFirst
	 *
	 * @return
	 */
	@NotNull
	public Optional<E> get(boolean returnFirst)
	{
		this.returnFirst = returnFirst;
		return get(getEntityClass());
	}

	/**
	 * Returns a list (distinct or not) and returns an empty optional if returns a list, or will simply return the first result found from
	 * a list with the same criteria
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <T> Optional<T> get(@NotNull @SuppressWarnings("unused") Class<T> asType)
	{
		if (!selected)
		{
			select();
		}
		TypedQuery<T> query = getEntityManager().createQuery(getCriteriaQuery());
		T j = null;
		try
		{
			j = query.getSingleResult();
			if (BaseEntity.class.isAssignableFrom(j.getClass()))
			{
				BaseEntity.class.cast(j)
				                .setFake(false);
			}
			if (detach)
			{
				getEntityManager().detach(j);
			}
			return Optional.of(j);
		}
		catch (NoResultException nre)
		{
			log.log(Level.FINER, "Couldn't find object : " + getEntityClass().getName() + "}", nre);
			return Optional.empty();
		}
		catch (NonUniqueResultException nure)
		{
			log.log(Level.FINER, "Non Unique Result. Couldn't find object for class : " + getEntityClass().getName() + "}", nure);
			if (returnFirst)
			{
				List<T> returnedList = query.getResultList();
				j = returnedList.get(0);
				if (BaseEntity.class.isAssignableFrom(j.getClass()))
				{
					BaseEntity.class.cast(j)
					                .setFake(false);
				}
				if (detach)
				{
					getEntityManager().detach(j);
				}
				return Optional.of(j);
			}
			else
			{
				return Optional.empty();
			}
		}
	}

	/**
	 * Assigns a cache for 2nd level caching to this query
	 *
	 * @param region
	 * @param prefix
	 * @param cacheName
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J cacheQuery(String region, String prefix, String cacheName, boolean refreshCache)
	{
		this.refreshCache = refreshCache;
		queryCache = new Pair<>(region, prefix);
		this.cacheName = cacheName;
		return (J) this;
	}


	/**
	 * Returns a list of entities from a distinct or non distinct list
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<E> getAll()
	{
		return getAll(getEntityClass());
	}

	/**
	 * Returns the list as the selected class type (for when specifying single select columns)
	 *
	 * @param returnClassType
	 * @param <T>
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <T> List<T> getAll(@SuppressWarnings("unused") Class<T> returnClassType)
	{
		if (!selected)
		{
			select();
		}
		TypedQuery<T> query = getEntityManager().createQuery(getCriteriaQuery());
		if (getMaxResults() != null)
		{
			query.setMaxResults(getMaxResults());
		}
		if (getFirstResults() != null)
		{
			query.setFirstResult(getFirstResults());
		}
		List<T> j;
		j = query.getResultList();
		if (!j.isEmpty())
		{
			if (detach)
			{
				getEntityManager().detach(j);
			}
			if (BaseEntity.class.isAssignableFrom(j.get(0)
			                                       .getClass()))
			{
				BaseEntity.class.cast(j.get(0))
				                .setFake(false);
			}
		}
		return j;
	}

	/**
	 * Force No Lock on the Criteria Query
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J noLock()
	{
		noLock = true;
		return (J) this;
	}

	/**
	 * Sets whether or not to detach the selected entity/ies
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J detach()
	{
		detach = true;
		return (J) this;
	}

	/**
	 * Returns the number of rows affected by the delete.
	 * <p>
	 * Bulk Delete Operation
	 * <p>
	 * WARNING : Be very careful if you haven't added a filter this will truncate the table or throw a unsupported exception if no filters.
	 *
	 * @return
	 */
	public int delete()
	{
		if (getFilters().isEmpty())
		{
			throw new UnsupportedOperationException("Calling the delete method with no filters. This will truncate the table. Rather call truncate()");
		}
		CriteriaDelete deletion = getCriteriaBuilder().createCriteriaDelete(getEntityClass());
		setCriteriaDelete(deletion);
		select();
		checkForTransaction();
		int results = getEntityManager().createQuery(deletion)
		                                .executeUpdate();
		commitTransaction();
		return results;
	}

	/**
	 * Removes the entity using the entity manager
	 *
	 * @return
	 */
	public E deleteEntity(E entity)
	{
		checkForTransaction();
		getEntityManager().remove(entity);
		commitTransaction();
		return entity;
	}

	/**
	 * Deletes the given entity through the entity manager
	 *
	 * @param entity
	 *
	 * @return
	 */
	public E delete(E entity)
	{
		checkForTransaction();
		getEntityManager().remove(entity);
		commitTransaction();
		return entity;
	}

	/**
	 * Returns the number of rows affected by the delete.
	 * WARNING : Be very careful if you haven't added a filter this will truncate the table or throw a unsupported exception if no filters.
	 *
	 * @return
	 */
	public int truncate()
	{
		CriteriaDelete deletion = getCriteriaBuilder().createCriteriaDelete(getEntityClass());
		setCriteriaDelete(deletion);
		getFilters().clear();
		select();
		checkForTransaction();
		int results = getEntityManager().createQuery(deletion)
		                                .executeUpdate();
		commitTransaction();
		return results;
	}

	/**
	 * Returns the number of rows or an unsupported exception if there are no filters added
	 *
	 * @param updateFields
	 *
	 * @return
	 */
	public int bulkUpdate(E updateFields)
	{
		if (getFilters().isEmpty())
		{
			throw new UnsupportedOperationException("Calling the bulk update method with no filters. This will update the entire table.");
		}
		CriteriaUpdate update = getCriteriaBuilder().createCriteriaUpdate(getEntityClass());
		Map<String, Object> updateFieldMap = getUpdateFieldMap(updateFields);
		updateFieldMap.forEach(update::set);
		select();
		checkForTransaction();
		int results = getEntityManager().createQuery(update)
		                                .executeUpdate();
		commitTransaction();
		return results;
	}

	/**
	 * Goes through the object looking for fields, returns a set where the field name is mapped to the object
	 *
	 * @param updateFields
	 *
	 * @return
	 */
	protected Map<String, Object> getUpdateFieldMap(E updateFields)
	{
		Map<String, Object> map = new HashMap<>();
		Field[] fields = updateFields.getClass()
		                             .getDeclaredFields();
		Arrays.asList(fields)
		      .forEach(a ->
		               {
			               a.setAccessible(true);
			               try
			               {
				               Object o = a.get(updateFields);
				               if (o != null)
				               {
					               map.put(a.getName(), o);
				               }
			               }
			               catch (IllegalAccessException e)
			               {
				               log.log(Level.SEVERE, "Unable to determine if field is populated or not", e);
			               }
		               });
		return map;
	}
}
