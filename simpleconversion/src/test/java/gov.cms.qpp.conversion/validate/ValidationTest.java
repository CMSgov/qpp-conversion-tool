package gov.cms.qpp.conversion.validate;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.conversion.BaseTest;
import gov.cms.qpp.conversion.ConversionHandler;
import org.junit.After;
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
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ValidationTest extends BaseTest {

	@After
	public void cleanup() throws IOException {
		Files.deleteIfExists(Paths.get("valid-QRDA-III.qpp.json"));
	}

	@Test
	public void shouldParseValidClinicalDocument() throws ParserConfigurationException, SAXException, IOException {
		long time = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		InputStream historical = getFixtureStream("gpro-biggest.xml");

		ConversionHandler converter = new ConversionHandler();
		parser.parse(historical, converter);
		System.out.println(ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime() - time);

		ObjectMapper mapper = new ObjectMapper();
		Path file = Files.createFile(Paths.get("valid-QRDA-III.qpp.json"));
		mapper.writeValue(file.toFile(), converter.getConverted());
	}

}
