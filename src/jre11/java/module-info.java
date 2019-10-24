import com.guicedee.entityassist.injections.EntityAssistBinder;
import com.guicedee.entityassist.injections.bigdecimal.*;
import com.guicedee.entityassist.injections.biginteger.*;
import com.guicedee.entityassist.injections.integer.*;
import com.guicedee.entityassist.injections.longs.*;
import com.guicedee.entityassist.injections.strings.*;
import com.guicedee.entityassist.services.EntityAssistIDMapping;
import com.guicedee.guicedinjection.interfaces.IGuiceDefaultBinder;
import com.guicedee.guicedpersistence.services.ITransactionHandler;

module com.guicedee.entityassist {

	exports com.guicedee.entityassist;
	exports com.guicedee.entityassist.converters;
	exports com.guicedee.entityassist.enumerations;
	exports com.guicedee.entityassist.querybuilder;
	exports com.guicedee.entityassist.exceptions;
	exports com.guicedee.entityassist.querybuilder.builders;
	exports com.guicedee.entityassist.querybuilder.statements;

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

	opens com.guicedee.entityassist to org.hibernate.orm.core, com.fasterxml.jackson.databind;


	opens com.guicedee.entityassist.injections.bigdecimal to org.hibernate.orm.core, com.fasterxml.jackson.databind, com.google.guice;
	opens com.guicedee.entityassist.injections.biginteger to org.hibernate.orm.core, com.fasterxml.jackson.databind, com.google.guice;
	opens com.guicedee.entityassist.injections.integer to org.hibernate.orm.core, com.fasterxml.jackson.databind, com.google.guice;
	opens com.guicedee.entityassist.injections.longs to org.hibernate.orm.core, com.fasterxml.jackson.databind, com.google.guice;
	opens com.guicedee.entityassist.injections.strings to org.hibernate.orm.core, com.fasterxml.jackson.databind, com.google.guice;

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
