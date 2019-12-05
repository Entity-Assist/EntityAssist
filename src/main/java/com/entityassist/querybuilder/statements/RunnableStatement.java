package com.entityassist.querybuilder.statements;

import com.entityassist.BaseEntity;
import com.entityassist.querybuilder.EntityAssistStrings;
import com.guicedee.guicedinjection.pairing.Pair;
import com.guicedee.logger.LogFactory;

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
import java.util.*;
import java.util.logging.Level;

import static com.entityassist.querybuilder.EntityAssistStrings.*;

abstract class RunnableStatement
{
	private static final String HEXES = "0123456789ABCDEF";

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

	protected final BaseEntity obj;

	protected RunnableStatement(BaseEntity obj)
	{
		this.obj = obj;
	}


	String getHex(byte[] raw)
	{
		StringBuilder hex = new StringBuilder(2 * raw.length);
		for (byte b : raw)
		{
			hex.append(HEXES.charAt((b & 0xF0) >> 4))
			   .append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	protected String getValue(Object columnValue)
	{
		StringBuilder insertString = new StringBuilder();
		if (columnValue instanceof Boolean)
		{
			insertString.append((Boolean) columnValue ? "1" : "0")
			            .append(EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof Long)
		{
			insertString.append(columnValue)
			            .append(EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof Integer)
		{
			insertString.append(columnValue)
			            .append(EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof BigInteger)
		{
			insertString.append(((BigInteger) columnValue).longValue())
			            .append(EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof BigDecimal)
		{
			insertString.append(((BigDecimal) columnValue).doubleValue())
			            .append(EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof Short)
		{
			short columnVal = (short) columnValue;
			insertString.append(columnVal)
			            .append(EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof String)
		{
			insertString.append(EntityAssistStrings.STRING_SINGLE_QUOTES)
			            .append(((String) columnValue).replaceAll(EntityAssistStrings.STRING_SINGLE_QUOTES, EntityAssistStrings.STRING_SINGLE_QUOTES + EntityAssistStrings.STRING_SINGLE_QUOTES))
			            .append(EntityAssistStrings.STRING_SINGLE_QUOTES + EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof Date)
		{
			Date date = (Date) columnValue;
			insertString.append(EntityAssistStrings.STRING_SINGLE_QUOTES)
			            .append(getSdf().format(date))
			            .append(EntityAssistStrings.STRING_SINGLE_QUOTES + EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof LocalDate)
		{
			LocalDate date = (LocalDate) columnValue;
			insertString.append(EntityAssistStrings.STRING_SINGLE_QUOTES)
			            .append(getDateFormat().format(date))
			            .append(EntityAssistStrings.STRING_SINGLE_QUOTES + EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof LocalDateTime)
		{
			LocalDateTime date = (LocalDateTime) columnValue;
			insertString.append(EntityAssistStrings.STRING_SINGLE_QUOTES)
			            .append(getDateTimeFormat().format(date))
			            .append(EntityAssistStrings.STRING_SINGLE_QUOTES + EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof BaseEntity)
		{
			BaseEntity wct = (BaseEntity) columnValue;
			insertString.append(getValue(wct.getId()));
		}
		else if (columnValue instanceof Enum)
		{
			Enum wct = (Enum) columnValue;
			insertString.append(EntityAssistStrings.STRING_SINGLE_QUOTES)
			            .append(wct.toString())
			            .append(EntityAssistStrings.STRING_SINGLE_QUOTES + EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof UUID)
		{
			UUID wct = (UUID) columnValue;
			insertString.append(EntityAssistStrings.STRING_SINGLE_QUOTES)
			            .append(wct.toString())
			            .append(EntityAssistStrings.STRING_SINGLE_QUOTES + EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof byte[])
		{

			String bitString = "0x" + getHex((byte[]) columnValue);
			insertString.append(bitString)
			            .append(EntityAssistStrings.STRING_COMMNA_SPACE);
		}
		return insertString.toString();
	}

	public String getTableName()
	{
		Class<?> c = obj.getClass();
		Table t = c.getAnnotation(Table.class);
		String tableName = "";
		if (t != null)
		{
			String catalog = t.catalog();
			if(!catalog.isEmpty())
				tableName += catalog + STRING_DOT;
			String schema = t.schema();
			if(!schema.isEmpty())
				tableName += schema + STRING_DOT;
			tableName += t.name();
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
			tableName = obj.getClass()
			               .getSimpleName();
		}
		return tableName;
	}


	public List<Field> getFields()
	{
		List<Field> fields = new ArrayList<>();
		Class<?> i = obj.getClass();
		while (i != null)
		{
			Collections.addAll(fields, i.getDeclaredFields());
			i = i.getSuperclass();
		}
		return fields;
	}

	public Pair<String, Object> getIdPair()
	{
		for (Field field : getFields())
		{
			if (field.isAnnotationPresent(Transient.class) || Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))
			{
				continue;
			}
			try
			{
				Id idCol = field.getAnnotation(Id.class);
				if (idCol != null)
				{
					field.setAccessible(true);
					return Pair.of(getColumnName(field), field.get(getObject()));
				}
			}
			catch (IllegalArgumentException | IllegalAccessException ex)
			{
				LogFactory.getLog("RunnableStatement")
				          .log(Level.SEVERE, null, ex);
			}
		}
		return Pair.empty();
	}

	public BaseEntity getObject()
	{
		return obj;
	}

	public String getColumnName(Field field)
	{
		JoinColumn joinCol = field.getAnnotation(JoinColumn.class);
		Column col = field.getAnnotation(Column.class);
		String columnName = col == null ? joinCol.name() : col.name();
		if (columnName.isEmpty())
		{
			columnName = field.getName();
		}
		return columnName;
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
