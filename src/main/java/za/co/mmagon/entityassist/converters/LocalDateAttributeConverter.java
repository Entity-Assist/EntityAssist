package za.co.mmagon.entityassist.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

@Converter(autoApply = true)
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
