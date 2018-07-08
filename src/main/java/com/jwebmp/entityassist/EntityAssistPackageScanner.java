package com.jwebmp.entityassist;

import com.jwebmp.guicedinjection.scanners.PackageContentsScanner;

import java.util.HashSet;
import java.util.Set;

public class EntityAssistPackageScanner implements PackageContentsScanner
{
	@Override
	public Set<String> searchFor()
	{
		Set<String> set = new HashSet<>();
		set.add("com.jwebmp.entityassist");
		return set;
	}
}
