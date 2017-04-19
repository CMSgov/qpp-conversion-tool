package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.decode.XmlInputFileException;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.encode.placeholder.DefaultEncoder;
import gov.cms.qpp.conversion.model.AnnotationMockHelper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.validate.NodeValidator;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.conversion.xml.XmlException;
import org.jdom2.Element;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

public class ConverterExplicitTest {
	private static String good = "../qrda-files/valid-QRDA-III.xml";
	private static String bad = "../qrda-files/not-a-QDRA-III-file.xml";
	private static String ugly = "../qrda-files/QRDA-III-without-required-measure.xml";

	@Test
	public void testStreamConversion() throws IOException {
		Path path = Paths.get(good);
		Converter converter = new Converter(Files.newInputStream(path));
		Integer status = converter.transform();
		String postName = converter.getOutputFile("meep").toString();
		InputStream result = converter.getConversionResult();

		assertEquals("no problems", 0, status.intValue());
		assertNotNull("the conversion has a result", result);
		assertEquals("expected conversion output name", "meep.qpp.json", postName);
	}

	@Test
	public void testStreamConversionError() throws IOException {
		Path path = Paths.get(ugly);
		Converter converter = new Converter(Files.newInputStream(path));
		Integer status = converter.transform();
		String postName = converter.getOutputFile("meep").toString();
		InputStream result = converter.getConversionResult();

		assertEquals("transformation errors", status.intValue(), 1);
		assertNotNull("the conversion has a result", result);
		assertEquals("expected conversion output name", "meep.err.txt", postName);
	}

	@Test
	public void testStreamConversionNonRecoverable() throws IOException {
		Path path = Paths.get(bad);
		Converter converter = new Converter(Files.newInputStream(path));
		Integer status = converter.transform();

		assertEquals("can't cope", status.intValue(), 2);
	}

	@Test(expected = XmlInputFileException.class)
	public void testStreamConversionEncodeException() throws IOException, EncodeException {
		JsonOutputEncoder encoderSpy = spy(new QppOutputEncoder());
		doThrow(new EncodeException("meep", new RuntimeException()))
				.when(encoderSpy).encode();

		Path path = Paths.get(good);
		Converter converterSpy = spy(new Converter(Files.newInputStream(path)));
		doReturn(encoderSpy).when(converterSpy).getEncoder();

		converterSpy.transform();
		converterSpy.getConversionResult();

		fail("should throw an EncodeException");
	}
}
