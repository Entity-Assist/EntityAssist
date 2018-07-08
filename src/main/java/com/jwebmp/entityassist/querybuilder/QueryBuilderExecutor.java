package com.jwebmp.entityassist.querybuilder;

import com.google.inject.persist.Transactional;
import com.jwebmp.entityassist.BaseEntity;
import com.jwebmp.entityassist.enumerations.OrderByType;
import com.jwebmp.entityassist.querybuilder.builders.DefaultQueryBuilder;
import com.jwebmp.entityassist.querybuilder.builders.JoinExpression;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
			if (!isDelete() && !isUpdate())
			{
				processCriteriaQuery();
			}
			else if (isDelete())
			{
				CriteriaDelete cq = getCriteriaDelete();
				List<Predicate> allWheres = new ArrayList<>(getFilters());
				Predicate[] preds = new Predicate[allWheres.size()];
				preds = allWheres.toArray(preds);
				cq.where(preds);
			}
			else if (isUpdate())
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
	 * Returns the number of rows or an unsupported exception if there are no filters added
	 *
	 * @param updateFields
	 *
	 * @return
	 */
	@Transactional
	public int bulkUpdate(E updateFields, boolean allowEmpty)
	{
		if (!allowEmpty && getFilters().isEmpty())
		{
			throw new UnsupportedOperationException("Calling the bulk update method with no filters. This will update the entire table.");
		}
		CriteriaUpdate update = getCriteriaUpdate();
		Map<SingularAttribute, Object> updateFieldMap = getUpdateFieldMap(updateFields);
		if (updateFieldMap.isEmpty())
		{
			log.warning("Nothing to update, ignore bulk update");
			return 0;
		}
		for (Map.Entry<SingularAttribute, Object> entries : updateFieldMap.entrySet())
		{
			SingularAttribute<?, ?> attributeName = entries.getKey();
			Object value = entries.getValue();
			update.set(attributeName.getName(), value);
		}
		select();
		int results = getEntityManager().createQuery(update)
		                                .executeUpdate();
		return results;
	}

	/**
	 * Processors the join section
	 *
	 * @param executor
	 */
	@SuppressWarnings("unchecked")
	protected void processJoins(JoinExpression executor)
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
		return getEntityManager().createQuery(deletion)
		                         .executeUpdate();
	}

	/**
	 * Removes the entity using the entity manager
	 *
	 * @return
	 */
	public E deleteEntity(E entity)
	{
		getEntityManager().remove(entity);
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
		getEntityManager().remove(entity);
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
		return getEntityManager().createQuery(deletion)
		                         .executeUpdate();
	}

	/**
	 * Goes through the object looking for fields, returns a set where the field name is mapped to the object
	 *
	 * @param updateFields
	 *
	 * @return
	 */
	protected Map<SingularAttribute, Object> getUpdateFieldMap(E updateFields)
	{
		Map<SingularAttribute, Object> map = new HashMap<>();
		List<Field> fieldList = allFields(updateFields.getClass(), new ArrayList<>());

		for (Field field : fieldList)
		{
			if (Modifier.isAbstract(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()) || field.isAnnotationPresent(
					Id.class))
			{
				continue;
			}
			field.setAccessible(true);
			try
			{
				Object o = field.get(updateFields);
				if (o != null)
				{
					String fieldName = field.getName();
					Path<SingularAttribute> at = getRoot().get(fieldName);
					SingularAttribute at2 = (SingularAttribute) getRoot().get(fieldName);
					map.put(at2, o);
				}
			}
			catch (IllegalAccessException e)
			{
				log.log(Level.SEVERE, "Unable to determine if field is populated or not", e);
			}
		}
		return map;
	}

	private List<Field> allFields(Class<?> object, List<Field> fieldList)
	{
		fieldList.addAll(Arrays.asList(object.getDeclaredFields()));
		if (object.getSuperclass() != Object.class)
		{
			allFields(object.getSuperclass(), fieldList);
		}
		return fieldList;
	}
}
