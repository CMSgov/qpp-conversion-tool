package gov.cms.qpp.conversion.model;


import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Construct that helps categorize submissions by program name.
 */
public enum Program {
	MIPS("MIPS_GROUP", "MIPS_INDIV", "MIPS"),
	CPC("CPCPLUS"),
	ALL;

	private final Set<String> aliases;

	/**
	 * Construct program
	 *
	 * @param value list of aliases
	 */
	Program(String... value) {
		this.aliases = Arrays.stream(value).collect(Collectors.toSet());
	}

	/**
	 * Retrieve a program for the given name.
	 *
	 * @param name used to find the corresponding program
	 * @return the corresponding program or {@link Program#ALL} if none found.
	 */
	public static Program getInstance(String name) {
		return Arrays.stream(Program.values())
				.filter(program -> program.aliases.contains(name))
				.findFirst()
				.orElse(Program.ALL);
	}
}
