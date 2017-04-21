package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.decode.XmlInputFileException;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.encode.placeholder.DefaultEncoder;
import gov.cms.qpp.conversion.model.AnnotationMockHelper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.stubs.JennyDecoder;
import gov.cms.qpp.conversion.stubs.TestDefaultValidator;
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
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*" })
public class ConverterTest {

	private static final String SEPERATOR = FileSystems.getDefault().getSeparator();

	@After
	public void cleanup() throws IOException {
		Files.deleteIfExists(Paths.get("defaultedNode.qpp.json"));
	}


	@Test
	@PrepareForTest({Converter.class, QrdaValidator.class})
	public void testValidationErrors() throws Exception {

		//mocking
		AnnotationMockHelper.mockDecoder("867.5309", JennyDecoder.class);
		QrdaValidator mockQrdaValidator = AnnotationMockHelper.mockValidator("867.5309", TestDefaultValidator.class, true);
		PowerMockito.whenNew(QrdaValidator.class).withNoArguments().thenReturn(mockQrdaValidator);

		//set-up
		Path defaultJson = Paths.get("errantDefaultedNode.qpp.json");
		Path defaultError = Paths.get("errantDefaultedNode.err.txt");

		Files.deleteIfExists(defaultJson);
		Files.deleteIfExists(defaultError);

		//execute
		Path path = Paths.get("src/test/resources/converter/errantDefaultedNode.xml");
		new Converter(path).transform();

		//assert
		assertThat("The JSON file must not exist", Files.exists(defaultJson), is(false));
		assertThat("The error file must exist", Files.exists(defaultError), is(true));

		String errorContent = new String(Files.readAllBytes(defaultError));
		assertThat("The error file is missing the specified content", errorContent, containsString("Jenny"));

		//clean-up
		Files.delete(defaultError);
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class})
	public void testInvalidXml() {

		//set-up
		mockStatic( LoggerFactory.class );
		Logger devLogger = mock( Logger.class );
		Logger clientLogger = mock( Logger.class );
		when( LoggerFactory.getLogger(any(Class.class)) ).thenReturn( devLogger );
		when( LoggerFactory.getLogger(anyString()) ).thenReturn( clientLogger );

		//execute
		Path path = Paths.get("src/test/resources/non-xml-file.xml");
		new Converter(path).transform();

		//assert
		verify(clientLogger).error( eq("The file is not a valid XML document") );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, QppOutputEncoder.class})
	public void testEncodingExceptions() throws Exception {

		//set-up
		mockStatic( LoggerFactory.class );
		Logger devLogger = mock( Logger.class );
		Logger clientLogger = mock( Logger.class );
		when( LoggerFactory.getLogger(any(Class.class)) ).thenReturn( devLogger );
		when( LoggerFactory.getLogger(anyString()) ).thenReturn( clientLogger );

		QppOutputEncoder encoder = mock( QppOutputEncoder.class );
		whenNew( QppOutputEncoder.class ).withNoArguments().thenReturn( encoder );
		EncodeException ex = new EncodeException( "mocked", new RuntimeException() );
		doThrow( ex ).when( encoder ).encode( any(Writer.class) );

		//execute
		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
		new Converter(path)
				.doDefaults(false)
				.doValidation(false)
				.transform();

		//assert
		verify(devLogger).error( eq("The file is not a valid XML document"), any(XmlException.class));
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, Files.class})
	public void testIOEncodingError() throws Exception {

		//set-up
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toThrow( new IOException() );

		mockStatic( LoggerFactory.class );
		Logger devLogger = mock( Logger.class );
		Logger clientLogger = mock( Logger.class );
		when( LoggerFactory.getLogger(any(Class.class)) ).thenReturn( devLogger );
		when( LoggerFactory.getLogger(anyString()) ).thenReturn( clientLogger );

		//execute
		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
		new Converter(path)
				.doDefaults(false)
				.doValidation(false)
				.transform();

		//assert
		verify(devLogger).error( eq("The file is not a valid XML document"),
				any(XmlInputFileException.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
	public void testUnexpectedEncodingError() throws Exception {

		//set-up
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toReturn( null );

		mockStatic( LoggerFactory.class );
		Logger logger = mock( Logger.class );
		when( LoggerFactory.getLogger(any(Class.class)) ).thenReturn( logger );

		//execute
		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
		new Converter(path)
				.doValidation(false)
				.doValidation(false)
				.transform();

		//assert
		verify(logger).error( eq("Unexpected exception occurred during conversion"), any(NullPointerException.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
	public void testExceptionOnWriterClose() throws Exception {

		//set-up
		BufferedWriter writer = mock( BufferedWriter.class );
		doThrow( new IOException() ).when( writer ).close();
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toReturn( writer );

		mockStatic( LoggerFactory.class );
		Logger devLogger = mock( Logger.class );
		Logger clientLogger = mock( Logger.class );
		when( LoggerFactory.getLogger(any(Class.class)) ).thenReturn( devLogger );
		when( LoggerFactory.getLogger(anyString()) ).thenReturn( clientLogger );

		//execute
		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
		new Converter(path)
				.doValidation(false)
				.doValidation(false)
				.transform();

		//assert
		verify(devLogger).error( eq("The file is not a valid XML document"),
				any(XmlInputFileException.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
	public void testValidationErrorWriterInstantiation() throws Exception {

		//set-up
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toThrow( new IOException() );

		mockStatic( LoggerFactory.class );
		Logger devLogger = mock( Logger.class );
		Logger clientLogger = mock( Logger.class );
		when( LoggerFactory.getLogger(any(Class.class)) ).thenReturn( devLogger );
		when( LoggerFactory.getLogger(anyString()) ).thenReturn( clientLogger );

		//execute
		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
		new Converter(path).transform();

		//assert
		verify(devLogger).error( eq("Could not write to file: {}" ),
				eq( "defaultedNode.err.txt" ), any(String.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
	public void testValidationErrorWriterInstantiationNull() throws Exception {

		//set-up
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toThrow( null );

		mockStatic( LoggerFactory.class );
		Logger logger = mock( Logger.class );
		when( LoggerFactory.getLogger(any(Class.class)) ).thenReturn( logger );

		//execute
		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
		new Converter(path).transform();

		//assert
		verify(logger).error( eq("Unexpected exception occurred during conversion"), any(NullPointerException.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
	public void testExceptionOnWriteValidationErrors() throws Exception {

		//set-up
		BufferedWriter writer = mock( BufferedWriter.class );
		doThrow( new IOException() ).when( writer ).write( anyString() );
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toReturn( writer );

		mockStatic( LoggerFactory.class );
		Logger devLogger = mock( Logger.class );
		Logger clientLogger = mock( Logger.class );
		when( LoggerFactory.getLogger(any(Class.class)) ).thenReturn( devLogger );
		when( LoggerFactory.getLogger(anyString()) ).thenReturn( clientLogger );

		//execute
		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
		new Converter(path).transform();

		//assert
		verify(devLogger).error( eq("Could not write to file: {}" ),
				eq("defaultedNode.err.txt"),
				any(IOException.class) );
	}

	@Test
	public void testInvalidXmlFile() throws InvocationTargetException, IllegalAccessException {
		Converter converter = new Converter(Paths.get("src/test/resources/not-a-QRDA-III-file.xml"));

		Method transformMethod = ReflectionUtils.findMethod(Converter.class, "transform");
		transformMethod.setAccessible(true);

		Integer returnValue = (Integer)transformMethod.invoke(converter);

		assertThat("Should not have a valid clinical document template id", returnValue, is(2));
	}
}
