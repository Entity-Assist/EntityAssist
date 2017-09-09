package za.co.mmagon.entityassist.querybuilder;


import com.armineasy.injection.GuiceContext;
import com.armineasy.injection.Pair;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Record;
import org.hibernate.Criteria;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @param <J> This Class
 * @param <E> Entity Class
 *
 * @author Marc Magon
 */
public abstract class QueryBuilderCore<J extends QueryBuilderCore<J, E, I>, E extends CoreEntity<E, J, I>, I extends Serializable>
{
	private static final Logger log = Logger.getLogger(QueryBuilderCore.class.getName());
	
	protected Class<E> entityClass;
	
	private final CriteriaBuilder criteriaBuilder;
	private CriteriaQuery criteriaQuery;
	
	private final Root<E> root;
	
	private final Set<Join<E, ? extends CoreEntity>> joins;
	
	private final Set<Predicate> filters;
	private final Set<Selection> selections;
	private final Set<Predicate> groupBys;
	private final Set<Pair<SingularAttribute,OrderByType>> orderBys;
	private final Set<Expression> having;
	
	private Integer maxResults;
	private Integer firstResults;
	
	
	/**
	 * Static provider to generate sql for
	 */
	private static Provider provider = Provider.EcliseLink;
	
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
	
	/**
	 * Performs a join on this entity
	 *
	 * @param <JOIN>
	 *
	 * @return
	 */
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
	private String getFieldNameForJoinEntityType(Class<? extends CoreEntity> joinClassType)
	{
		Class<? extends Annotation> joinAnnotation = JoinColumn.class;
		Optional<Field> fOpt = GuiceContext.reflect().getFieldAnnotatedWithOfType(joinAnnotation, joinClassType, entityClass);
		if (fOpt.isPresent())
		{
			String name = fOpt.get().getName();
			return name;
		}
		log.log(Level.WARNING, "Unable to get field name for specified join type to [" + joinClassType + "]");
		
		return "";
	}
	
