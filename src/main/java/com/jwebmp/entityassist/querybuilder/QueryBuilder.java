package com.jwebmp.entityassist.querybuilder;

import com.google.common.base.Strings;
import com.jwebmp.entityassist.BaseEntity;
import com.jwebmp.entityassist.enumerations.OrderByType;
import com.jwebmp.entityassist.querybuilder.builders.DefaultQueryBuilder;
import com.jwebmp.entityassist.querybuilder.builders.JoinExpression;
import com.jwebmp.logger.LogFactory;

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

import static com.jwebmp.entityassist.querybuilder.builders.IFilterExpression.*;

@SuppressWarnings("unchecked")
public abstract class QueryBuilder<J extends QueryBuilder<J, E, I>, E extends BaseEntity<E, J, I>, I extends Serializable>
		extends DefaultQueryBuilder<J, E, I>
{
	/**
	 * The logger
	 */
	private static final Logger log = LogFactory.getLog(QueryBuilder.class.getName());
	/**
	 * Marks if this query is selected
	 */
	private boolean selected;
	/**
	 * Whether or not to detach after select
	 */
	private boolean detach;
	/**
	 * If the first result must be returned from a list
	 */
	private boolean returnFirst;

	/**
	 * Returns a long of the count for the given builder
	 *
	 * @return Long of results - generally never null
	 */
	public Long getCount()
	{
		if (!selected)
		{
			selectCount();
			select();
		}
		TypedQuery<Long> query = getEntityManager().createQuery(getCriteriaQuery());
		applyCache(query);
		Long j;
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
	 * @return This
	 */
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

	/**
	 * Returns the assigned entity manager
	 *
	 * @return The entity manager to use for this run
	 */
	@Override
	public abstract EntityManager getEntityManager();

	/**
	 * Physically applies the cache attributes to the query
	 * <p>
	 * Adds cacheable, cache region, and sets persistence cache retrieve mode as use, and store mode as use
	 *
	 * @param query
	 * 		The query to apply to
	 */
	private void applyCache(TypedQuery query)
	{
		if (!Strings.isNullOrEmpty(getCacheName()))
		{
			query.setHint("org.hibernate.cacheable", true);
			query.setHint("org.hibernate.cacheRegion", getCacheRegion());
			query.setHint("javax.persistence.cache.retrieveMode", "USE");
			query.setHint("javax.persistence.cache.storeMode", "USE");
		}
	}

	/**
	 * Builds up the criteria query to perform (Criteria Query Only)
	 */
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

		for (Expression expression : getHavingExpressions())
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

	/**
	 * Processes the order bys into the given query
	 *
	 * @param key
	 * 		The attribute to apply
	 * @param value
	 * 		The value to use
	 * @param cq
	 * 		The criteria query to apply to
	 */
	private void processOrderBys(Attribute key, OrderByType value, CriteriaQuery cq)
	{
		switch (value)
		{

			case DESC:
			{
				if (isSingularAttribute(key))
				{
					cq.orderBy(getCriteriaBuilder().desc(getRoot().get((SingularAttribute) key)));
				}
				else if (isPluralOrMapAttribute(key))
				{
					cq.orderBy(getCriteriaBuilder().desc(getRoot().get((PluralAttribute) key)));
				}
				break;
			}
			case ASC:
			default:
			{
				if (isSingularAttribute(key))
				{
					cq.orderBy(getCriteriaBuilder().asc(getRoot().get((SingularAttribute) key)));
				}
				else if (isPluralOrMapAttribute(key))
				{
					cq.orderBy(getCriteriaBuilder().asc(getRoot().get((PluralAttribute) key)));
				}
				break;
			}
		}
	}

	/**
	 * Returns the number of rows or an unsupported exception if there are no filters added
	 *
	 * @param updateFields
	 * 		Allows to use the Criteria Update to run a bulk update on the table
	 *
	 * @return number of rows updated
	 */
	@SuppressWarnings("UnusedReturnValue")
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
		return getEntityManager().createQuery(update)
		                         .executeUpdate();
	}

	/**
	 * Processors the join section
	 *
	 * @param executor
	 * 		Processes the joins into the expression
	 */
	private void processJoins(JoinExpression executor)
	{
		Attribute value = executor.getAttribute();
		JoinType jt = executor.getJoinType();
		Join join = getRoot().join((SingularAttribute) value, jt);

		QueryBuilder key = executor.getExecutor();
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
	 * @return An optional of the result
	 */
	public Optional<E> get()
	{
		return get(false);
	}

	/**
	 * Returns the first result returned
	 *
	 * @param returnFirst
	 * 		If the first should be returned in the instance of many results
	 *
	 * @return Optional of the required object
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
	 * @return Optional of the given class type (which should be a select column)
	 */
	@NotNull
	public <T> Optional<T> get(@NotNull Class<T> asType)
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
		applyCache(query);
		T j;
		try
		{
			j = query.getSingleResult();
			if (j == null)
			{
				return Optional.empty();
			}
			if (BaseEntity.class.isAssignableFrom(j.getClass()))
			{
				((BaseEntity) j)
						.setFake(false);
			}
			if (detach)
			{
				getEntityManager().detach(j);
			}
			return Optional.of(j);
		}
		catch (NoResultException | NullPointerException nre)
		{
			log.log(Level.FINER, "Couldn't find object : " + getEntityClass().getName() + "}", nre);
			return Optional.empty();
		}
		catch (NonUniqueResultException nure)
		{
			log.log(Level.FINER, "Non Unique Result. Couldn't find object for class : " + getEntityClass().getName() + "}", nure);
			if (isReturnFirst())
			{
				query.setMaxResults(1);
				List<T> returnedList = query.getResultList();
				j = returnedList.get(0);
				if (j != null)
				{
					if (BaseEntity.class.isAssignableFrom(j.getClass()))
					{
						((BaseEntity) j)
								.setFake(false);
					}
					if (detach)
					{
						getEntityManager().detach(j);
					}
				}
				return Optional.ofNullable(j);
			}
			else
			{
				return Optional.empty();
			}
		}
	}

	/**
	 * If this builder is configured to return the first row
	 *
	 * @return If the first record must be returned
	 */
	@SuppressWarnings("WeakerAccess")
	public boolean isReturnFirst()
	{
		return returnFirst;
	}

	/**
	 * If a Non-Unique Exception is thrown re-run the query as a list and return the first item
	 *
	 * @param returnFirst
	 * 		if must return first
	 *
	 * @return J
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setReturnFirst(boolean returnFirst)
	{
		this.returnFirst = returnFirst;
		return (J) this;
	}

	/**
	 * Goes through the object looking for fields, returns a set where the field name is mapped to the object
	 *
	 * @param updateFields
	 * 		Returns a map of field to update with the values
	 *
	 * @return A map of SingularAttribute and its object type
	 */
	@NotNull
	public Map<SingularAttribute, Object> getUpdateFieldMap(E updateFields)
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
					String classPathReferenceName = updateFields.getClass()
					                                            .getName() + "_";
					Class clazz = Class.forName(classPathReferenceName);
					Field f = clazz.getField(fieldName);
					SingularAttribute at = (SingularAttribute) f.get(null);
					map.put(at, o);
				}
			}
			catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException e)
			{
				log.log(Level.SEVERE, "Unable to determine if field is populated or not", e);
			}
		}
		return map;
	}

	/**
	 * Returns a list of entities from a distinct or non distinct list
	 *
	 * @return A list of entities returned
	 */
	public List<E> getAll()
	{
		return getAll(getEntityClass());
	}

	/**
	 * Returns the list as the selected class type (for when specifying single select columns)
	 *
	 * @param returnClassType
	 * 		Returns a list of a given column
	 * @param <T>
	 * 		The type of the column returned
	 *
	 * @return The type of the column returned
	 */
	@NotNull
	public <T> List<T> getAll(Class<T> returnClassType)
	{
		if (!selected)
		{
			select();
		}
		TypedQuery<T> query = getEntityManager().createQuery(getCriteriaQuery());
		applyCache(query);
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
				((BaseEntity) j.get(0))
						.setFake(false);
			}
		}
		return j;
	}

	/**
	 * Sets whether or not to detach the selected entity/ies
	 *
	 * @return This
	 */
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
	 * @return number of results deleted
	 */
	public int delete()
	{
		if (getFilters().isEmpty())
		{
			throw new UnsupportedOperationException("Calling the delete method with no filters. This will truncate the table. Rather call truncate()");
		}
		CriteriaDelete deletion = getCriteriaBuilder().createCriteriaDelete(getEntityClass());
		reset(deletion.from(getEntityClass()));
		setCriteriaDelete(deletion);
		select();
		return getEntityManager().createQuery(deletion)
		                         .executeUpdate();
	}

	/**
	 * Deletes the given entity through the entity manager
	 *
	 * @param entity
	 * 		Deletes through the entity manager
	 *
	 * @return This
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
	 * @return The number of records deleted
	 */
	@SuppressWarnings("unused")
	public int truncate()
	{
		CriteriaDelete deletion = getCriteriaBuilder().createCriteriaDelete(getEntityClass());
		setCriteriaDelete(deletion);
		reset(deletion.from(getEntityClass()));
		getFilters().clear();
		select();
		return getEntityManager().createQuery(deletion)
		                         .executeUpdate();
	}

	/**
	 * Returns a lsit of all fields for an object recursively
	 *
	 * @param object
	 * 		THe object class
	 * @param fieldList
	 * 		The list of fields
	 *
	 * @return A list of type Field
	 */
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
