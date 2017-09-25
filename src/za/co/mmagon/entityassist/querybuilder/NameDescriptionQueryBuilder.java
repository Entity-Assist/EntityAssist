package za.co.mmagon.entityassist.querybuilder;

import za.co.mmagon.entityassist.CoreEntity;

import java.io.Serializable;

public abstract class NameDescriptionQueryBuilder<J extends NameDescriptionQueryBuilder<J, E, I>, E extends CoreEntity<E, J, I>, I extends Serializable>
		extends QueryBuilderCore<J, E, I>
{
	
	public J findByName(String... name)
	{
		getFilters().add(getRoot().get("name").in((Object[]) name));
		inActiveRange();
		return (J) this;
	}
	
	public J findByDescription(String... description)
	{
		getFilters().add(getRoot().get("description").in((Object[]) description));
		inActiveRange();
		return (J) this;
	}
	
}
