package gov.cms.qpp.conversion.segmentation;

import gov.cms.qpp.conversion.model.TemplateId;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;


public enum QrdaScoper {

	ACI_AGGREGATE_COUNT("ACI_AGG_COUNT", TemplateId.ACI_AGGREGATE_COUNT),
	ACI_NUMERATOR("ACI_NUM", TemplateId.ACI_NUMERATOR, ACI_AGGREGATE_COUNT),
	ACI_DENOMINATOR("ACI_DEN", TemplateId.ACI_DENOMINATOR, ACI_AGGREGATE_COUNT),
	ACI_NUMERATOR_DENOMINATOR("ACI_NUM_DEN", TemplateId.ACI_NUMERATOR_DENOMINATOR, ACI_NUMERATOR, ACI_DENOMINATOR),
	ACI_SECTION("ACI_SEC", TemplateId.ACI_SECTION, ACI_NUMERATOR_DENOMINATOR),
	IA_MEASURE("IA_MEASURE", TemplateId.IA_MEASURE),
	IA_SECTION("IA_SEC", TemplateId.IA_SECTION, IA_MEASURE),
	REPORTING_PARAMETERS_ACT("REP_PAR_ACT", TemplateId.REPORTING_PARAMETERS_ACT),
	REPORTING_PARAMETERS_SECTION("REP_PAR_SEC", TemplateId.REPORTING_PARAMETERS_SECTION, REPORTING_PARAMETERS_ACT),
	CLINICAL_DOCUMENT("CLI_DOC", TemplateId.CLINICAL_DOCUMENT, IA_SECTION, ACI_SECTION, REPORTING_PARAMETERS_SECTION);

	private Set<TemplateId> value;

	QrdaScoper(String name, Object... templates) {
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

	public Set<TemplateId> getTemplatesByName(String name) {
		Optional<QrdaScoper> found = Arrays.stream(QrdaScoper.values())
				.filter(inst -> inst.name().equals(name))
				.findFirst();

		return found.isPresent() ? found.get().value : CLINICAL_DOCUMENT.value;
	}

	public static String[] getNames() {
		return Arrays.stream(QrdaScoper.class.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
}