	/**
	 * Returns the associated query builder class
	 *
	 * @return
	 */
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
	public J find(Long id)
	{
		Optional<Field> idField = GuiceContext.reflect().getFieldAnnotatedWithOfType(Id.class, Long.class, entityClass);
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
	public J max(SingularAttribute attribute)
	{
		getSelections().add(getCriteriaBuilder().max(getRoot().get(attribute)));
		return (J) this;
	}
	
	public J min(SingularAttribute attribute)
	{
		getSelections().add(getCriteriaBuilder().min(getRoot().get(attribute)));
		return (J) this;
	}
	
	public J min(PluralAttribute attribute)
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
	 *
	 * @return
	 */
	public <T extends CoreEntity>  Optional<T> get(Class<T> returnType)
	{
		return get(false, false,returnType);
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
				//sqlQuery = getSelectSQLHibernate4(criteriaQuery);
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
			}
		}
		System.out.println("\n" + sqlQuery + "\n");
		E j = null;
		try
		{
			j = (E) query.getSingleResult();
			em.detach(j);
			((E) j).setFake(false);
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
				j = (E) returnedList.get(0);
				em.detach(j);
				((E) j).setFake(false);
				return Optional.of(j);
			}
			else
			{
				return Optional.empty();
			}
		}
		
	}
	
	/**
	 * Returns a list (distinct or not) and returns an empty optional if returns a list (use getAll)
	 *
	 * @param distinct
	 *
	 * @return
	 */
	public <T extends CoreEntity>  Optional<T> get(boolean distinct,Class<T> returnType)
	{
		return get(distinct, false,returnType);
	}
	
	
	/**
	 * Returns a list (distinct or not) and returns an empty optional if returns a list, or will simply return the first result found from a list with the same criteria
	 *
	 * @param distinct
	 * @param returnFirst
	 *
	 * @return
	 */
	public <T extends CoreEntity> Optional<T> get(boolean distinct, boolean returnFirst,Class<T> returnType)
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		TypedQuery<T> query = em.createQuery(getCriteriaQuery());
		
		String sqlQuery = "";
		switch (getProvider())
		{
			case Hibernate3:
			case Hibernate4:
			{
				//sqlQuery = getSelectSQLHibernate4(criteriaQuery);
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
			}
		}
		System.out.println("\n" + sqlQuery + "\n");
		T j = null;
		try
		{
			j = (T) query.getSingleResult();
			em.detach(j);
			((T) j).setFake(false);
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
				j = (T) returnedList.get(0);
				em.detach(j);
				((T) j).setFake(false);
				return Optional.of(j);
			}
			else
			{
				return Optional.empty();
			}
		}
		
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
	 * Returns a list of entities from a distinct or non distinct list
	 *
	 * @param distinct
	 *
	 * @return
	 */
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
			{
				//sqlQuery = getSelectSQLHibernate4(criteriaQuery);
				break;
			}
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
			}
		}
		System.out.println("\n" + sqlQuery + "\n");
		List<E> j = null;
		try
		{
			j = (List) query.getResultList();
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
	
	public J count()
	{
		getSelections().add(getCriteriaBuilder().count(getRoot()));
		return (J) this;
	}
	
	public J count(SingularAttribute attribute)
	{
		getSelections().add(getCriteriaBuilder().count(getRoot().get(attribute)));
		return (J) this;
	}
	
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
			for (Pair<SingularAttribute,OrderByType> p : getOrderBys())
			{
				switch (p.getValue())
				{
					case ASC:
					{
						cq.orderBy(criteriaBuilder.asc(getRoot().get(p.getKey())));
						break;
					}
					case DESC:
					{
						cq.orderBy(criteriaBuilder.desc(getRoot().get(p.getKey())));
						break;
					}
				}
			}
		}
		return (J) this;
	}
	
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
	
	public J inDateRange()
	{
		return inDateRange(LocalDateTime.now());
	}
	
	public J inDateRange(LocalDateTime date)
	{
		getFilters().add(getCriteriaBuilder().greaterThanOrEqualTo(getRoot().<LocalDateTime>get("effectiveFromDate"), date));
		getFilters().add(getCriteriaBuilder().lessThanOrEqualTo(getRoot().<LocalDateTime>get("effectiveToDate"), date));
		return (J) this;
	}
	
	public J inDateRangeSpecified(LocalDateTime fromDate)
	{
		return inDateRange(LocalDateTime.now());
	}
	
	public J inDateRange(LocalDateTime fromDate, LocalDateTime toDate)
	{
		getFilters().add(getCriteriaBuilder().greaterThanOrEqualTo(getRoot().<LocalDateTime>get("effectiveFromDate"), fromDate));
		getFilters().add(getCriteriaBuilder().lessThanOrEqualTo(getRoot().<LocalDateTime>get("effectiveToDate"), toDate));
		return (J) this;
	}
	
	public Set<Predicate> getFilters()
	{
		return filters;
	}
	
	private String getSelectSQLEclipseLink(TypedQuery query, EntityManager em)
	{
		org.eclipse.persistence.sessions.Session session = em.unwrap(JpaEntityManager.class).getActiveSession();
		DatabaseQuery databaseQuery = query.unwrap(EJBQueryImpl.class).getDatabaseQuery();
		databaseQuery.prepareCall(session, new DatabaseRecord());
		databaseQuery.bindAllParameters();
		Record r = databaseQuery.getTranslationRow();
		String bound = databaseQuery.getTranslatedSQLString(session, r);
		bound = bound.replace("{ts ", "");
		bound = bound.replace("'})", "')");
		return bound;
	}
	
	public String getSelectSQLHibernate4(Criteria criteria)
	{
		String sql = "";
		Object[] parameters = null;
		try
		{
			CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
			SessionImpl sessionImpl = (SessionImpl) criteriaImpl.getSession();
			SessionFactoryImplementor factory = sessionImpl.getSessionFactory();
			String[] implementors = factory.getImplementors(criteriaImpl.getEntityOrClassName());
			OuterJoinLoadable persister = (OuterJoinLoadable) factory.getEntityPersister(implementors[0]);
			LoadQueryInfluencers loadQueryInfluencers = new LoadQueryInfluencers();
			CriteriaLoader loader = new CriteriaLoader(persister, factory,
			                                           criteriaImpl, implementors[0].toString(), loadQueryInfluencers);
			Field f = OuterJoinLoader.class.getDeclaredField("sql");
			f.setAccessible(true);
			sql = (String) f.get(loader);
			Field fp = CriteriaLoader.class.getDeclaredField("translator");
			fp.setAccessible(true);
			CriteriaQueryTranslator translator = (CriteriaQueryTranslator) fp.get(loader);
			parameters = translator.getQueryParameters().getPositionalParameterValues();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		if (sql != null)
		{
			int fromPosition = sql.indexOf(" from ");
			sql = "\nSELECT * " + sql.substring(fromPosition);
			
			if (parameters != null && parameters.length > 0)
			{
				for (Object val : parameters)
				{
					String value = "%";
					if (val instanceof Boolean)
					{
						value = ((Boolean) val) ? "1" : "0";
					}
					else if (val instanceof String)
					{
						value = "'" + val + "'";
					}
					else if (val instanceof Number)
					{
						value = val.toString();
					}
					else if (val instanceof Class)
					{
						value = "'" + ((Class) val).getCanonicalName() + "'";
					}
					else if (val instanceof Date)
					{
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss.SSS");
						value = "'" + sdf.format((Date) val) + "'";
					}
					else if (val instanceof LocalDate)
					{
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd");
						value = "'" + sdf.format(val) + "'";
					}
					else if (val instanceof LocalDateTime)
					{
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss.SSS");
						value = "'" + sdf.format(val) + "'";
					}
					else if (val instanceof Enum)
					{
						value = "" + ((Enum) val).ordinal();
					}
					else
					{
						value = val.toString();
					}
					sql = sql.replaceFirst("\\?", value);
				}
			}
		}
		return sql.replaceAll("left outer join", "\nleft outer join").replaceAll(
				" and ", "\nand ").replaceAll(" on ", "\non ").replaceAll("<>",
		                                                                  "!=").replaceAll("<", " < ").replaceAll(">", " > ");
	}
	
	public String getSelectSQLHibernate5(CriteriaQuery criteria)
	{
		String sql = "";
		Object[] parameters = null;
		try
		{
			CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
			SessionImpl sessionImpl = (SessionImpl) criteriaImpl.getSession();
			SessionFactoryImplementor factory = sessionImpl.getSessionFactory();
			String[] implementors = factory.getImplementors(criteriaImpl.getEntityOrClassName());
			OuterJoinLoadable persister = (OuterJoinLoadable) factory.getEntityPersister(implementors[0]);
			LoadQueryInfluencers loadQueryInfluencers = new LoadQueryInfluencers();
			CriteriaLoader loader = new CriteriaLoader(persister, factory,
			                                           criteriaImpl, implementors[0].toString(), loadQueryInfluencers);
			Field f = OuterJoinLoader.class.getDeclaredField("sql");
			f.setAccessible(true);
			sql = (String) f.get(loader);
			Field fp = CriteriaLoader.class.getDeclaredField("translator");
			fp.setAccessible(true);
			CriteriaQueryTranslator translator = (CriteriaQueryTranslator) fp.get(loader);
			parameters = translator.getQueryParameters().getPositionalParameterValues();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		if (sql != null)
		{
			int fromPosition = sql.indexOf(" from ");
			sql = "\nSELECT * " + sql.substring(fromPosition);
			
			if (parameters != null && parameters.length > 0)
			{
				for (Object val : parameters)
				{
					String value = "%";
					if (val instanceof Boolean)
					{
						value = ((Boolean) val) ? "1" : "0";
					}
					else if (val instanceof String)
					{
						value = "'" + val + "'";
					}
					else if (val instanceof Number)
					{
						value = val.toString();
					}
					else if (val instanceof Class)
					{
						value = "'" + ((Class) val).getCanonicalName() + "'";
					}
					else if (val instanceof Date)
					{
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss.SSS");
						value = "'" + sdf.format((Date) val) + "'";
					}
					else if (val instanceof LocalDate)
					{
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd");
						value = "'" + sdf.format(val) + "'";
					}
					else if (val instanceof LocalDateTime)
					{
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss.SSS");
						value = "'" + sdf.format(val) + "'";
					}
					else if (val instanceof Enum)
					{
						value = "" + ((Enum) val).ordinal();
					}
					else
					{
						value = val.toString();
					}
					sql = sql.replaceFirst("\\?", value);
				}
			}
		}
		return sql.replaceAll("left outer join", "\nleft outer join").replaceAll(
				" and ", "\nand ").replaceAll(" on ", "\non ").replaceAll("<>",
		                                                                  "!=").replaceAll("<", " < ").replaceAll(">", " > ");
	}
	
	
	public Integer getFirstResults()
	{
		return firstResults;
	}
	
	public Integer getMaxResults()
	{
		return maxResults;
	}
	
	/**
	 * Returns the current sql generator provider
	 *
	 * @return
	 */
	public static Provider getProvider()
	{
		return provider;
	}
	
	/**
	 * Returns the current sql generator provider
	 *
	 * @param provider
	 *
	 * @return
	 */
	public static void setProvider(Provider provider)
	{
		provider = provider;
	}
	
	/**
	 * Sets the maximum results to return
	 *
	 * @param maxResults
	 *
	 * @return
	 */
	public J setMaxResults(Integer maxResults)
	{
		this.maxResults = maxResults;
		return (J) this;
	}
	
	/**
	 * Sets the first restults to return
	 *
	 * @param firstResults
	 *
	 * @return
	 */
	public J setFirstResults(Integer firstResults)
	{
		this.firstResults = firstResults;
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
	 * Sets the maximum number of results to return
	 *
	 * @param maxResults
	 *
	 * @return
	 */
	public J setMaxResults(int maxResults)
	{
		return (J) this;
	}
	
	/**
	 * Sets the number of first results to return
	 *
	 * @param firstResult
	 *
	 * @return
	 */
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
	public Set<Pair<SingularAttribute,OrderByType>> getOrderBys()
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
