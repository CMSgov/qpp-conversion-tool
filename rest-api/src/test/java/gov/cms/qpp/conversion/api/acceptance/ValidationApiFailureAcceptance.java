package gov.cms.qpp.conversion.api.acceptance;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import io.restassured.response.Response;
import org.jdom2.Attribute;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import gov.cms.qpp.test.annotations.AcceptanceTest;
import gov.cms.qpp.test.annotations.ParameterizedAcceptanceTest;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(RestExtension.class)
class ValidationApiFailureAcceptance {
	private static final XPathFactory XPF = XPathFactory.instance();
	private MustacheFactory mf = new DefaultMustacheFactory();
	private Mustache fixture = mf.compile("valid-QRDA-III-latest-fixture.xml");

	@AcceptanceTest
	void testSuccess() {
		Response response = performRequest(getQrda(null));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@ParameterizedAcceptanceTest
	@MethodSource("getFailureScenarios")
	void testBadPerformanceStart(String comparison, Map<String, String> override) {
		String qrda = getQrda(override);
		Response response = performRequest(qrda);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
		AllErrors blah = response.getBody().as(AllErrors.class);
		blah.getErrors().stream().flatMap(error -> error.getDetails().stream())
			.forEach(verifyDetail(comparison, qrda));
	}

	private static Stream<Arguments> getFailureScenarios() {
		return Stream.of(
			Arguments.of("20160101", Collections.singletonMap("performanceStart", "20160101")),
//TODO: create issue to track this
//			Arguments.of("20180101", Collections.singletonMap("performanceEnd", "20180101")),
			Arguments.of("ACI_PEA_42", Collections.singletonMap("aciMeasure", "ACI_PEA_42")),
			Arguments.of("ACI_PEA_2", new HashMap<String, String>() {{
				put("aciDenominator", "600");
				put("aciNumerator", "800");
			}}),
			Arguments.of("1000", Collections.singletonMap("qualityDenomExclusion", "1100")),
			Arguments.of("1000", Collections.singletonMap("qualityDenomException", "1100")),
			Arguments.of("1000", Collections.singletonMap("qualityNumerator", "1100")));
	}

	private Response performRequest(String qrda) {
		return given()
			.multiPart("file", "file", qrda.getBytes())
			.when()
			.post("/");
	}

	private String getQrda(Map<String, String> overrides) {
		return fixture.execute(new StringWriter(), defaults(overrides)).toString();
	}

	private Map<String, String> defaults(Map<String, String> overrides) {
		Map<String, String> defaultValues = new HashMap<String, String>() {
			{
				put("performanceStart", "20170101");
				put("performanceEnd", "20171231");
				put("aciMeasure", "ACI_PEA_2");
				put("aciNumerator", "600");
				put("aciDenominator", "800");
				put("qualityDenominator", "1000");
				put("qualityNumerator", "800");
				put("qualityDenomExclusion", "50");
				put("qualityDenomException", "50");
			}
		};

		if (overrides != null) {
			defaultValues.putAll(overrides);
		}

		return defaultValues;
	}

	private Consumer<Detail> verifyDetail(String comparison, String xml) {
		return detail -> {
			String xPath = detail.getPath();
			Filter filter = xPath.contains("@") ? Filters.attribute() : Filters.element();
			try {
				Object found = evaluateXpath(detail.getPath(), filter, xml);
				if (filter.equals(Filters.attribute())) {
					Attribute attribute = (Attribute) found;
					assertThat(attribute.getValue()).isEqualTo(comparison);
				} else {
					assertThat(found).isNotNull();
				}
			} catch (XmlException ex) {
				fail("This xpath could not be found: " + detail.getPath(), ex);
			}
		};
	}

	private <T> T evaluateXpath(String xPath, Filter<T> filter, String xml) throws XmlException {
		XPathExpression<T> xpath = XPF.compile(xPath, filter);
		return xpath.evaluateFirst(
			XmlUtils.parseXmlStream(
				new ByteArrayInputStream(xml.getBytes())));
	}
}

