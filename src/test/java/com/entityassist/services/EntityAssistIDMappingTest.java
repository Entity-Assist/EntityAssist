package com.entityassist.services;

import com.entityassist.injections.bigdecimal.BigDecimalToLongIDMapping;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class EntityAssistIDMappingTest
{
	@Test
	public void testSearchIDMapping()
	{
		Class<?> type1 = new BigDecimalToLongIDMapping().getDBClassType();
		Class<?> type2 = new BigDecimalToLongIDMapping().getObjectClassType();

		assertEquals(type1, BigDecimal.class);
	}
}