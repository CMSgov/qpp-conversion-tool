package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.InputStreamQrdaSource;
import gov.cms.qpp.conversion.QrdaSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;

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

	private static final QrdaSource MOCK_SUCCESS_QRDA_SOURCE = new InputStreamQrdaSource("Good Qrda", new ByteArrayInputStream("Good Qrda".getBytes()));
	private static final QrdaSource MOCK_ERROR_QRDA_SOURCE = new InputStreamQrdaSource("Error Qrda", new ByteArrayInputStream("Error Qrda".getBytes()));

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

		whenNew(Converter.class).withArguments(MOCK_SUCCESS_QRDA_SOURCE).thenAnswer(invocationOnMock -> {
			JsonWrapper qpp = new JsonWrapper();
			qpp.putString(KEY, MOCK_SUCCESS_QPP_STRING);
			when(mockConverter.transform()).thenReturn(qpp);

			return mockConverter;
		});

		whenNew(Converter.class).withArguments(MOCK_ERROR_QRDA_SOURCE).thenAnswer(invocationOnMock -> {
			AllErrors allErrors = new AllErrors();
			allErrors.addError(new Error(MOCK_ERROR_SOURCE_IDENTIFIER, null));
			TransformException transformException = new TransformException("mock problem", new NullPointerException(), allErrors);
			when(mockConverter.transform()).thenThrow(transformException);

			return mockConverter;
		});
	}

	@Test
	public void testConvertQrda3ToQppSuccess() {
		JsonWrapper qpp = objectUnderTest.convertQrda3ToQpp(MOCK_SUCCESS_QRDA_SOURCE);
		assertThat("The JSON content is incorrect.", qpp.getString(KEY), is(MOCK_SUCCESS_QPP_STRING));
	}

	@Test
	public void testConvertQrda3ToQppError() {
		try {
			JsonWrapper qpp = objectUnderTest.convertQrda3ToQpp(MOCK_ERROR_QRDA_SOURCE);
			fail("An exception should have occurred. Instead was: " + qpp);
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			assertThat("", allErrors.getErrors().get(0).getSourceIdentifier(), is(MOCK_ERROR_SOURCE_IDENTIFIER));
		} catch (Exception exception) {
			fail("The wrong exception occurred.");
		}
	}
}
