package za.co.mmagon.entityassist.querybuilder.builders;

import za.co.mmagon.entityassist.BaseEntity;
import za.co.mmagon.entityassist.enumerations.Operand;
import za.co.mmagon.entityassist.enumerations.OrderByType;

import javax.persistence.Id;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

public abstract class DefaultQueryBuilder<J extends DefaultQueryBuilder<J, E, I>, E extends BaseEntity<E, J, I>, I extends Serializable>
		extends QueryBuilderBase<J, E, I>
{
	private static final Logger log = Logger.getLogger(DefaultQueryBuilder.class.getName());

	/**
	 * The actual builder for the entity
	 */
	private final CriteriaBuilder criteriaBuilder;
	/**
	 * Returns the root object of this entity
	 */
	private final Root<E> root;
	/**
	 * A set of all the joins applied to this specific entity
	 */
	private final Set<Join<E, ? extends BaseEntity>> joins;
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

	/**
	 * The physical criteria query
	 */
	private CriteriaQuery criteriaQuery;

	/**
	 * Constructs a new query builder core with typed classes instantiated
	 */
	@SuppressWarnings("unchecked")
	public DefaultQueryBuilder()
	{
		this.filters = new HashSet<>();
		selections = new HashSet<>();
		groupBys = new HashSet<>();
		orderBys = new LinkedHashMap<>();
		having = new HashSet<>();
		joins = new HashSet<>();
		this.criteriaBuilder = getEntityManager().getCriteriaBuilder();
		this.criteriaQuery = criteriaBuilder.createQuery();
		root = criteriaQuery.from(getEntityClass());
	}

	/**
	 * Joins a specific attribute
	 *
	 * @param attribute
	 * @param <X>
	 * @param <Y>
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <X, Y> J join(Attribute<X, Y> attribute)
	{
		return join(attribute, JoinType.INNER);
	}

	/**
	 * Joins a specific attribute
	 *
	 * @param attribute
	 * @param <X>
	 * @param <Y>
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <X, Y> J join(Attribute<X, Y> attribute, JoinType joinType)
	{
		if (isSingularAttribute(attribute))
		{
			getRoot().join(SingularAttribute.class.cast(attribute), joinType);
		}
		else if (isCollectionAttribute(attribute))
		{
			getRoot().join(CollectionAttribute.class.cast(attribute), joinType);
		}
		else if (isMapAttribute(attribute))
		{
			getRoot().join(MapAttribute.class.cast(attribute), joinType);
		}
		else
		{
			getRoot().join(attribute.getName(), joinType);
		}
		return (J) this;
	}

	/**
	 * Gets my given root
	 *
	 * @return
	 */
	protected Root<? extends BaseEntity> getRoot()
	{
		return root;
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
		getFilters().add(getRoot().get(fieldName).in(value));
		return (J) this;
	}

	protected Set<Predicate> getFilters()
	{
		return filters;
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

	protected Set<Join<E, ? extends BaseEntity>> getJoins()
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
	 * @param criteriaQuery
	 */
	protected void setCriteriaQuery(CriteriaQuery criteriaQuery)
	{
		this.criteriaQuery = criteriaQuery;
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
		Field[] allFields = id.getClass().getFields();
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
			getFilters().add(getRoot().get(idField.get().getName()).in(id));
		}
		else
		{
			getFilters().add(getRoot().get("id").in(id));
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
	public J selectColumn(Attribute selectColumn)
	{
		if (isSingularAttribute(selectColumn))
		{
			getSelections().add(getRoot().get(SingularAttribute.class.cast(selectColumn)));
		}
		else if (isPluralOrMapAttribute(selectColumn))
		{
			getSelections().add(getRoot().get(PluralAttribute.class.cast(selectColumn)));
		}
		return (J) this;
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
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().min(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().min(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
		return (J) this;
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
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().max(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().max(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
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
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().count(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().count(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
		return (J) this;
	}

	/**
	 * Selects the minimum count of the root object (select count(*))
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J selectCount()
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
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().countDistinct(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().countDistinct(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
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
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sum(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sum(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
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
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sumAsLong(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sumAsLong(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
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
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sumAsDouble(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().sumAsDouble(getRoot().get(PluralAttribute.class.cast(attribute))));
		}
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
		if (isSingularAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().avg(getRoot().get(SingularAttribute.class.cast(attribute))));
		}
		else if (isPluralOrMapAttribute(attribute))
		{
			getSelections().add(getCriteriaBuilder().avg(getRoot().get(PluralAttribute.class.cast(attribute))));
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

	/**
	 * Returns the current list of group by's
	 *
	 * @return
	 */
	protected Set<Expression> getGroupBys()
	{
		return groupBys;
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
	public J where(Attribute attribute, Operand operator, Object value)
	{
		switch (operator)
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
				break;
			}
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
				break;
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
				break;
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
				break;
			}
			case InList:
			{
				CriteriaBuilder.In<Object> in = null;
				Expression<Object> path = null;
				if (isSingularAttribute(attribute))
				{
					path = getRoot().get(SingularAttribute.class.cast(attribute));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					path = getRoot().get(PluralAttribute.class.cast(attribute));
				}
				in = getCriteriaBuilder().in(path);
				buildInObject(in, value);
				getFilters().add(in);
				break;
			}
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
				break;
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
				break;
			}
			case LessThan:
			case LessThanEqualTo:
			case GreaterThan:
			case GreaterThanEqualTo:
			default:
			{
				return where(attribute, operator, (Number) value);
			}
		}

		return (J) this;
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
	private Set buildInObject(CriteriaBuilder.In<Object> inClause, @NotNull Object object)
	{
		boolean isArray = object.getClass().isArray();
		boolean isCollection = Collection.class.isAssignableFrom(object.getClass());
		boolean isMap = Map.class.isAssignableFrom(object.getClass());

		if (!(isArray || isCollection || isMap))
		{
			log.warning("Where In List Clause was not an array collection or map");
			return new HashSet();
		}

		Set output = new LinkedHashSet();
		if (isArray)
		{
			Collections.addAll(output, (Object[]) object);
		}
		if (isCollection)
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
	private <N extends Number> J where(Attribute attribute, Operand operator, N value)
	{
		switch (operator)
		{
			case LessThan:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().lt(getRoot().get((SingularAttribute) attribute), value));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().lt(getRoot().get((PluralAttribute) attribute), value));
				}
				break;
			}
			case LessThanEqualTo:
			{
				if (isSingularAttribute(attribute))
				{
					Expression<? extends Number> path = getRoot().get(SingularAttribute.class.cast(attribute));
					getFilters().add(getCriteriaBuilder().le(path, value));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().le(getRoot().get((PluralAttribute) attribute), value));
				}
				break;
			}
			case GreaterThan:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().gt(getRoot().get((SingularAttribute) attribute), value));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().gt(getRoot().get((PluralAttribute) attribute), value));
				}
				break;
			}
			case GreaterThanEqualTo:
			{
				if (isSingularAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().ge(getRoot().get((SingularAttribute) attribute), value));
				}
				else if (isPluralOrMapAttribute(attribute))
				{
					getFilters().add(getCriteriaBuilder().ge(getRoot().get((PluralAttribute) attribute), value));
				}
				break;
			}
			default:
			{
				break;
			}
		}
		return (J) this;
	}

}
