package za.co.mmagon.entityassist.querybuilder.builders;

import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;
import java.io.Serializable;

final class JoinExpression implements Serializable
{
	private static final long serialVersionUID = 1L;

	private QueryBuilderExecutor executor;
	private JoinType joinType;
	private Attribute attribute;

	public JoinExpression()
	{

	}

	public JoinExpression(QueryBuilderExecutor executor, JoinType joinType, Attribute attribute)
	{
		this.executor = executor;
		this.joinType = joinType;
		this.attribute = attribute;
	}

	public QueryBuilderExecutor getExecutor()
	{
		return executor;
	}

	public void setExecutor(QueryBuilderExecutor executor)
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
