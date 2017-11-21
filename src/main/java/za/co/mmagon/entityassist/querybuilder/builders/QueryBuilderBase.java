package za.co.mmagon.entityassist.querybuilder.builders;

import za.co.mmagon.entityassist.BaseEntity;
import za.co.mmagon.entityassist.enumerations.Provider;

import javax.annotation.Nullable;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class QueryBuilderBase<J extends QueryBuilderBase<J, E, I>, E extends BaseEntity<E, J, I>, I extends Serializable>
{

	private static final Logger log = Logger.getLogger("QueryBuilderCore");
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
	/**
	 * Whether or not to log the rendered select sql
	 */
	private boolean logSelectSql;
	/**
	 * Whether or not to log the insert sql statement
	 */
	private boolean logInsertSql;

	/**
	 * Static provider to generate sql for
	 */
	private Provider provider;

	@SuppressWarnings("unchecked")
	protected QueryBuilderBase()
	{
		this.entityClass = getEntityClass();
		provider = Provider.Hibernate5jre8;
	}

	/**
	 * Returns the associated entity class for this builder
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<E> getEntityClass()
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
				log.log(Level.SEVERE, "Unable to read the entity class that this query builder core is built for\n", e);
			}
		}
		return entityClass;
	}

	/**
	 * Returns the current set first results
	 *
	 * @return
	 */
	@Nullable
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

	/**
	 * Returns the current set maximum results
	 *
	 * @return
	 */
	@Nullable
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
	 * Returns the current sql generator provider
	 *
	 * @return
	 */
	public Provider getProvider()
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
	@SuppressWarnings("unchecked")
	public J setProvider(@NotNull Provider provider)
	{
		this.provider = provider;
		return (J) this;
	}

	/**
	 * Whether or not the select queries should be logged out
	 *
	 * @return
	 */
	protected boolean isLogSelectSql()
	{
		return logSelectSql;
	}

	/**
	 * Whether or not the select queries should be logged out
	 *
	 * @param logSelectSql
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected J setLogSelectSql(boolean logSelectSql)
	{
		this.logSelectSql = logSelectSql;
		return (J) this;
	}

	/**
	 * Whether or not the insert sql must be logged
	 *
	 * @return
	 */
	protected boolean isLogInsertSql()
	{
		return logInsertSql;
	}

	/**
	 * Whether or not to insert sql must be logged
	 *
	 * @param logInsertSql
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected J setLogInsertSql(boolean logInsertSql)
	{
		this.logInsertSql = logInsertSql;
		return (J) this;
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
		return attribute.getClass().isAssignableFrom(SingularAttribute.class);
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
		return attribute.getClass().isAssignableFrom(PluralAttribute.class);
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
		return attribute.getClass().isAssignableFrom(MapAttribute.class);
	}
}
