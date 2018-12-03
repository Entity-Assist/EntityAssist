package com.jwebmp.entityassist.querybuilder.statements;

import com.jwebmp.entityassist.CoreEntity;
import com.jwebmp.entityassist.querybuilder.EntityAssistStrings;
import com.jwebmp.logger.LogFactory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An insert statement
 */
public class InsertStatement
		implements EntityAssistStrings
{
	private static final Logger log = LogFactory.getLog(InsertStatement.class.getName());

	private static final String HEXES = "0123456789ABCDEF";

	private static InsertStatement insertStatement = new InsertStatement();

	/**
	 * The standard sdf format
	 */
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	/**
	 * Returns teh date formatter
	 */
	private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	/**
	 * Returns the date time formmatter
	 */
	private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private InsertStatement()
	{
		//Nothing needed
	}

	/**
	 * Builds the physical insert string for this entity class
	 *
	 * @return
	 */
	@NotNull
	public static String buildInsertString(Object o)
	{
		StringBuilder insertString = new StringBuilder("INSERT INTO ");
		Class<?> c = o.getClass();
		Table t = c.getAnnotation(Table.class);
		String tableName = "";
		if (t != null)
		{
			tableName = t.name();
		}
		if (tableName.isEmpty())
		{
			Entity e = c.getAnnotation(Entity.class);
			if (e != null)
			{
				tableName = e.name();
			}
		}
		if (tableName.isEmpty())
		{
			tableName = o.getClass()
			             .getSimpleName();
		}
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
				Object fieldObject = field.get(o);
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
				if (col == joinCol && joinCol == idCol) //fuzzy logic, if everything is null go to next field, easier than is null
				{
					continue;
				}
				String columnName = col == null ? joinCol.name() : col.name();
				if (columnName.isEmpty())
				{
					columnName = field.getName();
				}

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
			if (columnValue instanceof Boolean)
			{
				insertString.append((Boolean) columnValue ? "1" : "0")
				            .append(STRING_COMMNA_SPACE);
			}
			else if (columnValue instanceof Long)
			{
				insertString.append(columnValue)
				            .append(STRING_COMMNA_SPACE);
			}
			else if (columnValue instanceof Integer)
			{
				insertString.append(columnValue)
				            .append(STRING_COMMNA_SPACE);
			}
			else if (columnValue instanceof BigInteger)
			{
				insertString.append(((BigInteger) columnValue).longValue())
				            .append(STRING_COMMNA_SPACE);
			}
			else if (columnValue instanceof BigDecimal)
			{
				insertString.append(((BigDecimal) columnValue).doubleValue())
				            .append(STRING_COMMNA_SPACE);
			}
			else if (columnValue instanceof Short)
			{
				short columnVal = (short) columnValue;
				insertString.append(columnVal)
				            .append(STRING_COMMNA_SPACE);
			}
			else if (columnValue instanceof String)
			{
				insertString.append(STRING_SINGLE_QUOTES)
				            .append(((String) columnValue).replaceAll(STRING_SINGLE_QUOTES, STRING_SINGLE_QUOTES + STRING_SINGLE_QUOTES))
				            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
			}
			else if (columnValue instanceof Date)
			{
				Date date = (Date) columnValue;
				insertString.append(STRING_SINGLE_QUOTES)
				            .append(getInsertStatement().sdf.format(date))
				            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
			}
			else if (columnValue instanceof LocalDate)
			{
				LocalDate date = (LocalDate) columnValue;
				insertString.append(STRING_SINGLE_QUOTES)
				            .append(getInsertStatement().dateFormat.format(date))
				            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
			}
			else if (columnValue instanceof LocalDateTime)
			{
				LocalDateTime date = (LocalDateTime) columnValue;
				insertString.append(STRING_SINGLE_QUOTES)
				            .append(getInsertStatement().dateTimeFormat.format(date))
				            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
			}
			else if (columnValue instanceof CoreEntity)
			{
				CoreEntity wct = (CoreEntity) columnValue;
				insertString.append(wct.getId())
				            .append(STRING_COMMNA_SPACE);
			}
			else if (columnValue instanceof Enum)
			{
				Enum wct = (Enum) columnValue;
				insertString.append(STRING_SINGLE_QUOTES)
				            .append(wct.toString())
				            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
			}
			else if (columnValue instanceof byte[])
			{

				String bitString = "0x" + getHex((byte[]) columnValue);
				insertString.append(bitString)
				            .append(STRING_COMMNA_SPACE);
			}
		}

		insertString.delete(insertString.length() - 2, insertString.length());
		insertString.append(");");

		return insertString.toString();
	}

	public static InsertStatement getInsertStatement()
	{
		return insertStatement;
	}

	static String getHex(byte[] raw)
	{
		StringBuilder hex = new StringBuilder(2 * raw.length);
		for (byte b : raw)
		{
			hex.append(HEXES.charAt((b & 0xF0) >> 4))
			   .append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	/**
	 * Returns the sdf format
	 *
	 * @return
	 */
	@NotNull
	public SimpleDateFormat getSdf()
	{
		return sdf;
	}

	/**
	 * Returns the date time formatter for LocalDate instances
	 *
	 * @return
	 */
	@NotNull
	public DateTimeFormatter getDateFormat()
	{
		return dateFormat;
	}

	/**
	 * Return the date time formatter for LocalDateTime instances
	 *
	 * @return
	 */
	@NotNull
	public DateTimeFormatter getDateTimeFormat()
	{
		return dateTimeFormat;
	}
}
