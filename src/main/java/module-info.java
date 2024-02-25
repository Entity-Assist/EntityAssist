import com.guicedee.guicedpersistence.services.*;

module com.entityassist {
	
	exports com.entityassist;
	exports com.entityassist.services.entities;
	exports com.entityassist.services.querybuilders;
	exports com.entityassist.converters;
	exports com.entityassist.enumerations;
	exports com.entityassist.querybuilder;
	exports com.entityassist.exceptions;
	exports com.entityassist.querybuilder.builders;

	requires com.guicedee.guicedpersistence;
	
	requires java.naming;
	requires java.sql;
	
	requires jakarta.persistence;
	requires jakarta.xml.bind;
	
	opens com.entityassist to org.hibernate.orm.core, com.fasterxml.jackson.databind, com.google.guice, org.hibernate.validator;
	
	uses ITransactionHandler;

}
