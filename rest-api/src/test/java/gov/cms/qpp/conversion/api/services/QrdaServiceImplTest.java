package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.TransformationStatus;
import gov.cms.qpp.conversion.api.model.ConversionResult;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Converter.class, QrdaServiceImpl.class})
public class QrdaServiceImplTest {
	private static QrdaServiceImpl objectUnderTest;

	private static final InputStream MOCK_SUCCESS_QRDA_INPUT_STREAM = new ByteArrayInputStream("Good Qrda".getBytes());
	private static final InputStream MOCK_ERROR_QRDA_INPUT_STREAM = new ByteArrayInputStream("Error Qrda".getBytes());
	private static final InputStream MOCK_NON_RECOVERABLE_QRDA_INPUT_STREAM = new ByteArrayInputStream("Non-recoverable Qrda".getBytes());

	private static final String MOCK_SUCCESS_QPP_STRING = "Good Qpp";
	private static final String MOCK_ERROR_QPP_STRING = "Error Qpp";
	private static final String MOCK_NON_RECOVERABLE_QPP_STRING = "Non-recoverable Qpp";

	@BeforeClass
	public static void initBean() {
		objectUnderTest = new QrdaServiceImpl();
	}

	@Before
	public void mockConverter() throws Exception {
		Converter mockConverter = mock(Converter.class);

		whenNew(Converter.class).withArguments(MOCK_SUCCESS_QRDA_INPUT_STREAM).thenAnswer(invocationOnMock -> {
			when(mockConverter.getConversionResult()).thenReturn(new ByteArrayInputStream(MOCK_SUCCESS_QPP_STRING.getBytes()));
			when(mockConverter.getStatus()).thenReturn(TransformationStatus.SUCCESS);

			return mockConverter;
		});

		whenNew(Converter.class).withArguments(MOCK_ERROR_QRDA_INPUT_STREAM).thenAnswer(invocationOnMock -> {
			when(mockConverter.getConversionResult()).thenReturn(new ByteArrayInputStream(MOCK_ERROR_QPP_STRING.getBytes()));
			when(mockConverter.getStatus()).thenReturn(TransformationStatus.ERROR);

			return mockConverter;
		});

		whenNew(Converter.class).withArguments(MOCK_NON_RECOVERABLE_QRDA_INPUT_STREAM).thenAnswer(invocationOnMock -> {
			when(mockConverter.getConversionResult()).thenReturn(new ByteArrayInputStream(MOCK_NON_RECOVERABLE_QPP_STRING.getBytes()));
			when(mockConverter.getStatus()).thenReturn(TransformationStatus.NON_RECOVERABLE);

			return mockConverter;
		});
	}

	@Test
	public void testConvertQrda3ToQppSuccess() throws IOException {
		ConversionResult conversionResult = objectUnderTest.convertQrda3ToQpp(MOCK_SUCCESS_QRDA_INPUT_STREAM);
		assertThat("The JSON content is incorrect.", conversionResult.getContent(), is(MOCK_SUCCESS_QPP_STRING));
		assertThat("The conversion status is incorrect.", conversionResult.getStatus(), is(TransformationStatus.SUCCESS));
	}

	@Test
	public void testConvertQrda3ToQppError() throws IOException {
		ConversionResult conversionResult = objectUnderTest.convertQrda3ToQpp(MOCK_ERROR_QRDA_INPUT_STREAM);
		assertThat("The conversion status is incorrect.", conversionResult.getContent(), is(MOCK_ERROR_QPP_STRING));
		assertThat("The JSON content is incorrect.", conversionResult.getStatus(), is(TransformationStatus.ERROR));
	}

	@Test
	public void testConvertQrda3ToQppNonRecoverable() throws IOException {
		ConversionResult conversionResult = objectUnderTest.convertQrda3ToQpp(MOCK_NON_RECOVERABLE_QRDA_INPUT_STREAM);
		assertThat("The conversion status is incorrect.", conversionResult.getContent(), is(MOCK_NON_RECOVERABLE_QPP_STRING));
		assertThat("The JSON content is incorrect.", conversionResult.getStatus(), is(TransformationStatus.NON_RECOVERABLE));
	}
}
