package gov.cms.qpp.conversion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConversionReportTest {
	private static Converter.ConversionReport report;
	private static Converter.ConversionReport errorReport;
	private static JsonWrapper wrapper;
	private static Source inputSource;

	@BeforeAll
	static void setup() {
		inputSource = new PathSource(Paths.get("../qrda-files/valid-QRDA-III-latest.xml"));
		Converter converter = new Converter(inputSource);
		wrapper = converter.transform();
		report = converter.getReport();

		Converter otherConverter = new Converter(
				new PathSource(Paths.get("../qrda-files/QRDA-III-without-required-measure.xml")));
		try {
			otherConverter.transform();
		} catch (TransformException ex) {
			//no worries
			errorReport = ex.getConversionReport();
		}
	}

	@Test
	void testReportRetrieval() {
		assertThat(report).isNotNull();
	}

	@Test
	void testGetDecoded() {
		assertThat(report.getDecoded()).isNotNull();
	}

	@Test
	void testGetEncoded() {
		assertThat(report.getEncoded().toString())
				.isEqualTo(wrapper.toString());
	}

	@Test
	void getReportDetails() {
		assertThat(errorReport.getReportDetails()).isNotNull();
	}

	@Test
	void testGetQrdaSource() {
		assertThat(report.getQrdaSource()).isEqualTo(inputSource);
	}

	@Test
	void testGetQppSource() throws IOException {
		assertThat(IOUtils.toString(report.getQppSource().toInputStream(), StandardCharsets.UTF_8))
			.isEqualTo(IOUtils.toString(wrapper.toSource().toInputStream(), StandardCharsets.UTF_8));
	}

	@Test
	void getBadReportDetails() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
		ObjectMapper mockMapper = mock(ObjectMapper.class);
		when(mockMapper.writeValueAsBytes(any(AllErrors.class)))
			.thenThrow(new JsonMappingException("meep"));

		Converter converter = new Converter(
			new PathSource(Paths.get("../qrda-files/valid-QRDA-III-latest.xml")));
		Converter.ConversionReport badReport = converter.getReport();

		Field field = badReport.getClass().getDeclaredField("mapper");
		field.setAccessible(true);
		field.set(badReport, mockMapper);

		assertThrows(EncodeException.class, badReport::getValidationErrorsSource);
	}

	@Test
	void getGoodReportDetails() {
		assertThat(errorReport.getValidationErrorsSource().toInputStream()).isNotNull();
	}

	@Test
	void getErrorStream() {
		Converter converter = new Converter(
			new PathSource(Paths.get("../qrda-files/valid-QRDA-III-latest.xml")));
		Converter.ConversionReport badReport = converter.getReport();
		Error error = new Error();
		error.setMessage("meep");
		AllErrors errors = new AllErrors();
		errors.addError(error);
		badReport.setReportDetails(errors);

		AllErrors echo = JsonHelper.readJson(badReport.getValidationErrorsSource().toInputStream(), AllErrors.class);
		assertThat(echo.toString()).isEqualTo(errors.toString());
	}

	@Test
	void rawValidationErrors() throws IOException {
		Converter converter = new Converter(
				new PathSource(Paths.get("../qrda-files/valid-QRDA-III-latest.xml")));
		Converter.ConversionReport aReport = converter.getReport();
		aReport.setRawValidationDetails("meep");
		String details = IOUtils.toString(aReport.getRawValidationErrorsOrEmptySource().toInputStream(), "UTF-8");

		assertThat(details).isEqualTo("meep");
	}

	@Test
	void emptyRawValidationErrors() throws IOException {
		String details = IOUtils.toString(errorReport.getRawValidationErrorsOrEmptySource().toInputStream(), "UTF-8");

		assertThat(details).isEmpty();
	}
}
