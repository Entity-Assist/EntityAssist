package com.jwebmp.entityassist.querybuilder.builders;

import com.jwebmp.entityassist.BaseEntity;
import com.jwebmp.entityassist.querybuilder.QueryBuilder;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;

/**
 * Public join expression
 */
@SuppressWarnings("unused")
public final class JoinExpression<X extends BaseEntity<X, ?, ?>, Y>
{
	/**
	 * The query build that will run the execution for the join
	 */
	private QueryBuilder<?, X, ?> executor;
	/**
	 * The given join type
	 */
	private JoinType joinType;
	/**
	 * The attribute type to apply
	 */
	private Attribute<X, Y> attribute;
	/**
	 * The builder to use for the on clause
	 */
	private QueryBuilder<?, X, ?> onBuilder;
	/**
	 * The generated root from the join
	 */
	private Root<X> generatedRoot;

	/**
	 * The join expression to build
	 */
	public JoinExpression()
	{
		//No config required
	}

	/**
	 * A new expression with the given configurations
	 *
	 * @param executor
	 * 		The Query Builder to use
	 * @param joinType
	 * 		The join type to apply
	 * @param attribute
	 * 		The attribute to apply it with
	 */
	@SuppressWarnings("WeakerAccess")
	public JoinExpression(QueryBuilder<?, X, ?> executor, JoinType joinType, Attribute<X, Y> attribute)
	{
		this.executor = executor;
		this.joinType = joinType;
		this.attribute = attribute;
	}

	@SuppressWarnings("WeakerAccess")
	public JoinExpression(QueryBuilder<?, X, ?> executor, JoinType joinType, Attribute<X, Y> attribute, QueryBuilder<?, X, ?> onBuilder)
	{
		this.executor = executor;
		this.joinType = joinType;
		this.attribute = attribute;
		this.onBuilder = onBuilder;
	}

	/**
	 * Method getExecutor returns the executor of this JoinExpression object.
	 * <p>
	 * The query build that will run the execution for the join
	 *
	 * @return the executor (type QueryBuilder ?, X, ? ) of this JoinExpression object.
	 */
	public QueryBuilder<?, X, ?> getExecutor()
	{
		return executor;
	}

	/**
	 * Method setExecutor sets the executor of this JoinExpression object.
	 * <p>
	 * The query build that will run the execution for the join
	 *
	 * @param executor
	 * 		the executor of this JoinExpression object.
	 */
	public void setExecutor(QueryBuilder<?, X, ?> executor)
	{
		this.executor = executor;
	}

	/**
	 * Method getJoinType returns the joinType of this JoinExpression object.
	 * <p>
	 * The given join type
	 *
	 * @return the joinType (type JoinType) of this JoinExpression object.
	 */
	public JoinType getJoinType()
	{
		return joinType;
	}

	/**
	 * Method setJoinType sets the joinType of this JoinExpression object.
	 * <p>
	 * The given join type
	 *
	 * @param joinType
	 * 		the joinType of this JoinExpression object.
	 */
	public void setJoinType(JoinType joinType)
	{
		this.joinType = joinType;
	}

	/**
	 * Method getAttribute returns the attribute of this JoinExpression object.
	 * <p>
	 * The attribute type to apply
	 *
	 * @return the attribute (type Attribute X, Y) of this JoinExpression object.
	 */
	public Attribute<X, Y> getAttribute()
	{
		return attribute;
	}

	/**
	 * Method setAttribute sets the attribute of this JoinExpression object.
	 * <p>
	 * The attribute type to apply
	 *
	 * @param attribute
	 * 		the attribute of this JoinExpression object.
	 */
	public void setAttribute(Attribute<X, Y> attribute)
	{
		this.attribute = attribute;
	}

	/**
	 * Getter for property 'onBuilder'.
	 *
	 * @return Value for property 'onBuilder'.
	 */
	public QueryBuilder<?, X, ?> getOnBuilder()
	{
		return onBuilder;
	}

	/**
	 * Setter for property 'onBuilder'.
	 *
	 * @param onBuilder
	 * 		Value to set for property 'onBuilder'.
	 */
	public void setOnBuilder(QueryBuilder<?, X, ?> onBuilder)
	{
		this.onBuilder = onBuilder;
	}

	/**
	 * The generated root if any
	 * @return
	 */
	public Root<X> getGeneratedRoot()
	{
		return generatedRoot;
	}

	/**
	 * The generated root
	 * @param generatedRoot
	 * @return
	 */
	public JoinExpression<X, Y> setGeneratedRoot(Root<X> generatedRoot)
	{
		this.generatedRoot = generatedRoot;
		return this;
	}
}
