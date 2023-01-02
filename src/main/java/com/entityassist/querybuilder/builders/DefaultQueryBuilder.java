package com.entityassist.querybuilder.builders;

import com.entityassist.*;
import com.entityassist.enumerations.*;
import com.entityassist.querybuilder.*;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.*;
import jakarta.validation.constraints.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

import static com.entityassist.querybuilder.builders.IFilterExpression.*;

@SuppressWarnings({"UnusedReturnValue", "WeakerAccess", "unused"})
public abstract class DefaultQueryBuilder<J extends DefaultQueryBuilder<J, E, I>,
		E extends DefaultEntity<E, J, I>,
		I extends Serializable>
		extends QueryBuilderRoot<J, E, I>
		implements com.entityassist.services.querybuilders.IDefaultQueryBuilder<J, E, I>
{
	
	/**
	 * The logger
	 */
	private static final Logger log = Logger.getLogger("DefaultQueryBuilder");
	/**
	 * The set of joins to apply
	 */
	private final Set<JoinExpression<?, ?, ?>> joins;
	/**
	 * Select Expressions
	 */
	private final Set<SelectExpression> selectExpressions;
	/**
	 * A predefined list of filters for this entity
	 */
	private final Set<Predicate> filters;
	/**
	 * A list of columns to select if specified
	 */
	private final Set<Selection<?>> selections;
	/**
	 * A list of group by's to go by. Built at generation time
	 */
	private final Set<Expression<?>> groupBys;
	/**
	 * A list of order by's. Generated at generation time
	 */
	private final Map<Attribute<?, ?>, OrderByType> orderBys;
	/**
	 * A list of havingExpressions clauses
	 */
	private final Set<Expression<?>> havingExpressions;
	/**
	 * All of the where expressions to apply
	 */
	private final Set<IFilterExpression> whereExpressions;
	/**
	 * All of the order by's to apply
	 */
	private final Set<OrderByExpression> orderByExpressions;
	/**
	 * All of the group by's to apply
	 */
	private final Set<GroupByExpression> groupByExpressions;
	/**
	 * The actual builder for the entity
	 */
	private CriteriaBuilder criteriaBuilder;
	/**
	 * A cache region name to apply
	 */
	private String cacheRegion;
	/**
	 * The physical criteria query
	 */
	private CriteriaQuery<?> criteriaQuery;
	/**
	 * The physical criteria query
	 */
	private CriteriaDelete<E> criteriaDelete;
	/**
	 * The physical criteria query
	 */
	private CriteriaUpdate<E> criteriaUpdate;
	
	/**
	 * If a dto construct is required (classes that extend the entity as transports)
	 */
	private Class<? extends BaseEntity> construct;
	/**
	 * If a delete is currently running
	 */
	private boolean delete;
	/**
	 * If the builder is currently running an update
	 */
	private boolean update;
	/**
	 * The cache name to use
	 */
	private String cacheName;
	/**
	 * Returns the root object of this entity
	 */
	private From<?, ?> root;
	
	/**
	 * Constructs a new query builder core with typed classes instantiated
	 */
	public DefaultQueryBuilder()
	{
		filters = new LinkedHashSet<>();
		selections = new LinkedHashSet<>();
		groupBys = new LinkedHashSet<>();
		orderBys = new LinkedHashMap<>();
		joins = new LinkedHashSet<>();
		havingExpressions = new LinkedHashSet<>();
		selectExpressions = new LinkedHashSet<>();
		whereExpressions = new LinkedHashSet<>();
		orderByExpressions = new LinkedHashSet<>();
		groupByExpressions = new LinkedHashSet<>();
	}
	
	/**
	 * Selects the minimum count distinct of the root object (select distinct count(*))
	 *
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCountDistinct()
	{
		getSelections().add(getCriteriaBuilder().countDistinct(getRoot()));
		return (J) this;
	}
	
	/**
	 * Gets the selections that are going to be applied, leave empty for all columns
	 *
	 * @return set of selections
	 */
	protected Set<Selection<?>> getSelections()
	{
		return selections;
	}
	
	/**
	 * Gets the criteria builder
	 *
	 * @return The criteria builder
	 */
	@Override
	public CriteriaBuilder getCriteriaBuilder()
	{
		if (criteriaBuilder == null)
		{ criteriaBuilder = getEntityManager().getCriteriaBuilder(); }
		return criteriaBuilder;
	}
	
	/**
	 * Gets my given root
	 *
	 * @return The From object that is being used
	 */
	@Override
	public From getRoot()
	{
		return root;
	}
	
	/**
	 * Sets the root of this builder
	 *
	 * @param root The FROM to use
	 * @return this
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J setRoot(From<?, ?> root)
	{
		this.root = root;
		return (J) this;
	}
	
	/**
	 * Where the "id" field is in
	 *
	 * @param id Finds by ID
	 * @return This (Use get to return results)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public J find(I id)
	{
		Field found = null;
		Field[] allFields = id.getClass()
		                      .getFields();
		for (Field allField : allFields)
		{
			if (allField.isAnnotationPresent(Id.class))
			{
				found = allField;
				break;
			}
		}
		
		Optional<Field> idField = Optional.ofNullable(found);
		if (!idField.isPresent())
		{
			Field[] fields = getEntityClass().getDeclaredFields();
			for (Field field : fields)
			{
				if (field.isAnnotationPresent(Id.class))
				{
					idField = Optional.of(field);
				}
			}
		}
		
		if (idField.isPresent())
		{
			where(getAttribute(idField.get()
			                          .getName()), Operand.Equals, id);
		}
		else
		{
			where(getAttribute("id"), Operand.Equals, id);
		}
		return (J) this;
	}
	
	
	/**
	 * Where the "id" field is in
	 *
	 * @param id Finds by ID
	 * @return This (Use get to return results)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public J find(Collection<I> id)
	{
		List<I> idList = new ArrayList(Arrays.asList(id));
		
		Field found = null;
		Field[] allFields = id.getClass()
		                      .getFields();
		for (Field allField : allFields)
		{
			if (allField.isAnnotationPresent(Id.class))
			{
				found = allField;
				break;
			}
		}
		
		Optional<Field> idField = Optional.ofNullable(found);
		if (!idField.isPresent())
		{
			Field[] fields = getEntityClass().getDeclaredFields();
			for (Field field : fields)
			{
				if (field.isAnnotationPresent(Id.class))
				{
					idField = Optional.of(field);
				}
			}
		}
		
		if (idField.isPresent())
		{
			where((Attribute<Object, Object>) getAttribute(idField.get()
			                                                      .getName()), Operand.InList, idList);
		}
		else
		{
			where((Attribute<Object, Object>) getAttribute("id"), Operand.InList, idList);
		}
		return (J) this;
	}
	
	/**
	 * Returns the collection of filters that are going to be applied in build
	 *
	 * @return A set of predicates
	 */
	@Override
	public Set<Predicate> getFilters()
	{
		return filters;
	}
	
	/**
	 * Selects the minimum count of the root object (select count(*))
	 *
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCount()
	{
		getSelections().add(getCriteriaBuilder().count(getRoot()));
		return (J) this;
	}
	
	/**
	 * Joins the given builder with an inner join and no associated builder
	 *
	 * @param attribute The given attribute to join on
	 * @return This object - Configure each joins filters separately in their builders
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute)
	{
		return join(attribute, null, JoinType.INNER);
	}
	
	/**
	 * Joins the given builder with the given builder and build type
	 *
	 * @param attribute The given attribute to join on
	 * @param builder   A Query Builder object that contains the construct of the query
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, QueryBuilder builder, JoinType joinType, JoinExpression joinExpression)
	{
		joinExpression.setAttribute(attribute);
		joinExpression.setExecutor(builder);
		joinExpression.setJoinType(joinType);
		if (joinExpression.getGeneratedRoot() == null)
		{ joinExpression.setGeneratedRoot(getRoot().join(attribute.getName(), joinType)); }
		else
		{
			joinExpression.setGeneratedRoot(joinExpression.getGeneratedRoot()
			                                              .join(attribute.getName(), joinType));
		}
		joins.add(joinExpression);
		return (J) this;
	}
	
	/**
	 * Joins the given builder with the given builder and build type
	 *
	 * @param attribute The given attribute to join on
	 * @param builder   A Query Builder object that contains the construct of the query
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, QueryBuilder<?, ?, ?> builder, JoinType joinType)
	{
		JoinExpression joinExpression = new JoinExpression(builder, joinType, attribute);
		joins.add(joinExpression);
		joinExpression.setExecutor(builder);
		joinExpression.setJoinType(joinType);
		if (joinExpression.getGeneratedRoot() == null)
		{ joinExpression.setGeneratedRoot(getRoot().join(attribute.getName(), joinType)); }
		else
		{
			joinExpression.setGeneratedRoot(joinExpression.getGeneratedRoot()
			                                              .join(attribute.getName(), joinType));
		}
		
		
		return (J) this;
	}
	
	/**
	 * Joins the given builder with the given builder and build type
	 *
	 * @param attribute The given attribute to join on
	 * @param builder   A Query Builder object that contains the construct of the query
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, QueryBuilder builder, JoinType joinType, QueryBuilder onClauses, JoinExpression joinExpression)
	{
		joinExpression.setExecutor(builder);
		joinExpression.setJoinType(joinType);
		joinExpression.setOnBuilder(onClauses);
		if (joinExpression.getGeneratedRoot() == null)
		{ joinExpression.setGeneratedRoot(getRoot().join(attribute.getName(), joinType)); }
		else
		{
			joinExpression.setGeneratedRoot(joinExpression.getGeneratedRoot()
			                                              .join(attribute.getName(), joinType));
		}
		joins.add(joinExpression);
		return (J) this;
	}
	
	/**
	 * Joins the given builder with the given builder and build type
	 *
	 * @param attribute The given attribute to join on
	 * @param builder   A Query Builder object that contains the construct of the query
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, QueryBuilder builder, JoinType joinType, QueryBuilder onClauses)
	{
		JoinExpression joinExpression = new JoinExpression(builder, joinType, attribute);
		joinExpression.setOnBuilder(onClauses);
		joinExpression.setExecutor(builder);
		joinExpression.setJoinType(joinType);
		if (joinExpression.getGeneratedRoot() == null)
		{ joinExpression.setGeneratedRoot(getRoot().join(attribute.getName(), joinType)); }
		else
		{
			joinExpression.setGeneratedRoot(joinExpression.getGeneratedRoot()
			                                              .join(attribute.getName(), joinType));
		}
		joins.add(joinExpression);
		return (J) this;
	}
	
	/**
	 * Joins the given builder in an inner join with the given builder
	 *
	 * @param attribute The given attribute to join on
	 * @param builder   A Query Builder object that contains the construct of the query
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, QueryBuilder builder)
	{
		return join(attribute, builder, JoinType.INNER);
	}
	
	/**
	 * Joins the given builder With the given join type and no associated builder
	 *
	 * @param attribute The given attribute to join on
	 * @return The join type to use
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, JoinType joinType)
	{
		return join(attribute, null, joinType);
	}
	
	
	/**
	 * Joins the given builder With the given join type and no associated builder
	 *
	 * @param attribute The given attribute to join on
	 * @return The join type to use
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, JoinType joinType, JoinExpression joinExpression)
	{
		return join(attribute, null, joinType, joinExpression);
	}
	
	
	/**
	 * Where the field name is equal to the value
	 *
	 * @param fieldName The field name
	 * @param value     The value to use - Collection, Arrays, etc
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	public J in(String fieldName, Object value)
	{
		where((Attribute) getRoot().get(fieldName), Operand.Equals, value);
		return (J) this;
	}
	
	/**
	 * Processes the where expressions into filters
	 *
	 * @param whereExpression The where expressions
	 * @param <X>             The attribute type
	 * @param <Y>             The column type
	 */
	private <X, Y> void doWhere(WhereExpression<X, Y> whereExpression)
	{
		Optional<Predicate> predicate = whereExpression.toPredicate(getCriteriaBuilder());
		if (predicate.isPresent())
		{
			getFilters().add(predicate.get());
		}
		else
		{
			log.warning("Where Filter could not be added, predicate could not be built.");
		}
	}
	
	/**
	 * Where the field name is equal to the value
	 *
	 * @param fieldName The field name
	 * @param value     The value to use - Collection, Arrays, etc
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <X, Y> J in(Attribute<X, Y> fieldName, Y value)
	{
		where(fieldName, Operand.InList, value);
		return (J) this;
	}
	
	/**
	 * Where the field name is equal to the value
	 *
	 * @param fieldName The field name
	 * @param value     The value to use - Collection, Arrays, etc
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <X, Y> J in(Attribute<X, Y> fieldName, Collection<Y> value)
	{
		where(fieldName, Operand.InList, value);
		return (J) this;
	}
	
	
	/**
	 * Where the field name is equal to the value
	 *
	 * @param fieldName The field name
	 * @param value     The value to use - Collection, Arrays, etc
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J in(Attribute<X, Y> fieldName, Y[] value)
	{
		where(fieldName, Operand.InList, value);
		return (J) this;
	}
	
	
	/**
	 * Where the operand is the type of collection or list
	 *
	 * @param attribute Select column
	 * @param operator  The operand to use
	 * @param value     The value to use
	 * @param <X>       The the attribute column type
	 * @param <Y>       The field value type
	 * @return This
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public <X, Y> J where(Attribute<X, Y> attribute, Operand operator, Y[] value)
	{
		return (J) where(attribute, getRoot().get(attribute.getName()), operator, value);
	}
	
	/**
	 * Where the operand is the type of collection or list
	 *
	 * @param attribute Select column
	 * @param operator  The operand to use
	 * @param value     The value to use
	 * @param <X>       The the attribute column type
	 * @param <Y>       The field value type
	 * @return This
	 */
	//@Override
	@NotNull
	@SuppressWarnings("unchecked")
	<X, Y> J where(Attribute attr, Expression<X> attribute, Operand operator, Y[] value)
	{
		WhereExpression<X, Y> whereExpression = new WhereExpression<>(attr, attribute, operator, value);
		whereExpressions.add(whereExpression);
		doWhere(whereExpression);
		return (J) this;
	}
	
	/**
	 * Where the operand is the type of collection or list
	 *
	 * @param attribute The column to where on
	 * @param operator  The operand to use
	 * @param value     The value to apply
	 * @param <X>       The attribute type
	 * @param <Y>       The attribute value type
	 * @return This
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public <X, Y> J where(Attribute<X, Y> attribute, Operand operator, Collection<Y> value)
	{
		return (J) where(attribute, getRoot().get(attribute.getName()), operator, value);
	}
	
	/**
	 * Where the operand is the type of collection or list
	 *
	 * @param attribute The column to where on
	 * @param operator  The operand to use
	 * @param value     The value to apply
	 * @param <X>       The attribute type
	 * @param <Y>       The attribute value type
	 * @return This
	 */
	//@Override
	@NotNull
	@SuppressWarnings("unchecked")
	<X, Y> J where(Attribute attr, Expression<X> attribute, Operand operator, Collection<Y> value)
	{
		WhereExpression<X, Y> whereExpression = new WhereExpression<>(attr, attribute, operator, value);
		whereExpressions.add(whereExpression);
		doWhere(whereExpression);
		return (J) this;
	}
	
	/**
	 * Performs a filter on the database with the where clauses
	 *
	 * @param attribute The attribute to be used
	 * @param operator  The operand to use
	 * @param value     The value to apply (Usually serializable)
	 * @return This object
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public <X, Y> J where(Attribute<X, Y> attribute, Operand operator, Y value)
	{
		return (J) where(attribute, getRoot().get(attribute.getName()), operator, value);
	}
	
	/**
	 * Performs a filter on the database with the where clauses
	 *
	 * @param attribute The attribute to be used
	 * @param operator  The operand to use
	 * @param value     The value to apply (Usually serializable)
	 * @return This object
	 */
	//@Override
	@NotNull
	@SuppressWarnings("unchecked")
	<X, Y> J where(Attribute attr, Expression<X> attribute, Operand operator, Y value)
	{
		WhereExpression<X, Y> whereExpression = new WhereExpression<>(attr, attribute, operator, value);
		whereExpressions.add(whereExpression);
		doWhere(whereExpression);
		return (J) this;
	}
	
	
	/**
	 * Gets the cache region for this query
	 *
	 * @return The applied cache region or null
	 */
	@Override
	public String getCacheRegion()
	{
		return cacheRegion;
	}
	
	/**
	 * Sets a cache region for this query
	 *
	 * @param cacheRegion To a cache region
	 * @return This
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public J setCacheRegion(String cacheRegion)
	{
		this.cacheRegion = cacheRegion;
		return (J) this;
	}
	
	/**
	 * Orders by column ascending
	 *
	 * @param orderBy Which attribute to order by
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J orderBy(Attribute<X, Y> orderBy)
	{
		return orderBy(orderBy, OrderByType.ASC);
	}
	
	/**
	 * Adds an order by column to the query
	 *
	 * @param orderBy   Order by which column
	 * @param direction The direction to apply
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J orderBy(Attribute<X, Y> orderBy, OrderByType direction)
	{
		getOrderByExpressions().add(new OrderByExpression(orderBy, direction));
		if (isSingularAttribute(orderBy))
		{
			getOrderBys().put(orderBy, direction);
		}
		else if (isPluralOrMapAttribute(orderBy))
		{
			getOrderBys().put(orderBy, direction);
		}
		return (J) this;
	}
	
	/**
	 * Returns the set of order by expressions to apply
	 *
	 * @return Set of Order Expressions
	 */
	public Set<OrderByExpression> getOrderByExpressions()
	{
		return orderByExpressions;
	}
	
	/**
	 * Returns the current list of order by's
	 *
	 * @return A map of attributes and order by types
	 */
	@Override
	public Map<Attribute<?, ?>, OrderByType> getOrderBys()
	{
		return orderBys;
	}
	
	/**
	 * Selects a given column
	 *
	 * @param selectColumn The column to group by
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J groupBy(Attribute<X, Y> selectColumn)
	{
		getGroupByExpressions().add(new GroupByExpression(selectColumn));
		if (isSingularAttribute(selectColumn))
		{
			getGroupBys().add(getRoot().get((SingularAttribute) selectColumn));
		}
		else if (isPluralOrMapAttribute(selectColumn))
		{
			getGroupBys().add(getRoot().get((PluralAttribute) selectColumn));
		}
		return (J) this;
	}
	
	/**
	 * Returns the list of group by expressions
	 *
	 * @return A set of group by expressions
	 */
	public Set<GroupByExpression> getGroupByExpressions()
	{
		return groupByExpressions;
	}
	
	/**
	 * Returns the current list of group by's
	 *
	 * @return A set of expressions
	 */
	@Override
	public Set<Expression<?>> getGroupBys()
	{
		return groupBys;
	}
	
	/**
	 * If the builder is set to delete
	 *
	 * @return if it is in a delete statement
	 */
	@Override
	public boolean isDelete()
	{
		return delete;
	}
	
	/**
	 * If the builder is set to delete
	 *
	 * @param delete if this must run as a delete statement
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J setDelete(boolean delete)
	{
		this.delete = delete;
		return (J) this;
	}
	
	/**
	 * Returns the criteria delete, which is nullable
	 *
	 * @return The criteria delete or null
	 */
	
	@Override
	public CriteriaDelete<E> getCriteriaDelete()
	{
		return criteriaDelete;
	}
	
	/**
	 * Sets the criteria delete
	 *
	 * @param criteriaDelete A delete criteria delete
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public J setCriteriaDelete(CriteriaDelete<E> criteriaDelete)
	{
		this.criteriaDelete = criteriaDelete;
		setDelete(true);
		return (J) this;
	}
	
	/**
	 * If the builder is set to update
	 *
	 * @return if in a update statement
	 */
	@Override
	public boolean isUpdate()
	{
		return update;
	}
	
	/**
	 * If the builder is set to update
	 *
	 * @param update If is update
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J setUpdate(boolean update)
	{
		this.update = update;
		return (J) this;
	}
	
	/**
	 * Gets the criteria update object
	 *
	 * @return A criteria update
	 */
	
	@Override
	@SuppressWarnings("unchecked")
	public CriteriaUpdate<E> getCriteriaUpdate()
	{
		if (criteriaUpdate == null)
		{
			criteriaUpdate = getCriteriaBuilder().createCriteriaUpdate(getEntityClass());
			EntityType<E> eEntityType = getEntityManager().getEntityManagerFactory()
			                                              .getMetamodel()
			                                              .entity(getEntityClass());
			criteriaUpdate.from(eEntityType);
			setRoot(criteriaUpdate.getRoot());
			reset(criteriaUpdate.getRoot());
			update = true;
		}
		return criteriaUpdate;
	}
	
	/**
	 * Sets the criteria update object
	 *
	 * @param criteriaUpdate The criteria update from a criteria builder
	 * @return This
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public J setCriteriaUpdate(CriteriaUpdate<E> criteriaUpdate)
	{
		this.criteriaUpdate = criteriaUpdate;
		return (J) this;
	}
	
	/**
	 * Resets to the given new root and constructs the select query
	 * Not CRP to make sure you know whats going on
	 *
	 * @param newRoot A FROM object to reset to
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void reset(From newRoot)
	{
		setRoot(newRoot);
		getFilters().clear();
		getSelections().clear();
		getGroupBys().clear();
		getOrderBys().clear();
		getHavingExpressions().clear();
		
		for (IFilterExpression whereExpression : getWhereExpressions())
		{
			if (WhereExpression.class.isAssignableFrom(whereExpression.getClass()))
			{
				WhereExpression we = (WhereExpression) whereExpression;
				we.switchRoot(newRoot);
				doWhere(we);
			}
		}
		getSelectExpressions().forEach(this::redoSelectExpression);
	}
	
	/**
	 * Gets the havingExpressions list for this builder
	 *
	 * @return A set of expressions for the havingExpressions clause
	 */
	@Override
	public Set<Expression<?>> getHavingExpressions()
	{
		return havingExpressions;
	}
	
	/**
	 * Returns a set of the where expressions
	 *
	 * @return A set of IFilterExpressions
	 */
	@Override
	public Set<IFilterExpression> getWhereExpressions()
	{
		return whereExpressions;
	}
	
	/**
	 * A set of select expression
	 *
	 * @return Returns the select expressions
	 */
	public Set<SelectExpression> getSelectExpressions()
	{
		return selectExpressions;
	}
	
	/**
	 * Rebuilds the expressions for the select options
	 *
	 * @param selectExpression The column to reapply
	 */
	private void redoSelectExpression(SelectExpression selectExpression)
	{
		switch (selectExpression.getAggregrate())
		{
			case None:
			{
				selectColumn(selectExpression.getAttribute());
				break;
			}
			case Avg:
			{
				selectAverage(selectExpression.getAttribute());
				break;
			}
			case Count:
			{
				selectCount(selectExpression.getAttribute());
				break;
			}
			case CountDistinct:
			{
				selectCountDistinct(selectExpression.getAttribute());
				break;
			}
			case Max:
			{
				selectMax(selectExpression.getAttribute());
				break;
			}
			case Min:
			{
				selectMin(selectExpression.getAttribute());
				break;
			}
			case Sum:
			{
				selectSum(selectExpression.getAttribute());
				break;
			}
			case SumDouble:
			{
				selectSumAsDouble(selectExpression.getAttribute());
				break;
			}
			case SumLong:
			{
				selectSumAsLong(selectExpression.getAttribute());
				break;
			}
			default:
			{
				log.warning("Unknown expression type? " + selectExpression.getAttribute());
			}
		}
	}
	
	/**
	 * Selects a given column
	 *
	 * @param selectColumn The given column from the static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectColumn(Expression selectColumn)
	{
		return selectColumn(selectColumn, null);
	}
	
	/**
	 * Selects a given column
	 *
	 * @param selectColumn The given column from the static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectColumn(Expression selectColumn, String aliasName)
	{
		SelectExpression selectExpression = new SelectExpression(selectColumn, SelectAggregrate.None);
		selectExpression.setAlias(aliasName);
		selectExpressions.add(selectExpression);
		processSelectExpressionNone(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectAverage(Expression attribute)
	{
		return selectAverage(attribute, null);
	}
	
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectAverage(Expression attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, SelectAggregrate.Avg);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectAverage(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCount(Expression attribute)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, SelectAggregrate.Count);
		selectExpressions.add(selectExpression);
		processSelectCount(selectExpression);
		return (J) this;
	}
	
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCount(Expression attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, SelectAggregrate.Count);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectCount(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return this
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCountDistinct(Expression attribute)
	{
		return selectCountDistinct(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return this
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCountDistinct(Expression attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, SelectAggregrate.CountDistinct);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectCountDistinct(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectMax(Expression attribute)
	{
		return selectMax(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectMax(Expression attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, SelectAggregrate.Max);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectExpressionMax(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectMin(Expression attribute)
	{
		return selectMin(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectMin(Expression attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, SelectAggregrate.Min);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectExpressionMin(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSum(Expression attribute)
	{
		return selectSum(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSum(Expression attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, SelectAggregrate.Sum);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectSum(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSumAsDouble(Expression attribute)
	{
		return selectSumAsDouble(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSumAsDouble(Expression attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, SelectAggregrate.SumDouble);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectSumAsDouble(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSumAsLong(Expression attribute)
	{
		return selectSumAsLong(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSumAsLong(Expression attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, SelectAggregrate.SumLong);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectSumAsLong(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects a given column
	 *
	 * @param selectColumn The given column from the static metadata
	 * @return This
	 */
	@Override
	@NotNull
	public J selectColumn(Attribute selectColumn)
	{
		return selectColumn(selectColumn, null);
	}
	
	/**
	 * Selects a given column
	 *
	 * @param selectColumn The given column from the static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectColumn(Attribute selectColumn, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(getRoot().get(selectColumn.getName()), SelectAggregrate.None);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectExpressionNone(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectAverage(Attribute attribute)
	{
		return selectAverage(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectAverage(Attribute attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(getRoot().get(attribute.getName()), SelectAggregrate.Avg);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectAverage(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCount(Attribute attribute)
	{
		return selectColumn(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCount(Attribute attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(getRoot().get(attribute.getName()), SelectAggregrate.Count);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectCount(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return this
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCountDistinct(Attribute attribute)
	{
		return selectCountDistinct(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return this
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCountDistinct(Attribute attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(getRoot().get(attribute.getName()), SelectAggregrate.CountDistinct);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectCountDistinct(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectMax(Attribute attribute)
	{
		return selectMax(attribute, null);
	}
	
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectMax(Attribute attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(getRoot().get(attribute.getName()), SelectAggregrate.Max);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectExpressionMax(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectMin(Attribute attribute)
	{
		return selectMin(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectMin(Attribute attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(getRoot().get(attribute.getName()), SelectAggregrate.Min);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectExpressionMin(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSum(Attribute attribute)
	{
		return selectSum(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSum(Attribute attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(getRoot().get(attribute.getName()), SelectAggregrate.Sum);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectSum(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSumAsDouble(Attribute attribute)
	{
		return selectSumAsDouble(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSumAsDouble(Attribute attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(getRoot().get(attribute.getName()), SelectAggregrate.SumDouble);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectSumAsDouble(selectExpression);
		return (J) this;
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSumAsLong(Attribute attribute)
	{
		return selectSumAsLong(attribute, null);
	}
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSumAsLong(Attribute attribute, String alias)
	{
		SelectExpression selectExpression = new SelectExpression(getRoot().get(attribute.getName()), SelectAggregrate.SumLong);
		selectExpression.setAlias(alias);
		selectExpressions.add(selectExpression);
		processSelectSumAsLong(selectExpression);
		return (J) this;
	}
	
	/**
	 * Processes the select expression
	 *
	 * @param selectExpression A given column from static metadata
	 * @return true or false
	 */
	private boolean processSelectExpressionNone(SelectExpression selectExpression)
	{
		Expression<?> selectColumn = selectExpression.getAttribute();
		if (!isNullOrEmpty(selectExpression.getAlias()))
		{
			selectColumn.alias(selectExpression.getAlias());
		}
		getSelections().add(selectColumn);
		return true;
	}
	
	/**
	 * processes the select average
	 *
	 * @param selectExpression A given column from static metadata
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	private boolean processSelectAverage(SelectExpression selectExpression)
	{
		Expression selectColumn = selectExpression.getAttribute();
		if (!isNullOrEmpty(selectExpression.getAlias()))
		{
			selectColumn.alias(selectExpression.getAlias());
		}
		getSelections().add(getCriteriaBuilder().avg(selectColumn));
		return true;
	}
	
	/**
	 * Adds a select count to the criteria builder
	 *
	 * @param selectExpression A given column from static metadata
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	private boolean processSelectCount(SelectExpression selectExpression)
	{
		Expression selectColumn = selectExpression.getAttribute();
		if (!isNullOrEmpty(selectExpression.getAlias()))
		{
			selectColumn.alias(selectExpression.getAlias());
		}
		getSelections().add(getCriteriaBuilder().count(selectColumn));
		
		return true;
	}
	
	/**
	 * Processes to return the select count
	 *
	 * @param selectExpression A given column from static metadata
	 * @return true or false for this
	 */
	@SuppressWarnings("unchecked")
	private boolean processSelectCountDistinct(SelectExpression selectExpression)
	{
		Expression selectColumn = selectExpression.getAttribute();
		if (!isNullOrEmpty(selectExpression.getAlias()))
		{
			selectColumn.alias(selectExpression.getAlias());
		}
		getSelections().add(getCriteriaBuilder().countDistinct(selectColumn));
		return true;
	}
	
	@SuppressWarnings({"unchecked", "MissingMethodJavaDoc"})
	private boolean processSelectExpressionMax(SelectExpression selectExpression)
	{
		Expression selectColumn = selectExpression.getAttribute();
		if (!isNullOrEmpty(selectExpression.getAlias()))
		{
			selectColumn.alias(selectExpression.getAlias());
		}
		getSelections().add(getCriteriaBuilder().max(selectColumn));
		return true;
	}
	
	@SuppressWarnings({"unchecked", "MissingMethodJavaDoc"})
	private boolean processSelectExpressionMin(SelectExpression selectExpression)
	{
		Expression selectColumn = selectExpression.getAttribute();
		if (!isNullOrEmpty(selectExpression.getAlias()))
		{
			selectColumn.alias(selectExpression.getAlias());
		}
		getSelections().add(getCriteriaBuilder().min(selectColumn));
		return true;
	}
	
	@SuppressWarnings({"unchecked", "MissingMethodJavaDoc"})
	private boolean processSelectSum(SelectExpression selectExpression)
	{
		Expression selectColumn = selectExpression.getAttribute();
		if (!isNullOrEmpty(selectExpression.getAlias()))
		{
			selectColumn.alias(selectExpression.getAlias());
		}
		getSelections().add(getCriteriaBuilder().sum(selectColumn));
		return true;
	}
	
	@SuppressWarnings({"unchecked", "MissingMethodJavaDoc"})
	private boolean processSelectSumAsDouble(SelectExpression selectExpression)
	{
		Expression selectColumn = selectExpression.getAttribute();
		if (!isNullOrEmpty(selectExpression.getAlias()))
		{
			selectColumn.alias(selectExpression.getAlias());
		}
		getSelections().add(getCriteriaBuilder().sumAsDouble(selectColumn));
		return true;
	}
	
	@SuppressWarnings({"unchecked", "MissingMethodJavaDoc"})
	private boolean processSelectSumAsLong(SelectExpression selectExpression)
	{
		Expression selectColumn = selectExpression.getAttribute();
		if (!isNullOrEmpty(selectExpression.getAlias()))
		{
			selectColumn.alias(selectExpression.getAlias());
		}
		getSelections().add(getCriteriaBuilder().sumAsLong(selectColumn));
		return true;
	}
	
	/**
	 * Gets the criteria query linked to this root and builder
	 *
	 * @return A Criteria Query
	 */
	@Override
	public CriteriaQuery getCriteriaQuery()
	{
		if (criteriaQuery == null)
		{ criteriaQuery = getCriteriaBuilder().createQuery(); }
		return criteriaQuery;
	}
	
	/**
	 * Sets the criteria query for this instance
	 *
	 * @param criteriaDelete A delete statement to run
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J setCriteriaQuery(CriteriaDelete<E> criteriaDelete)
	{
		this.criteriaDelete = criteriaDelete;
		return (J) this;
	}
	
	/**
	 * Returns the map of join executors
	 *
	 * @return Returns a set of join expressions
	 */
	@Override
	@NotNull
	public Set<JoinExpression<?, ?, ?>> getJoins()
	{
		return joins;
	}
	
	/**
	 * Sets the entity to the given item
	 *
	 * @param entity The entity
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J setEntity(E entity)
	{
		super.setEntity(entity);
		root = getCriteriaQuery().from(entity.getClass());
		return (J) this;
	}
	
	/**
	 * Sets the entity to the given item
	 *
	 * @param entity The entity
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J setEntity(Object entity)
	{
		super.setEntity((E) entity);
		root = getCriteriaQuery().from(entity.getClass());
		return (J) this;
	}
	
	/**
	 * If a dto construct is required (classes that extend the entity as transports)
	 *
	 * @return Class of type that extends Base Entity
	 */
	@Override
	public Class<? extends BaseEntity> getConstruct()
	{
		return construct;
	}
	
	/**
	 * If a dto construct is required (classes that extend the entity as transports)
	 *
	 * @param construct The construct
	 * @return This object
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J construct(Class<? extends BaseEntity> construct)
	{
		this.construct = construct;
		return (J) this;
	}
	
	/**
	 * Returns the currently associated cache name
	 *
	 * @return The cache name associated
	 */
	@Override
	public String getCacheName()
	{
		return cacheName;
	}
	
	/**
	 * Enables query caching on the given query with the associated name
	 *
	 * @param cacheName The name for the given query
	 * @return Always this object
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public J setCacheName(@NotNull String cacheName, @NotNull String cacheRegion)
	{
		this.cacheName = cacheName;
		this.cacheRegion = cacheRegion;
		return (J) this;
	}
	
	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute The attribute to apply
	 * @param operator  The operator to apply
	 * @param value     The value to use
	 * @param <X>       The attribute type
	 * @param <Y>       The attribute field type
	 * @return This
	 */
	@Override
	@NotNull
	public <X, Y> J or(Attribute<X, Y> attribute, Operand operator, Collection<Y> value)
	{
		return or(attribute, operator, value, false);
	}
	
	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute The attribute to apply
	 * @param operator  The operator to apply
	 * @param value     The value to use
	 * @param <X>       The attribute type
	 * @param <Y>       The attribute field type
	 * @return This
	 */
	@Override
	@SuppressWarnings({"Duplicates", "unchecked"})
	@NotNull
	public <X, Y> J or(Attribute<X, Y> attribute, Operand operator, Collection<Y> value, boolean nest)
	{
		return (J) or(attribute,  getRoot().get(attribute.getName()), operator, value, nest);
	}
	
	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute The attribute to apply
	 * @param operator  The operator to apply
	 * @param value     The value to use
	 * @param <X>       The attribute type
	 * @param <Y>       The attribute field type
	 * @return This
	 */
	//@Override
	@SuppressWarnings({"Duplicates", "unchecked"})
	@NotNull
	 <X, Y> J or(Attribute attr, Expression<X> attribute, Operand operator, Collection<Y> value, boolean nest)
	{
		GroupedExpression groupedExpression = new GroupedExpression();
		groupedExpression.setGroupedFilterType(GroupedFilterType.Or);
		WhereExpression<X, Y> whereExpression = new WhereExpression<>(attr, attribute, operator, value);
		processOr(groupedExpression, whereExpression, nest);
		return (J) this;
	}
	
	/**
	 * Processes the OR statements
	 *
	 * @param groupedExpression The grouped expression to use
	 * @param whereExpression   The where expression to apply
	 * @param nest              Where to begin a new nest of expressions or not
	 */
	private void processOr(GroupedExpression groupedExpression, WhereExpression whereExpression, boolean nest)
	{
		if (!getWhereExpressions().isEmpty())
		{
			//Grab the last where expression, remove it from the list, then group into the or
			IFilterExpression fe = new ArrayList<>(getWhereExpressions()).get(getWhereExpressions().size() - 1);
			Predicate p = new ArrayList<>(getFilters()).get(getFilters().size() - 1);
			if (WhereExpression.class.isAssignableFrom(fe.getClass()))
			{
				WhereExpression<?, ?> lastExpression = (WhereExpression<?, ?>) fe;
				groupedExpression.getFilterExpressions()
				                 .add(lastExpression);
				getWhereExpressions().remove(lastExpression);
				getFilters().remove(p);
			}
			else if (GroupedExpression.class.isAssignableFrom(fe.getClass()))
			{
				GroupedExpression lastExpression = (GroupedExpression) fe;
				if (nest)
				{
					lastExpression.getFilterExpressions()
					              .add(groupedExpression);
				}
				else
				{
					groupedExpression.getFilterExpressions()
					                 .add(lastExpression);
					getWhereExpressions().remove(lastExpression);
					getFilters().remove(p);
				}
			}
		}
		
		//or/then add an expression for or as a group of 1
		groupedExpression.getFilterExpressions()
		                 .add(whereExpression);
		
		getWhereExpressions().add(groupedExpression);
		Optional<Predicate> predicate = groupedExpression.toPredicate(getCriteriaBuilder());
		predicate.ifPresent(predicate1 -> getFilters().add(predicate1));
	}
	
	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute The attribute to apply
	 * @param operator  The operator to apply
	 * @param value     The value to use
	 * @param <X>       The attribute type
	 * @param <Y>       The attribute field type
	 * @return This
	 */
	@Override
	public <X, Y> J or(Attribute<X, Y> attribute, Operand operator, Y value)
	{
		return or(attribute, operator, value, false);
	}
	
	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute The attribute to apply
	 * @param operator  The operator to apply
	 * @param value     The value to use
	 * @param <X>       The attribute type
	 * @param <Y>       The attribute field type
	 * @param nest      If must nest a new group or not
	 * @return This
	 */
	@Override
	@SuppressWarnings({"Duplicates", "unchecked"})
	@NotNull
	public <X, Y> J or(Attribute<X, Y> attribute, Operand operator, Y value, boolean nest)
	{
		return (J) or(attribute, getRoot().get(attribute.getName()), operator, value, nest);
	}
	
	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute The attribute to apply
	 * @param operator  The operator to apply
	 * @param value     The value to use
	 * @param <X>       The attribute type
	 * @param <Y>       The attribute field type
	 * @param nest      If must nest a new group or not
	 * @return This
	 */
	//@Override
	@SuppressWarnings({"Duplicates", "unchecked"})
	@NotNull
	<X, Y> J or(Attribute attr, Expression<X> attribute, Operand operator, Y value, boolean nest)
	{
		GroupedExpression groupedExpression = new GroupedExpression();
		groupedExpression.setGroupedFilterType(GroupedFilterType.Or);
		WhereExpression<X, Y> whereExpression = new WhereExpression<>(attr, attribute, operator, value);
		processOr(groupedExpression, whereExpression, nest);
		return (J) this;
	}
	
	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute The attribute to apply
	 * @param operator  The operator to apply
	 * @param value     The value to use
	 * @param <X>       The attribute type
	 * @param <Y>       The attribute field type
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J or(Attribute<X, Y> attribute, Operand operator, Y[] value)
	{
		return or(attribute, operator, value, false);
	}
	
	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute The attribute to apply
	 * @param operator  The operator to apply
	 * @param value     The value to use
	 * @param <X>       The attribute type
	 * @param <Y>       The attribute field type
	 * @param nest      To start a new group or not
	 * @return This
	 */
	@Override
	@SuppressWarnings({"Duplicates", "unchecked"})
	@NotNull
	public <X, Y> J or(Attribute<X, Y> attribute, Operand operator, Y[] value, boolean nest)
	{
		return (J) or(attribute, getRoot().get(attribute.getName()), operator, value, nest);
	}
	
	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute The attribute to apply
	 * @param operator  The operator to apply
	 * @param value     The value to use
	 * @param <X>       The attribute type
	 * @param <Y>       The attribute field type
	 * @param nest      To start a new group or not
	 * @return This
	 */
//	@Override
	@SuppressWarnings({"Duplicates", "unchecked"})
	@NotNull
	public <X, Y> J or(Attribute attr, Expression<X> attribute, Operand operator, Y[] value, boolean nest)
	{
		GroupedExpression groupedExpression = new GroupedExpression();
		groupedExpression.setGroupedFilterType(GroupedFilterType.Or);
		WhereExpression<X, Y> whereExpression = new WhereExpression<>(attr, attribute, operator, value);
		processOr(groupedExpression, whereExpression, nest);
		return (J) this;
	}
	
	private boolean isNullOrEmpty(String value)
	{
		return value == null || value.isEmpty() || value.isBlank()  || value.trim().isEmpty()  || value.trim().isBlank();
	}
}
