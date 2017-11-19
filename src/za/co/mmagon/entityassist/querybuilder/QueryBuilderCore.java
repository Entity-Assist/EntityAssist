package za.co.mmagon.entityassist.querybuilder;
import com.armineasy.injection.GuiceContext;
import za.co.mmagon.entityassist.CoreEntity;
import za.co.mmagon.entityassist.enumerations.ActiveFlag;
import za.co.mmagon.entityassist.enumerations.OrderByType;
import za.co.mmagon.entityassist.enumerations.Provider;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static za.co.mmagon.entityassist.querybuilder.statements.SelectStatement.getSelectSQLEclipseLink;
import static za.co.mmagon.entityassist.querybuilder.statements.SelectStatement.getSelectSQLHibernate5;

/**
 * @param <J> This Class
 * @param <E> Entity Class
 *
 * @author Marc Magon
 */
public abstract class QueryBuilderCore<J extends QueryBuilderCore<J, E, I>, E extends CoreEntity<E, J, I>, I extends Serializable>
{
	private static final Logger log = Logger.getLogger(QueryBuilderCore.class.getName());

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
	private final Set<Join<E, ? extends CoreEntity>> joins;
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
	 * The given entity class
	 */
	protected Class<E> entityClass;
	/**
	 * Static provider to generate sql for
	 */
	private Provider provider = Provider.EcliseLink;
	/**
	 * The physical criteria query
	 */
	private CriteriaQuery criteriaQuery;
	/**
	 * The maximum number of results
	 */
	private Integer maxResults;
	/**
	 * The minimum number of results
	 */
	private Integer firstResults;
	
	/**
	 * Constructs a new query builder core with typed classes instantiated
	 */
	public QueryBuilderCore()
	{
		this.entityClass = getClassEntity();
		this.filters = new HashSet<>();
		selections = new HashSet<>();
		groupBys = new HashSet<>();
		orderBys = new HashSet<>();
		having = new HashSet<>();
		joins = new HashSet<>();
		this.criteriaBuilder = GuiceContext.getInstance(EntityManager.class).getCriteriaBuilder();
		this.criteriaQuery = criteriaBuilder.createQuery();
		root = criteriaQuery.from(this.entityClass);
	}

	/**
	 * Returns a list (distinct or not) and returns an empty optional if returns a list, or will simply return the first result found from a list with the same criteria
	 *
	 * @param distinct
	 * @param returnFirst
	 *
	 * @return
	 */
	public Optional<E> get(boolean distinct, boolean returnFirst)
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		TypedQuery<E> query = em.createQuery(getCriteriaQuery());

