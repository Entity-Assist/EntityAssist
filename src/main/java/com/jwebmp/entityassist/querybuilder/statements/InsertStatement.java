package com.jwebmp.entityassist.querybuilder.statements;

import com.jwebmp.entityassist.BaseEntity;
import com.jwebmp.entityassist.querybuilder.EntityAssistStrings;
import com.jwebmp.logger.LogFactory;

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
		Table t = c.getAnnotation(Table.class);
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

				JoinColumn joinCol = field.getAnnotation(JoinColumn.class);
				Column col = field.getAnnotation(Column.class);
				Id idCol = field.getAnnotation(Id.class);
				OneToOne oneToOne = field.getAnnotation(OneToOne.class);
				OneToMany oneToMany = field.getAnnotation(OneToMany.class);
				ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
				ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
				if (col == joinCol && joinCol == idCol
					/*	&& joinCol == oneToOne
						&& joinCol == oneToMany
						&& joinCol == manyToMany
						&& joinCol == manyToOne*/
				) //fuzzy logic, if everything is null go to next field, easier than is null
				{
					continue;
				}

				if (col == null && joinCol == null)
				{
					//TODO Nested inserts
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
