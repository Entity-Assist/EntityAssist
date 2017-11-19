package za.co.mmagon.entityassist.querybuilder.builders;

import com.armineasy.injection.GuiceContext;
import za.co.mmagon.entityassist.BaseEntity;
import za.co.mmagon.entityassist.CoreEntity;
import za.co.mmagon.entityassist.enumerations.OrderByType;

import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static za.co.mmagon.entityassist.querybuilder.EntityAssistStrings.STRING_EMPTY;

public class DefaultQueryBuilder<J extends DefaultQueryBuilder<J, E, I>, E extends BaseEntity<E, J, I>, I extends Serializable>
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
	private final Set<Predicate> groupBys;
	/**
	 * A list of order by's. Generated at generation time
	 */
	private final Set<Map<SingularAttribute, OrderByType>> orderBys;
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
	public DefaultQueryBuilder()
	{
		this.filters = new HashSet<>();
		selections = new HashSet<>();
		groupBys = new HashSet<>();
		orderBys = new HashSet<>();
		having = new HashSet<>();
		joins = new HashSet<>();
		this.criteriaBuilder = GuiceContext.getInstance(EntityManager.class).getCriteriaBuilder();
		this.criteriaQuery = criteriaBuilder.createQuery();
		root = criteriaQuery.from(getEntityClass());
	}


	/**
	 * Performs a join on this entity
	 *
	 * @param <JOIN>
	 *
	 * @return
	 */
	public <O extends CoreEntity> J join(Class<O> entityClassJoinTo)
	{
		return join(entityClassJoinTo, Optional.empty(), Optional.empty(), Optional.empty());
	}

	/**
	 * Performs a join on this entity
	 *
	 * @param <JOIN>
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <O extends CoreEntity> J join(Class<O> entityClassJoinTo, Optional<List<Predicate>> onFilters, Optional<List<Predicate>> entityFilters, Optional<JoinType> joinType)
	{
		String joinFieldName = getFieldNameForJoinEntityType(entityClassJoinTo);
		Join<E, O> join;
		if (joinType.isPresent())
		{
			join = getRoot().join(joinFieldName, joinType.get());
		}
		else
		{
			join = getRoot().join(joinFieldName, JoinType.LEFT);
		}
		if (onFilters.isPresent())
		{
			Predicate[] preds = new Predicate[onFilters.get().size()];
			join.on(onFilters.get().toArray(preds));
		}
		entityFilters.ifPresent(predicates -> getFilters().addAll(predicates));
		return (J) this;
	}

	/**
	 * Returns the first field name for the given class type
	 *
	 * @param joinClassType
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getFieldNameForJoinEntityType(Class<? extends CoreEntity> joinClassType)
	{
		Class<? extends Annotation> joinAnnotation = JoinColumn.class;
		Optional<Field> fOpt = GuiceContext.reflect().getFieldAnnotatedWithOfType(joinAnnotation, joinClassType, getEntityClass());
		if (fOpt.isPresent())
		{
			return fOpt.get().getName();
		}
		log.log(Level.WARNING, "Unable to get field name for specified join type to [" + joinClassType + "]");

		return STRING_EMPTY;
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

	protected Set<Predicate> getFilters()
	{
		return filters;
	}

	/**
	 * Performs a join on this entity
	 *
	 * @param <JOIN>
	 *
	 * @return
	 */
	public <O extends CoreEntity> J join(Class<O> entityClassJoinTo, Optional<List<Predicate>> onFilters)
	{
		return join(entityClassJoinTo, onFilters, Optional.empty(), Optional.empty());
	}

	/**
	 * Performs a join on this entity
	 *
	 * @param <JOIN>
	 *
	 * @return
	 */
	public <O extends CoreEntity> J join(Class<O> entityClassJoinTo, Optional<List<Predicate>> onFilters, Optional<List<Predicate>> entityFilters)
	{
		return join(entityClassJoinTo, onFilters, entityFilters, Optional.empty());
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

	/**
	 * Adds a max column to be added with a group by clause at the end
	 *
	 * @param attribute
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J max(PluralAttribute attribute)
	{
		getSelections().add(getCriteriaBuilder().max(getRoot().get(attribute)));
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
	 * Gets the criteria builder
	 *
	 * @return
	 */
	protected CriteriaBuilder getCriteriaBuilder()
	{
		return criteriaBuilder;
	}

	/**
	 * Adds a max column to be added with a group by clause at the end
	 *
	 * @param attribute
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J max(SingularAttribute attribute)
	{
		getSelections().add(getCriteriaBuilder().max(getRoot().get(attribute)));
		return (J) this;
	}

	@SuppressWarnings("unchecked")
	public J min(SingularAttribute attribute)
	{
		getSelections().add(getCriteriaBuilder().min(getRoot().get(attribute)));
		return (J) this;
	}

	@SuppressWarnings("unchecked")
	public J min(PluralAttribute attribute)
	{
		getSelections().add(getCriteriaBuilder().min(getRoot().get(attribute)));
		return (J) this;
	}

	@SuppressWarnings("unchecked")
	public J count()
	{
		getSelections().add(getCriteriaBuilder().count(getRoot()));
		return (J) this;
	}

	@SuppressWarnings("unchecked")
	public J count(SingularAttribute attribute)
	{
		getSelections().add(getCriteriaBuilder().count(getRoot().get(attribute)));
		return (J) this;
	}

	@SuppressWarnings("unchecked")
	public J count(PluralAttribute attribute)
	{
		getSelections().add(getCriteriaBuilder().count(getRoot().get(attribute)));
		return (J) this;
	}

	/**
	 * Returns the current list of group by's
	 *
	 * @return
	 */
	protected Set<Predicate> getGroupBys()
	{
		return groupBys;
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

	/**
	 * Returns the current list of order by's
	 *
	 * @return
	 */
	protected Set<Map<SingularAttribute, OrderByType>> getOrderBys()
	{
		return orderBys;
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
}
