package gov.cms.qpp.conversion;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class ConversionFileWriterWrapperTest {

	@After
	public void deleteFiles() throws IOException {
		Files.deleteIfExists(Paths.get("valid-QRDA-III.qpp.json"));
		Files.deleteIfExists(Paths.get("not-a-QRDA-III-file.err.json"));
		Files.deleteIfExists(Paths.get("qrda_bad_denominator.qpp.json"));
	}

	@Test
	public void testValidQpp() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.doDefaults(false).transform();

		assertFileExists("valid-QRDA-III.qpp.json");
	}

	@Test
	public void testInvalidQpp() {
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.transform();

		assertFileExists("not-a-QRDA-III-file.err.json");
	}

	@Test
	public void testSkipValidations() {
		Path path = Paths.get("src/test/resources/qrda_bad_denominator.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.doValidation(false).transform();

		assertFileExists("qrda_bad_denominator.qpp.json");
	}

	@Test
	@PrepareForTest({Files.class, ConversionFileWriterWrapper.class})
	public void testFailureToWriteQpp() throws IOException {
		mockStatic(Files.class);
		when(Files.newBufferedWriter(any(Path.class))).thenThrow(new IOException());

		Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.transform();

		assertFileDoesNotExists("valid-QRDA-III.qpp.json");
	}

	@Test
	@PrepareForTest({Files.class, ConversionFileWriterWrapper.class})
	public void testFailureToWriteErrors() throws IOException {
		mockStatic(Files.class);
		when(Files.newBufferedWriter(any(Path.class))).thenThrow(new IOException());

		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		converterWrapper.transform();

		assertFileDoesNotExists("not-a-QRDA-III-file.err.json");
	}

	private void assertFileExists(final String fileName) {
		Path possibleFile = Paths.get(fileName);
		assertTrue("The file " + fileName + " must exist.", Files.exists(possibleFile));
	}

	private void assertFileDoesNotExists(final String fileName) {
		Path possibleFile = Paths.get(fileName);
		assertFalse("The file " + fileName + " must NOT exist.", Files.exists(possibleFile));
	}

	//	@Test
//	@PrepareForTest({LoggerFactory.class, Converter.class})
//	public void testExceptionOnWriteValidationErrors() throws Exception {
//		mockStatic(LoggerFactory.class);
//		Logger devLogger = mock(Logger.class);
//		Logger clientLogger = mock(Logger.class);
//		when(LoggerFactory.getLogger(any(Class.class))).thenReturn(devLogger);
//		when(LoggerFactory.getLogger(anyString())).thenReturn(clientLogger);
//
//		//execute
//		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
//
//		Converter converter = spy(new Converter(path));
//		doThrow(new IOException()).when(converter, "writeErrorJson", any(AllErrors.class), any(Writer.class));
//		converter.transform();
//
//		//assert
//		verify(devLogger).error(eq("Could not write to error file defaultedNode.err.json"), any(NullPointerException.class));
//	}
//
//	@Test
//	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
//	public void testValidationErrorWriterInstantiationNull() throws Exception {
//
//		//set-up
//		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toThrow(new IOException());
//
//		mockStatic(LoggerFactory.class);
//		Logger devLogger = mock(Logger.class);
//		Logger clientLogger = mock(Logger.class);
//		when(LoggerFactory.getLogger(any(Class.class))).thenReturn(devLogger);
//		when(LoggerFactory.getLogger(anyString())).thenReturn(clientLogger);
//
//		//execute
//		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
//		new Converter(path).transform();
//
//		//assert
//		verify(devLogger).error( eq("Could not write to error file defaultedNode.err.json"), any(NullPointerException.class) );
//	}
//
//	@Test
//	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
//	public void testValidationErrorWriterInstantiation() throws Exception {
//
//		//set-up
//		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toThrow( new IOException() );
//
//		mockStatic(LoggerFactory.class);
//		Logger devLogger = mock(Logger.class);
//		Logger clientLogger = mock(Logger.class);
//		when(LoggerFactory.getLogger(any(Class.class))).thenReturn(devLogger);
//		when(LoggerFactory.getLogger(anyString())).thenReturn(clientLogger);
//
//		//execute
//		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
//		new Converter(path).transform();
//
//		//assert
//		verify(devLogger).error( eq("Could not write to error file defaultedNode.err.json" ),
//			any(IOException.class));
//	}
//
//	@Test
//	@PrepareForTest({LoggerFactory.class, Converter.class, Files.class})
//	public void testExceptionOnWriterClose() throws Exception {
//
//		//set-up
//		BufferedWriter writer = mock( BufferedWriter.class );
//		doThrow( new IOException() ).when( writer ).close();
//		mockStatic(Files.class);
//		when(Files.newBufferedWriter(any(Path.class))).thenReturn(writer).thenCallRealMethod();
//
//		mockStatic( LoggerFactory.class );
//		Logger devLogger = mock( Logger.class );
//		Logger clientLogger = mock( Logger.class );
//		when( LoggerFactory.getLogger(any(Class.class)) ).thenReturn( devLogger );
//		when( LoggerFactory.getLogger(anyString()) ).thenReturn( clientLogger );
//
//		//execute
//		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
//		new Converter(path)
//			.doDefaults(false)
//			.doValidation(false)
//			.transform();
//
//		//assert
//		verify(devLogger).error( eq("The file is not a valid XML document"),
//			any(XmlInputFileException.class) );
//	}
//
//	@Test
//	@PrepareForTest({LoggerFactory.class, Converter.class, Files.class})
//	public void testUnexpectedEncodingError() throws Exception {
//
//		//set-up
//		mockStatic(Files.class);
//		when(Files.newBufferedWriter(any(Path.class))).thenReturn(null).thenCallRealMethod();
//		when(Files.readAllBytes(any(Path.class))).thenCallRealMethod();
//
//		mockStatic( LoggerFactory.class );
//		Logger devLogger = mock( Logger.class );
//		Logger clientLogger = mock( Logger.class );
//		when( LoggerFactory.getLogger(any(Class.class)) ).thenReturn( devLogger );
//		when( LoggerFactory.getLogger(anyString()) ).thenReturn( clientLogger );
//
//		//execute
//		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
//		new Converter(path)
//			.doDefaults(false)
//			.doValidation(false)
//			.transform();
//
//		Path errOutputPath = Paths.get("defaultedNode.err.json");
//		String errorOutput = new String(Files.readAllBytes(errOutputPath));
//
//		//assert
//		verify(devLogger).error( eq(Converter.UNEXPECTED_ERROR), any(NullPointerException.class) );
//		assertThat("File must contain error message", errorOutput, containsString(Converter.UNEXPECTED_ERROR));
//	}
//
//	@Test
//	@PrepareForTest({LoggerFactory.class, Converter.class, Files.class})
//	public void testIOEncodingError() throws Exception {
//
//		//set-up
//		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toThrow(new IOException());
//
//		mockStatic( LoggerFactory.class );
//		Logger devLogger = mock( Logger.class );
//		Logger clientLogger = mock( Logger.class );
//		when( LoggerFactory.getLogger(any(Class.class)) ).thenReturn( devLogger );
//		when( LoggerFactory.getLogger(anyString()) ).thenReturn( clientLogger );
//
//		//execute
//		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
//		new Converter(path)
//			.doDefaults(false)
//			.doValidation(false)
//			.transform();
//
//		//assert
//		verify(devLogger).error( eq(Converter.NOT_VALID_XML_DOCUMENT),
//			any(XmlInputFileException.class) );
//	}

//	@Test
//	public void testJsonCreation() throws IOException {
//		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(Paths.get("src/test/resources/qrda_bad_denominator.xml"));
//
//		converterWrapper.transform();
//
//		assertThat("A non-zero return value was expected.", returnValue, is(not(TransformationStatus.SUCCESS)));
//
//		InputStream errorResultsStream = converter.getConversionResult();
//		String errorResults = IOUtils.toString(errorResultsStream, StandardCharsets.UTF_8);
//
//		assertThat("The error results must have the source identifier.", errorResults, containsString("sourceIdentifier"));
//		assertThat("The error results must have some error text.", errorResults, containsString("errorText"));
//		assertThat("The error results must have an XPath.", errorResults, containsString("path"));
//	}

//	@Test
//	@PrepareForTest({Converter.class, ObjectMapper.class})
//	public void testJsonStreamFailure() throws Exception {
//		//mock
//		whenNew(ObjectMapper.class).withNoArguments().thenThrow(new JsonGenerationException("test exception", (JsonGenerator)null));
//
//		//run
//		Converter converter = new Converter(XmlUtils.fileToStream(Paths.get("src/test/resources/qrda_bad_denominator.xml")));
//		TransformationStatus returnValue = converter.transform();
//
//		//assert
//		assertThat("A failure was expected.", returnValue, is(not(TransformationStatus.SUCCESS)));
//		String expectedExceptionJson = "{ \"exception\": \"JsonProcessingException\" }";
//		InputStream errorResultsStream = converter.getConversionResult();
//		String errorResults = IOUtils.toString(errorResultsStream, StandardCharsets.UTF_8);
//
//		assertThat("An exception creating the JSON should have been thrown resulting in a basic error JSON being returned.",
//			expectedExceptionJson, is(errorResults));
//	}
}