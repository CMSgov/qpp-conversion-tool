package gov.cms.qpp.conversion.segmentation;

import gov.cms.qpp.conversion.model.TemplateId;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;


public enum QrdaScope {

	ACI_AGGREGATE_COUNT(TemplateId.ACI_AGGREGATE_COUNT),
	ACI_NUMERATOR(TemplateId.ACI_NUMERATOR, ACI_AGGREGATE_COUNT),
	ACI_DENOMINATOR(TemplateId.ACI_DENOMINATOR, ACI_AGGREGATE_COUNT),
	//ACI_NUMERATOR_DENOMINATOR cannot be validated without it's parent
	ACI_NUMERATOR_DENOMINATOR(TemplateId.ACI_SECTION, TemplateId.ACI_NUMERATOR_DENOMINATOR, ACI_NUMERATOR, ACI_DENOMINATOR),
	ACI_SECTION(TemplateId.ACI_SECTION, ACI_NUMERATOR_DENOMINATOR),
	MEASURE_PERFORMED(TemplateId.MEASURE_PERFORMED),
	IA_MEASURE(TemplateId.IA_MEASURE, MEASURE_PERFORMED),
	IA_SECTION(TemplateId.IA_SECTION, IA_MEASURE),
	REPORTING_PARAMETERS_ACT(TemplateId.REPORTING_PARAMETERS_ACT),
	REPORTING_PARAMETERS_SECTION(TemplateId.REPORTING_PARAMETERS_SECTION, REPORTING_PARAMETERS_ACT),
	CLINICAL_DOCUMENT(TemplateId.CLINICAL_DOCUMENT, IA_SECTION, ACI_SECTION, REPORTING_PARAMETERS_SECTION);

	private Set<TemplateId> value;

	QrdaScope(Object... templates) {
		value = assemble(templates);
	}

	private Set<TemplateId> assemble(Object... tiers) {
		Set<TemplateId> templates = new HashSet<>();

		Arrays.stream(tiers).forEach(tier -> {
			if (tier instanceof TemplateId) {
				templates.add((TemplateId) tier);
			} else {
				templates.addAll(((QrdaScope) tier).getValue());
			}
		});

		return templates;
	}

	public static QrdaScope getInstanceByName(String name) {
		Optional<QrdaScope> found = Arrays.stream(QrdaScope.values())
				.filter(inst -> inst.name().equals(name))
				.findFirst();

		return found.orElse(null);
	}

	public static Set<TemplateId> getTemplates(Collection<QrdaScope> scopes) {
		if (scopes == null) {
			return Collections.emptySet();
		}

		return scopes.stream()
				.flatMap(scope -> scope.getValue().stream())
				.collect(Collectors.toSet());
	}

	public static String[] getNames() {
		return Arrays.stream(QrdaScope.class.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}

	public Set<TemplateId> getValue() {
		return value;
	}
}
