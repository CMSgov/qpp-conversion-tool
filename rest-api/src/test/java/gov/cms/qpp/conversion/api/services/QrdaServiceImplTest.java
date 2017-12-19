package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.test.MockitoExtension;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;

import java.io.ByteArrayInputStream;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@ExtendWith(MockitoExtension.class)
class QrdaServiceImplTest {
	private static final Source MOCK_SUCCESS_QRDA_SOURCE =
			new InputStreamSupplierSource("Good Qrda", () -> new ByteArrayInputStream("Good Qrda".getBytes()));
	private static final Source MOCK_ERROR_QRDA_SOURCE =
			new InputStreamSupplierSource("Error Qrda", () ->new ByteArrayInputStream("Error Qrda".getBytes()));

	private static final String KEY = "key";
	private static final String MOCK_SUCCESS_QPP_STRING = "Good Qpp";
	private static final String MOCK_ERROR_SOURCE_IDENTIFIER = "Error Identifier";

	@Spy
	private QrdaServiceImpl objectUnderTest;

	@BeforeEach
	void mockConverter() throws Exception {
		Converter success = successConverter();
		when(objectUnderTest.initConverter(MOCK_SUCCESS_QRDA_SOURCE))
				.thenReturn(success);

		Converter error = errorConverter();
		when(objectUnderTest.initConverter(MOCK_ERROR_QRDA_SOURCE))
				.thenReturn(error);
	}

	@Test
	void testConvertQrda3ToQppSuccess() {
		JsonWrapper qpp = objectUnderTest.convertQrda3ToQpp(MOCK_SUCCESS_QRDA_SOURCE).getEncoded();
		assertWithMessage("The JSON content is incorrect.")
				.that(qpp.getString(KEY)).isSameAs(MOCK_SUCCESS_QPP_STRING);
	}

	@Test
	void testConvertQrda3ToQppError() {
		TransformException exception = assertThrows(TransformException.class,
				() -> objectUnderTest.convertQrda3ToQpp(MOCK_ERROR_QRDA_SOURCE));
		AllErrors allErrors = exception.getDetails();
		assertThat(allErrors.getErrors().get(0).getSourceIdentifier()).isSameAs(MOCK_ERROR_SOURCE_IDENTIFIER);
	}

	private Converter successConverter() {
		Converter mockConverter = mock(Converter.class);

		JsonWrapper qpp = new JsonWrapper();
		qpp.putString(KEY, MOCK_SUCCESS_QPP_STRING);

		Converter.ConversionReport report = mock(Converter.ConversionReport.class);

		when(report.getEncoded()).thenReturn(qpp);
		when(mockConverter.getReport()).thenReturn(report);

		return mockConverter;
	}

	private Converter errorConverter() {
		Converter mockConverter = mock(Converter.class);
		AllErrors allErrors = new AllErrors();
		allErrors.addError(new Error(MOCK_ERROR_SOURCE_IDENTIFIER, null));

		Converter.ConversionReport report = mock(Converter.ConversionReport.class);
		when(report.getReportDetails()).thenReturn(allErrors);

		TransformException transformException = new TransformException("mock problem", new NullPointerException(), report);
		when(mockConverter.transform()).thenThrow(transformException);

		return mockConverter;
	}
}
