package com.entityassist.querybuilder.statements;

import com.entityassist.BaseEntity;
import com.entityassist.querybuilder.EntityAssistStrings;
import com.guicedee.guicedinjection.pairing.Pair;
import com.guicedee.logger.LogFactory;

import java.util.Map;
import java.util.logging.Logger;

public class DeleteStatement
		extends RunnableStatement
{
	public DeleteStatement(BaseEntity<?, ?, ?> obj)
	{
		super(obj);
	}

	@SuppressWarnings("unchecked")
	public String buildUpdateStatement()
	{
		StringBuilder string = new StringBuilder();
		string.append("DELETE FROM  ");

		string.append(getTableName())
		      .append(EntityAssistStrings.STRING_SPACE);
		string.append("WHERE ");
		Pair<String, Object> idPair = getIdPair();
		string.append(idPair.getKey())
		      .append(EntityAssistStrings.STRING_EQUALS_SPACE_EQUALS)
		      .append(getValue(idPair.getValue()));
		string.deleteCharAt(string.lastIndexOf(EntityAssistStrings.STRING_COMMNA));
		return string.toString();
	}

	public String toString()
	{
		if(obj == null || obj.getId() == null)
		{
			throw new RuntimeException("Unable to run delete statement, no ID to delete on. These deletes happen per id - " + obj);
		}
		return buildUpdateStatement();
	}

}
