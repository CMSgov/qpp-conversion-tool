package gov.cms.qpp.conversion.model.validation;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * {@link SubPopulation} utilities
 */
public class SubPopulations {

	private static final Map<String, Function<SubPopulation, String>> KEY_TO_UNIQUEID;
	public static final String DENEXCEP = "DENEXCEP";
	public static final String DENEX = "DENEX";
	public static final String NUMER = "NUMER";
	public static final String DENOM = "DENOM";
	public static final String IPOP = "IPOP";
	public static final String IPP = "IPP";

	static {
		Map<String, Function<SubPopulation, String>> keyToUniqueId = new HashMap<>();
		keyToUniqueId.put(DENEXCEP, SubPopulation::getDenominatorExceptionsUuid);
		keyToUniqueId.put(DENEX, SubPopulation::getDenominatorExclusionsUuid);
		keyToUniqueId.put(DENOM, SubPopulation::getDenominatorUuid);
		keyToUniqueId.put(NUMER, SubPopulation::getNumeratorUuid);
		keyToUniqueId.put(IPOP, SubPopulation::getNumeratorUuid);
		KEY_TO_UNIQUEID = Collections.unmodifiableMap(keyToUniqueId);
	}

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

		Function<SubPopulation, String> lookup = KEY_TO_UNIQUEID.get(key);
		if (lookup == null) {
			throw new IllegalArgumentException("Illegal key: " + key + ", expected one of " + KEY_TO_UNIQUEID.keySet());
		}

		return lookup.apply(subPopulation);
	}

	/**
	 * Get an exclusive key set of sub population lebels.
	 *
	 * @param exclusions keys to exclude
	 * @return exclusive key set
	 */
	public static Set<String> getExclusiveKeys(Set<String> exclusions) {
		Set<String> exclusive = Sets.newHashSet(KEY_TO_UNIQUEID.keySet());
		exclusive.removeAll(exclusions);
		return exclusive;
	}

	/**
	 * Gets all the valid subpopulation lookup keys
	 *
	 * @return DENEXCEP, DENEX, DENOM, NUMER, IPOP
	 */
	static Set<String> getKeys() {
		return KEY_TO_UNIQUEID.keySet();
	}

}
