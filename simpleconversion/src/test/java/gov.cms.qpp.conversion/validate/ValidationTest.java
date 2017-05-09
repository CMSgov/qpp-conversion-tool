package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.BaseTest;
import gov.cms.qpp.conversion.ConversionHandler;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ValidationTest extends BaseTest {
	private static String validQrda;
	private static String clinicalDocument;
	private static String invalidClinicalDocument;


	@BeforeClass
	public static void setup() throws IOException {
		validQrda = getFixture("valid-QRDA-III.xml");
		clinicalDocument = getFixture("hasClinicalDocument.xml");
		invalidClinicalDocument = getFixture("invalidClinicalDocument.xml");
	}

	@Test
	public void shouldParseValidClinicalDocument() throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		InputStream historical = getFixtureStream("valid-QRDA-III.xml");

		parser.parse(historical, new ConversionHandler());
	}

}
