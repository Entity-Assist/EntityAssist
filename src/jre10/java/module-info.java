import com.jwebmp.entityassist.EntityAssistPackageScanner;
import com.jwebmp.entityassist.querybuilder.builders.LocalDateEntityManagerConvertorProperties;
import com.jwebmp.guicedinjection.scanners.PackageContentsScanner;
import com.jwebmp.guicedpersistence.db.PropertiesEntityManagerReader;
import com.jwebmp.guicedpersistence.services.ITransactionHandler;

module com.jwebmp.entityassist {

	exports com.jwebmp.entityassist;
	exports com.jwebmp.entityassist.converters;
	exports com.jwebmp.entityassist.enumerations;
	exports com.jwebmp.entityassist.querybuilder;
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
	requires commons.lang3;
	requires com.jwebmp.guicedinjection;

	uses ITransactionHandler;

	provides PropertiesEntityManagerReader with LocalDateEntityManagerConvertorProperties;
	provides PackageContentsScanner with EntityAssistPackageScanner;
}
