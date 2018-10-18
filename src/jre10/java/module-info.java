import com.jwebmp.entityassist.injections.EntityAssistBinder;
import com.jwebmp.entityassist.services.EntityAssistIDMapping;
import com.jwebmp.guicedinjection.interfaces.IGuiceDefaultBinder;
import com.jwebmp.guicedpersistence.services.ITransactionHandler;

module com.jwebmp.entityassist {

	exports com.jwebmp.entityassist;
	exports com.jwebmp.entityassist.converters;
	exports com.jwebmp.entityassist.enumerations;
	exports com.jwebmp.entityassist.querybuilder;
	exports com.jwebmp.entityassist.exceptions;
	exports com.jwebmp.entityassist.querybuilder.builders;
	exports com.jwebmp.entityassist.querybuilder.statements;

	requires com.fasterxml.jackson.annotation;
	requires java.validation;

	requires com.jwebmp.guicedpersistence;

	requires com.jwebmp.logmaster;
	requires com.google.guice.extensions.persist;
	requires java.persistence;
	requires java.logging;
	requires java.sql;
	requires java.naming;

	requires com.jwebmp.guicedinjection;
	requires com.google.common;
	requires com.google.guice;

	opens com.jwebmp.entityassist to org.hibernate.orm.core, com.fasterxml.jackson.databind;

	provides IGuiceDefaultBinder with EntityAssistBinder;
	provides EntityAssistIDMapping with com.jwebmp.entityassist.injections.bigdecimal.BigDecimalToBigIntIDMapping,
			                               com.jwebmp.entityassist.injections.bigdecimal.BigDecimalToDoubleIDMapping,
			                               com.jwebmp.entityassist.injections.bigdecimal.BigDecimalToFloatIDMapping,
			                               com.jwebmp.entityassist.injections.bigdecimal.BigDecimalToIntIDMapping,
			                               com.jwebmp.entityassist.injections.bigdecimal.BigDecimalToStringIDMapping,
			                               com.jwebmp.entityassist.injections.bigdecimal.BigDecimalToLongIDMapping,
			                               com.jwebmp.entityassist.injections.bigdecimal.BigDecimalIDMapping,
			                               com.jwebmp.entityassist.injections.biginteger.BigIntegerBigDecimalIDMapping,
			                               com.jwebmp.entityassist.injections.biginteger.BigIntegerFloatIDMapping,
			                               com.jwebmp.entityassist.injections.biginteger.BigIntegerIntegerIDMapping,
			                               com.jwebmp.entityassist.injections.biginteger.BigIntegerLongIDMapping,
			                               com.jwebmp.entityassist.injections.biginteger.BigIntegerDoubleIDMapping,
			                               com.jwebmp.entityassist.injections.biginteger.BigIntegerStringIDMapping,
			                               com.jwebmp.entityassist.injections.biginteger.BigIntegerIDMapping,
			                               com.jwebmp.entityassist.injections.longs.LongBigIntegerIDMapping,
			                               com.jwebmp.entityassist.injections.longs.LongIntegerIDMapping,
			                               com.jwebmp.entityassist.injections.longs.LongStringIDMapping,
			                               com.jwebmp.entityassist.injections.longs.LongBigDecimalIDMapping,
			                               com.jwebmp.entityassist.injections.longs.LongFloatIDMapping,
			                               com.jwebmp.entityassist.injections.longs.LongIDMapping,
			                               com.jwebmp.entityassist.injections.integer.IntegerBigDecimalIDMapping,
			                               com.jwebmp.entityassist.injections.integer.IntegerBigIntegerIDMapping,
			                               com.jwebmp.entityassist.injections.integer.IntegerDoubleIDMapping,
			                               com.jwebmp.entityassist.injections.integer.IntegerFloatIDMapping,
			                               com.jwebmp.entityassist.injections.integer.IntegerLongIDMapping,
			                               com.jwebmp.entityassist.injections.integer.IntegerStringIDMapping,
			                               com.jwebmp.entityassist.injections.integer.IntegerIDMapping,
			                               com.jwebmp.entityassist.injections.strings.StringIDMapping,
			                               com.jwebmp.entityassist.injections.strings.StringIntegerIDMapping,
			                               com.jwebmp.entityassist.injections.strings.StringUUIDIDMapping,
			                               com.jwebmp.entityassist.injections.strings.StringBigDecimalIDMapping,
			                               com.jwebmp.entityassist.injections.strings.StringBigIntegerIDMapping,
			                               com.jwebmp.entityassist.injections.strings.StringLongIDMapping
			;

	uses ITransactionHandler;
	uses com.jwebmp.entityassist.services.EntityAssistIDMapping;

}
