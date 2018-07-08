package com.jwebmp.entityassist.querybuilder.builders;

import com.jwebmp.entityassist.BaseEntity;
import com.jwebmp.entityassist.enumerations.Operand;
import com.jwebmp.entityassist.enumerations.OrderByType;
import com.jwebmp.entityassist.querybuilder.QueryBuilderExecutor;

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

public abstract class DefaultQueryBuilder<J extends DefaultQueryBuilder<J, E, I>, E extends BaseEntity<E, ? extends QueryBuilderExecutor, I>, I extends Serializable>
		extends QueryBuilderBase<J, E, I>
{
	private static final Logger log = Logger.getLogger(DefaultQueryBuilder.class.getName());

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
	 * A list of having clauses
	 */
	private final Set<Expression> having;
	private final Set<WhereExpression> whereExpressions;
	private final Set<OrderByExpression> orderByExpressions;
	private final Set<GroupByExpression> groupByExpressions;
	/**
	 * The physical criteria query
	 */
	private CriteriaQuery criteriaQuery;
	/**
	 * The physical criteria query
	 */
	private CriteriaDelete criteriaDelete;
	/**
	 * The physical criteria query
	 */
	private CriteriaUpdate criteriaUpdate;
	private Class<? extends BaseEntity> construct;

	private boolean delete;
	private boolean update;

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
		having = new HashSet<>();
		criteriaBuilder = getEntityManager().getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery();

		joins = new LinkedHashSet<>();
		selectExpressions = new LinkedHashSet<>();
		whereExpressions = new LinkedHashSet<>();
		orderByExpressions = new LinkedHashSet<>();
		groupByExpressions = new LinkedHashSet<>();
	}

	private void redoSelectExpression(SelectExpression a)
	{
		switch (a.getAggregrate())
		{
			case None:
			{
				selectColumn(a.getAttribute());
				break;
			}
			case Avg:
			{
				selectAverage(a.getAttribute());
				break;
			}
			case Count:
			{
				selectCount(a.getAttribute());
				break;
			}
			case CountDistinct:
			{
				selectCountDistinct(a.getAttribute());
				break;
			}
			case Max:
			{
				selectMax(a.getAttribute());
				break;
			}
			case Min:
			{
				selectMin(a.getAttribute());
				break;
			}
			case Sum:
			{
				selectSum(a.getAttribute());
				break;
			}
			case SumDouble:
			{
				selectSumAsDouble(a.getAttribute());
				break;
			}
			case SumLong:
			{
				selectSumAsLong(a.getAttribute());
				break;
			}
		}
	}

	/**
	 * Selects a given column
	 *
	 * @param selectColumn
	 *
	 * @return
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
	 *
	 * @return
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
	 *
	 * @return
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
	 *
	 * @return
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
	 *
	 * @return
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
	 *
	 * @return
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
	 *
	 * @return
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
	 *
	 * @return
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
	 *
	 * @return
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

	@SuppressWarnings("unchecked")
	private boolean processSelectExpressionNone(SelectExpression selectExpression)
	{
		Attribute selectColumn = selectExpression.getAttribute();
		if (isSingularAttribute(selectColumn))
		{
			getSelections().add(getRoot().get(SingularAttribute.class.cast(selectColumn)));
		}
		else if (isPluralOrMapAttribute(selectColumn))
		{
			getSelections().add(getRoot().get(PluralAttribute.class.cast(selectColumn)));
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean processSelectAverage(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().avg(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().avg(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean processSelectCount(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().count(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().count(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean processSelectCountDistinct(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().countDistinct(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().countDistinct(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean processSelectExpressionMax(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().max(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().max(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean processSelectExpressionMin(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().min(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().min(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean processSelectSum(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sum(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sum(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean processSelectSumAsDouble(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sumAsDouble(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sumAsDouble(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean processSelectSumAsLong(SelectExpression selectExpression)
	{
		Attribute attribute = selectExpression.getAttribute();
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sumAsLong(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sumAsLong(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
		return true;
	}

	/**
	 * Gets the selections that are going to be applied, leave empty for all columns
	 *
	 * @return
	 */
	protected Set<Selection> getSelections()
	{
		return selections;
	}

	/**
	 * Gets my given root
	 *
	 * @return
	 */
	protected From getRoot()
	{
		return root;
	}

	/**
	 * Gets the criteria builder
	 *
	 * @return
	 */
	protected CriteriaBuilder getCriteriaBuilder()
	{
		return criteriaBuilder;
	}

	/**
	 * Sets the root of this builder
	 *
	 * @param root
	 */
	public void setRoot(From root)
	{
		this.root = root;
	}

	/**
	 * Where the "id" field is in
	 *
	 * @param id
	 *
	 * @return
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
	 * @return
	 */
	protected Set<Predicate> getFilters()
	{
		return filters;
	}

	/**
	 * Gets the criteria update object
	 *
	 * @return
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
	 * Resets to the given new root and constructs the select query
	 *
	 * @param newRoot
	 */
	public void reset(From newRoot)
	{
		setRoot(newRoot);
		getFilters().clear();
		getSelections().clear();
		getGroupBys().clear();
		getOrderBys().clear();
		getHaving().clear();

		getWhereExpressions().forEach(this::where);

		getSelectExpressions().forEach(this::redoSelectExpression);

	}

	/**
	 * Returns the current list of group by's
	 *
	 * @return
	 */
	public Set<Expression> getGroupBys()
	{
		return groupBys;
	}

	/**
	 * Returns the current list of order by's
	 *
	 * @return
	 */
	protected Map<Attribute, OrderByType> getOrderBys()
	{
		return orderBys;
	}

	/**
	 * Gets the havingn list for this builder
	 *
	 * @return
	 */
	protected Set<Expression> getHaving()
	{
		return having;
	}

	Set<WhereExpression> getWhereExpressions()
	{
		return whereExpressions;
	}

	Set<SelectExpression> getSelectExpressions()
	{
		return selectExpressions;
	}

	/**
	 * Sets the criteria update object
	 *
	 * @param criteriaUpdate
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	protected J setCriteriaUpdate(CriteriaUpdate criteriaUpdate)
	{
		this.criteriaUpdate = criteriaUpdate;
		return (J) this;
	}

	/**
	 * Selects the minimum count of the root object (select count(*))
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	protected J selectCount()
	{
		getSelections().add(getCriteriaBuilder().count(getRoot()));
		return (J) this;
	}

	/**
	 * Selects the minimum count distinct of the root object (select distinct count(*))
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCountDistinct()
	{
		getSelections().add(getCriteriaBuilder().count(getRoot()));
		return (J) this;
	}

	/**
	 * Joins the given builder
	 *
	 * @param attribute
	 * 		The given attribute to join on
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute)
	{
		return join(attribute, null, JoinType.INNER);
	}

	/**
	 * Joins the given builder
	 *
	 * @param attribute
	 * 		The given attribute to join on
	 * @param builder
	 * 		A Query Builder object that contains the construct of the query
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, QueryBuilderExecutor builder, JoinType joinType)
	{
		JoinExpression joinExpression = new JoinExpression(builder, joinType, attribute);
		joins.add(joinExpression);
		return (J) this;
	}

	/**
	 * Joins the given builder
	 *
	 * @param attribute
	 * 		The given attribute to join on
	 * @param builder
	 * 		A Query Builder object that contains the construct of the query
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <X, Y> J join(Attribute<X, Y> attribute, QueryBuilderExecutor builder)
	{
		return join(attribute, builder, JoinType.INNER);
	}

	/**
	 * Joins the given builder
	 *
	 * @param attribute
	 * 		The given attribute to join on
	 *
	 * @return
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
	 * @param value
	 *
	 * @return
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
		WhereExpression whereExpression = new WhereExpression(attribute, operator, value);
		whereExpressions.add(whereExpression);
		processWhereExpression(whereExpression);
		return (J) this;
	}

	private void processWhereExpression(WhereExpression whereExpression)
	{
		boolean result;
		result = processWhereNulls(whereExpression);
		if (!result)
		{
			result = processWhereEquals(whereExpression);
		}
		if (!result)
		{
			result = processWhereLike(whereExpression);
		}
		if (!result)
		{
			result = processWhereLists(whereExpression);
		}
		if (!result)
		{
			result = processWhereCompare(whereExpression);
		}
		if (!result)
		{
			log.severe("Unable to generate a where clause for the given expression");
		}
	}

	@SuppressWarnings("unchecked")
	private boolean processWhereNulls(WhereExpression whereExpression)
	{
		Attribute attribute = whereExpression.getAttribute();
		switch (whereExpression.getOperand())
		{
			case Null:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().isNull(getRoot().get(SingularAttribute.class.cast(attribute))));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().isNull(getRoot().get(PluralAttribute.class.cast(attribute))));
				}
				return true;
			}
			case NotNull:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().isNotNull(getRoot().get(SingularAttribute.class.cast(attribute))));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().isNotNull(getRoot().get(PluralAttribute.class.cast(attribute))));
				}
				return true;
			}
			default:
			{
				return false;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean processWhereEquals(WhereExpression whereExpression)
	{
		Attribute attribute = whereExpression.getAttribute();
		Object value = whereExpression.getValue();
		switch (whereExpression.getOperand())
		{
			case Equals:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().equal(getRoot().get(SingularAttribute.class.cast(attribute)), value));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().equal(getRoot().get(PluralAttribute.class.cast(attribute)), value));
				}
				return true;
			}

			case NotEquals:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().notEqual(getRoot().get(SingularAttribute.class.cast(attribute)), value));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().notEqual(getRoot().get(PluralAttribute.class.cast(attribute)), value));
				}
				return true;
			}
			default:
			{
				return false;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean processWhereLike(WhereExpression whereExpression)
	{
		Attribute attribute = whereExpression.getAttribute();
		Object value = whereExpression.getValue();
		switch (whereExpression.getOperand())
		{
			case Like:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().like(getRoot().get(SingularAttribute.class.cast(attribute)), value.toString()));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().like(getRoot().get(PluralAttribute.class.cast(attribute)), value.toString()));
				}
				return true;
			}
			case NotLike:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().notLike(getRoot().get(SingularAttribute.class.cast(attribute)), value.toString()));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().notLike(getRoot().get(PluralAttribute.class.cast(attribute)), value.toString()));
				}
				return true;
			}
			default:
			{
				return false;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean processWhereLists(WhereExpression whereExpression)
	{
		Attribute attribute = whereExpression.getAttribute();
		Object value = whereExpression.getValue();
		switch (whereExpression.getOperand())
		{
			case InList:
			{
				Expression<Object> path = null;
				if (isSingularAttribute(attribute))
				{
					path = getRoot().get(SingularAttribute.class.cast(attribute));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					path = getRoot().get(PluralAttribute.class.cast(attribute));
				}
				CriteriaBuilder.In<Object> in = getCriteriaBuilder().in(path);
				buildInObject(in, value);
				getFilters().add(in);
				return true;
			}
			case NotInList:
			{
				Expression<Object> path = null;
				if (isSingularAttribute(attribute))
				{
					path = getRoot().get(SingularAttribute.class.cast(attribute));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					path = getRoot().get(PluralAttribute.class.cast(attribute));
				}
				CriteriaBuilder.In<Object> in = getCriteriaBuilder().in(path);
				buildInObject(in, value);
				getFilters().add(getCriteriaBuilder().not(in));
				return true;
			}
			default:
			{
				return false;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private <T extends Comparable<T>> boolean processWhereCompare(WhereExpression whereExpression)
	{
		Attribute attribute = whereExpression.getAttribute();
		T value = (T) whereExpression.getValue();
		switch (whereExpression.getOperand())
		{
			case LessThan:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().lessThan(getRoot().get((SingularAttribute<E, T>) attribute), value));
				}
				return true;
			}
			case LessThanEqualTo:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().lessThanOrEqualTo(getRoot().get((SingularAttribute<E, T>) attribute), value));
				}
				return true;
			}
			case GreaterThan:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().greaterThan(getRoot().get((SingularAttribute<E, T>) attribute), value));
				}
				return true;
			}
			case GreaterThanEqualTo:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().greaterThanOrEqualTo(getRoot().get((SingularAttribute<E, T>) attribute), value));
				}
				return true;
			}
			default:
			{
				return false;
			}
		}
	}

	/**
	 * Builds the in cluase query
	 *
	 * @param inClause
	 * 		The in clause to add the values to
	 * @param object
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	private Set buildInObject(CriteriaBuilder.In<Object> inClause, @NotNull Object object)
	{
		boolean isArray = object.getClass()
		                        .isArray();
		boolean isCollection = Collection.class.isAssignableFrom(object.getClass());
		boolean isMap = Map.class.isAssignableFrom(object.getClass());

		Set output = new LinkedHashSet();

		if (!(isArray || isCollection || isMap))
		{
			log.warning("Where In List Clause was not an array collection or map, adding to set");
			output.add(object);
		}
		else if (isArray)
		{
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
	 * Where the operand is the type of collection or list
	 *
	 * @param attribute
	 * @param operator
	 * @param value
	 * @param <X>
	 * @param <Y>
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public <X, Y> J where(Attribute<X, Y> attribute, Operand operator, Collection<Y> value)
	{
		WhereExpression whereExpression = new WhereExpression(attribute, operator, value);
		whereExpressions.add(whereExpression);
		processWhereExpression(whereExpression);
		return (J) this;
	}

	/**
	 * Where the operand is the type of collection or list
	 *
	 * @param attribute
	 * @param operator
	 * @param value
	 * @param <X>
	 * @param <Y>
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public <X, Y> J where(Attribute<X, Y> attribute, Operand operator, Y[] value)
	{
		WhereExpression whereExpression = new WhereExpression(attribute, operator, value);
		whereExpressions.add(whereExpression);
		processWhereExpression(whereExpression);
		return (J) this;
	}

	/**
	 * Ords by column ascending
	 *
	 * @param orderBy
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J orderBy(Attribute orderBy)
	{
		return orderBy(orderBy, OrderByType.ASC);
	}

	/**
	 * Adds an order by column to the query
	 *
	 * @param orderBy
	 * @param direction
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J orderBy(Attribute orderBy, OrderByType direction)
	{
		orderByExpressions.add(new OrderByExpression(orderBy, direction));
		if (isSingularAttribute(orderBy))
		{
			getOrderBys().put(SingularAttribute.class.cast(orderBy), direction);
		}
		else if (isPluralOrMapAttribute(orderBy))
		{
			getOrderBys().put(PluralAttribute.class.cast(orderBy), direction);
		}
		return (J) this;
	}

	/**
	 * Selects a given column
	 *
	 * @param selectColumn
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J groupBy(Attribute selectColumn)
	{
		groupByExpressions.add(new GroupByExpression(selectColumn));
		if (isSingularAttribute(selectColumn))
		{
			getGroupBys().add(getRoot().get(SingularAttribute.class.cast(selectColumn)));
		}
		else if (isPluralOrMapAttribute(selectColumn))
		{
			getGroupBys().add(getRoot().get(PluralAttribute.class.cast(selectColumn)));
		}
		return (J) this;
	}

	@SuppressWarnings("unchecked")
	private J where(WhereExpression whereExpression)
	{
		processWhereExpression(whereExpression);
		return (J) this;
	}

	/**
	 * Returns the map of join executors
	 *
	 * @return
	 */
	@NotNull
	public Set<JoinExpression> getJoins()
	{
		return joins;
	}

	/**
	 * Gets the criteria query linked to this root and builder
	 *
	 * @return
	 */
	protected CriteriaQuery getCriteriaQuery()
	{
		return criteriaQuery;
	}

	/**
	 * Sets the criteria query for this instance
	 *
	 * @param criteriaDelete
	 */
	protected void setCriteriaQuery(CriteriaDelete criteriaDelete)
	{
		this.criteriaDelete = criteriaDelete;
	}

	/**
	 * Returns the criteria delete, which is nullable
	 *
	 * @return
	 */

	protected CriteriaDelete<E> getCriteriaDelete()
	{
		return criteriaDelete;
	}

	/**
	 * Sets the criteria delete
	 *
	 * @param criteriaDelete
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public J setCriteriaDelete(CriteriaDelete criteriaDelete)
	{
		this.criteriaDelete = criteriaDelete;
		delete = true;
		return (J) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setEntity(Object entity)
	{
		super.setEntity(entity);
		root = criteriaQuery.from(entity.getClass());
	}

	Set<OrderByExpression> getOrderByExpressions()
	{
		return orderByExpressions;
	}

	Set<GroupByExpression> getGroupByExpressions()
	{
		return groupByExpressions;
	}

	protected Class<? extends BaseEntity> getConstruct()
	{
		return construct;
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public J construct(Class<? extends BaseEntity> construct)
	{
		this.construct = construct;
		return (J) this;
	}

	/**
	 * If the builder is set to delete
	 *
	 * @return
	 */
	protected boolean isDelete()
	{
		return delete;
	}

	/**
	 * If the builder is set to delete
	 *
	 * @param delete
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	protected J setDelete(boolean delete)
	{
		this.delete = delete;
		return (J) this;
	}

	/**
	 * If the builder is set to update
	 *
	 * @return
	 */
	public boolean isUpdate()
	{
		return update;
	}

	/**
	 * If the builder is set to update
	 *
	 * @param update
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setUpdate(boolean update)
	{
		this.update = update;
		return (J) this;
	}
}
