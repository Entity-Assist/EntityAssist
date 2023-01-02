package com.entityassist;

import com.entityassist.querybuilder.*;

public class TransYtdQueryBuilder
		extends QueryBuilder<TransYtdQueryBuilder, TransYtd, TransYtdPK>
{
	public boolean onCreate(EntityClass entity)
	{
		return true;
	}
	
	public boolean onUpdate(EntityClass entity)
	{
		return true;
	}
}
