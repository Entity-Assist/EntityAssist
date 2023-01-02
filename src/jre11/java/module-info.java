module com.entityassist {
	
	exports com.entityassist;
	exports com.entityassist.services.entities;
	exports com.entityassist.services.querybuilders;
	exports com.entityassist.enumerations;
	exports com.entityassist.querybuilder;
	exports com.entityassist.exceptions;
	exports com.entityassist.querybuilder.builders;

	requires java.sql;
	
	requires jakarta.persistence;
	requires static org.hibernate.orm.core;
	
	opens com.entityassist to org.hibernate.orm.core, com.fasterxml.jackson.databind, com.google.guice, org.hibernate.validator;
	opens com.entityassist.exceptions to com.fasterxml.jackson.databind, com.google.guice, org.hibernate.orm.core, org.hibernate.validator;
}
