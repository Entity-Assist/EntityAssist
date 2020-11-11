package com.entityassist.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

@Converter()
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date>, Serializable
{

	@Override
	public Date convertToDatabaseColumn(LocalDate locDate)
	{
		return (locDate == null ? null : Date.valueOf(locDate));
	}

	@Override
	public LocalDate convertToEntityAttribute(Date sqlDate)
	{
		return (sqlDate == null ? null : sqlDate.toLocalDate());
	}
}
