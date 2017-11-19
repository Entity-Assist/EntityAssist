package za.co.mmagon.entityassist.querybuilder.builders;

import za.co.mmagon.entityassist.CoreEntity;
import za.co.mmagon.entityassist.querybuilder.QueryBuilderCore;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public abstract class NameDescriptionQueryBuilder<J extends NameDescriptionQueryBuilder<J, E, I>, E extends CoreEntity<E, J, I>, I extends Serializable>
		extends QueryBuilderCore<J, E, I>
{
	@SuppressWarnings("unchecked")
	@NotNull
	public J findByName(String... name)
	{
		getFilters().add(getRoot().get("name").in((Object[]) name));
		inActiveRange();
		return (J) this;
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public J findByDescription(String... description)
	{
		getFilters().add(getRoot().get("description").in((Object[]) description));
		inActiveRange();
		return (J) this;
	}

}
