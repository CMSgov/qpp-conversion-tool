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
	APP("MIPS_APP1_INDIV", "MIPS_APP1_GROUP", "MIPS_APP1_APMENTITY"),
	APP_PLUS("APP_PLUS_INDIV", "APP_PLUS_GROUP", "APP_PLUS_APMENTITY"),
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
	 * Checks if a node is using the appPlus program
	 *
	 * @param node
	 * @return
	 */
	public static boolean isAppPlus(Node node) {
		return extractProgram(node) == Program.APP_PLUS;
	}

	/**
	 * Checks if a node is using the ssp program
	 *
	 * @param node
	 * @return
	 */
	public static boolean isSsp(Node node) {
		return extractProgram(node) == Program.SSP;
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
