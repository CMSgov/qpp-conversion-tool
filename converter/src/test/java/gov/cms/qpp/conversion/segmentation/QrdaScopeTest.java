package gov.cms.qpp.conversion.segmentation;

import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.segmentation.QrdaScope.ACI_AGGREGATE_COUNT;
import static gov.cms.qpp.conversion.segmentation.QrdaScope.MEASURE_PERFORMED;

import java.util.HashSet;
import java.util.Set;

import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.test.enums.EnumContract;

class QrdaScopeTest implements EnumContract {

	@Test
	void testGetTemplates() {
		//when
		Set<QrdaScope> scopes = new HashSet<>();
		scopes.add(ACI_AGGREGATE_COUNT);
		scopes.add(MEASURE_PERFORMED);

		//then
		assertWithMessage("Should be two scopes")
				.that(QrdaScope.getTemplates(scopes)).hasSize(2);
	}

	@Test
	void testGetIaSectionTemplates() {
		//then
		assertWithMessage("IaSection contents")
			.that(QrdaScope.IA_SECTION.getValue())
			.containsExactly(TemplateId.MEASURE_PERFORMED, TemplateId.IA_MEASURE,
				TemplateId.IA_SECTION, TemplateId.REPORTING_PARAMETERS_ACT);
	}

	@Test
	void testGetTemplatesNull() {
		//expect
		assertWithMessage("Should be no scopes")
				.that(QrdaScope.getTemplates(null)).isEmpty();
	}

	@Test
	void testGetTemplatesEmpty() {
		//expect
		assertWithMessage("Should be no scopes")
				.that(QrdaScope.getTemplates(new HashSet<>())).isEmpty();
	}

	@Override
	public Class<? extends Enum<?>> getEnumType() {
		return QrdaScope.class;
	}
}
