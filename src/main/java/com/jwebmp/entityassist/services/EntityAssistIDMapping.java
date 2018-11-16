package com.jwebmp.entityassist.services;

import com.jwebmp.guicedinjection.interfaces.IDefaultService;
import com.jwebmp.logger.LogFactory;

import java.lang.reflect.ParameterizedType;
import java.util.logging.Level;

/**
 * Maps an entity assist ID Mapping for db returned data from the query.
 * <p>
 * Is a class and not an interface to read the typed mappings
 *
 * @param <DB>
 * 		The Database Returned Type
 * @param <OBJECT>
 * 		The object to render
 */
public abstract class EntityAssistIDMapping<DB, OBJECT>
		implements IDefaultService<EntityAssistIDMapping<DB, OBJECT>>
{
	public abstract OBJECT toObject(DB dbReturned);

	@SuppressWarnings("unchecked")
	public Class<DB> getDBClassType()
	{
		try
		{
			return (Class<DB>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

		}
		catch (Exception e)
		{
			LogFactory.getLog("EntityAssistIDMapping")
			          .log(Level.SEVERE,
			               "Cannot return the db or entity id class - config seems wrong. " +
			               "Check that a builder is attached to this entity as the second generic field type\n" +
			               e, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class<OBJECT> getObjectClassType()
	{
		try
		{
			return (Class<OBJECT>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		}
		catch (Exception e)
		{
			LogFactory.getLog("EntityAssistIDMapping")
			          .log(Level.SEVERE,
			               "Cannot return the db or entity id class - config seems wrong. " +
			               "Check that a builder is attached to this entity as the second generic field type\n" +
			               e, e);
		}
		return null;
	}
}
