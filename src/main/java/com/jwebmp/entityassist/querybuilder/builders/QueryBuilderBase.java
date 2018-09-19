package com.jwebmp.entityassist.querybuilder.builders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jwebmp.entityassist.BaseEntity;
import com.jwebmp.entityassist.querybuilder.QueryBuilder;
import com.jwebmp.entityassist.querybuilder.statements.InsertStatement;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.metamodel.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jwebmp.entityassist.querybuilder.EntityAssistStrings.*;

/**
 * Builds a Query Base
 *
 * @param <J>
 * 		This class type
 * @param <E>
 * 		The entity type
 * @param <I>
 * 		The entity ID type
 */
abstract class QueryBuilderBase<J extends QueryBuilderBase<J, E, I>, E extends BaseEntity<E, ? extends QueryBuilder, I>, I extends Serializable>
{

	private static final Logger log = Logger.getLogger("QueryBuilderBase");
	/**
	 * The maximum number of results
	 */
	private Integer maxResults;
	/**
	 * The minimum number of results
	 */
	private Integer firstResults;
	/**
	 * The given entity class
	 */
	private Class<E> entityClass;

	private String selectIdentityString = "SELECT @@IDENTITY";

	private E entity;

	/**
	 * Whether or not to run these queries as detached objects or within the entity managers scope
	 */
	@JsonIgnore
	@Transient
	private boolean runDetached;

	protected QueryBuilderBase()
	{
		entityClass = getEntityClass();
	}

	/**
	 * Returns the associated entity class for this builder
	 *
	 * @return
	 */
	protected Class<E> getEntityClass()
	{
		return entityClass;
	}

	/**
	 * Returns a mapped entity on this builder
	 *
	 * @return
	 */
	public E getEntity()
	{
		return entity;
	}

	public void setEntity(Object entity)
	{
		this.entity = (E) entity;
		entityClass = (Class<E>) entity.getClass();
	}

	/**
	 * Returns the current set first results
	 *
	 * @return
	 */
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
	public J setFirstResults(Integer firstResults)
	{
		this.firstResults = firstResults;
		return (J) this;
	}

	/**
	 * Returns the current set maximum results
	 *
	 * @return
	 */
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
	public J setMaxResults(Integer maxResults)
	{
		this.maxResults = maxResults;
		return (J) this;
	}

	/**
	 * Persist and Flush
	 *
	 * @return
	 */
	@NotNull
	public J persistNow(E entity)
	{
		persist(entity);
		getEntityManager().flush();
		return (J) this;
	}

	/**
	 * Persists this entity. Uses the get instance entity manager to operate.
	 *
	 * @return
	 */
	@NotNull
	public J persist(E entity)
	{
		try
		{
			onCreate(entity);
			EntityManager entityManager = getEntityManager();
			if (isRunDetached())
			{
				String insertString = InsertStatement.buildInsertString(entity);
				log.fine(insertString);
				entityManager.createNativeQuery(insertString)
				             .executeUpdate();
				if (isIdGenerated())
				{
					Query statmentSelectId = entityManager.createNativeQuery(selectIdentityString);
					BigDecimal generatedId = ((BigDecimal) statmentSelectId.getSingleResult());
					entity.setId((I) (Long) generatedId.longValue());
				}
			}
			else
			{
				entityManager.persist(entity);
			}
			entity.setFake(false);
		}
		catch (IllegalStateException ise)
		{
			log.log(Level.SEVERE, "This entity is not in a state to be persisted. Perhaps an update merge remove or refresh?", ise);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Unable to persist, exception occured\n", e);
			throw new UnsupportedOperationException(e);
		}
		return (J) this;
	}

	/**
	 * Returns the assigned entity manager
	 *
	 * @return
	 */
	@NotNull
	@Transient
	protected abstract EntityManager getEntityManager();

	/**
	 * Performed on create/persist
	 *
	 * @param entity
	 * 		The entity
	 *
	 * @return true if must still create
	 */
	protected boolean onCreate(E entity)
	{
		return true;
	}

	/**
	 * If this entity should run in a detached and separate to the entity manager
	 *
	 * @return
	 */
	public boolean isRunDetached()
	{
		return runDetached;
	}

	/**
	 * If this entity should run in a detached and separate to the entity manager
	 *
	 * @param runDetached
	 *
	 * @return
	 */
	public J setRunDetached(boolean runDetached)
	{
		this.runDetached = runDetached;
		return (J) this;
	}

