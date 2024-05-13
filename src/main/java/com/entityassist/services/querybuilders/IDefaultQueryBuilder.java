package com.entityassist.services.querybuilders;

import com.entityassist.BaseEntity;
import com.entityassist.enumerations.Operand;
import com.entityassist.enumerations.OrderByType;
import com.entityassist.querybuilder.QueryBuilder;
import com.entityassist.querybuilder.builders.IFilterExpression;
import com.entityassist.querybuilder.builders.JoinExpression;
import com.entityassist.services.entities.IDefaultEntity;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.Attribute;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"UnusedReturnValue", "unused", "rawtypes"})
public interface IDefaultQueryBuilder<J extends IDefaultQueryBuilder<J, E, I>,
		E extends IDefaultEntity<E, J, I>,
		I extends Serializable>
		extends IQueryBuilderRoot<J, E, I>
{
	/**
	 * Selects the minimum count distinct of the root object (select distinct count(*))
	 *
	 * @return This
	 */
	
	J selectCountDistinct();

	/**
	 * Gets my given root
	 *
	 * @return The From object that is being used
	 */
	From getRoot();
	
	/**
	 * Sets the root of this builder
	 *
	 * @param root The FROM to use
	 * @return this
	 */
	
	J setRoot(From<?, ?> root);
	
	/**
	 * Where the "id" field is in
	 *
	 * @param id Finds by ID
	 * @return This (Use get to return results)
	 */
	
	J find(I id);
	
	/**
	 * Where the "id" field is in
	 *
	 * @param id Finds by ID
	 * @return This (Use get to return results)
	 */
	
	J find(Collection<I> id);
	
	/**
	 * Returns the collection of filters that are going to be applied in build
	 *
	 * @return A set of predicates
	 */
	Set<Predicate> getFilters();
	
	/**
	 * Selects the minimum count of the root object (select count(*))
	 *
	 * @return This
	 */
	
	J selectCount();
	
	/**
	 * Joins the given builder with an inner join and no associated builder
	 *
	 * @param attribute The given attribute to join on
	 * @return This object - Configure each joins filters separately in their builders
	 */
	
	<X, Y> J join(Attribute<X, Y> attribute);
	
	/**
	 * Joins the given builder with the given builder and build type
	 *
	 * @param attribute The given attribute to join on
	 * @param builder   A Query Builder object that contains the construct of the query
	 * @return This
	 */
	
	<X, Y> J join(Attribute<X, Y> attribute, QueryBuilder builder, JoinType joinType, JoinExpression joinExpression);
	
	/**
	 * Joins the given builder with the given builder and build type
	 *
	 * @param attribute The given attribute to join on
	 * @param builder   A Query Builder object that contains the construct of the query
	 * @return This
	 */
	
	<X, Y> J join(Attribute<X, Y> attribute, QueryBuilder<?, ?, ?> builder, JoinType joinType);
	
	/**
	 * Joins the given builder with the given builder and build type
	 *
	 * @param attribute The given attribute to join on
	 * @param builder   A Query Builder object that contains the construct of the query
	 * @return This
	 */
	
	<X, Y> J join(Attribute<X, Y> attribute, QueryBuilder builder, JoinType joinType, QueryBuilder onClauses, JoinExpression joinExpression);
	
	/**
	 * Joins the given builder with the given builder and build type
	 *
	 * @param attribute The given attribute to join on
	 * @param builder   A Query Builder object that contains the construct of the query
	 * @return This
	 */
	
	<X, Y> J join(Attribute<X, Y> attribute, QueryBuilder builder, JoinType joinType, QueryBuilder onClauses);
	
	/**
	 * Joins the given builder in an inner join with the given builder
	 *
	 * @param attribute The given attribute to join on
	 * @param builder   A Query Builder object that contains the construct of the query
	 * @return This
	 */
	
	<X, Y> J join(Attribute<X, Y> attribute, QueryBuilder builder);
	
	/**
	 * Joins the given builder With the given join type and no associated builder
	 *
	 * @param attribute The given attribute to join on
	 * @return The join type to use
	 */
	
	<X, Y> J join(Attribute<X, Y> attribute, JoinType joinType);
	
	/**
	 * Joins the given builder With the given join type and no associated builder
	 *
	 * @param attribute The given attribute to join on
	 * @return The join type to use
	 */
	
	<X, Y> J join(Attribute<X, Y> attribute, JoinType joinType, JoinExpression joinExpression);
	
	/**
	 * Where the field name is equal to the value
	 *
	 * @param fieldName The field name
	 * @param value     The value to use - Collection, Arrays, etc
	 * @return This
	 */
	
	J in(String fieldName, Object value);
	
	/**
	 * Where the field name is equal to the value
	 *
	 * @param fieldName The field name
	 * @param value     The value to use - Collection, Arrays, etc
	 * @return This
	 */
	
	<X, Y> J in(Attribute<X, Y> fieldName, Y value);
	
	/**
	 * Where the field name is equal to the value
	 *
	 * @param fieldName The field name
	 * @param value     The value to use - Collection, Arrays, etc
	 * @return This
	 */
	
	<X, Y> J in(Attribute<X, Y> fieldName, Collection<Y> value);
	
	/**
	 * Where the field name is equal to the value
	 *
	 * @param fieldName The field name
	 * @param value     The value to use - Collection, Arrays, etc
	 * @return This
	 */
	
	<X, Y> J in(Attribute<X, Y> fieldName, Y[] value);
	
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
	
	<X, Y> J where(Attribute<X, Y> attribute, Operand operator, Y[] value);
	
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
	
//	<X, Y> J where(Expression<X> attribute, Operand operator, Y[] value);
	
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
	
	<X, Y> J where(Attribute<X, Y> attribute, Operand operator, Collection<Y> value);
	
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
	
	//<X, Y> J where(Expression<X> attribute, Operand operator, Collection<Y> value);
	
	/**
	 * Performs a filter on the database with the where clauses
	 *
	 * @param attribute The attribute to be used
	 * @param operator  The operand to use
	 * @param value     The value to apply (Usually serializable)
	 * @return This object
	 */
	
	<X, Y> J where(Attribute<X, Y> attribute, Operand operator, Y value);
	
	/**
	 * Performs a filter on the database with the where clauses
	 *
	 * @param attribute The attribute to be used
	 * @param operator  The operand to use
	 * @param value     The value to apply (Usually serializable)
	 * @return This object
	 */
	
//	<X, Y> J where(Expression<X> attribute, Operand operator, Y value);
	
	/**
	 * Gets the cache region for this query
	 *
	 * @return The applied cache region or null
	 */
	String getCacheRegion();
	
	/**
	 * Sets a cache region for this query
	 *
	 * @param cacheRegion To a cache region
	 * @return This
	 */
	
	J setCacheRegion(String cacheRegion);
	
	/**
	 * Orders by column ascending
	 *
	 * @param orderBy Which attribute to order by
	 * @return This
	 */
	
	<X, Y> J orderBy(Attribute<X, Y> orderBy);
	
	/**
	 * Adds an order by column to the query
	 *
	 * @param orderBy   Order by which column
	 * @param direction The direction to apply
	 * @return This
	 */
	
	<X, Y> J orderBy(Attribute<X, Y> orderBy, OrderByType direction);
	
	
	/**
	 * Returns the current list of order by's
	 *
	 * @return A map of attributes and order by types
	 */
	Map<Attribute<?, ?>, OrderByType> getOrderBys();
	
	/**
	 * Selects a given column
	 *
	 * @param selectColumn The column to group by
	 * @return This
	 */
	
	<X, Y> J groupBy(Attribute<X, Y> selectColumn);
	
	/**
	 * Returns the current list of group by's
	 *
	 * @return A set of expressions
	 */
	Set<Expression<?>> getGroupBys();
	
	void reset(From newRoot);
	
	/**
	 * Gets the havingExpressions list for this builder
	 *
	 * @return A set of expressions for the havingExpressions clause
	 */
	Set<Expression<?>> getHavingExpressions();
	
	/**
	 * Returns a set of the where expressions
	 *
	 * @return A set of IFilterExpressions
	 */
	Set<IFilterExpression> getWhereExpressions();
	
	
	/**
	 * Selects a given column
	 *
	 * @param selectColumn The given column from the static metadata
	 * @return This
	 */
	
	J selectColumn(Expression selectColumn);
	
	@SuppressWarnings("unchecked")
	J selectColumn(Expression selectColumn, String aliasName);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectAverage(Expression attribute);
	
	@SuppressWarnings("unchecked")
	J selectAverage(Expression attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectCount(Expression attribute);
	
	@SuppressWarnings("unchecked")
	J selectCount(Expression attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return this
	 */
	
	J selectCountDistinct(Expression attribute);
	
	@SuppressWarnings("unchecked")
	J selectCountDistinct(Expression attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectMax(Expression attribute);
	
	@SuppressWarnings("unchecked")
	J selectMax(Expression attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectMin(Expression attribute);
	
	@SuppressWarnings("unchecked")
	J selectMin(Expression attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectSum(Expression attribute);
	
	@SuppressWarnings("unchecked")
	J selectSum(Expression attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectSumAsDouble(Expression attribute);
	
	@SuppressWarnings("unchecked")
	J selectSumAsDouble(Expression attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectSumAsLong(Expression attribute);
	
	@SuppressWarnings("unchecked")
	J selectSumAsLong(Expression attribute, String alias);
	
	/**
	 * Selects a given column
	 *
	 * @param selectColumn The given column from the static metadata
	 * @return This
	 */
	
	J selectColumn(Attribute selectColumn);
	
	@SuppressWarnings("unchecked")
	J selectColumn(Attribute selectColumn, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectAverage(Attribute attribute);
	
	@SuppressWarnings("unchecked")
	J selectAverage(Attribute attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectCount(Attribute attribute);
	
	@SuppressWarnings("unchecked")
	J selectCount(Attribute attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return this
	 */
	
	J selectCountDistinct(Attribute attribute);
	
	@SuppressWarnings("unchecked")
	J selectCountDistinct(Attribute attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectMax(Attribute attribute);
	
	@SuppressWarnings("unchecked")
	J selectMax(Attribute attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectMin(Attribute attribute);
	
	@SuppressWarnings("unchecked")
	J selectMin(Attribute attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectSum(Attribute attribute);
	
	@SuppressWarnings("unchecked")
	J selectSum(Attribute attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectSumAsDouble(Attribute attribute);
	
	@SuppressWarnings("unchecked")
	J selectSumAsDouble(Attribute attribute, String alias);
	
	/**
	 * Selects the minimum min() of a column
	 *
	 * @param attribute A given column from static metadata
	 * @return This
	 */
	
	J selectSumAsLong(Attribute attribute);
	
	@SuppressWarnings("unchecked")
	J selectSumAsLong(Attribute attribute, String alias);

	/**
	 * Returns the map of join executors
	 *
	 * @return Returns a set of join expressions
	 */
	@NotNull Set<JoinExpression<?, ?, ?>> getJoins();
	
	/**
	 * Sets the entity to the given item
	 *
	 * @param entity The entity
	 * @return This
	 */
	@Override
	J setEntity(E entity);
	
	/**
	 * Sets the entity to the given item
	 *
	 * @param entity The entity
	 * @return This
	 */
	
	J setEntity(Object entity);
	
	/**
	 * If a dto construct is required (classes that extend the entity as transports)
	 *
	 * @return Class of type that extends Base Entity
	 */
	Class<? extends BaseEntity> getConstruct();
	
	/**
	 * If a dto construct is required (classes that extend the entity as transports)
	 *
	 * @param construct The construct
	 * @return This object
	 */
	
	J construct(Class<? extends BaseEntity> construct);
	
	/**
	 * Returns the currently associated cache name
	 *
	 * @return The cache name associated
	 */
	String getCacheName();
	
	/**
	 * Enables query caching on the given query with the associated name
	 *
	 * @param cacheName The name for the given query
	 * @return Always this object
	 */
	
	J setCacheName(String cacheName, String cacheRegion);
	
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
	<X, Y> J or(Attribute<X, Y> attribute, Operand operator, Collection<Y> value);
	
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
	
	<X, Y> J or(Attribute<X, Y> attribute, Operand operator, Collection<Y> value, boolean nest);
	
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
	
	//<X, Y> J or(Expression<X> attribute, Operand operator, Collection<Y> value, boolean nest);
	
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
	<X, Y> J or(Attribute<X, Y> attribute, Operand operator, Y value);
	
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
	
	<X, Y> J or(Attribute<X, Y> attribute, Operand operator, Y value, boolean nest);
	
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
	
//	<X, Y> J or(Expression<X> attribute, Operand operator, Y value, boolean nest);
	
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
	
	<X, Y> J or(Attribute<X, Y> attribute, Operand operator, Y[] value);
	
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
	
	<X, Y> J or(Attribute<X, Y> attribute, Operand operator, Y[] value, boolean nest);
	
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
	
	//<X, Y> J or(Expression<X> attribute, Operand operator, Y[] value, boolean nest);
}
