package com.jwebmp.entityassist.querybuilder.builders;

import com.jwebmp.entityassist.BaseEntity;
import com.jwebmp.entityassist.enumerations.GroupedFilterType;
import com.jwebmp.entityassist.enumerations.Operand;
import com.jwebmp.entityassist.enumerations.OrderByType;
import com.jwebmp.entityassist.querybuilder.QueryBuilder;
import com.jwebmp.logger.LogFactory;

import javax.persistence.Id;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

import static com.jwebmp.entityassist.enumerations.SelectAggregrate.*;
import static com.jwebmp.entityassist.querybuilder.builders.IFilterExpression.*;

@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
public abstract class DefaultQueryBuilder<J extends DefaultQueryBuilder<J, E, I>, E extends BaseEntity<E, ? extends QueryBuilder, I>, I extends Serializable>
		extends QueryBuilderBase<J, E, I>
{
	/**
	 * The logger
	 */
	private static final Logger log = LogFactory.getLog("DefaultQueryBuilder");

	/**
	 * The actual builder for the entity
	 */
	private final CriteriaBuilder criteriaBuilder;
	/**
	 * The set of joins to apply
	 */
	private final Set<JoinExpression> joins;
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
	private final Set<Selection> selections;
	/**
	 * A list of group by's to go by. Built at generation time
	 */
	private final Set<Expression> groupBys;
	/**
	 * A list of order by's. Generated at generation time
	 */
	private final Map<Attribute, OrderByType> orderBys;
	/**
	 * A list of havingExpressions clauses
	 */
	private final Set<Expression> havingExpressions;
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
	 * A cache region name to apply
	 */
	private String cacheRegion;
	/**
	 * The physical criteria query
	 */
	private CriteriaQuery criteriaQuery;
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
	private From root;

	/**
	 * Constructs a new query builder core with typed classes instantiated
	 */
	@SuppressWarnings("unchecked")
	public DefaultQueryBuilder()
	{
		filters = new HashSet<>();
		selections = new HashSet<>();
		groupBys = new HashSet<>();
		orderBys = new LinkedHashMap<>();

		criteriaBuilder = getEntityManager().getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery();

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
	protected Set<Selection> getSelections()
	{
		return selections;
	}

	/**
	 * Gets the criteria builder
	 *
	 * @return The criteria builder
	 */
	protected CriteriaBuilder getCriteriaBuilder()
	{
		return criteriaBuilder;
	}

	/**
	 * Gets my given root
	 *
	 * @return The From object that is being used
	 */
	public From getRoot()
	{
		return root;
	}

	/**
	 * Sets the root of this builder
	 *
	 * @param root
	 * 		The FROM to use
	 *
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setRoot(From root)
	{
		this.root = root;
		return (J) this;
	}

	/**
	 * Where the "id" field is in
	 *
	 * @param id
	 * 		Finds by ID
	 *
	 * @return This (Use get to return results)
	 */
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
			getFilters().add(getRoot().get(idField.get()
			                                      .getName())
			                          .in(id));
		}
		else
		{
			getFilters().add(getRoot().get("id")
			                          .in(id));
		}
		return (J) this;
	}

	/**
	 * Returns the collection of filters that are going to be applied in build
	 *
	 * @return A set of predicates
	 */
	protected Set<Predicate> getFilters()
	{
		return filters;
	}

	/**
	 * Selects the minimum count of the root object (select count(*))
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	protected J selectCount()
	{
		getSelections().add(getCriteriaBuilder().count(getRoot()));
		return (J) this;
	}

	/**
	 * Joins the given builder with an inner join and no associated builder
	 *
	 * @param attribute
	 * 		The given attribute to join on
	 *
	 * @return This object - Configure each joins filters separately in their builders
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute)
	{
		return join(attribute, null, JoinType.INNER);
	}

	/**
	 * Joins the given builder with the given builder and build type
	 *
	 * @param attribute
	 * 		The given attribute to join on
	 * @param builder
	 * 		A Query Builder object that contains the construct of the query
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, QueryBuilder builder, JoinType joinType)
	{
		JoinExpression joinExpression = new JoinExpression(builder, joinType, attribute);
		joins.add(joinExpression);
		return (J) this;
	}

	/**
	 * Joins the given builder with the given builder and build type
	 *
	 * @param attribute
	 * 		The given attribute to join on
	 * @param builder
	 * 		A Query Builder object that contains the construct of the query
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, QueryBuilder builder, JoinType joinType, QueryBuilder onClauses)
	{
		JoinExpression joinExpression = new JoinExpression(builder, joinType, attribute);
		joinExpression.setOnBuilder(onClauses);
		joins.add(joinExpression);
		return (J) this;
	}

	/**
	 * Joins the given builder in an inner join with the given builder
	 *
	 * @param attribute
	 * 		The given attribute to join on
	 * @param builder
	 * 		A Query Builder object that contains the construct of the query
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, QueryBuilder builder)
	{
		return join(attribute, builder, JoinType.INNER);
	}

	/**
	 * Joins the given builder With the given join type and no associated builder
	 *
	 * @param attribute
	 * 		The given attribute to join on
	 *
	 * @return The join type to use
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, JoinType joinType)
	{
		return join(attribute, null, joinType);
	}

	/**
	 * Where the field name is equal to the value
	 *
	 * @param fieldName
	 * 		The field name
	 * @param value
	 * 		The value to use - Collection, Arrays, etc
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	public J in(String fieldName, Object value)
	{
		where((Attribute) getRoot().get(fieldName), Operand.Equals, value);
		return (J) this;
	}

	/**
	 * Performs a filter on the database with the where clauses
	 *
	 * @param attribute
	 * 		The attribute to be used
	 * @param operator
	 * 		The operand to use
	 * @param value
	 * 		The value to apply (Usually serializable)
	 *
	 * @return This object
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public <X, Y> J where(Attribute<X, Y> attribute, Operand operator, Y value)
	{
		WhereExpression<X, Y> whereExpression = new WhereExpression<>(attribute, operator, value);
		whereExpressions.add(whereExpression);
		doWhere(whereExpression);
		return (J) this;
	}

	/**
	 * Processes the where expressions into filters
	 *
	 * @param whereExpression
	 * 		The where expressions
	 * @param <X>
	 * 		The attribute type
	 * @param <Y>
	 * 		The column type
	 */
	private <X, Y> void doWhere(WhereExpression<X, Y> whereExpression)
	{
		Optional<Predicate> predicate = whereExpression.toPredicate(getRoot(), getCriteriaBuilder());
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
	 * @param fieldName
	 * 		The field name
	 * @param value
	 * 		The value to use - Collection, Arrays, etc
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	public <X, Y> J in(Attribute<X, Y> fieldName, Y value)
	{
		where(fieldName, Operand.InList, value);
		return (J) this;
	}

	/**
	 * Where the field name is equal to the value
	 *
	 * @param fieldName
	 * 		The field name
	 * @param value
	 * 		The value to use - Collection, Arrays, etc
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	public <X, Y> J in(Attribute<X, Y> fieldName, Collection<Y> value)
	{
		where(fieldName, Operand.InList, value);
		return (J) this;
	}

	/**
	 * Where the operand is the type of collection or list
	 *
	 * @param attribute
	 * 		The column to where on
	 * @param operator
	 * 		The operand to use
	 * @param value
	 * 		The value to apply
	 * @param <X>
	 * 		The attribute type
	 * @param <Y>
	 * 		The attribute value type
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public <X, Y> J where(Attribute<X, Y> attribute, Operand operator, Collection<Y> value)
	{
		WhereExpression<X, Y> whereExpression = new WhereExpression<>(attribute, operator, value);
		whereExpressions.add(whereExpression);
		doWhere(whereExpression);
		return (J) this;
	}

	/**
	 * Where the field name is equal to the value
	 *
	 * @param fieldName
	 * 		The field name
	 * @param value
	 * 		The value to use - Collection, Arrays, etc
	 *
	 * @return This
	 */
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
	 * @param attribute
	 * 		Select column
	 * @param operator
	 * 		The operand to use
	 * @param value
	 * 		The value to use
	 * @param <X>
	 * 		The the attribute column type
	 * @param <Y>
	 * 		The field value type
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public <X, Y> J where(Attribute<X, Y> attribute, Operand operator, Y[] value)
	{
		WhereExpression<X, Y> whereExpression = new WhereExpression<>(attribute, operator, value);
		whereExpressions.add(whereExpression);
		doWhere(whereExpression);
		return (J) this;
	}

	/**
	 * Gets the cache region for this query
	 *
	 * @return The applied cache region or null
	 */
	public String getCacheRegion()
	{
		return cacheRegion;
	}

	/**
	 * Sets a cache region for this query
	 *
	 * @param cacheRegion
	 * 		To a cache region
	 *
	 * @return This
	 */
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
	 * @param orderBy
	 * 		Which attribute to order by
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J orderBy(Attribute<X, Y> orderBy)
	{
		return orderBy(orderBy, OrderByType.ASC);
	}

	/**
	 * Adds an order by column to the query
	 *
	 * @param orderBy
	 * 		Order by which column
	 * @param direction
	 * 		The direction to apply
	 *
	 * @return This
	 */
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
	Set<OrderByExpression> getOrderByExpressions()
	{
		return orderByExpressions;
	}

	/**
	 * Returns the current list of order by's
	 *
	 * @return A map of attributes and order by types
	 */
	protected Map<Attribute, OrderByType> getOrderBys()
	{
		return orderBys;
	}

	/**
	 * Selects a given column
	 *
	 * @param selectColumn
	 * 		The column to group by
	 *
	 * @return This
	 */
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
	Set<GroupByExpression> getGroupByExpressions()
	{
		return groupByExpressions;
	}

	/**
	 * Returns the current list of group by's
	 *
	 * @return A set of expressions
	 */
	public Set<Expression> getGroupBys()
	{
		return groupBys;
	}

	/**
	 * If the builder is set to delete
	 *
	 * @return if it is in a delete statement
	 */
	protected boolean isDelete()
	{
		return delete;
	}

	/**
	 * If the builder is set to delete
	 *
	 * @param delete
	 * 		if this must run as a delete statement
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	protected J setDelete(boolean delete)
	{
		this.delete = delete;
		return (J) this;
	}

	/**
	 * Returns the criteria delete, which is nullable
	 *
	 * @return The criteria delete or null
	 */

	protected CriteriaDelete<E> getCriteriaDelete()
	{
		return criteriaDelete;
	}

	/**
	 * Sets the criteria delete
	 *
	 * @param criteriaDelete
	 * 		A delete criteria delete
	 */
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
	public boolean isUpdate()
	{
		return update;
	}

	/**
	 * If the builder is set to update
	 *
	 * @param update
	 * 		If is update
	 *
	 * @return This
	 */
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

	@SuppressWarnings("unchecked")
	protected CriteriaUpdate<E> getCriteriaUpdate()
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
	 * @param criteriaUpdate
	 * 		The criteria update from a criteria builder
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	protected J setCriteriaUpdate(CriteriaUpdate<E> criteriaUpdate)
	{
		this.criteriaUpdate = criteriaUpdate;
		return (J) this;
	}

	/**
	 * Resets to the given new root and constructs the select query
	 * Not CRP to make sure you know whats going on
	 *
	 * @param newRoot
	 * 		A FROM object to reset to
	 */
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
				doWhere((WhereExpression) whereExpression);
			}
		}
		getSelectExpressions().forEach(this::redoSelectExpression);
	}

	/**
	 * Gets the havingExpressions list for this builder
	 *
	 * @return A set of expressions for the havingExpressions clause
	 */
	protected Set<Expression> getHavingExpressions()
	{
		return havingExpressions;
	}

	/**
	 * Returns a set of the where expressions
	 *
	 * @return A set of IFilterExpressions
	 */
	Set<IFilterExpression> getWhereExpressions()
	{
		return whereExpressions;
	}

	/**
	 * A set of select expression
	 *
	 * @return Returns the select expressions
	 */
	Set<SelectExpression> getSelectExpressions()
	{
		return selectExpressions;
	}

	/**
	 * Rebuilds the expressions for the select options
	 *
	 * @param selectExpression
	 * 		The column to reapply
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
	 * @param selectColumn
	 * 		The given column from the static metadata
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectColumn(Attribute selectColumn)
	{
		SelectExpression selectExpression = new SelectExpression(selectColumn, None);
		selectExpressions.add(selectExpression);
		processSelectExpressionNone(selectExpression);
		return (J) this;
	}

	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute
	 * 		A given column from static metadata
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectAverage(Attribute attribute)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, None);
		selectExpressions.add(selectExpression);
		processSelectAverage(selectExpression);
		return (J) this;
	}

	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute
	 * 		A given column from static metadata
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCount(Attribute attribute)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, None);
		selectExpressions.add(selectExpression);
		processSelectCount(selectExpression);
		return (J) this;
	}

	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute
	 * 		A given column from static metadata
	 *
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCountDistinct(Attribute attribute)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, None);
		selectExpressions.add(selectExpression);
		processSelectCountDistinct(selectExpression);
		return (J) this;
	}

	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute
	 * 		A given column from static metadata
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectMax(Attribute attribute)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, None);
		selectExpressions.add(selectExpression);
		processSelectExpressionMax(selectExpression);
		return (J) this;
	}

	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute
	 * 		A given column from static metadata
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectMin(Attribute attribute)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, None);
		selectExpressions.add(selectExpression);
		processSelectExpressionMin(selectExpression);
		return (J) this;
	}

	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute
	 * 		A given column from static metadata
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSum(Attribute attribute)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, None);
		selectExpressions.add(selectExpression);
		processSelectSum(selectExpression);
		return (J) this;
	}

	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute
	 * 		A given column from static metadata
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSumAsDouble(Attribute attribute)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, None);
		selectExpressions.add(selectExpression);
		processSelectSumAsDouble(selectExpression);
		return (J) this;
	}

	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute
	 * 		A given column from static metadata
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectSumAsLong(Attribute attribute)
	{
		SelectExpression selectExpression = new SelectExpression(attribute, None);
		selectExpressions.add(selectExpression);
		processSelectSumAsLong(selectExpression);
		return (J) this;
	}

	/**
	 * Processes the select expression
	 *
	 * @param selectExpression
	 * 		A given column from static metadata
	 *
	 * @return true or false
	 */
	@SuppressWarnings("unchecked")
	private boolean processSelectExpressionNone(SelectExpression selectExpression)
	{
		Attribute selectColumn = selectExpression.getAttribute();
		if (isSingularAttribute(selectColumn))
		{
			getSelections().add(getRoot().get((SingularAttribute) selectColumn));
		}
		else if (isPluralOrMapAttribute(selectColumn))
		{
			getSelections().add(getRoot().get((PluralAttribute) selectColumn));
		}
		return true;
	}

	/**
	 * processes the select average
	 *
	 * @param selectExpression
	 * 		A given column from static metadata
	 *
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	private boolean processSelectAverage(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().avg(getRoot().get((SingularAttribute) attribute)));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().avg(getRoot().get((PluralAttribute) attribute)));
		}
		return true;
	}

	/**
	 * Adds a select count to the criteria builder
	 *
	 * @param selectExpression
	 * 		A given column from static metadata
	 *
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	private boolean processSelectCount(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().count(getRoot().get((SingularAttribute) attribute)));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().count(getRoot().get((PluralAttribute) attribute)));
		}
		return true;
	}

	/**
	 * Processes to return the select count
	 *
	 * @param selectExpression
	 * 		A given column from static metadata
	 *
	 * @return true or false for this
	 */
	@SuppressWarnings("unchecked")
	private boolean processSelectCountDistinct(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().countDistinct(getRoot().get((SingularAttribute) attribute)));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().countDistinct(getRoot().get((PluralAttribute) attribute)));
		}
		return true;
	}

	@SuppressWarnings({"unchecked", "MissingMethodJavaDoc"})
	private boolean processSelectExpressionMax(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().max(getRoot().get((SingularAttribute) attribute)));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().max(getRoot().get((PluralAttribute) attribute)));
		}
		return true;
	}

	@SuppressWarnings({"unchecked", "MissingMethodJavaDoc"})
	private boolean processSelectExpressionMin(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().min(getRoot().get((SingularAttribute) attribute)));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().min(getRoot().get((PluralAttribute) attribute)));
		}
		return true;
	}

	@SuppressWarnings({"unchecked", "MissingMethodJavaDoc"})
	private boolean processSelectSum(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sum(getRoot().get((SingularAttribute) attribute)));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sum(getRoot().get((PluralAttribute) attribute)));
		}
		return true;
	}

	@SuppressWarnings({"unchecked", "MissingMethodJavaDoc"})
	private boolean processSelectSumAsDouble(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sumAsDouble(getRoot().get((SingularAttribute) attribute)));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sumAsDouble(getRoot().get((PluralAttribute) attribute)));
		}
		return true;
	}

	@SuppressWarnings({"unchecked", "MissingMethodJavaDoc"})
	private boolean processSelectSumAsLong(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sumAsLong(getRoot().get((SingularAttribute) attribute)));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sumAsLong(getRoot().get((PluralAttribute) attribute)));
		}
		return true;
	}

	/**
	 * Gets the criteria query linked to this root and builder
	 *
	 * @return A Criteria Query
	 */
	protected CriteriaQuery getCriteriaQuery()
	{
		return criteriaQuery;
	}

	/**
	 * Sets the criteria query for this instance
	 *
	 * @param criteriaDelete
	 * 		A delete statement to run
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	protected J setCriteriaQuery(CriteriaDelete<E> criteriaDelete)
	{
		this.criteriaDelete = criteriaDelete;
		return (J) this;
	}

	/**
	 * Returns the map of join executors
	 *
	 * @return Returns a set of join expressions
	 */
	@NotNull
	public Set<JoinExpression> getJoins()
	{
		return joins;
	}

	/**
	 * Sets the entity to the given item
	 *
	 * @param entity
	 * 		The entity
	 *
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J setEntity(E entity)
	{
		super.setEntity(entity);
		root = criteriaQuery.from(entity.getClass());
		return (J) this;
	}

	/**
	 * Sets the entity to the given item
	 *
	 * @param entity
	 * 		The entity
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setEntity(Object entity)
	{
		super.setEntity((E) entity);
		root = criteriaQuery.from(entity.getClass());
		return (J) this;
	}

	/**
	 * If a dto construct is required (classes that extend the entity as transports)
	 *
	 * @return Class of type that extends Base Entity
	 */
	protected Class<? extends BaseEntity> getConstruct()
	{
		return construct;
	}

	/**
	 * If a dto construct is required (classes that extend the entity as transports)
	 *
	 * @param construct
	 * 		The construct
	 *
	 * @return This object
	 */
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
	public String getCacheName()
	{
		return cacheName;
	}

	/**
	 * Enables query caching on the given query with the associated name
	 *
	 * @param cacheName
	 * 		The name for the given query
	 *
	 * @return Always this object
	 */
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
	 * @param attribute
	 * 		The attribute to apply
	 * @param operator
	 * 		The operator to apply
	 * @param value
	 * 		The value to use
	 * @param <X>
	 * 		The attribute type
	 * @param <Y>
	 * 		The attribute field type
	 *
	 * @return This
	 */
	@NotNull
	public <X, Y> J or(Attribute<X, Y> attribute, Operand operator, Collection<Y> value)
	{
		return or(attribute, operator, value, false);
	}

	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute
	 * 		The attribute to apply
	 * @param operator
	 * 		The operator to apply
	 * @param value
	 * 		The value to use
	 * @param <X>
	 * 		The attribute type
	 * @param <Y>
	 * 		The attribute field type
	 *
	 * @return This
	 */
	@SuppressWarnings({"Duplicates", "unchecked"})
	@NotNull
	public <X, Y> J or(Attribute<X, Y> attribute, Operand operator, Collection<Y> value, boolean nest)
	{
		GroupedExpression groupedExpression = new GroupedExpression();
		groupedExpression.setGroupedFilterType(GroupedFilterType.Or);
		WhereExpression<X, Y> whereExpression = new WhereExpression<>(attribute, operator, value);
		processOr(groupedExpression, whereExpression, nest);
		return (J) this;
	}

	/**
	 * Processes the OR statements
	 *
	 * @param groupedExpression
	 * 		The grouped expression to use
	 * @param whereExpression
	 * 		The where expression to apply
	 * @param nest
	 * 		Where to begin a new nest of expressions or not
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
		Optional<Predicate> predicate = groupedExpression.toPredicate(getRoot(), getCriteriaBuilder());
		predicate.ifPresent(predicate1 -> getFilters().add(predicate1));
	}

	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute
	 * 		The attribute to apply
	 * @param operator
	 * 		The operator to apply
	 * @param value
	 * 		The value to use
	 * @param <X>
	 * 		The attribute type
	 * @param <Y>
	 * 		The attribute field type
	 *
	 * @return This
	 */
	public <X, Y> J or(Attribute<X, Y> attribute, Operand operator, Y value)
	{
		return or(attribute, operator, value, false);
	}

	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute
	 * 		The attribute to apply
	 * @param operator
	 * 		The operator to apply
	 * @param value
	 * 		The value to use
	 * @param <X>
	 * 		The attribute type
	 * @param <Y>
	 * 		The attribute field type
	 * @param nest
	 * 		If must nest a new group or not
	 *
	 * @return This
	 */
	@SuppressWarnings({"Duplicates", "unchecked"})
	@NotNull
	public <X, Y> J or(Attribute<X, Y> attribute, Operand operator, Y value, boolean nest)
	{
		GroupedExpression groupedExpression = new GroupedExpression();
		groupedExpression.setGroupedFilterType(GroupedFilterType.Or);
		WhereExpression<X, Y> whereExpression = new WhereExpression<>(attribute, operator, value);
		processOr(groupedExpression, whereExpression, nest);
		return (J) this;
	}

	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute
	 * 		The attribute to apply
	 * @param operator
	 * 		The operator to apply
	 * @param value
	 * 		The value to use
	 * @param <X>
	 * 		The attribute type
	 * @param <Y>
	 * 		The attribute field type
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J or(Attribute<X, Y> attribute, Operand operator, Y[] value)
	{
		return or(attribute, operator, value, false);
	}

	/**
	 * Adds an OR group to the filter expressions with the previous where statement
	 *
	 * @param attribute
	 * 		The attribute to apply
	 * @param operator
	 * 		The operator to apply
	 * @param value
	 * 		The value to use
	 * @param <X>
	 * 		The attribute type
	 * @param <Y>
	 * 		The attribute field type
	 * @param nest
	 * 		To start a new group or not
	 *
	 * @return This
	 */
	@SuppressWarnings({"Duplicates", "unchecked"})
	@NotNull
	public <X, Y> J or(Attribute<X, Y> attribute, Operand operator, Y[] value, boolean nest)
	{
		GroupedExpression groupedExpression = new GroupedExpression();
		groupedExpression.setGroupedFilterType(GroupedFilterType.Or);
		WhereExpression<X, Y> whereExpression = new WhereExpression<>(attribute, operator, value);
		processOr(groupedExpression, whereExpression, nest);
		return (J) this;
	}
}
