package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.InputStreamSupplierQrdaSource;
import gov.cms.qpp.conversion.QrdaSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({Converter.class})
public class QrdaServiceImplTest {
	private static final QrdaSource MOCK_SUCCESS_QRDA_SOURCE =
			new InputStreamSupplierQrdaSource("Good Qrda", () -> new ByteArrayInputStream("Good Qrda".getBytes()));
	private static final QrdaSource MOCK_ERROR_QRDA_SOURCE =
			new InputStreamSupplierQrdaSource("Error Qrda", () ->new ByteArrayInputStream("Error Qrda".getBytes()));

	private static final String KEY = "key";
	private static final String MOCK_SUCCESS_QPP_STRING = "Good Qpp";
	private static final String MOCK_ERROR_SOURCE_IDENTIFIER = "Error Identifier";

	@Spy
	private QrdaServiceImpl objectUnderTest;

	@Before
	public void mockConverter() throws Exception {
		Converter success = successConverter();
		when(objectUnderTest.initConverter(MOCK_SUCCESS_QRDA_SOURCE))
				.thenReturn(success);

		Converter error = errorConverter();
		when(objectUnderTest.initConverter(MOCK_ERROR_QRDA_SOURCE))
				.thenReturn(error);
	}

	@Test
	public void testConvertQrda3ToQppSuccess() {
		JsonWrapper qpp = objectUnderTest.convertQrda3ToQpp(MOCK_SUCCESS_QRDA_SOURCE).getEncoded();
		assertWithMessage("The JSON content is incorrect.")
				.that(qpp.getString(KEY)).isSameAs(MOCK_SUCCESS_QPP_STRING);
	}

	@Test
	public void testConvertQrda3ToQppError() {
		try {
			objectUnderTest.convertQrda3ToQpp(MOCK_ERROR_QRDA_SOURCE);
			fail("An exception should have occurred.");
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			assertThat(allErrors.getErrors().get(0).getSourceIdentifier()).isSameAs(MOCK_ERROR_SOURCE_IDENTIFIER);
		} catch (Exception exception) {
			fail("The wrong exception occurred.");
		}
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
