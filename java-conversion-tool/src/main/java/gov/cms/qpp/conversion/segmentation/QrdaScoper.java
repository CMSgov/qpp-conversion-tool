package gov.cms.qpp.conversion.segmentation;

import gov.cms.qpp.conversion.model.TemplateId;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;


public enum QrdaScoper {

	ACI_AGGREGATE_COUNT(TemplateId.ACI_AGGREGATE_COUNT),
	ACI_NUMERATOR(TemplateId.ACI_NUMERATOR, ACI_AGGREGATE_COUNT),
	ACI_DENOMINATOR(TemplateId.ACI_DENOMINATOR, ACI_AGGREGATE_COUNT),
	ACI_NUMERATOR_DENOMINATOR(TemplateId.ACI_NUMERATOR_DENOMINATOR, ACI_NUMERATOR, ACI_DENOMINATOR),
	ACI_SECTION(TemplateId.ACI_SECTION, ACI_NUMERATOR_DENOMINATOR),
	IA_MEASURE(TemplateId.IA_MEASURE),
	IA_SECTION(TemplateId.IA_SECTION, IA_MEASURE),
	REPORTING_PARAMETERS_ACT(TemplateId.REPORTING_PARAMETERS_ACT),
	REPORTING_PARAMETERS_SECTION(TemplateId.REPORTING_PARAMETERS_SECTION, REPORTING_PARAMETERS_ACT),
	CLINICAL_DOCUMENT(TemplateId.CLINICAL_DOCUMENT, IA_SECTION, ACI_SECTION, REPORTING_PARAMETERS_SECTION);

	private Set<TemplateId> value;

	QrdaScoper(Object... templates) {
		value = assemble(templates);
	}

	@SuppressWarnings("unchecked")
	private Set<TemplateId> assemble(Object... tiers) {
		Set<TemplateId> templates = new HashSet<>();

		Arrays.stream(tiers).forEach(tier -> {
			if (tier instanceof TemplateId) {
				templates.add((TemplateId) tier);
			} else if (tier instanceof Collection) {
				templates.addAll((Collection<TemplateId>) tier);
			}
		});

		return templates;
	}

	public static QrdaScoper getInstanceByName(String name) {
		Optional<QrdaScoper> found = Arrays.stream(QrdaScoper.values())
				.filter(inst -> inst.name().equals(name))
				.findFirst();

		return found.orElse(null);
	}

	public static Set<TemplateId> getTemplatesByName(String name) {
		QrdaScoper scope = getInstanceByName(name);

		return scope == null ? null : scope.value;
	}

	public static String[] getNames() {
		return Arrays.stream(QrdaScoper.class.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}

	public Set<TemplateId> getValue() {
		return value;
	}
}
