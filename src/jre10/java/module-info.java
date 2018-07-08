module com.jwebmp.entityassist {
	requires com.fasterxml.jackson.annotation;
	requires java.validation;

	requires com.jwebmp.guicedpersistence;
	requires com.jwebmp.logmaster;
	requires com.google.guice.extensions.persist;
	requires java.persistence;
	requires java.logging;
	requires java.sql;
	requires java.naming;
	requires commons.lang3;
	requires com.jwebmp.guicedinjection;

	provides com.jwebmp.guicedpersistence.db.PropertiesEntityManagerReader with com.jwebmp.entityassist.querybuilder.builders.LocalDateEntityManagerConvertorProperties;
}
