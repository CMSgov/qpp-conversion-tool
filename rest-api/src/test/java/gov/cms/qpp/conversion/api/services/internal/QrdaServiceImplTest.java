package gov.cms.qpp.conversion.api.services.internal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.services.StorageService;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.test.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrdaServiceImplTest {
	private static final Source MOCK_SUCCESS_QRDA_SOURCE =
			new InputStreamSupplierSource("Good Qrda", new ByteArrayInputStream("Good Qrda".getBytes()));
	private static final Source MOCK_ERROR_QRDA_SOURCE =
			new InputStreamSupplierSource("Error Qrda", new ByteArrayInputStream("Error Qrda".getBytes()));

	private static final String KEY = "key";
	private static final String MOCK_SUCCESS_QPP_STRING = "Good Qpp";
	private static final String MOCK_ERROR_SOURCE_IDENTIFIER = "Error Identifier";
	private static final Path VALIDATION_JSON_FILE_PATH = Path.of("src/test/resources/testCpcPlusValidationFile.json");
	private static final Path VALIDATION_APM_FILE_PATH = Path.of("src/test/resources/test_apm_entity_ids.json");
	private InputStream MOCK_INPUT_STREAM;
	private InputStream MOCK_APM_INPUT_STREAM;

	@Spy
	@InjectMocks
	private QrdaServiceImpl objectUnderTest;

	@Mock
	private StorageService storageService;

	@BeforeEach
	void mockConverter() throws IOException {
		MOCK_INPUT_STREAM = Files.newInputStream(VALIDATION_JSON_FILE_PATH);
		MOCK_APM_INPUT_STREAM = Files.newInputStream(VALIDATION_APM_FILE_PATH);
		Converter success = successConverter();
		when(objectUnderTest.initConverter(MOCK_SUCCESS_QRDA_SOURCE))
				.thenReturn(success);

		when(objectUnderTest.retrieveCpcPlusValidationFile())
				.thenReturn(MOCK_INPUT_STREAM);

		when(objectUnderTest.retrieveApmValidationFile(Constants.CPC_PLUS_APM_FILE_NAME_KEY))
			.thenReturn(MOCK_APM_INPUT_STREAM);

		Converter error = errorConverter();
		when(objectUnderTest.initConverter(MOCK_ERROR_QRDA_SOURCE))
				.thenReturn(error);
	}

	@AfterEach
	void tearDown() throws IOException {
		MOCK_APM_INPUT_STREAM.close();
		MOCK_APM_INPUT_STREAM.close();
	}

	@Test
	void testConvertQrda3ToQppSuccess() {
		JsonWrapper qpp = objectUnderTest.convertQrda3ToQpp(MOCK_SUCCESS_QRDA_SOURCE).getEncodedWithMetadata();
		assertThat(qpp.getString(KEY)).isSameInstanceAs(MOCK_SUCCESS_QPP_STRING);
	}

//	@Test
//	void testConvertQrda3ToQppError() {
//		TransformException exception = assertThrows(TransformException.class,
//				() -> objectUnderTest.convertQrda3ToQpp(MOCK_ERROR_QRDA_SOURCE));
//		AllErrors allErrors = exception.getDetails();
//		assertThat(allErrors.getErrors().get(0).getSourceIdentifier()).isSameInstanceAs(MOCK_ERROR_SOURCE_IDENTIFIER);
//	}

	@Test
	void testPostConstructForCoverage() {
		objectUnderTest.preloadMeasureConfigs();
	}

	private Converter successConverter() {
		Converter mockConverter = mock(Converter.class);

		JsonWrapper qpp = new JsonWrapper();
		qpp.put(KEY, MOCK_SUCCESS_QPP_STRING);

		ConversionReport report = mock(ConversionReport.class);

		when(report.getEncodedWithMetadata()).thenReturn(qpp);
		when(mockConverter.getReport()).thenReturn(report);

		return mockConverter;
	}

	private Converter errorConverter() {
		Converter mockConverter = mock(Converter.class);
		AllErrors allErrors = new AllErrors();
		allErrors.addError(new Error(MOCK_ERROR_SOURCE_IDENTIFIER, null));

		ConversionReport report = mock(ConversionReport.class);
		when(report.getReportDetails()).thenReturn(allErrors);

		TransformException transformException = new TransformException("mock problem", new NullPointerException(), report);
		when(mockConverter.transform()).thenThrow(transformException);

		return mockConverter;
	}
}
