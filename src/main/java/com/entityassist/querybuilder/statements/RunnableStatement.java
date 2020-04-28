package com.entityassist.querybuilder.statements;

import com.entityassist.BaseEntity;
import com.guicedee.guicedinjection.GuiceContext;
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

import static com.guicedee.guicedinjection.json.StaticStrings.*;

abstract class RunnableStatement
{
	private static final String HEXES = "0123456789ABCDEF";
	protected final BaseEntity obj;
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

	protected RunnableStatement(BaseEntity obj)
	{
		this.obj = obj;
	}

	private String getHex(byte[] raw)
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
	 * To T-SQL Value for simple global compatibility
	 *
	 * @param columnValue
	 * 		The column value to use
	 *
	 * @return The key
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	protected String getValue(Object columnValue, Field field)
	{
		StringBuilder insertString = new StringBuilder();
		if (field != null && field.isAnnotationPresent(Convert.class))
		{
			Class cc = field.getAnnotation(Convert.class)
			                .converter();
			if (AttributeConverter.class.isAssignableFrom(cc))
			{
				AttributeConverter ac = (AttributeConverter) GuiceContext.get(cc);
				columnValue = ac.convertToDatabaseColumn(columnValue);
			}
		}

		if (columnValue instanceof Boolean)
		{
			insertString.append((Boolean) columnValue ? STRING_1 : STRING_0)
			            .append(STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof RawInsertObjectValue)
		{
			insertString.append(columnValue)
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
		else if (columnValue instanceof Double)
		{
			insertString.append(columnValue)
			            .append(STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof Float)
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
			insertString.append(((BigDecimal) columnValue).toPlainString())
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
			if (!columnValue.toString()
			                .startsWith(STRING_SINGLE_QUOTES))
			{
				insertString.append(STRING_SINGLE_QUOTES);
			}
			insertString.append(((String) columnValue).replaceAll(STRING_SINGLE_QUOTES,
			                                                      STRING_SINGLE_QUOTES + STRING_SINGLE_QUOTES));
			if (!columnValue.toString()
			                .endsWith(STRING_SINGLE_QUOTES))
			{
				insertString.append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
			}
		}
		else if (columnValue instanceof Character)
		{
			insertString.append(STRING_SINGLE_QUOTES)
			            .append(columnValue)
			            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof Date)
		{
			Date date = (Date) columnValue;
			insertString.append(STRING_SINGLE_QUOTES)
			            .append(getSdf().format(date))
			            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof LocalDate)
		{
			LocalDate date = (LocalDate) columnValue;
			insertString.append(STRING_SINGLE_QUOTES)
			            .append(getDateFormat().format(date))
			            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof LocalDateTime)
		{
			LocalDateTime date = (LocalDateTime) columnValue;
			insertString.append(STRING_SINGLE_QUOTES)
			            .append(getDateTimeFormat().format(date))
			            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof BaseEntity)
		{
			BaseEntity wct = (BaseEntity) columnValue;
			Object value = wct.getId();
			Field fff = field;
			if (field != null &&
			    field.isAnnotationPresent(JoinColumn.class) &&
			    !field.getAnnotation(JoinColumn.class)
			          .referencedColumnName()
			          .equals(STRING_EMPTY))
			{
				InsertStatement is = new InsertStatement(wct);
				fff = is.getColumn(field.getAnnotation(JoinColumn.class)
				                        .referencedColumnName());
				fff.setAccessible(true);
				try
				{
					value = fff.get(wct);
				}
				catch (Exception e)
				{
					LogFactory.getLog(getClass())
					          .log(Level.WARNING, "Unable to extract", e);
				}
			}
			insertString.append(getValue(value, fff));
		}
		else if (columnValue instanceof Enum)
		{
			Enum wct = (Enum) columnValue;
			if (field != null && field.isAnnotationPresent(Enumerated.class))
			{
				Enumerated eee = field.getAnnotation(Enumerated.class);
				if (eee.value()
				       .equals(EnumType.STRING))
				{
					insertString.append(STRING_SINGLE_QUOTES)
					            .append(wct.name())
					            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
				}
				else
				{
					insertString.append(STRING_SINGLE_QUOTES)
					            .append(wct.ordinal())
					            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
				}
			}
			else
			{
				insertString.append(STRING_SINGLE_QUOTES)
				            .append(wct.toString())
				            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
			}
		}
		else if (columnValue instanceof UUID)
		{
			UUID wct = (UUID) columnValue;
			insertString.append(STRING_SINGLE_QUOTES)
			            .append(wct.toString())
			            .append(STRING_SINGLE_QUOTES + STRING_COMMNA_SPACE);
		}
		else if (columnValue instanceof byte[])
		{

			String bitString = STRING_HEX_SQL_START + getHex((byte[]) columnValue);
			insertString.append(bitString)
			            .append(STRING_COMMNA_SPACE);
		}
		else
		{
			insertString.append("NOT KNOWN TYPE - ")
			            .append(columnValue.getClass()
			                               .getCanonicalName());
		}
		return insertString.toString();
	}

	public String getTableName()
	{
		Class<?> c = obj.getClass();
		Table t = c.getAnnotation(Table.class);
		String tableName = STRING_EMPTY;
		if (t != null)
		{
			String catalog = t.catalog();
			if (!catalog.isEmpty())
			{
				tableName += catalog + STRING_DOT;
			}
			String schema = t.schema();
			if (!schema.isEmpty())
			{
				tableName += schema + STRING_DOT;
			}
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

	/**
	 * Returns a column field for a given name
	 *
	 * @param columnName
	 *
	 * @return
	 */
	public Field getColumn(String columnName)
	{
		for (Field field : getFields())
		{
			if (columnName.equalsIgnoreCase(getColumnName(field)))
			{
				return field;
			}
		}
		throw new UnsupportedOperationException("Invalid Column Name to Find - " + columnName);
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
				EmbeddedId embId = field.getAnnotation(EmbeddedId.class);
				if (idCol != null)
				{
					field.setAccessible(true);
					return Pair.of(getColumnName(field), field.get(getObject()));
				}
				if (embId != null)
				{
					//run the object through the analyzer
					field.setAccessible(true);
					Object be = field.get(obj);
					Field[] fields = be.getClass()
					                   .getFields();
					StringBuilder sb = new StringBuilder();
					StringBuilder valueList = new StringBuilder();
					for (Field field1 : fields)
					{
						if (isColumnReadable(field1))
						{
							sb.append(getColumnName(field1))
							  .append(STRING_COMMNA);

							Object fo = field.get(be);
							valueList.append(getValue(fo, field1))
							         .append(STRING_COMMNA);
						}
					}
					sb.deleteCharAt(sb.length() - 1);
					valueList.deleteCharAt(valueList.length() - 1);
					RawInsertObjectValue r = new RawInsertObjectValue().setRawInsert(valueList.toString());
					return Pair.of(sb.toString(), r);
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

	@SuppressWarnings("EqualsBetweenInconvertibleTypes")
	protected boolean isColumnReadable(Field field)
	{
		JoinColumn joinCol = field.getAnnotation(JoinColumn.class);
		Column col = field.getAnnotation(Column.class);
		Id idCol = field.getAnnotation(Id.class);
		EmbeddedId embId = field.getAnnotation(EmbeddedId.class);
		OneToOne oneToOne = field.getAnnotation(OneToOne.class);
		OneToMany oneToMany = field.getAnnotation(OneToMany.class);
		ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
		ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
		GeneratedValue genVal = field.getAnnotation(GeneratedValue.class);
		if (col == joinCol && joinCol == idCol && idCol == embId
		    && joinCol == oneToOne
		    && joinCol == oneToMany
		    && joinCol == manyToMany
		    && joinCol == manyToOne
		    && joinCol == genVal
		) //if everything is null go to next field, easier than is nulls
		{
			return false;
		}
		if (Collection.class.isAssignableFrom(field.getType()))
		{
			return false;
		}
		return true;
	}

	public BaseEntity getObject()
	{
		return obj;
	}

	public String getColumnName(Field field)
	{
		JoinColumn joinCol = field.getAnnotation(JoinColumn.class);
		Column col = field.getAnnotation(Column.class);
		EmbeddedId embId = field.getAnnotation(EmbeddedId.class);
		String columnName = STRING_EMPTY;

		if (joinCol != null)
		{
			columnName = joinCol.name();
		}
		else if (col != null)
		{
			columnName = col.name();
		}
		else if (embId != null)
		{
			columnName = STRING_EMPTY;
		}

		if (embId != null)
		{
			try
			{
				Object o = field.get(obj);
				Field[] f = o.getClass()
				             .getDeclaredFields();
				StringBuilder colNames = new StringBuilder();
				for (Field field1 : f)
				{
					if (isColumnReadable(field1))
					{
						colNames.append(getColumnName(field1))
						        .append(STRING_COMMNA);
					}
				}
				colNames.deleteCharAt(colNames.length() - 1);
				return colNames.toString();
			}
			catch (IllegalAccessException e)
			{
				columnName = STRING_EMPTY;
			}
		}
		if (columnName.isEmpty())
		{
			columnName = field.getName();
		}
		return columnName;
	}


	/**
	 * Goes through the object looking for fields, returns a set where the field name is mapped to the object
	 *
	 * @param updateFields
	 * 		Returns a map of field to update with the values
	 *
	 * @return A map of SingularAttribute and its object type
	 */
	@SuppressWarnings("WeakerAccess")
	@NotNull
	public Map<Field, Object> getUpdateFieldMap(BaseEntity<?, ?, ?> updateFields)
	{
		Map<Field, Object> map = new HashMap<>();
		List<Field> fieldList = allFields(updateFields.getClass(), new ArrayList<>());

		for (Field field : fieldList)
		{
			if (Modifier.isAbstract(field.getModifiers()) ||
			    Modifier.isStatic(field.getModifiers()) ||
			    Modifier.isFinal(field.getModifiers()) ||
			    field.isAnnotationPresent(Id.class) ||
			    !(
					    (field.isAnnotationPresent(Column.class)
					     //  || field.isAnnotationPresent(JoinColumn.class)
					     || field.isAnnotationPresent(ManyToOne.class))

			    )
			)
			{
				continue;
			}
			field.setAccessible(true);
			try
			{
				Object o = field.get(updateFields);
				if (o != null)
				{
					map.put(field, getValue(o, field));
				}
			}
			catch (IllegalAccessException e)
			{
				LogFactory.getLog("RunnableStatementUpdate")
				          .log(Level.SEVERE, "Unable to determine if field is populated or not", e);
			}
		}
		return map;
	}

	/**
	 * Returns a lsit of all fields for an object recursively
	 *
	 * @param object
	 * 		THe object class
	 * @param fieldList
	 * 		The list of fields
	 *
	 * @return A list of type Field
	 */
	private List<Field> allFields(Class<?> object, List<Field> fieldList)
	{
		fieldList.addAll(Arrays.asList(object.getDeclaredFields()));
		if (object.getSuperclass() != Object.class)
		{
			allFields(object.getSuperclass(), fieldList);
		}
		return fieldList;
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
