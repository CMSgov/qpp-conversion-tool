package gov.cms.qpp.conversion;

import com.google.common.truth.Truth;
import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.decode.XmlInputFileException;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.*;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.stubs.JennyDecoder;
import gov.cms.qpp.conversion.stubs.TestDefaultValidator;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.test.helper.NioHelper;
import org.junit.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ConverterTest {

	public static final String VALID_FILE = "../qrda-files/valid-QRDA-III-latest.xml";
	public static final String ERROR_FILE = "src/test/resources/converter/errantDefaultedNode.xml";
	public static final String EXCEPT_FILE = "src/test/resources/converter/defaultedNode.xml";
	public static final String INVALID_XML = "src/test/resources/non-xml-file.xml";
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

	@Test
	public void testValidQppFile() {
		Path path = Path.of(VALID_FILE);
		Converter converter = new Converter(new PathSource(path));
		converter.getContext().setDoValidation(false); // skip validation so we don't fail on sample XML
		converter.transform();
	}

	@Test
	public void testValidQppStream() {
		Path path = Path.of(VALID_FILE);
		Converter converter = new Converter(
				new InputStreamSupplierSource(path.toString(), NioHelper.fileToStream(path)));
		converter.getContext().setDoValidation(false); // skip validation
		converter.transform();
	}

	@Test
	public void testInvalidXml() {
		Path path = Path.of(INVALID_XML);
		Converter converter = new Converter(new PathSource(path));
		TransformException exception = Assertions.assertThrows(
				TransformException.class, converter::transform);
		checkup(exception, ProblemCode.NOT_VALID_XML_DOCUMENT);
	}

	@Test
	public void testEncodingExceptionsWithoutPowerMock() throws Exception {
		JsonOutputEncoder encoder = mock(JsonOutputEncoder.class);
		doThrow(new EncodeException("mocked", new RuntimeException())).when(encoder).encode();

		Converter converter = new Converter(new PathSource(Path.of(EXCEPT_FILE))) {
			@Override
			protected JsonOutputEncoder getEncoder() {
				return encoder;
			}
		};
		converter.getContext().setDoValidation(false); // skip validation so encoding is reached
		TransformException exception = Assertions.assertThrows(
				TransformException.class, converter::transform);
		checkup(exception, ProblemCode.NOT_VALID_XML_DOCUMENT);
	}

	@Test
	public void testInvalidXmlFile() {
		Converter converter = new Converter(new PathSource(Path.of(INVALID_QRDA)));
		TransformException exception = Assertions.assertThrows(
				TransformException.class, converter::transform);
		checkup(exception, ProblemCode.NOT_VALID_QRDA_DOCUMENT.format(
				Context.REPORTING_YEAR, DocumentationReference.CLINICAL_DOCUMENT));
	}

	@Test
	public void testUnexpectedError() {
		Source source = mock(Source.class);
		when(source.toInputStream()).thenThrow(RuntimeException.class);

		Converter converter = new Converter(source);
		TransformException exception = Assertions.assertThrows(
				TransformException.class, converter::transform);
		checkup(exception, ProblemCode.UNEXPECTED_ERROR);
	}

	@Test
	public void testSkipDefaults() {
		Converter converter = new Converter(new PathSource(Path.of(EXCEPT_FILE)));
		converter.getContext().setDoValidation(false); // skip validation to avoid failing on test XML
		JsonWrapper qpp = converter.transform();
		assertThat(qpp.toString()).doesNotContain("Jenny");
	}

	@Test
	public void testEncodeThrowingEncodeExceptionReflection() throws Exception {
		Converter converter = Mockito.mock(Converter.class);
		Field field = Converter.class.getDeclaredField("decoded");
		field.setAccessible(true);
		field.set(converter, new Node());
		JsonOutputEncoder mockEncoder = Mockito.mock(JsonOutputEncoder.class);
		Mockito.when(mockEncoder.encode()).thenThrow(EncodeException.class);
		Mockito.when(converter.getEncoder()).thenReturn(mockEncoder);
		Method encode = Converter.class.getDeclaredMethod("encode");
		encode.setAccessible(true);
		Exception thrown = Assertions.assertThrows(
				InvocationTargetException.class, () -> encode.invoke(converter));
		Truth.assertThat(thrown).hasCauseThat().isInstanceOf(XmlInputFileException.class);
	}

	@Test
	public void testTestSourceCreatesTestReport() {
		Source source = mock(Source.class);
		when(source.getPurpose()).thenReturn("Test");
		Truth.assertThat(new Converter(source).getReport().getPurpose()).isEqualTo("Test");
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
