package gov.cms.qpp.conversion.model.validation;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Construct that helps classify SubPopulation types.
 */
public enum SubPopulationLabel {
	DENEXCEP(SubPopulation::getDenominatorExceptionsUuid, "DENEXCEP"),
	DENEX(SubPopulation::getDenominatorExclusionsUuid, "DENEX"),
	NUMER(SubPopulation::getNumeratorUuid, "NUMER"),
	DENOM(SubPopulation::getDenominatorUuid, "DENOM"),
	IPOP(SubPopulation::getNumeratorUuid, "IPOP", "IPP");

	private Set<String> aka;
	private Function<SubPopulation, String> getter;

	/**
	 * Initialize enum constituents with functions to retrieve respective sub-population ids
	 * along with aliases to contextualize the retrieval
	 *
	 * @param getter {@link SubPopulation} getter
	 * @param aliases determines to which sub-population aliases the getter applies
	 */
	SubPopulationLabel(Function<SubPopulation, String> getter, String... aliases) {
		this.getter = getter;
		this.aka = Sets.newHashSet(aliases);
	}

	/**
	 * Retrieve the associated aliases.
	 *
	 * @return an array of aliases
	 */
	public String[] getAliases() {
		return aka.toArray(new String[aka.size()]);
	}

	/**
	 * Determine if the given value is a valid alias.
	 *
	 * @param alias Potential alias
	 * @return whether or not the given alias is valid for this {@link SubPopulationLabel}
	 */
	public boolean hasAlias(String alias) {
		return this.aka.contains(alias);
	}

	/**
	 * Get all aliases
	 *
	 * @return a {@link Set} of all aliases
	 */
	static Set<String> aliasSet() {
		return Arrays.stream(SubPopulationLabel.values())
			.flatMap(pop -> pop.aka.stream())
			.collect(Collectors.toSet());
	}

	/**
	 * Find the {@link SubPopulationLabel} that corresponds to the given alias.
	 *
	 * @param key a given alias
	 * @return the corresponding SubPopulationLabel if any
	 */
	public static SubPopulationLabel findPopulation(String key) {
		return Arrays.stream(SubPopulationLabel.values())
			.filter(subPop -> subPop.hasAlias(key))
			.findFirst()
			.orElse(null);
	}

	/**
	 * Retrieve a getter for the given aliases' UUID.
	 *
	 * @param key a given alias
	 * @return the appropriate {@link SubPopulation} getter
	 */
	static Function<SubPopulation, String> getGetter(String key) {
		SubPopulationLabel subPopulationLabel = SubPopulationLabel.findPopulation(key);
		return subPopulationLabel != null ? subPopulationLabel.getter : null;
	}

	/**
	 * Retrieve a {@link Set} of all SubPopulationLabel names.
	 *
	 * @return Set of names
	 */
	static Set<String> names() {
		return Stream.of(SubPopulationLabel.values())
			.map(Enum::name)
			.collect(Collectors.toSet());
	}
}
