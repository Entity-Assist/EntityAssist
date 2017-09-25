package za.co.mmagon.entityassist.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class LocalDateTimestampAttributeConverter implements AttributeConverter<LocalDate, Timestamp>
{
	@Override
	public LocalDate convertToEntityAttribute(Timestamp sqlTimestamp)
	{
		return (sqlTimestamp == null ? null : sqlTimestamp.toLocalDateTime().toLocalDate());
	}
	
	@Override
	public Timestamp convertToDatabaseColumn(LocalDate attribute)
	{
		return (attribute == null ? null : Timestamp.valueOf(attribute.format(DateTimeFormatter.ISO_DATE_TIME)));
	}
}
