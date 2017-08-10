package gov.cms.qpp.conversion.model;


import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Program {
	MIPS("MIPS_GROUP", "MIPS_INDIV", "MIPS"),
	CPC("CPCPLUS"),
	ALL;

	private final Set<String> aliases;

	Program(String... value) {
		this.aliases = Arrays.stream(value).collect(Collectors.toSet());
	}

	public static Program getInstance(String name) {
		return Arrays.stream(Program.values())
				.filter(program -> program.aliases.contains(name))
				.findFirst()
				.orElse(Program.ALL);
	}
}
