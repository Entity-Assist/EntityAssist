package com.jwebmp.entityassist.querybuilder.statements;

import com.jwebmp.entityassist.BaseEntity;
import com.jwebmp.guicedinjection.pairing.Pair;
import com.jwebmp.logger.LogFactory;

import javax.persistence.metamodel.SingularAttribute;
import java.util.Map;
import java.util.logging.Logger;

import static com.jwebmp.entityassist.querybuilder.EntityAssistStrings.*;

public class UpdateStatement
		extends RunnableStatement
{
	private static final Logger log = LogFactory.getLog(UpdateStatement.class.getName());

	public UpdateStatement(BaseEntity obj)
	{
		super(obj);
	}

	public String buildUpdateStatement()
	{
		StringBuilder string = new StringBuilder();
		string.append("UPDATE ");
		string.append(getTableName() + STRING_SPACE);
		string.append("SET ");

		Map<String,Object> updateMap = getObject().builder()
		                                                     .getUpdateFieldMap(getObject());

		for (Map.Entry<String, Object> entry : updateMap.entrySet())
		{
			Object value = entry.getValue();
			String valueString = getValue(value);
			string.append(entry.getKey());
			string.append(STRING_EQUALS_SPACE_EQUALS);
			string.append(valueString);
		}

		string = string.deleteCharAt(string.lastIndexOf(STRING_COMMNA));
		string.append(STRING_SPACE);
		string.append("WHERE ");
		Pair<String, Object> idPair = getIdPair();
		string.append(idPair.getKey() + STRING_EQUALS_SPACE_EQUALS + getValue(idPair.getValue()));
		string = string.deleteCharAt(string.lastIndexOf(STRING_COMMNA));

		return string.toString();
	}


	public String toString()
	{
		return buildUpdateStatement();
	}

}
