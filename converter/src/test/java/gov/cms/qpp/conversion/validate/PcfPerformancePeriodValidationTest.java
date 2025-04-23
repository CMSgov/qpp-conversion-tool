package gov.cms.qpp.conversion.validate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

import java.util.List;
import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.model.Constants.*;

public class PcfPerformancePeriodValidationTest {

	private PcfPerformancePeriodValidation validator;
	private Node node;
	private String programName = PCF_PROGRAM_NAME.toUpperCase(Locale.ROOT);

	@BeforeEach
	void setup() {
		validator = new PcfPerformancePeriodValidation();
		Node clinicalDocument = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocument.putValue(PROGRAM_NAME, programName);

		Node measureSection = new Node(TemplateId.MEASURE_SECTION_V5);
		measureSection.setParent(clinicalDocument);

		node = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		node.putValue(PERFORMANCE_YEAR, "2025");
		node.putValue(PERFORMANCE_START, "20250101");
		node.putValue(PERFORMANCE_END, "20251231");
		node.setParent(measureSection);
	}

	@Test
	void testPerformancePeriodIsValid() {
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertWithMessage("Should be no errors")
			.that(details).isEmpty();
	}

	@Test
	void testPerformancePeriodStartIsInvalid() {
		node.putValue(PERFORMANCE_START, "not what we want");
		List<Detail> details = validator.validateSingleNode(node).getErrors();

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.PCF_PERFORMANCE_PERIOD_START.format(programName));
	}

	@Test
	void testPerformancePeriodEndIsInvalid() {
		node.putValue(PERFORMANCE_END, "not what we want");
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.PCF_PERFORMANCE_PERIOD_END.format(programName));
	}
}
