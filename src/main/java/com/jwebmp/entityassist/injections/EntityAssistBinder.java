package com.jwebmp.entityassist.injections;

import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.jwebmp.entityassist.EntityAssistException;
import com.jwebmp.entityassist.services.EntityAssistIDMapping;
import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;
import com.jwebmp.guicedinjection.interfaces.IGuiceDefaultBinder;
import com.jwebmp.guicedinjection.pairing.Pair;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EntityAssistBinder
		implements IGuiceDefaultBinder<EntityAssistBinder, GuiceInjectorModule>
{
	public static final Key<Map> entityIDMappingsKey = Key.get(Map.class, Names.named("EntityAssistIDMap"));

	private final Map<Class<? extends EntityAssistIDMapping>, Pair<Class<?>, Class<?>>> entityIDMappings = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public static EntityAssistIDMapping lookup(Class dbType, Class fieldType)
	{
		Map<Class<? extends EntityAssistIDMapping>, Pair<Class<?>, Class<?>>> map = GuiceContext.get(entityIDMappingsKey);
		for (Map.Entry<Class<? extends EntityAssistIDMapping>, Pair<Class<?>, Class<?>>> entry : map.entrySet())
		{
			Class<? extends EntityAssistIDMapping> db = entry.getKey();
			Pair<Class<?>, Class<?>> field = entry.getValue();
			if (field.getKey()
			         .equals(dbType) && field.getValue()
			                                 .equals(fieldType))
			{
				return GuiceContext.get(db);
			}
		}
		throw new EntityAssistException("Unable to find an ID mapping for db type [" + dbType + "] and id field type [" + fieldType +
		                                "]. You can create a service for EntityAssistIDMapping to resolve this");
	}

	@Override
	public void onBind(GuiceInjectorModule module)
	{
		module.bind(entityIDMappingsKey)
		      .toProvider(() ->
		                  {
			                  if (entityIDMappings.isEmpty())
			                  {
				                  @NotNull Set<EntityAssistIDMapping> loader = GuiceContext.instance()
				                                                                           .getLoader(EntityAssistIDMapping.class, ServiceLoader.load(EntityAssistIDMapping.class));
				                  for (EntityAssistIDMapping<?, ?> mapping : loader)
				                  {
					                  @SuppressWarnings("unchecked")
					                  Pair<Class<?>, Class<?>> pair = new Pair<>(mapping.getDBClassType(), mapping.getObjectClassType());
					                  entityIDMappings.put(mapping.getClass(), pair);
				                  }
			                  }
			                  return entityIDMappings;
		                  })
		      .in(Singleton.class);
	}
}