	/**
	 * If this ID is generated from the source and which form to use
	 * Default is Generated
	 *
	 * @return Returns if the id column is a generated type
	 */
	protected abstract boolean isIdGenerated();

	/**
	 * Returns the annotation associated with the entity manager
	 *
	 * @return
	 */
	public Class<? extends Annotation> getEntityManagerAnnotation()
	{
		EntityManager em = getEntityManager();
		Class<? extends Annotation> annotation = (Class<? extends Annotation>) em.getProperties()
		                                                                         .get("annotation");
		return annotation;
	}

	private void iterateThroughResultSetForGeneratedIDs(ResultSet generatedKeys) throws SQLException
	{
		if (generatedKeys.next())
		{
			Object o = generatedKeys.getObject(1);
			processId(generatedKeys, o);
		}
		else
		{
			throw new SQLException("Creating user failed, no ID obtained.");
		}
	}

	private void processId(ResultSet generatedKeys, Object o) throws SQLException
	{
		if (o instanceof BigDecimal)
		{
			if (entity.getClassIDType()
			          .isAssignableFrom(Long.class))
			{
				entity.setId((I) (Long) BigDecimal.class.cast(o)
				                                        .longValue());
			}
			else if (entity.getClassIDType()
			               .isAssignableFrom(Integer.class))
			{
				entity.setId((I) (Integer) BigDecimal.class.cast(o)
				                                           .intValue());
			}
			else
			{
				entity.setId((I) generatedKeys.getObject(1));
			}
		}
		else
		{
			entity.setId((I) generatedKeys.getObject(1));
		}
	}

	/**
	 * Merges this entity with the database copy. Uses getInstance(EntityManager.class)
	 *
	 * @return
	 */
	@NotNull
	public J update(E entity)
	{
		try
		{
			onUpdate(entity);
			if (isRunDetached())
			{
				//TODO UpdateStatement Generation
				getEntityManager().merge(entity);
			}
			else
			{
				getEntityManager().merge(entity);
			}
		}
		catch (IllegalStateException ise)
		{
			log.log(Level.SEVERE, "Cannot update this entity the state of this object is not ready : \n", ise);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Cannot update this entity the state of this object is not ready : \n", e);
		}
		return (J) this;
	}

	/**
	 * Performed on update/persist
	 *
	 * @param entity
	 * 		The entity
	 *
	 * @return true if must carry on updating
	 */
	protected boolean onUpdate(E entity)
	{
		return true;
	}

	/**
	 * Performs the constraint validation and returns a list of all constraint errors.
	 *
	 * <b>Great for form checking</b>
	 *
	 * @return
	 */
	@NotNull
	public List<String> validateEntity(E entity)
	{
		List<String> errors = new ArrayList<>();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set constraintViolations = validator.validate(entity);

		if (!constraintViolations.isEmpty())
		{
			for (Object constraintViolation : constraintViolations)
			{
				ConstraintViolation contraints = (ConstraintViolation) constraintViolation;
				String error = contraints.getRootBeanClass()
				                         .getSimpleName() + STRING_DOT + contraints.getPropertyPath() + STRING_EMPTY + contraints.getMessage();
				errors.add(error);
			}
		}
		return errors;
	}

	/**
	 * Returns if the class is a singular attribute
	 *
	 * @param attribute
	 *
	 * @return
	 */
	protected boolean isSingularAttribute(Attribute attribute)
	{
		return SingularAttribute.class.isAssignableFrom(attribute.getClass());
	}

	/**
	 * Returns if the attribute is plural or map
	 *
	 * @param attribute
	 *
	 * @return
	 */
	protected boolean isPluralOrMapAttribute(Attribute attribute)
	{
		return isPluralAttribute(attribute) || isMapAttribute(attribute);
	}

	/**
	 * Returns if the class is a singular attribute
	 *
	 * @param attribute
	 *
	 * @return
	 */
	protected boolean isPluralAttribute(Attribute attribute)
	{
		return PluralAttribute.class.isAssignableFrom(attribute.getClass());
	}

	/**
	 * Returns if the class is a singular attribute
	 *
	 * @param attribute
	 *
	 * @return
	 */
	protected boolean isMapAttribute(Attribute attribute)
	{
		return MapAttribute.class.isAssignableFrom(attribute.getClass());
	}

	/**
	 * Returns if the attribute is plural or map
	 *
	 * @param attribute
	 *
	 * @return
	 */
	protected boolean isCollectionAttribute(Attribute attribute)
	{
		return CollectionAttribute.class.isAssignableFrom(attribute.getClass());
	}
}
