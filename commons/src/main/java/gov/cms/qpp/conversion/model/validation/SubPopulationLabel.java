package gov.cms.qpp.conversion.model.validation;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SubPopulationLabel {
	DENEXCEP(SubPopulation::getDenominatorExceptionsUuid, "DENEXCEP"),
	DENEX(SubPopulation::getDenominatorExclusionsUuid, "DENEX"),
	NUMER(SubPopulation::getNumeratorUuid, "NUMER"),
	DENOM(SubPopulation::getDenominatorUuid, "DENOM"),
	IPOP(SubPopulation::getNumeratorUuid, "IPOP", "IPP");

	private Set<String> aka;
	private Function<SubPopulation, String> getter;

	SubPopulationLabel(Function<SubPopulation, String> getter, String... aliases) {
		this.getter = getter;
		this.aka = Sets.newHashSet(aliases);
	}

	public String[] getAliases() {
		return aka.toArray(new String[aka.size()]);
	}

	public boolean hasAlias(String alias) {
		return this.aka.contains(alias);
	}

	static Set<String> aliasSet() {
		return Arrays.stream(SubPopulationLabel.values())
			.flatMap(pop -> pop.aka.stream())
			.collect(Collectors.toSet());
	}

	static SubPopulationLabel findPopulation(String key) {
		return Arrays.stream(SubPopulationLabel.values())
			.filter(subPop -> subPop.hasAlias(key))
			.findFirst()
			.orElse(null);
	}

	static Function<SubPopulation, String> getGetter(String key) {
		SubPopulationLabel subPopulationLabel = SubPopulationLabel.findPopulation(key);
		return subPopulationLabel != null ? subPopulationLabel.getter : null;
	}

	static Set<String> names() {
		return Stream.of(SubPopulationLabel.values())
			.map(Enum::name)
			.collect(Collectors.toSet());
	}
}
