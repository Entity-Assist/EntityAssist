package com.entityassist.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Converter()
public class LocalDateTimestampAttributeConverter implements AttributeConverter<LocalDate, Timestamp>
{
	@Override
	public Timestamp convertToDatabaseColumn(LocalDate attribute)
	{
		return (attribute == null ? null : Timestamp.valueOf(attribute.format(DateTimeFormatter.ISO_DATE_TIME)));
	}

	@Override
	public LocalDate convertToEntityAttribute(Timestamp sqlTimestamp)
	{
		return (sqlTimestamp == null ? null : sqlTimestamp.toLocalDateTime().toLocalDate());
	}
}
