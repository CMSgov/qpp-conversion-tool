package gov.cms.qpp.conversion.model.validation;

import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * {@link SubPopulation} utilities
 */
public class SubPopulations {

	/**
	 * Helper class, should not be instantiated.
	 */
	private SubPopulations() {
	}

	/**
	 * Returns a unique id for a sub population type
	 *
	 * @param key Accepts NUMER, DENOM, DENEX, and DENEXCEP
	 * @param subPopulation The subpopulation from which to get the unique id
	 * @return A unique id, or null if one is not present for the given key
	 */
	public static String getUniqueIdForKey(String key, SubPopulation subPopulation) {
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(subPopulation, "subPopulation");

		Function<SubPopulation, String> lookup = SubPopulationLabel.getGetter(key);
		if (lookup == null) {
			throw new IllegalArgumentException("Illegal key: " + key + ", expected one of " + SubPopulationLabel.names());
		}

		return lookup.apply(subPopulation);
	}

	/**
	 * Get an exclusive key set of sub population labels.
	 *
	 * @param exclusions keys to exclude
	 * @return exclusive key set
	 */
	public static Set<String> getExclusiveKeys(Set<String> exclusions) {
		Set<String> exclusive = Sets.newHashSet(SubPopulationLabel.aliasSet());
		exclusive.removeAll(exclusions);
		return exclusive;
	}

	public static String[] getKeyAliases(String key) {
		SubPopulationLabel found = SubPopulationLabel.findPopulation(key);
		return found == null ? new String[] {} : found.getAliases();
	}

	/**
	 * Gets all the valid subpopulation lookup keys
	 *
	 * @return DENEXCEP, DENEX, DENOM, NUMER, IPOP
	 */
	static Set<String> getKeys() {
		return SubPopulationLabel.aliasSet();
	}

}
