import com.entityassist.injections.EntityAssistBinder;
import com.entityassist.injections.bigdecimal.*;
import com.entityassist.injections.biginteger.*;
import com.entityassist.injections.integer.*;
import com.entityassist.injections.longs.*;
import com.entityassist.injections.strings.*;
import com.entityassist.services.EntityAssistIDMapping;
import com.guicedee.guicedinjection.interfaces.IGuiceDefaultBinder;
import com.guicedee.guicedpersistence.services.ITransactionHandler;

module com.entityassist {

	exports com.entityassist;
	exports com.entityassist.converters;
	exports com.entityassist.enumerations;
	exports com.entityassist.querybuilder;
	exports com.entityassist.exceptions;
	exports com.entityassist.querybuilder.builders;
	exports com.entityassist.querybuilder.statements;

	requires com.fasterxml.jackson.annotation;
	requires java.validation;

	requires com.guicedee.guicedpersistence;

	requires com.guicedee.logmaster;
	requires com.google.guice.extensions.persist;
	requires java.persistence;
	requires java.logging;
	requires java.sql;
	requires java.naming;

	requires com.guicedee.guicedinjection;
	requires com.google.common;
	requires com.google.guice;

	requires org.hibernate.orm.core;

	opens com.entityassist to org.hibernate.orm.core, com.fasterxml.jackson.databind,com.google.guice;


	opens com.entityassist.injections.bigdecimal to org.hibernate.orm.core, com.fasterxml.jackson.databind, com.google.guice;
	opens com.entityassist.injections.biginteger to org.hibernate.orm.core, com.fasterxml.jackson.databind, com.google.guice;
	opens com.entityassist.injections.integer to org.hibernate.orm.core, com.fasterxml.jackson.databind, com.google.guice;
	opens com.entityassist.injections.longs to org.hibernate.orm.core, com.fasterxml.jackson.databind, com.google.guice;
	opens com.entityassist.injections.strings to org.hibernate.orm.core, com.fasterxml.jackson.databind, com.google.guice;

	provides IGuiceDefaultBinder with EntityAssistBinder;
	provides EntityAssistIDMapping with
			BigDecimalToBigIntIDMapping,
			BigDecimalToDoubleIDMapping,
			BigDecimalToFloatIDMapping,
			BigDecimalToIntIDMapping,
			BigDecimalToStringIDMapping,
			BigDecimalToLongIDMapping,
			BigDecimalIDMapping,
			BigIntegerBigDecimalIDMapping,
			BigIntegerFloatIDMapping,
			BigIntegerIntegerIDMapping,
			BigIntegerLongIDMapping,
			BigIntegerDoubleIDMapping,
			BigIntegerStringIDMapping,
			BigIntegerIDMapping,
			LongBigIntegerIDMapping,
			LongIntegerIDMapping,
			LongStringIDMapping,
			LongBigDecimalIDMapping,
			LongFloatIDMapping,
			LongIDMapping,
			IntegerBigDecimalIDMapping,
			IntegerBigIntegerIDMapping,
			IntegerDoubleIDMapping,
			IntegerFloatIDMapping,
			IntegerLongIDMapping,
			IntegerStringIDMapping,
			IntegerIDMapping,
			StringIDMapping,
			StringIntegerIDMapping,
			StringUUIDIDMapping,
			StringBigDecimalIDMapping,
			StringBigIntegerIDMapping,
			StringLongIDMapping
			;

	uses ITransactionHandler;
	uses EntityAssistIDMapping;

}
