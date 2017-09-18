package gov.cms.qpp.conversion.model.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class SubPopulations {

	private static final Map<String, Function<SubPopulation, String>> KEY_TO_UNIQUEID;

	static {
		Map<String, Function<SubPopulation, String>> keyToUniqueId = new HashMap<>();
		keyToUniqueId.put("DENEXCEP", SubPopulation::getDenominatorExceptionsUuid);
		keyToUniqueId.put("DENEX", SubPopulation::getDenominatorExclusionsUuid);
		keyToUniqueId.put("DENOM", SubPopulation::getDenominatorUuid);
		keyToUniqueId.put("NUMER", SubPopulation::getNumeratorUuid);
		KEY_TO_UNIQUEID = Collections.unmodifiableMap(keyToUniqueId);
	}

	public static String getUniqueIdForKey(String key, SubPopulation subPopulation) {
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(subPopulation, "subPopulation");

		Function<SubPopulation, String> lookup = KEY_TO_UNIQUEID.get(key);
		if (lookup == null) {
			throw new IllegalArgumentException("Illegal key: " + key + ", expected one of " + KEY_TO_UNIQUEID.keySet());
		}

		return lookup.apply(subPopulation);
	}

	public static Set<String> getKeys() {
		return KEY_TO_UNIQUEID.keySet();
	}

	private SubPopulations() {
	}

}
