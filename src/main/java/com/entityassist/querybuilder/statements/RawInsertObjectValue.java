package com.entityassist.querybuilder.statements;

public class RawInsertObjectValue
{
	private String rawInsert;

	public String getRawInsert()
	{
		return rawInsert;
	}

	public RawInsertObjectValue setRawInsert(String rawInsert)
	{
		this.rawInsert = rawInsert;
		return this;
	}

	@Override
	public String toString()
	{
		return rawInsert;
	}
}
