package com.jwebmp.entityassist.querybuilder.builders;

import com.jwebmp.entityassist.querybuilder.QueryBuilder;

import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;
import java.io.Serializable;

public final class JoinExpression implements Serializable
{
	private static final long serialVersionUID = 1L;

	private transient QueryBuilder executor;
	private JoinType joinType;
	private transient Attribute attribute;

	public JoinExpression()
	{

	}

	public JoinExpression(QueryBuilder executor, JoinType joinType, Attribute attribute)
	{
		this.executor = executor;
		this.joinType = joinType;
		this.attribute = attribute;
	}

	public QueryBuilder getExecutor()
	{
		return executor;
	}

	public void setExecutor(QueryBuilder executor)
	{
		this.executor = executor;
	}

	public JoinType getJoinType()
	{
		return joinType;
	}

	public void setJoinType(JoinType joinType)
	{
		this.joinType = joinType;
	}

	public Attribute getAttribute()
	{
		return attribute;
	}

	public void setAttribute(Attribute attribute)
	{
		this.attribute = attribute;
	}
}
