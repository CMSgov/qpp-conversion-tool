package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

class ReportingParametersActValidatorTest {
	private final String PERFORMANCE_END = "20171201";
	private final String PERFORMANCE_START = "20170101";
	private final String PERFORMANCE_YEAR = "2017";
	private final String TIMESTAMPED_DATE = "1521231541231";

	private ReportingParametersActValidator reportingParametersActValidator;

	@BeforeEach
	void setup() {
		reportingParametersActValidator = new ReportingParametersActValidator();
	}

	@Test
	void testReportingParametersActValidDateSuccess() {
		Node reportingParametersActNode = createReportingParametersAct(PERFORMANCE_START, PERFORMANCE_END, PERFORMANCE_YEAR);
		reportingParametersActValidator.internalValidateSingleNode(reportingParametersActNode);

		Set<Detail> error = reportingParametersActValidator.getDetails();

		assertThat(error).isEmpty();
	}

	@Test
	void testMissingPerformanceStartFromReportingParametersAct() {
		Node reportingParametersActNode = createReportingParametersAct(null, PERFORMANCE_END, PERFORMANCE_YEAR);

		reportingParametersActValidator.internalValidateSingleNode(reportingParametersActNode);

		Set<Detail> error = reportingParametersActValidator.getDetails();

		assertThat(error).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ErrorCode.REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_START);
	}

	@Test
	void testMissingPerformanceEndFromReportingParametersAct() {
		Node reportingParametersActNode = createReportingParametersAct(PERFORMANCE_START, null, PERFORMANCE_YEAR);

		reportingParametersActValidator.internalValidateSingleNode(reportingParametersActNode);

		Set<Detail> error = reportingParametersActValidator.getDetails();

		assertThat(error).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ErrorCode.REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_END);
	}

	@Test
	void testMissingPerformanceYearFromReportingParametersAct() {
		Node reportingParametersActNode = createReportingParametersAct(PERFORMANCE_START, PERFORMANCE_END, null);

		reportingParametersActValidator.internalValidateSingleNode(reportingParametersActNode);

		Set<Detail> error = reportingParametersActValidator.getDetails();

		assertThat(error).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ErrorCode.REPORTING_PARAMETERS_MISSING_PERFORMANCE_YEAR);
	}

	@Test
	void testInvalidPerformanceStartFormat(){
		Node reportingParametersActNode = createReportingParametersAct(TIMESTAMPED_DATE, PERFORMANCE_END, PERFORMANCE_YEAR);

		reportingParametersActValidator.internalValidateSingleNode(reportingParametersActNode);

		Set<Detail> error = reportingParametersActValidator.getDetails();

		assertThat(error).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ErrorCode.INVALID_PERFORMANCE_PERIOD_FORMAT.format(TIMESTAMPED_DATE));
	}

	@Test
	void testInvalidPerformanceEndFormat() {
		Node reportingParametersActNode = createReportingParametersAct(PERFORMANCE_START, TIMESTAMPED_DATE, PERFORMANCE_YEAR);

		reportingParametersActValidator.internalValidateSingleNode(reportingParametersActNode);

		Set<Detail> error = reportingParametersActValidator.getDetails();

		assertThat(error).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ErrorCode.INVALID_PERFORMANCE_PERIOD_FORMAT.format(TIMESTAMPED_DATE));
	}

	@Test
	void testPerformanceStartAndEndWithDashesAndTimezone() {
		Node reportingParametersActNode = createReportingParametersAct("2017-01-01T01:45:23.123",
			"2017-12-01T01:45:23.123", PERFORMANCE_YEAR);

		reportingParametersActValidator.internalValidateSingleNode(reportingParametersActNode);

		Set<Detail> error = reportingParametersActValidator.getDetails();

		assertThat(error).isEmpty();
	}

	@Test
	void testPerformanceStartAndEndWithSlashesAndTimezone() {
		Node reportingParametersActNode = createReportingParametersAct("2017/01/01",
			"2017/12/01T01:45:23.123", PERFORMANCE_YEAR);

		reportingParametersActValidator.internalValidateSingleNode(reportingParametersActNode);

		Set<Detail> error = reportingParametersActValidator.getDetails();

		assertThat(error).isEmpty();
	}

	@Test
	void testPerformanceStartAndEndSlashesAndDashesNoTimezone() {
		Node reportingParametersActNode = createReportingParametersAct("2017/01/01T01:45:23",
			"2017-012-01T01:45:23", PERFORMANCE_YEAR);

		reportingParametersActValidator.internalValidateSingleNode(reportingParametersActNode);

		Set<Detail> error = reportingParametersActValidator.getDetails();

		assertThat(error).isEmpty();
	}

	private Node createReportingParametersAct(String startDate, String endDate, String performanceYear) {
		Node reportingParametersActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		reportingParametersActNode.putValue(ReportingParametersActDecoder.PERFORMANCE_START, startDate);
		reportingParametersActNode.putValue(ReportingParametersActDecoder.PERFORMANCE_END, endDate);
		reportingParametersActNode.putValue(ReportingParametersActDecoder.PERFORMANCE_YEAR, performanceYear);

		return reportingParametersActNode;
	}
}
