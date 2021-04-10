package com.entityassist.querybuilder;

import com.entityassist.BaseEntity;
import com.entityassist.enumerations.OrderByType;
import com.entityassist.exceptions.QueryBuilderException;
import com.entityassist.querybuilder.builders.DefaultQueryBuilder;
import com.entityassist.querybuilder.builders.JoinExpression;
import com.entityassist.querybuilder.statements.DeleteStatement;
import com.entityassist.querybuilder.statements.UpdateStatement;
import com.entityassist.services.querybuilders.IQueryBuilder;
import com.google.common.base.Strings;
import com.google.inject.Key;
import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.guicedpersistence.services.ITransactionHandler;
import com.guicedee.guicedpersistence.services.PersistenceServicesModule;
import com.guicedee.logger.LogFactory;
import org.hibernate.Session;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import javax.sql.DataSource;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.entityassist.querybuilder.builders.IFilterExpression.isPluralOrMapAttribute;
import static com.entityassist.querybuilder.builders.IFilterExpression.isSingularAttribute;
import static com.guicedee.guicedpersistence.scanners.PersistenceServiceLoadersBinder.ITransactionHandlerReader;

@SuppressWarnings({"unchecked", "unused"})
public abstract class QueryBuilder<J extends QueryBuilder<J, E, I>, E extends BaseEntity<E, J, I>, I extends Serializable>
		extends DefaultQueryBuilder<J, E, I>
		implements IQueryBuilder<J, E, I>
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
	 * Trigger if select should happen
	 *
	 * @return if select should occur
	 */
	@Override
	public boolean onSelect()
	{
		return true;
	}
	
	/**
	 * Trigger events on the query when selects occur
	 */
	@Override
	public void onSelectExecution(TypedQuery<?> query)
	{
		//For inheritance
	}
	
	/**
	 * Returns a long of the count for the given builder
	 *
	 * @return Long of results - generally never null
	 */
	@Override
	public Long getCount()
	{
		if (!selected)
		{
			selectCount();
			select();
		}
		if (onSelect())
		{
			TypedQuery<Long> query = getQueryCount();
			applyCache(query);
			onSelectExecution(query);
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
		return null;
	}
	
	/**
	 * Returns the generated query, always created new
	 *
	 * @param <T> Any type returned
	 * @return A built typed query
	 */
	@Override
	public <T> TypedQuery<T> getQuery()
	{
		if (!selected)
		{
			select();
		}
		return getEntityManager().createQuery(getCriteriaQuery());
	}
	
	/**
	 * Returns the query for a count, always created new
	 *
	 * @param <T> Any type returned
	 * @return A built typed query
	 */
	@Override
	public <T> TypedQuery<T> getQueryCount()
	{
		if (!selected)
		{
			selectCount();
			select();
		}
		return getEntityManager().createQuery(getCriteriaQuery());
	}
	
	/**
	 * Prepares the select statement
	 *
	 * @return This
	 */
	@SuppressWarnings({"UnusedReturnValue", "Duplicates"})
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
				CriteriaDelete<E> cq = getCriteriaDelete();
				List<Predicate> allWheres = new ArrayList<>(getFilters());
				Predicate[] preds = new Predicate[allWheres.size()];
				preds = allWheres.toArray(preds);
				cq.where(preds);
			}
			else if (isUpdate())
			{
				CriteriaUpdate<E> cq = getCriteriaUpdate();
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
	 * Physically applies the cache attributes to the query
	 * <p>
	 * Adds cacheable, cache region, and sets persistence cache retrieve mode as use, and store mode as use
	 *
	 * @param query The query to apply to
	 */
	private void applyCache(TypedQuery<?> query)
	{
		if (!Strings.isNullOrEmpty(getCacheName()))
		{
			query.setHint("org.hibernate.cacheable", true);
			query.setHint("org.hibernate.cacheRegion", getCacheRegion());
			query.setHint("jakarta.persistence.cache.retrieveMode", "USE");
			query.setHint("jakarta.persistence.cache.storeMode", "USE");
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
		for (Expression<?> p : getGroupBys())
		{
			cq.groupBy(p);
		}
		
		for (Expression<?> expression : getHavingExpressions())
		{
			cq.having((Expression<Boolean>) expression);
		}
		
		if (!getOrderBys().isEmpty())
		{
			List<Order> orderBys = new ArrayList<>();
			getOrderBys().forEach((key, value) ->
					                      orderBys.add(processOrderBys(key, value)));
			cq.orderBy(orderBys);
		}
		
		if (getSelections().isEmpty())
		{
			getCriteriaQuery().select(getRoot());
		}
		else if (getSelections().size() > 1)
		{
			if (getConstruct() != null)
			{
				ArrayList<Selection<?>> aS = new ArrayList<>(getSelections());
				Selection<?>[] selections = aS.toArray(new Selection[0]);
				CompoundSelection<?> cs = getCriteriaBuilder().construct(getConstruct(), selections);
				getCriteriaQuery().select(cs);
			}
			else
			{
				getCriteriaQuery().multiselect(new ArrayList<>(getSelections()));
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
	 * @param key   The attribute to apply
	 * @param value The value to use
	 */
	private Order processOrderBys(Attribute<?, ?> key, OrderByType value)
	{
		//noinspection EnhancedSwitchMigration
		switch (value)
		{
			
			case DESC:
			{
				if (isSingularAttribute(key))
				{
					return getCriteriaBuilder().desc(getRoot().get((SingularAttribute<?, ?>) key));
				}
				else if (isPluralOrMapAttribute(key))
				{
					return getCriteriaBuilder().desc(getRoot().get((PluralAttribute<?, ?, ?>) key));
				}
				return getCriteriaBuilder().desc(getRoot().get((SingularAttribute<?, ?>) key));
			}
			case ASC:
			default:
			{
				if (isSingularAttribute(key))
				{
					return getCriteriaBuilder().asc(getRoot().get((SingularAttribute<?, ?>) key));
				}
				else if (isPluralOrMapAttribute(key))
				{
					return getCriteriaBuilder().asc(getRoot().get((PluralAttribute<?, ?, ?>) key));
				}
				return getCriteriaBuilder().asc(getRoot().get((SingularAttribute<?, ?>) key));
			}
		}
	}
	
	/**
	 * Returns the number of rows or an unsupported exception if there are no filters added
	 *
	 * @param updateFields Allows to use the Criteria Update to run a bulk update on the table
	 * @return number of rows updated
	 */
	@Override
	@SuppressWarnings({"UnusedReturnValue", "Duplicates"})
	public int bulkUpdate(E updateFields, boolean allowEmpty)
	{
		if (!allowEmpty && getFilters().isEmpty())
		{
			throw new UnsupportedOperationException("Calling the bulk update method with no filters. This will update the entire table.");
		}
		CriteriaUpdate<E> update = getCriteriaUpdate();
		Map<Field, Object> updateFieldMap = new UpdateStatement(updateFields).getUpdateFieldMap(updateFields);
		if (updateFieldMap.isEmpty())
		{
			log.warning("Nothing to update, ignore bulk update");
			return 0;
		}
		for (Map.Entry<Field, Object> entries : updateFieldMap.entrySet())
		{
			String attributeName = new UpdateStatement(updateFields).getColumnName(entries.getKey());
			Object value = entries.getValue();
			try
			{
				update.set(attributeName, value);
			}
			catch (IllegalArgumentException iae)
			{
				log.warning("Unable to find attribute name [" + attributeName + "] on type [" + updateFields.getClass()
				                                                                                            .getCanonicalName() + "]");
				log.log(Level.FINER, "Illegal Attribute", iae);
				return -1;
			}
		}
		select();
		
		boolean transactionAlreadyStarted = false;
		ParsedPersistenceXmlDescriptor unit = GuiceContext.get(Key.get(ParsedPersistenceXmlDescriptor.class, getEntityManagerAnnotation()));
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (handler.active(unit) && handler.transactionExists(getEntityManager(), unit))
			{
				transactionAlreadyStarted = true;
				break;
			}
		}
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(unit))
			{
				handler.beginTransacation(false, getEntityManager(), unit);
			}
		}
		
		int results = getEntityManager().createQuery(update)
		                                .executeUpdate();
		
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(unit))
			{
				handler.commitTransacation(false, getEntityManager(), unit);
			}
		}
		return results;
	}
	
	/**
	 * Processors the join section
	 *
	 * @param executor Processes the joins into the expression
	 */
	private void processJoins(JoinExpression<?, ?, ?> executor)
	{
		Attribute<?, ?> value = executor.getAttribute();
		JoinType jt = executor.getJoinType();
		List<Predicate> onClause = new ArrayList<>();
		if (executor.getOnBuilder() != null)
		{
			executor.getOnBuilder()
			        .select();
			onClause.addAll(executor.getOnBuilder()
			                        .getFilters());
		}
		
		Join<?, ?> join;
		if (executor.getGeneratedRoot() == null)
		{
			join = getRoot().join(value.getName(), jt);
		}
		else
		{
			//join = getRoot().join(value.getName(), jt);
			join = executor.getGeneratedRoot();
		}
		if (!onClause.isEmpty())
		{
			join = join.on(onClause.toArray(new Predicate[]{}));
		}
		QueryBuilder<?, ?, ?> key = executor.getExecutor();
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
	 * Returns the result set as a stream
	 *
	 * @param resultType The result type
	 * @param <T>        The Class for the type to gerenify
	 * @return A stream of the type
	 */
	@Override
	@SuppressWarnings({"Duplicates", "unused"})
	public <T> Stream<T> getResultStream(Class<T> resultType)
	{
		if (!selected)
		{
			select();
		}
		TypedQuery<T> query = getQuery();
		applyCache(query);
		if (getMaxResults() != null)
		{
			query.setMaxResults(getMaxResults());
		}
		if (getFirstResults() != null)
		{
			query.setFirstResult(getFirstResults());
		}
		return query.getResultStream();
	}
	
	/**
	 * Returns a non-distinct list and returns an empty optional if a non-unique-result exception is thrown
	 *
	 * @return An optional of the result
	 */
	@Override
	public Optional<E> get()
	{
		return get(this.returnFirst);
	}
	
	/**
	 * Returns the first result returned
	 *
	 * @param returnFirst If the first should be returned in the instance of many results
	 * @return Optional of the required object
	 */
	@Override
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
	@Override
	@SuppressWarnings({"Duplicates", "unused"})
	@NotNull
	public <T> Optional<T> get(@NotNull Class<T> asType)
	{
		if (!selected)
		{
			select();
		}
		if (onSelect())
		{
			TypedQuery<T> query = getQuery();
			if (getMaxResults() != null)
			{
				query.setMaxResults(getMaxResults());
			}
			if (getFirstResults() != null)
			{
				query.setFirstResult(getFirstResults());
			}
			applyCache(query);
			onSelectExecution(query);
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
					//noinspection rawtypes
					((BaseEntity) j)
							.setFake(false);
				}
				if (detach)
				{
					try
					{
						getEntityManager().detach(j);
					}
					catch (Throwable T)
					{
						log.finer("Unable to detach : " + j.getClass()
						                                   .getName());
					}
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
				if (isReturnFirst())
				{
					query.setMaxResults(1);
					List<T> returnedList = query.getResultList();
					j = returnedList.get(0);
					if (j != null)
					{
						if (BaseEntity.class.isAssignableFrom(j.getClass()))
						{
							//noinspection rawtypes
							((BaseEntity) j)
									.setFake(false);
						}
						if (detach)
						{
							try
							{
								getEntityManager().detach(j);
							}
							catch (Throwable T)
							{
								log.finer("Unable to detach : " + j.getClass()
								                                   .getName());
							}
						}
					}
					return Optional.ofNullable(j);
				}
				else
				{
					log.log(Level.FINE, "Non Unique Result. Found too many for a get() for class : " + getEntityClass().getName() + "}. Get First Result disabled. Returning empty",
					        nure);
					return Optional.empty();
				}
			}
		}
		return Optional.empty();
	}
	
	/**
	 * If this builder is configured to return the first row
	 *
	 * @return If the first record must be returned
	 */
	@Override
	@SuppressWarnings("WeakerAccess")
	public boolean isReturnFirst()
	{
		return returnFirst;
	}
	
	/**
	 * If a Non-Unique Exception is thrown re-run the query as a list and return the first item
	 *
	 * @param returnFirst if must return first
	 * @return J
	 */
	@Override
	@SuppressWarnings({"unchecked", "unused"})
	@NotNull
	public J setReturnFirst(boolean returnFirst)
	{
		this.returnFirst = returnFirst;
		return (J) this;
	}
	
	/**
	 * Returns a list of entities from a distinct or non distinct list
	 *
	 * @return A list of entities returned
	 */
	@Override
	public List<E> getAll()
	{
		return getAll(getEntityClass());
	}
	
	/**
	 * Returns the list as the selected class type (for when specifying single select columns)
	 *
	 * @param returnClassType Returns a list of a given column
	 * @param <T>             The type of the column returned
	 * @return The type of the column returned
	 */
	@Override
	@SuppressWarnings({"Duplicates", "unused"})
	@NotNull
	public <T> List<T> getAll(Class<T> returnClassType)
	{
		if (!selected)
		{
			select();
		}
		if (onSelect())
		{
			TypedQuery<T> query = getQuery();
			applyCache(query);
			if (getMaxResults() != null)
			{
				query.setMaxResults(getMaxResults());
			}
			if (getFirstResults() != null)
			{
				query.setFirstResult(getFirstResults());
			}
			onSelectExecution(query);
			List<T> j;
			j = query.getResultList();
			if (!j.isEmpty())
			{
				if (detach)
				{
					for (T t : j)
					{
						try
						{
							if (BaseEntity.class.isAssignableFrom(t.getClass()))
							{
								//noinspection rawtypes
								((BaseEntity) t)
										.setFake(false);
							}
							getEntityManager().detach(t);
						}
						catch (Throwable T)
						{
							log.finer("Unable to detach : " + t.getClass()
							                                   .getName());
						}
					}
				}
			}
			return j;
		}
		return null;
	}
	
	/**
	 * Sets whether or not to detach the selected entity/ies
	 *
	 * @return This
	 */
	@Override
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
	@Override
	public int delete()
	{
		if (getFilters().isEmpty())
		{
			throw new UnsupportedOperationException("Calling the delete method with no filters. This will truncate the table. Rather call truncate()");
		}
		CriteriaDelete<E> deletion = getCriteriaBuilder().createCriteriaDelete(getEntityClass());
		reset(deletion.from(getEntityClass()));
		setCriteriaDelete(deletion);
		select();
		return getEntityManager().createQuery(deletion)
		                         .executeUpdate();
	}
	
	/**
	 * Deletes a specific ID the good old almost fast way
	 * <p>
	 * Delete where ID = getId();
	 *
	 * @param entity entity with id populated
	 */
	@Override
	public void deleteId(E entity) throws QueryBuilderException
	{
		String deleteString = new DeleteStatement(entity).toString();
		log.fine(deleteString);
		if (PersistenceServicesModule.getJtaConnectionBaseInfo()
		                             .containsKey(getEntityManagerAnnotation()))
		{
			DataSource ds = GuiceContext.get(DataSource.class, getEntityManagerAnnotation());
			if (ds == null)
			{
				Query query = getEntityManager().createNativeQuery(deleteString);
				query.executeUpdate();
			}
			else
			{
				Session session = getEntityManager().unwrap(Session.class);
				session.doWork(c -> {
					try (Statement st = c.createStatement()) {
						st.executeUpdate(deleteString);
					}
				});
			}
		}
	}
	
	
	/**
	 * Deletes the given entity through the entity manager
	 *
	 * @param entity Deletes through the entity manager
	 * @return This
	 */
	@Override
	@SuppressWarnings("Duplicates")
	public E delete(E entity)
	{
		boolean transactionAlreadyStarted = false;
		ParsedPersistenceXmlDescriptor unit = GuiceContext.get(Key.get(ParsedPersistenceXmlDescriptor.class, getEntityManagerAnnotation()));
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (handler.active(unit) && handler.transactionExists(getEntityManager(), unit))
			{
				transactionAlreadyStarted = true;
				break;
			}
		}
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(unit))
			{
				handler.beginTransacation(false, getEntityManager(), unit);
			}
		}
		getEntityManager().remove(entity);
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(unit))
			{
				handler.commitTransacation(false, getEntityManager(), unit);
			}
		}
		
		return entity;
	}
	
	/**
	 * Returns the assigned entity manager
	 *
	 * @return The entity manager to use for this run
	 */
	@Override
	public abstract EntityManager getEntityManager();
	
	/**
	 * Returns the number of rows affected by the delete.
	 * WARNING : Be very careful if you haven't added a filter this will truncate the table or throw a unsupported exception if no filters.
	 *
	 * @return The number of records deleted
	 */
	@Override
	@SuppressWarnings({"unused", "Duplicates"})
	public int truncate()
	{
		CriteriaDelete<E> deletion = getCriteriaBuilder().createCriteriaDelete(getEntityClass());
		setCriteriaDelete(deletion);
		reset(deletion.from(getEntityClass()));
		getFilters().clear();
		select();
		boolean transactionAlreadyStarted = false;
		ParsedPersistenceXmlDescriptor unit = GuiceContext.get(Key.get(ParsedPersistenceXmlDescriptor.class, getEntityManagerAnnotation()));
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (handler.active(unit) && handler.transactionExists(getEntityManager(), unit))
			{
				transactionAlreadyStarted = true;
				break;
			}
		}
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(unit))
			{
				handler.beginTransacation(false, getEntityManager(), unit);
			}
		}
		
		int results = getEntityManager().createQuery(deletion)
		                                .executeUpdate();
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(unit))
			{
				handler.commitTransacation(false, getEntityManager(), unit);
			}
		}
		return results;
	}
	
	/**
	 * If must be detached from the entity manager
	 *
	 * @return if the entity automatically detaches from the entity manager
	 */
	@Override
	public boolean isDetach()
	{
		return detach;
	}
	
	/**
	 * If must be detached from the entity manager
	 *
	 * @param detach if the entity must detach
	 * @return this object
	 */
	@Override
	public J setDetach(boolean detach)
	{
		this.detach = detach;
		return (J) this;
	}
}
