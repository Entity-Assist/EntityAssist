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

	uses ITransactionHandler;

}
