package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.decode.XmlInputFileException;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.AnnotationMockHelper;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.ErrorSource;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.ValidationError;
import gov.cms.qpp.conversion.stubs.JennyDecoder;
import gov.cms.qpp.conversion.stubs.TestDefaultValidator;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.conversion.xml.XmlException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.containsValidationErrorInAnyOrderIgnoringPath;
import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.validationErrorTextMatches;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*" })
public class ConverterTest {

	@After
	public void cleanup() throws IOException {
		Files.deleteIfExists(Paths.get("defaultedNode.qpp.json"));
		Files.deleteIfExists(Paths.get("defaultedNode.err.json"));
		Files.deleteIfExists(Paths.get("non-xml-file.err.json"));
		Files.deleteIfExists(Paths.get("not-a-QRDA-III-file.err.json"));
	}

	@Test
	@PrepareForTest({Converter.class, QrdaValidator.class})
	public void testValidationErrors() throws Exception {

		//mocking
		AnnotationMockHelper.mockDecoder(TemplateId.DEFAULT, JennyDecoder.class);
		QrdaValidator mockQrdaValidator = AnnotationMockHelper.mockValidator(TemplateId.DEFAULT, TestDefaultValidator.class, true);
		PowerMockito.whenNew(QrdaValidator.class).withNoArguments().thenReturn(mockQrdaValidator);

		//set-up
//		Path defaultJson = Paths.get("errantDefaultedNode.qpp.json");
//		Path defaultError = Paths.get("errantDefaultedNode.err.json");
//
//		Files.deleteIfExists(defaultJson);
//		Files.deleteIfExists(defaultError);

		//execute
		Path path = Paths.get("src/test/resources/converter/errantDefaultedNode.xml");
		Converter converter = new Converter(path);

		//assert
//		assertThat("The JSON file must not exist", Files.exists(defaultJson), is(false));
//		assertThat("The error file must exist", Files.exists(defaultError), is(true));
//
//		String errorContent = new String(Files.readAllBytes(defaultError));
//		assertThat("The error file is missing the specified content", errorContent, containsString("Jenny"));
//
//		//clean-up
//		Files.delete(defaultError);

		try {
			converter.transform();
			fail("The converter should not create valid QPP JSON");
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			List<ErrorSource> errorSources = allErrors.getErrorSources();
			assertThat("There must only be one error source.", errorSources, hasSize(1));
			List<ValidationError> validationErrors = errorSources.get(0).getValidationErrors();

			assertThat("The expected validation error was missing", validationErrors, containsValidationErrorInAnyOrderIgnoringPath("Test validation error for Jenny", "moof"));
		}
	}

