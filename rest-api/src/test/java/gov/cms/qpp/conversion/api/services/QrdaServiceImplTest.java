package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.ErrorSource;
import gov.cms.qpp.conversion.model.error.TransformException;
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
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Converter.class, QrdaServiceImpl.class})
public class QrdaServiceImplTest {
	private static QrdaServiceImpl objectUnderTest;

	private static final InputStream MOCK_SUCCESS_QRDA_INPUT_STREAM = new ByteArrayInputStream("Good Qrda".getBytes());
	private static final InputStream MOCK_ERROR_QRDA_INPUT_STREAM = new ByteArrayInputStream("Error Qrda".getBytes());

	private static final String KEY = "key";
	private static final String MOCK_SUCCESS_QPP_STRING = "Good Qpp";
	private static final String MOCK_ERROR_SOURCE_IDENTIFIER = "Error Identifier";

	@BeforeClass
	public static void initBean() {
		objectUnderTest = new QrdaServiceImpl();
	}

	@Before
	public void mockConverter() throws Exception {
		Converter mockConverter = mock(Converter.class);

		whenNew(Converter.class).withArguments(MOCK_SUCCESS_QRDA_INPUT_STREAM).thenAnswer(invocationOnMock -> {
			JsonWrapper qpp = new JsonWrapper();
			qpp.putString(KEY, MOCK_SUCCESS_QPP_STRING);
			when(mockConverter.transform()).thenReturn(qpp);

			return mockConverter;
		});

		whenNew(Converter.class).withArguments(MOCK_ERROR_QRDA_INPUT_STREAM).thenAnswer(invocationOnMock -> {
			AllErrors allErrors = new AllErrors();
			allErrors.addErrorSource(new ErrorSource(MOCK_ERROR_SOURCE_IDENTIFIER, null));
			TransformException transformException = new TransformException("mock problem", new NullPointerException(), allErrors);
			when(mockConverter.transform()).thenThrow(transformException);

			return mockConverter;
		});
	}

	@Test
	public void testConvertQrda3ToQppSuccess() throws IOException {
		JsonWrapper qpp = objectUnderTest.convertQrda3ToQpp(MOCK_SUCCESS_QRDA_INPUT_STREAM);
		assertThat("The JSON content is incorrect.", qpp.getString(KEY), is(MOCK_SUCCESS_QPP_STRING));
	}

	@Test
	public void testConvertQrda3ToQppError() throws IOException {
		try {
			JsonWrapper qpp = objectUnderTest.convertQrda3ToQpp(MOCK_ERROR_QRDA_INPUT_STREAM);
			fail();
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			assertThat("", allErrors.getErrorSources().get(0).getSourceIdentifier(), is(MOCK_ERROR_SOURCE_IDENTIFIER));
		} catch (Exception exception) {
			fail();
		}
	}
}
