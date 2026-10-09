package gov.cms.qpp.conversion.decode;

import static com.google.common.truth.Truth.assertThat;
import static gov.cms.qpp.conversion.model.Constants.PERFORMANCE_START;
import static gov.cms.qpp.conversion.model.Constants.PERFORMANCE_YEAR;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

class ReportingParametersActDecoderTest {

	private static String reportingParametersAct(String performanceStart) {
		return "<act xmlns=\"urn:hl7-org:v3\" classCode=\"ACT\" moodCode=\"EVN\">"
			+ "<templateId root=\"2.16.840.1.113883.10.20.17.3.8\"/>"
			+ "<id root=\"16570cc6-31a6-4e0e-b7d6-0e9b6e9e5c6f\"/>"
			+ "<code code=\"252116004\" codeSystem=\"2.16.840.1.113883.6.96\"/>"
			+ "<effectiveTime>"
			+ "<low value=\"" + performanceStart + "\"/>"
			+ "<high value=\"20251231\"/>"
			+ "</effectiveTime>"
			+ "</act>";
	}

	private static Node decode(String performanceStart) throws XmlException {
		Node root = new QrdaDecoderEngine(new Context())
			.decode(XmlUtils.stringToDom(reportingParametersAct(performanceStart)));
		return root.findFirstNode(TemplateId.REPORTING_PARAMETERS_ACT);
	}

	@Test
	void testFullDateYieldsTheYear() throws XmlException {
		Node act = decode("20250101");

		assertThat(act.getValue(PERFORMANCE_START)).isEqualTo("20250101");
		assertThat(act.getValue(PERFORMANCE_YEAR)).isEqualTo("2025");
	}

	/**
	 * A performance start shorter than the year is malformed input, and
	 * ReportingParametersActValidator has an isValidDate check that exists to
	 * report it as INVALID_PERFORMANCE_PERIOD_FORMAT. The decoder has to hand the
	 * value on rather than throwing before validation gets a chance to run.
	 */
	@Test
	void testShortDateIsPassedToValidationRatherThanThrowing() throws XmlException {
		Node act = decode("202");

		assertThat(act.getValue(PERFORMANCE_START)).isEqualTo("202");
		assertThat(act.getValue(PERFORMANCE_YEAR)).isNull();
	}

	@Test
	void testEmptyDateIsPassedToValidationRatherThanThrowing() throws XmlException {
		Node act = decode("");

		assertThat(act.getValue(PERFORMANCE_START)).isEqualTo("");
		assertThat(act.getValue(PERFORMANCE_YEAR)).isNull();
	}
}
