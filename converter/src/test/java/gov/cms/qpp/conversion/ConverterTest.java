package gov.cms.qpp.conversion;

import com.google.common.truth.Truth;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.TextParsingException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.decode.XmlInputFileException;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.FormattedProblemCode;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.stubs.JennyDecoder;
import gov.cms.qpp.conversion.stubs.TestDefaultValidator;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.test.helper.NioHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*" })
public class ConverterTest {
	
	public static final String VALID_FILE   = "../qrda-files/valid-QRDA-III-latest.xml";
	public static final String ERROR_FILE   = "src/test/resources/converter/errantDefaultedNode.xml";
	public static final String EXCEPT_FILE  = "src/test/resources/converter/defaultedNode.xml";
	public static final String INVALID_XML  = "src/test/resources/non-xml-file.xml";
	public static final String INVALID_QRDA = "src/test/resources/not-a-QRDA-III-file.xml";
	private static final String TOO_MANY_ERRORS = "src/test/resources/negative/tooManyErrors.xml";

	@Before
	public void setup() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.TEST_MEASURE_DATA);
	}

	@After
	public void teardown() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@Test(expected = org.junit.Test.None.class)
	public void testValidQppFile() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
		Path path = Paths.get(VALID_FILE);
		Converter converter = new Converter(new PathSource(path));

		converter.transform();
		//no exception should be thrown, hence explicitly stating the expected exception is None
	}

	@Test(expected = org.junit.Test.None.class)
	public void testValidQppStream() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
		Path path = Paths.get(VALID_FILE);
		Converter converter = new Converter(
				new InputStreamSupplierSource(path.toString(), NioHelper.fileToStream(path)));

		converter.transform();
		//no exception should be thrown, hence explicitly stating the expected exception is None
	}

	@Test
	@PrepareForTest({Converter.class, QrdaValidator.class})
	public void testValidationErrors() throws Exception {
		Context context = new Context();
		TestHelper.mockDecoder(context, JennyDecoder.class, new ComponentKey(TemplateId.IA_SECTION, Program.ALL));
		QrdaValidator mockQrdaValidator = TestHelper.mockValidator(context, TestDefaultValidator.class, new ComponentKey(TemplateId.IA_SECTION, Program.ALL), true);
		PowerMockito.whenNew(QrdaValidator.class)
			.withAnyArguments()
			.thenReturn(mockQrdaValidator);

		Path path = Paths.get(ERROR_FILE);
		Converter converter = new Converter(new PathSource(path), context);

		try {
			converter.transform();
			fail("The converter should not create valid QPP JSON");
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			List<Error> errors = allErrors.getErrors();
			assertWithMessage("There must only be one error source.")
				.that(errors).hasSize(1);

			List<Detail> details = errors.get(0).getDetails();
			assertWithMessage("The expected validation error was missing")
				.that(details)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Test validation error for Jenny"));
		}
	}

	@Test
	public void testInvalidXml() {
		Path path = Paths.get(INVALID_XML);
		Converter converter = new Converter(new PathSource(path));

		try {
			converter.transform();
			fail();
		} catch (TransformException exception) {
			checkup(exception, ProblemCode.NOT_VALID_XML_DOCUMENT);
		}
	}

	@Test
	@PrepareForTest({Converter.class, QppOutputEncoder.class})
	public void testEncodingExceptions() throws Exception {
		QppOutputEncoder encoder = mock(QppOutputEncoder.class);
		whenNew(QppOutputEncoder.class).withAnyArguments().thenReturn(encoder);
		EncodeException ex = new EncodeException("mocked", new RuntimeException());
		doThrow(ex).when(encoder).encode();

		Path path = Paths.get(EXCEPT_FILE);
		Converter converter = new Converter(new PathSource(path));
		converter.getContext().setDoValidation(false);

		try {
			converter.transform();
			fail();
		} catch (TransformException exception) {
			checkup(exception, ProblemCode.NOT_VALID_XML_DOCUMENT);
		}
	}

	@Test
	public void testInvalidXmlFile() {
		Converter converter = new Converter(new PathSource(Paths.get("src/test/resources/not-a-QRDA-III-file.xml")));
		
		try {
			converter.transform();
			fail();
		} catch (TransformException exception) {
			checkup(exception, ProblemCode.NOT_VALID_QRDA_DOCUMENT.format(Context.REPORTING_YEAR, DocumentationReference.CLINICAL_DOCUMENT));
		}
	}

	@Test
	public void testNotAValidQrdaIIIFile() {
		Path path = Paths.get(INVALID_QRDA);
		Converter converter = new Converter(new PathSource(path));
		converter.getContext().setDoValidation(false);

		try {
			converter.transform();
			fail();
		} catch (TransformException exception) {
			checkup(exception, ProblemCode.NOT_VALID_QRDA_DOCUMENT.format(Context.REPORTING_YEAR, DocumentationReference.CLINICAL_DOCUMENT));
		}
	}

	@Test
	public void testUnexpectedError() {
		Source source = mock(Source.class);
		when(source.toInputStream()).thenThrow(RuntimeException.class);

		Converter converter = new Converter(source);

		try {
			converter.transform();
			fail();
		} catch (TransformException exception) {
			checkup(exception, ProblemCode.UNEXPECTED_ERROR);
		}
		
		source = mock(Source.class);
		when(source.toInputStream()).thenThrow(TextParsingException.class);

		converter = new Converter(source);

		try {
			converter.transform();
			fail();
		} catch (TransformException exception) {
			checkup(exception, ProblemCode.UNEXPECTED_ERROR);
		}
	}

	@Test
	public void testSkipDefaults() {
		Converter converter = new Converter(new PathSource(Paths.get("src/test/resources/converter/defaultedNode.xml")));
		converter.getContext().setDoValidation(false);
		JsonWrapper qpp = converter.transform();

		String content = qpp.toString();

		assertThat(content).doesNotContain("Jenny");
	}

	@Test
	public void testEncodeThrowingEncodeException() throws Exception {
		Converter converter = Mockito.mock(Converter.class);
		Field field = Converter.class.getDeclaredField("decoded");
		field.setAccessible(true);
		field.set(converter, new Node());
		JsonOutputEncoder mockEncoder = Mockito.mock(JsonOutputEncoder.class);
		Mockito.when(mockEncoder.encode()).thenThrow(EncodeException.class);
		Mockito.when(converter.getEncoder()).thenReturn(mockEncoder);
		Method encode = Converter.class.getDeclaredMethod("encode");
		encode.setAccessible(true);
		Exception thrown = Assertions.assertThrows(InvocationTargetException.class, () -> encode.invoke(converter));
		Truth.assertThat(thrown).hasCauseThat().isInstanceOf(XmlInputFileException.class);
	}

	@Test
	public void testTestSourceCreatesTestReport() {
		Source source = mock(Source.class);
		when(source.getPurpose()).thenReturn("Test");
		Truth.assertThat(new Converter(source).getReport().getPurpose()).isEqualTo("Test");
	}

	@Test
	public void testNormalSourceCreatesNormalReport() {
		Source source = mock(Source.class);
		when(source.getPurpose()).thenReturn(null);
		Truth.assertThat(new Converter(source).getReport().getPurpose()).isNull();
	}

	@Test
	public void testTooManyErrorsInQrdaIIIFile() {
		LocalizedProblem expectedError = ProblemCode.TOO_MANY_ERRORS.format(108);

		Path path = Paths.get(TOO_MANY_ERRORS);
		Converter converter = new Converter(new PathSource(path));
		try {
			converter.transform();
			fail();
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			List<Error> errors = allErrors.getErrors();
			assertWithMessage("The validation error was incorrect")
				.that(errors.get(0).getDetails())
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
		}

	}

	private void checkup(TransformException exception, LocalizedProblem error) {
		AllErrors allErrors = exception.getDetails();
		List<Error> errors = allErrors.getErrors();
		assertWithMessage("There must only be one error source.")
				.that(errors).hasSize(1);
		List<Detail> details = errors.get(0).getDetails();
		assertWithMessage("There must be only one validation error.")
				.that(details).hasSize(1);
		assertWithMessage("The validation error was incorrect")
				.that(details)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(error);
	}
}