	@Test
	public void testInvalidXml() throws IOException {
		Path path = Paths.get("src/test/resources/non-xml-file.xml");
		Converter converter = new Converter(path);

		try {
			converter.transform();
			fail();
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			List<ErrorSource> errorSources = allErrors.getErrorSources();
			assertThat("There must only be one error source.", errorSources, hasSize(1));
			List<ValidationError> validationErrors = errorSources.get(0).getValidationErrors();
			assertThat("There must be only one validation error.", validationErrors, hasSize(1));
			ValidationError validationError = validationErrors.get(0);
			assertThat("The validation error was incorrect", validationError, validationErrorTextMatches(Converter.NOT_VALID_XML_DOCUMENT));
		}
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
		verify(devLogger).error( eq(Converter.NOT_VALID_XML_DOCUMENT), any(XmlException.class));
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
		verify(devLogger).error( eq(Converter.NOT_VALID_XML_DOCUMENT),
				any(XmlInputFileException.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, Files.class})
	public void testUnexpectedEncodingError() throws Exception {

		//set-up
		mockStatic(Files.class);
		when(Files.newBufferedWriter(any(Path.class))).thenReturn(null).thenCallRealMethod();
		when(Files.readAllBytes(any(Path.class))).thenCallRealMethod();

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

		Path errOutputPath = Paths.get("defaultedNode.err.json");
		String errorOutput = new String(Files.readAllBytes(errOutputPath));

		//assert
		verify(devLogger).error( eq(Converter.UNEXPECTED_ERROR), any(NullPointerException.class) );
		assertThat("File must contain error message", errorOutput, containsString(Converter.UNEXPECTED_ERROR));
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, Files.class})
	public void testExceptionOnWriterClose() throws Exception {

		//set-up
		BufferedWriter writer = mock( BufferedWriter.class );
		doThrow( new IOException() ).when( writer ).close();
		mockStatic(Files.class);
		when(Files.newBufferedWriter(any(Path.class))).thenReturn(writer).thenCallRealMethod();

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
	public void testValidationErrorWriterInstantiation() throws Exception {

		//set-up
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toThrow( new IOException() );

		mockStatic(LoggerFactory.class);
		Logger devLogger = mock(Logger.class);
		Logger clientLogger = mock(Logger.class);
		when(LoggerFactory.getLogger(any(Class.class))).thenReturn(devLogger);
		when(LoggerFactory.getLogger(anyString())).thenReturn(clientLogger);

		//execute
		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
		new Converter(path).transform();

		//assert
		verify(devLogger).error( eq("Could not write to error file defaultedNode.err.json" ),
				any(IOException.class));
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
	public void testValidationErrorWriterInstantiationNull() throws Exception {

		//set-up
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toThrow(new IOException());

		mockStatic(LoggerFactory.class);
		Logger devLogger = mock(Logger.class);
		Logger clientLogger = mock(Logger.class);
		when(LoggerFactory.getLogger(any(Class.class))).thenReturn(devLogger);
		when(LoggerFactory.getLogger(anyString())).thenReturn(clientLogger);

		//execute
		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
		new Converter(path).transform();

		//assert
		verify(devLogger).error( eq("Could not write to error file defaultedNode.err.json"), any(NullPointerException.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class})
	public void testExceptionOnWriteValidationErrors() throws Exception {
		mockStatic(LoggerFactory.class);
		Logger devLogger = mock(Logger.class);
		Logger clientLogger = mock(Logger.class);
		when(LoggerFactory.getLogger(any(Class.class))).thenReturn(devLogger);
		when(LoggerFactory.getLogger(anyString())).thenReturn(clientLogger);
		
		//execute
		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
		
		Converter converter = spy(new Converter(path));
		doThrow(new IOException()).when(converter, "writeErrorJson", any(AllErrors.class), any(Writer.class));
		converter.transform();

		//assert
		verify(devLogger).error(eq("Could not write to error file defaultedNode.err.json"), any(NullPointerException.class));
	}

	@Test
	public void testInvalidXmlFile() {
		Converter converter = new Converter(Paths.get("src/test/resources/not-a-QRDA-III-file.xml"));
		
		try {
			converter.transform();
			fail();
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			List<ErrorSource> errorSources = allErrors.getErrorSources();
			assertThat("There must only be one error source.", errorSources, hasSize(1));
			List<ValidationError> validationErrors = errorSources.get(0).getValidationErrors();
			assertThat("There must be only one validation error.", validationErrors, hasSize(1));
			ValidationError validationError = validationErrors.get(0);
			assertThat("The validation error was incorrect", validationError, validationErrorTextMatches("The file is not a QRDA-III XML document"));
		}
	}

	@Test
	public void testNotAValidQrdaIIIFile() throws IOException {
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		Converter converter = new Converter(path)
				.doDefaults(false)
				.doValidation(false);

		try {
			converter.transform();
			fail();
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			List<ErrorSource> errorSources = allErrors.getErrorSources();
			assertThat("There must only be one error source.", errorSources, hasSize(1));
			List<ValidationError> validationErrors = errorSources.get(0).getValidationErrors();
			assertThat("There must be only one validation error.", validationErrors, hasSize(1));
			ValidationError validationError = validationErrors.get(0);
			assertThat("The validation error was incorrect", validationError, validationErrorTextMatches("The file is not a QRDA-III XML document"));
		}
	}
}
