package gov.cms.qpp.conversion.api.helper;

import ch.qos.logback.classic.PatternLayout;

public class ThreadRequestPartContext extends PatternLayout {
	static {
		PatternLayout.defaultConverterMap.put(
				"thread", ThreadRequestPartConverter.class.getName());
	}
}