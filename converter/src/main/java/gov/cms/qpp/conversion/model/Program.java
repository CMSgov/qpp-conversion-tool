package gov.cms.qpp.conversion.model;


import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Construct that helps categorize submissions by program name.
 */
public enum Program {
	MIPS("MIPS_GROUP", "MIPS_INDIV"),
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
	 * Checks if a node is using the CPC program
	 *
	 * @param node
	 * @return
	 */
	public static boolean isCpc(Node node) {
		return extractProgram(node) == Program.CPC;
	}

	/**
	 * Extracts a program type from a node
	 *
	 * @param node
	 * @return
	 */
	public static Program extractProgram(Node node) {
		return Program.getInstance(node.getValue(ClinicalDocumentDecoder.PROGRAM_NAME));
	}

	/**
	 * Retrieve a program for the given name.
	 *
	 * @param name used to find the corresponding program
	 * @return the corresponding program or {@link Program#ALL} if none found.
	 */
	public static Program getInstance(final String name) {
		String upperName = Optional.ofNullable(name).map(String::toUpperCase).orElse("");
		return Arrays.stream(Program.values())
				.filter(program -> program.aliases.contains(upperName))
				.findFirst()
				.orElse(Program.ALL);
	}

	/**
	 * Returns the {@link Set} of all the valid program names under each program.
	 *
	 * @return A {@link Set} of the aliases.
	 */
	public static Set<String> setOfAliases() {
		return Arrays.stream(Program.values()).flatMap(program -> program.aliases.stream()).collect(Collectors.toCollection(HashSet::new));
	}
}
