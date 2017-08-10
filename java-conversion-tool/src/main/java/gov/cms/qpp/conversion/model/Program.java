package gov.cms.qpp.conversion.model;


import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Program {
	MIPS("MIPS_GROUP", "MIPS_INDIV", "MIPS"),
	CPC("CPCPLUS"),
	ALL();

	private Set<String> values;

	Program(String... value) {
		this.values = Arrays.stream(value).collect(Collectors.toSet());
	}
}
