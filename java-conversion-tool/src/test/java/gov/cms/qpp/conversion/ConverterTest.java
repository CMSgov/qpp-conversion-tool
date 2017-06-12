package gov.cms.qpp.conversion;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.validationErrorTextMatches;
import static gov.cms.qpp.conversion.util.ConverterTestHelper.run;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.AnnotationMockHelper;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.stubs.JennyDecoder;
import gov.cms.qpp.conversion.stubs.TestDefaultValidator;
import gov.cms.qpp.conversion.util.NamedInputStream;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.conversion.xml.XmlUtils;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*" })
public class ConverterTest {

	@Test(expected = org.junit.Test.None.class)
	public void testValidQppFile() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
		Converter converter = new Converter(path);

		run(converter);
		//no exception should be thrown, hence explicitly stating the expected exception is None
	}

	@Test(expected = org.junit.Test.None.class)
	public void testValidQppStream() throws IOException {
		Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
		NamedInputStream inputStream = new NamedInputStream(XmlUtils.fileToStream(path), path.toString());
		Converter converter = new Converter(inputStream);

		run(converter);
		//no exception should be thrown, hence explicitly stating the expected exception is None
	}

	@Test
	@PrepareForTest({Converter.class, QrdaValidator.class})
	public void testValidationErrors() throws Exception {

		//mocking
		AnnotationMockHelper.mockDecoder(TemplateId.DEFAULT, JennyDecoder.class);
		QrdaValidator mockQrdaValidator = AnnotationMockHelper.mockValidator(TemplateId.DEFAULT, TestDefaultValidator.class, true);
		PowerMockito.whenNew(QrdaValidator.class).withNoArguments().thenReturn(mockQrdaValidator);

		Path path = Paths.get("src/test/resources/converter/errantDefaultedNode.xml");
		Converter converter = new Converter(path);

		try {
			run(converter);
			fail("The converter should not create valid QPP JSON");
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			List<Error> errors = allErrors.getErrors();
			assertThat("There must only be one error source.", errors, hasSize(1));
			List<Detail> details = errors.get(0).getDetails();
			assertThat("The expected validation error was missing", details, hasValidationErrorsIgnoringPath("Test validation error for Jenny"));
		}
	}

	@Test
	public void testInvalidXml() {
		Path path = Paths.get("src/test/resources/non-xml-file.xml");
		Converter converter = new Converter(path);

		try {
			run(converter);
			fail();
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			List<Error> errors = allErrors.getErrors();
			assertThat("There must only be one error source.", errors, hasSize(1));
			List<Detail> details = errors.get(0).getDetails();
			assertThat("There must be only one validation error.", details, hasSize(1));
			Detail detail = details.get(0);
			assertThat("The validation error was incorrect", detail, validationErrorTextMatches(Converter.NOT_VALID_XML_DOCUMENT));
		}
	}

	@Test
	@PrepareForTest({Converter.class, QppOutputEncoder.class})
	public void testEncodingExceptions() throws Exception {
		QppOutputEncoder encoder = mock(QppOutputEncoder.class);
		whenNew(QppOutputEncoder.class).withNoArguments().thenReturn(encoder);
		EncodeException ex = new EncodeException("mocked", new RuntimeException());
		doThrow(ex).when(encoder).encode();

		Path path = Paths.get("src/test/resources/converter/defaultedNode.xml");
		Converter converter = new Converter(path)
				.doDefaults(false)
				.doValidation(false);

		try {
			run(converter);
			fail();
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			List<Error> errors = allErrors.getErrors();
			assertThat("There must only be one error source.", errors, hasSize(1));
			List<Detail> details = errors.get(0).getDetails();
			assertThat("There must be only one validation error.", details, hasSize(1));
			Detail detail = details.get(0);
			assertThat("The validation error was incorrect", detail, validationErrorTextMatches(Converter.NOT_VALID_XML_DOCUMENT));
		}
	}

	@Test
	public void testInvalidXmlFile() {
		Converter converter = new Converter(Paths.get("src/test/resources/not-a-QRDA-III-file.xml"));
		
		try {
			run(converter);
			fail();
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			List<Error> errors = allErrors.getErrors();
			assertThat("There must only be one error source.", errors, hasSize(1));
			List<Detail> details = errors.get(0).getDetails();
			assertThat("There must be only one validation error.", details, hasSize(1));
			Detail detail = details.get(0);
			assertThat("The validation error was incorrect", detail, validationErrorTextMatches("The file is not a QRDA-III XML document"));
		}
	}

	@Test
	public void testNotAValidQrdaIIIFile() {
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		Converter converter = new Converter(path)
				.doDefaults(false)
				.doValidation(false);

		try {
			run(converter);
			fail();
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			List<Error> errors = allErrors.getErrors();
			assertThat("There must only be one error source.", errors, hasSize(1));
			List<Detail> details = errors.get(0).getDetails();
			assertThat("There must be only one validation error.", details, hasSize(1));
			Detail detail = details.get(0);
			assertThat("The validation error was incorrect", detail, validationErrorTextMatches("The file is not a QRDA-III XML document"));
		}
	}

	@Test
	@PrepareForTest({Converter.class, XmlUtils.class})
	public void testUnexpectedError() throws IOException {

		mockStatic(XmlUtils.class);
		when(XmlUtils.fileToStream(any(Path.class))).thenReturn(null);
		//when(Files.readAllBytes(any(Path.class))).thenCallRealMethod();

		Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
		Converter converter = new Converter(path);

		try {
			run(converter);
			fail();
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			List<Error> errors = allErrors.getErrors();
			assertThat("There must only be one error source.", errors, hasSize(1));
			List<Detail> details = errors.get(0).getDetails();
			assertThat("There must be only one validation error.", details, hasSize(1));
			Detail detail = details.get(0);
			assertThat("The validation error was incorrect", detail, validationErrorTextMatches(Converter.UNEXPECTED_ERROR));
		}
	}
}
