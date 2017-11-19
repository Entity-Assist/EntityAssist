package za.co.mmagon.entityassist.querybuilder;

import za.co.mmagon.entityassist.CoreEntity;

import java.io.Serializable;

public class DefaultQueryBuilder<J extends DefaultQueryBuilder<J, E, I>, E extends CoreEntity<E, J, I>, I extends Serializable>
		extends QueryBuilderCore<J, E, I>
{

}
