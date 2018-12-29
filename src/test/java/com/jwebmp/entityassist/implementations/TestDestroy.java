package com.jwebmp.entityassist.implementations;

import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.guicedinjection.interfaces.IDefaultService;
import com.jwebmp.testing.BaseTest;
import com.jwebmp.testing.services.ITestInstanceDestroyService;
import com.jwebmp.testing.services.ITestInstanceInitializerService;

public class TestDestroy
		implements ITestInstanceDestroyService, ITestInstanceInitializerService, IDefaultService<TestDestroy>
{
	@Override
	public void destroy(BaseTest testInstance)
	{
		GuiceContext.destroy();
	}

	@Override
	public void initialize(BaseTest testInstance)
	{
		GuiceContext.inject();
	}
}