		String sqlQuery = "";
		switch (getProvider())
		{
			case Hibernate3:
			case Hibernate4:
			{
				break;
			}
			case Hibernate5:
			case Hibernate5jre8:
			{
				sqlQuery = getSelectSQLHibernate5(getCriteriaQuery());
				break;
			}
			case EcliseLink:
			{
				sqlQuery = getSelectSQLEclipseLink(query, em);
				break;
			}
			default:
			{
				break;
			}
		}
		log.info(sqlQuery);
		E j = null;
		try
		{
			j = query.getSingleResult();
			j.setFake(false);
			return Optional.of(j);
		}
		catch (NoResultException nre)
		{
			log.log(Level.WARNING, "Couldn''t find object with name : {0}}", new Object[]
					                                                                 {
							                                                                 getClass().getName()
					                                                                 });

			return Optional.empty();
		}
		catch (NonUniqueResultException nure)
		{
			if (returnFirst)
			{
				List<E> returnedList = query.getResultList();
				j = returnedList.get(0);
				em.detach(j);
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
	 * Returns the current sql generator provider
	 *
	 * @return
	 */
	public Provider getProvider()
	{
		return provider;
	}

	/**
	 * Performs a join on this entity
	 *
	 * @param <JOIN>
	 *
	 * @return
	 */
	public <JOIN extends CoreEntity> J join(Class<JOIN> entityClassJoinTo)
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
	public <JOIN extends CoreEntity> J join(Class<JOIN> entityClassJoinTo, Optional<List<Predicate>> onFilters)
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
	public <JOIN extends CoreEntity> J join(Class<JOIN> entityClassJoinTo, Optional<List<Predicate>> onFilters, Optional<List<Predicate>> entityFilters)
	{
		return join(entityClassJoinTo, onFilters, entityFilters, Optional.empty());
	}

	@SuppressWarnings("unchecked")
	protected Class<E> getClassEntity()
	{
		if (entityClass == null)
		{
			try
			{
				this.entityClass = (Class<E>) ((ParameterizedType) getClass()
						                                                   .getGenericSuperclass()).getActualTypeArguments()[1];
			}
			catch (Exception e)
			{
				this.entityClass = null;
			}
		}
		return entityClass;
	}
	
	/**
	 * Performs a join on this entity
	 *
	 * @param <JOIN>
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <JOIN extends CoreEntity> J join(Class<JOIN> entityClassJoinTo, Optional<List<Predicate>> onFilters, Optional<List<Predicate>> entityFilters, Optional<JoinType> joinType)
	{
		String joinFieldName = getFieldNameForJoinEntityType(entityClassJoinTo);
		Join<E, JOIN> join;
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
		if (entityFilters.isPresent())
		{
			getFilters().addAll(entityFilters.get());
		}

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
		Optional<Field> fOpt = GuiceContext.reflect().getFieldAnnotatedWithOfType(joinAnnotation, joinClassType, entityClass);
		if (fOpt.isPresent())
		{
			return fOpt.get().getName();
		}
		log.log(Level.WARNING, "Unable to get field name for specified join type to [" + joinClassType + "]");

		return "";
	}
	
	/**
	 * Returns the associated query builder class
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<J> getClassQueryBuilder()
	{
		return (Class<J>) getClass();
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
	 * Where the "id" field is in
	 *
	 * @param id
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J find(Long id)
	{
		Optional<Field> idField = GuiceContext.reflect().getFieldAnnotatedWithOfType(Id.class, Long.class, entityClass);
		if (!idField.isPresent())
		{
			Field[] fields = entityClass.getDeclaredFields();
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
	 * Returns a list (distinct or not) and returns an empty optional if returns a list (use getAll)
	 *
	 * @return
	 */
	public <T extends CoreEntity> Optional<T> get(Class<T> returnType)
	{
		return get(false, false, returnType);
	}
	
	/**
	 * Returns a list (distinct or not) and returns an empty optional if returns a list (use getAll)
	 *
	 * @param distinct
	 *
	 * @return
	 */
	public Optional<E> get(boolean distinct)
	{
		return get(distinct, false);
	}

	@SuppressWarnings("unchecked")
	public J min(PluralAttribute attribute)
	{
		getSelections().add(getCriteriaBuilder().min(getRoot().get(attribute)));
		return (J) this;
	}
	
	/**
	 * Returns a list (distinct or not) and returns an empty optional if returns a list (use getAll)
	 *
	 * @param distinct
	 *
	 * @return
	 */
	public <T extends CoreEntity> Optional<T> get(boolean distinct, Class<T> returnType)
	{
		return get(distinct, false, returnType);
	}

	/**
	 * Returns the current sql generator provider
	 *
	 * @param provider
	 *
	 * @return
	 */
	public void setProvider(Provider provider)
	{
		this.provider = provider;
	}

	/**
	 * Returns a list of entities from a non-distinct select query
	 *
	 * @return
	 */
	public List<E> getAll()
	{
		return getAll(false);
	}
	
	/**
	 * Returns a list (distinct or not) and returns an empty optional if returns a list, or will simply return the first result found from a list with the same criteria
	 *
	 * @param distinct
	 * @param returnFirst
	 *
	 * @return
	 */
	public <T extends CoreEntity> Optional<T> get(boolean distinct, boolean returnFirst, Class<T> returnType)
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		TypedQuery<T> query = em.createQuery(getCriteriaQuery());

		String sqlQuery = "";
		switch (getProvider())
		{
			case Hibernate3:
			case Hibernate4:
			case Hibernate5:
			case Hibernate5jre8:
			{
				sqlQuery = getSelectSQLHibernate5(getCriteriaQuery());
				break;
			}
			case EcliseLink:
			{
				sqlQuery = getSelectSQLEclipseLink(query, em);
				break;
			}
			default:
			{
				break;
			}
		}
		log.info(sqlQuery);
		T j = null;
		try
		{
			j = query.getSingleResult();
			em.detach(j);
			j.setFake(false);
			return Optional.of(j);
		}
		catch (NoResultException nre)
		{
			log.log(Level.WARNING, "Couldn''t find object with name : {0}}", new Object[]
					{
							getClass().getName()
					});

			return Optional.empty();
		}
		catch (NonUniqueResultException nure)
		{
			if (returnFirst)
			{
				List<T> returnedList = query.getResultList();
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
	 * @param distinct
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<E> getAll(boolean distinct)
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		TypedQuery<E> query = em.createQuery(getCriteriaQuery());

		if (getMaxResults() != null)
		{
			query.setMaxResults(getMaxResults());
		}
		if (getFirstResults() != null)
		{
			query.setFirstResult(getFirstResults());
		}

		String sqlQuery = "";
		switch (getProvider())
		{
			case Hibernate3:
			case Hibernate4:
			case Hibernate5:
			case Hibernate5jre8:
			{
				sqlQuery = getSelectSQLHibernate5(criteriaQuery);
				break;
			}
			case EcliseLink:
			{
				sqlQuery = getSelectSQLEclipseLink(query, em);
				break;
			}
			default:
			{
				break;
			}
		}
		System.out.println(sqlQuery);
		List<E> j = null;
		try
		{
			j = query.getResultList();
			for (Object j1 : j)
			{
				CoreEntity wct = (CoreEntity) j1;
				wct.setFake(false);
				em.detach(wct);
			}
			return j;
		}
		catch (NoResultException nre)
		{
			return new ArrayList<>();
		}
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
	 * Prepares the select statement
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J select()
	{
		List<Predicate> allWheres = new ArrayList<>();
		allWheres.addAll(getFilters());

		Predicate[] preds = new Predicate[allWheres.size()];
		preds = allWheres.toArray(preds);

		CriteriaQuery<E> cq = getCriteriaQuery();

		if (!getSelections().isEmpty())
		{
			for (Selection selection : getSelections())
			{
				cq.select(selection);
			}
		}

		getCriteriaQuery().where(preds);
		if (!getGroupBys().isEmpty())
		{
			for (Predicate p : getGroupBys())
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
			getOrderBys().forEach(a -> a.forEach((key, value) ->
			                                     {
				                                     switch (value)
				                                     {
					                                     case ASC:
					                                     {
						                                     cq.orderBy(criteriaBuilder.asc(getRoot().get(key)));
						                                     break;
					                                     }
					                                     case DESC:
					                                     {
						                                     cq.orderBy(criteriaBuilder.desc(getRoot().get(key)));
						                                     break;
					                                     }
					                                     default:
					                                     {
						                                     break;
					                                     }
				                                     }
			                                     })
			                     );
		}
		return (J) this;
	}

	@SuppressWarnings("unchecked")
	public J inActiveRange()
	{
		List<ActiveFlag> flags = new ArrayList<>();
		for (ActiveFlag flag : ActiveFlag.values())
		{
			if (flag.ordinal() >= ActiveFlag.Active.ordinal())
			{
				flags.add(flag);
			}
		}
		getFilters().add(getRoot().get("activeFlag").in(flags));
		return (J) this;
	}

	public J inDateRange()
	{
		return inDateRange(LocalDateTime.now());
	}

	@SuppressWarnings("unchecked")
	public J inVisibleRange()
	{
		List<ActiveFlag> flags = new ArrayList<>();
		for (ActiveFlag flag : ActiveFlag.values())
		{
			if (flag.ordinal() >= ActiveFlag.Invisible.ordinal())
			{
				flags.add(flag);
			}
		}
		getFilters().add(getRoot().get("activeFlag").in(flags));
		return (J) this;
	}

	public J inDateRangeSpecified(LocalDateTime fromDate)
	{
		return inDateRange(LocalDateTime.now());
	}

	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime date)
	{
		getFilters().add(getCriteriaBuilder().greaterThanOrEqualTo(getRoot().get("effectiveFromDate"), date));
		getFilters().add(getCriteriaBuilder().lessThanOrEqualTo(getRoot().get("effectiveToDate"), date));
		return (J) this;
	}
	
	public Set<Predicate> getFilters()
	{
		return filters;
	}

	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime fromDate, LocalDateTime toDate)
	{
		getFilters().add(getCriteriaBuilder().greaterThanOrEqualTo(getRoot().get("effectiveFromDate"), fromDate));
		getFilters().add(getCriteriaBuilder().lessThanOrEqualTo(getRoot().get("effectiveToDate"), toDate));
		return (J) this;
	}

	
	public Integer getFirstResults()
	{
		return firstResults;
	}
	
	/**
	 * Sets the first restults to return
	 *
	 * @param firstResults
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J setFirstResults(Integer firstResults)
	{
		this.firstResults = firstResults;
		return (J) this;
	}
	
	public Integer getMaxResults()
	{
		return maxResults;
	}
	
	/**
	 * Sets the maximum results to return
	 *
	 * @param maxResults
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J setMaxResults(Integer maxResults)
	{
		this.maxResults = maxResults;
		return (J) this;
	}
	
	/**
	 * Sets the maximum number of results to return
	 *
	 * @param maxResults
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J setMaxResults(int maxResults)
	{
		return (J) this;
	}
	
	/**
	 * Gets the selections that are going to be applied, leave empty for all columns
	 *
	 * @return
	 */
	public Set<Selection> getSelections()
	{
		return selections;
	}
	
	/**
	 * Sets the number of first results to return
	 *
	 * @param firstResult
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J setFirstResult(int firstResult)
	{
		return (J) this;
	}
	
	/**
	 * Returns the associated entity class for this builder
	 *
	 * @return
	 */
	public Class<? extends CoreEntity> getEntityClass()
	{
		return entityClass;
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
	 * Gets the criteria builder
	 *
	 * @return
	 */
	public CriteriaBuilder getCriteriaBuilder()
	{
		return criteriaBuilder;
	}
	
	/**
	 * Gets my given root
	 *
	 * @return
	 */
	public Root<? extends CoreEntity> getRoot()
	{
		return root;
	}
	
	public Set<Join<E, ? extends CoreEntity>> getJoins()
	{
		return joins;
	}
	
	/**
	 * Returns the current list of group by's
	 *
	 * @return
	 */
	public Set<Predicate> getGroupBys()
	{
		return groupBys;
	}
	
	/**
	 * Returns the current list of order by's
	 *
	 * @return
	 */
	public Set<Map<SingularAttribute, OrderByType>> getOrderBys()
	{
		return orderBys;
	}
	
	/**
	 * Gets the havingn list for this builder
	 *
	 * @return
	 */
	public Set<Expression> getHaving()
	{
		return having;
	}
}
