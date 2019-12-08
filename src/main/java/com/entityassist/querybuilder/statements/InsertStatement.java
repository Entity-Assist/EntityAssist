package com.entityassist.querybuilder.statements;

import com.entityassist.BaseEntity;
import com.entityassist.querybuilder.EntityAssistStrings;
import com.guicedee.logger.LogFactory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An insert statement
 */
public class InsertStatement
		extends RunnableStatement
		implements EntityAssistStrings
{
	private static final Logger log = LogFactory.getLog(InsertStatement.class.getName());

	public InsertStatement(BaseEntity obj)
	{
		super(obj);
	}


	/**
	 * Builds the physical insert string for this entity class
	 *
	 * @return
	 */
	@NotNull
	public String buildInsertString()
	{
		StringBuilder insertString = new StringBuilder("INSERT INTO ");
		Class<?> c = obj.getClass();
		String tableName = getTableName();
		insertString.append(tableName)
		            .append(" (");
		List<Field> fields = new ArrayList<>();
		Class<?> i = c;
		while (i != null)
		{
			Collections.addAll(fields, i.getDeclaredFields());
			i = i.getSuperclass();
		}
		List<String> columnsNames = new ArrayList<>();
		List<Object> columnValues = new ArrayList<>();

		for (Field field : fields)
		{
			if (field.isAnnotationPresent(Transient.class) || Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))
			{
				continue;
			}
			field.setAccessible(true);
			try
			{
				Object fieldObject = field.get(obj);
				if (fieldObject == null)
				{
					continue;
				}

				GeneratedValue genValu = field.getAnnotation(GeneratedValue.class);
				if (genValu != null)
				{
					continue;
				}
				if (!isColumnReadable(field))
				{
					continue;
				}
				String columnName = getColumnName(field);
				if (fieldObject instanceof Long)
				{
					Long wct = (Long) fieldObject;
					if (wct == Long.MAX_VALUE)
					{
						continue;
					}
				}
				if (!columnsNames.contains(columnName))
				{
					columnsNames.add(columnName);
					if(fieldObject.getClass().isAnnotationPresent(Embeddable.class))
					{
						Field[] f = fieldObject.getClass().getDeclaredFields();
						for (Field field1 : f)
						{
							if(isColumnReadable(field1))
							{
								field1.setAccessible(true);
								columnValues.add(field1.get(fieldObject));
							}
						}
					}
					else
						columnValues.add(fieldObject);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException ex)
			{
				log.log(Level.SEVERE, null, ex);
			}
		}

		//columns
		for (String columnName : columnsNames)
		{
			insertString.append(columnName)
			            .append(STRING_COMMNA_SPACE);
		}
		insertString.delete(insertString.length() - 2, insertString.length());
		insertString.append(") VALUES (");
		for (Object columnValue : columnValues)
		{
			insertString.append(getValue(columnValue));
		}

		insertString.delete(insertString.length() - 2, insertString.length());
		insertString.append(");");

		return insertString.toString();
	}

	public String toString()
	{
		return buildInsertString();
	}

}
