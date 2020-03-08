package com.entityassist.querybuilder.statements;

import com.entityassist.BaseEntity;
import com.guicedee.guicedinjection.pairing.Pair;

import static com.guicedee.guicedinjection.json.StaticStrings.*;

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
		string.append(STRING_DELETE_FROM_SQL);

		string.append(getTableName())
		      .append(STRING_SPACE);
		string.append(STRING_WHERE_SQL);
		Pair<String, Object> idPair = getIdPair();

		string.append(idPair.getKey())
		      .append(STRING_EQUALS_SPACE_EQUALS)
		      .append(getValue(idPair.getValue(),null));
		string.deleteCharAt(string.lastIndexOf(STRING_COMMNA));
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
