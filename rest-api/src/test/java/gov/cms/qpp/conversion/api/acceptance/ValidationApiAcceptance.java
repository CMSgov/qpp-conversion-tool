package gov.cms.qpp.conversion.api.acceptance;

import io.restassured.response.Response;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.junit.jupiter.api.extension.ExtendWith;

import gov.cms.qpp.conversion.api.services.internal.ValidationServiceImpl;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import gov.cms.qpp.test.annotations.AcceptanceTest;
import gov.cms.qpp.test.helper.NioHelper;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(RestExtension.class)
class ValidationApiAcceptance {

	private static final XPathFactory XPF = XPathFactory.instance();
	private static final Path PATH = Paths.get("../sample-files/CPCPlus_Validation_API_Errors.xml");
	private static final int CANNED_VALUE = 1000;

	@AcceptanceTest
	void testUnprocessedFiles() {
		Response response = given()
			.multiPart("file", PATH.toFile())
			.when()
			.post("/");

		AllErrors blah = response.getBody().as(AllErrors.class);
		blah.getErrors().stream().flatMap(error -> error.getDetails().stream())
				.forEach(this::verifyDetail);
	}

	private void verifyDetail(Detail detail) {
		String xPath = detail.getLocation().getPath();
		Filter filter = xPath.contains("@") ? Filters.attribute() : Filters.element();
		try {
			Object found = evaluateXpath(xPath, filter);
			if (filter.equals(Filters.attribute())) {
				Attribute attribute = (Attribute) found;
				assertThat(attribute.getIntValue()).isEqualTo(CANNED_VALUE);
				assertThat(detail.getMessage()).startsWith(ValidationServiceImpl.SV_LABEL);
			} else {
				assertThat(found).isNotNull();
			}
		} catch (XmlException | DataConversionException ex) {
			fail("This xpath could not be found: " + xPath, ex);
		}
	}

	private <T> T evaluateXpath(String xPath, Filter<T> filter) throws XmlException {
		XPathExpression<T> xpath = XPF.compile(xPath, filter);
		return xpath.evaluateFirst(XmlUtils.parseXmlStream(NioHelper.fileToStream(PATH)));
	}
}

