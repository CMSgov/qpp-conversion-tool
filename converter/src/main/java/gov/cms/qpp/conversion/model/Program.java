package gov.cms.qpp.conversion.model;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.cms.qpp.conversion.model.Constants.RAW_PROGRAM_NAME;

/**
 * Construct that helps categorize submissions by program name.
 */
public enum Program {
	MIPS("MIPS_GROUP", "MIPS_INDIV", "MIPS_VIRTUALGROUP", "MIPS", "MIPS_APMENTITY", "MIPS_SUBGROUP"),
	PCF("PCF"),
	APP("MIPS_APP1_INDIV", "MIPS_APP1_GROUP", "MIPS_APP1_APMENTITY"),
	SSP("SSP_PI_INDIV", "SSP_PI_GROUP", "SSP_PI_APMENTITY"),
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
	 * Checks if a node is using the PCF program
	 *
	 * @param node to check
	 * @return result of check
	 */
	public static boolean isPcf(Node node) {
		return extractProgram(node) == Program.PCF;
	}

	/**
	 * Checks if a node is using the MIPS program
	 *
	 * @param node
	 * @return
	 */
	public static boolean isMips(Node node) {
		return extractProgram(node) == Program.MIPS;
	}

	/**
	 * Checks if a node is using the APP program
	 *
	 * @param node
	 * @return
	 */
	public static boolean isApp(Node node) {
		return extractProgram(node) == Program.APP;
	}

	/**
	 * Extracts a program type from a node
	 *
	 * @param node to interrogate
	 * @return program with which node is affiliated
	 */
	public static Program extractProgram(Node node) {
		return Program.getInstance(node.getValue(RAW_PROGRAM_NAME));
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
		return Arrays.stream(Program.values())
			.flatMap(program -> program.aliases.stream())
			.collect(Collectors.toCollection(HashSet::new));
	}

	Set<String> getAliases() {
		return aliases;
	}
}
