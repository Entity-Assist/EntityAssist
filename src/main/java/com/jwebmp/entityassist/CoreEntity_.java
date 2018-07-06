package com.jwebmp.entityassist;

import com.jwebmp.entityassist.enumerations.ActiveFlag;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@StaticMetamodel(CoreEntity.class)
public abstract class CoreEntity_
		extends BaseEntity_
{

	public static volatile SingularAttribute<CoreEntity, LocalDateTime> warehouseLastUpdatedTimestamp;
	public static volatile SingularAttribute<CoreEntity, LocalDateTime> effectiveToDate;
	public static volatile SingularAttribute<CoreEntity, LocalDateTime> effectiveFromDate;
	public static volatile SingularAttribute<CoreEntity, LocalDateTime> warehouseCreatedTimestamp;
	public static volatile SingularAttribute<CoreEntity, String> referenceId;
	public static volatile SingularAttribute<CoreEntity, ActiveFlag> activeFlag;

}

