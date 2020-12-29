package com.entityassist.querybuilder.statements;

import com.entityassist.RootEntity;
import com.guicedee.guicedinjection.pairing.Pair;
import com.guicedee.logger.LogFactory;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.guicedee.guicedinjection.json.StaticStrings.*;

/**
 * An insert statement
 */
public class InsertStatement
		extends RunnableStatement
{
	private static final Logger log = LogFactory.getLog(InsertStatement.class.getName());
	
	public InsertStatement(RootEntity obj)
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
		StringBuilder insertString = new StringBuilder(STRING_INSERT_INTO_SQL);
		Class<?> c = obj.getClass();
		String tableName = getTableName();
		insertString.append(tableName)
		            .append(STRING_SPACE_OPEN_BRACKET);
		List<Field> fields = new ArrayList<>();
		Class<?> i = c;
		while (i != null)
		{
			Collections.addAll(fields, i.getDeclaredFields());
			i = i.getSuperclass();
		}
		List<String> columnsNames = new ArrayList<>();
		List<Pair<Field, Object>> columnValues = new ArrayList<>();
		
		for (Field field : fields)
		{
			if (field.isAnnotationPresent(Transient.class) ||
					Modifier.isStatic(field.getModifiers()) ||
					Modifier.isFinal(field.getModifiers()))
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
					if (fieldObject.getClass()
					               .isAnnotationPresent(Embeddable.class))
					{
						Field[] f = fieldObject.getClass()
						                       .getDeclaredFields();
						for (Field field1 : f)
						{
							if (isColumnReadable(field1))
							{
								field1.setAccessible(true);
								columnValues.add(Pair.of(field1, field1.get(fieldObject)));
							}
						}
					}
					else
					{
						columnValues.add(Pair.of(field, fieldObject));
					}
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
		insertString.append(STRING_VALUES_SQL_INSERT);
		for (Pair<Field, Object> columnValue : columnValues)
		{
			insertString.append(getValue(columnValue.getValue(), columnValue.getKey()));
		}
		
		insertString.delete(insertString.length() - 2, insertString.length());
		insertString.append(STRING_CLOSING_BRACKET_SEMICOLON);
		
		return insertString.toString();
	}
	
	@Override
	public String toString()
	{
		return buildInsertString();
	}
	
}
