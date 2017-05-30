package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.TransformationStatus;
import gov.cms.qpp.conversion.api.model.ConversionResult;
import gov.cms.qpp.conversion.api.services.QrdaService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QrdaControllerV1Test {

	@InjectMocks
	private static QrdaControllerV1 objectUnderTest;

	@Mock
	private QrdaService qrdaService;

	private static MultipartFile successMultipartFile;
	private static MultipartFile errorMultipartFile;
	private static MultipartFile nonRecoverableMultipartFile;

	private static final ConversionResult GOOD_RESULT = new ConversionResult("Good Qpp", TransformationStatus.SUCCESS);
	private static final ConversionResult ERROR_RESULT = new ConversionResult("Error Qpp", TransformationStatus.ERROR);
	private static final ConversionResult NON_RECOVERABLE_RESULT = new ConversionResult("Non-recoverable Qpp", TransformationStatus.NON_RECOVERABLE);

	@Before
	public void setUp() throws IOException {
		successMultipartFile = new MockMultipartFile("Good file", new ByteArrayInputStream("Good file".getBytes()));
		errorMultipartFile = new MockMultipartFile("Error file", new ByteArrayInputStream("Error file".getBytes()));
		nonRecoverableMultipartFile = new MockMultipartFile("Non-recoverable file", new ByteArrayInputStream("Non-recoverable file".getBytes()));
	}

	@Test
	public void uploadQrdaFileSuccess() throws IOException {
		when(qrdaService.convertQrda3ToQpp(any(InputStream.class))).thenReturn(GOOD_RESULT);

		HttpServletResponse servletResponse = new MockHttpServletResponse();
		String qppResponse = objectUnderTest.uploadQrdaFile(successMultipartFile, servletResponse);

		assertThat("The response status is incorrect.", servletResponse.getStatus(), is(HttpStatus.CREATED.value()));
		assertThat("The QPP response body is incorrect.", qppResponse, is(GOOD_RESULT.getContent()));
	}

	@Test
	public void uploadQrdaFileError() throws IOException {
		when(qrdaService.convertQrda3ToQpp(any(InputStream.class))).thenReturn(ERROR_RESULT);

		HttpServletResponse servletResponse = new MockHttpServletResponse();
		String qppResponse = objectUnderTest.uploadQrdaFile(errorMultipartFile, servletResponse);

		assertThat("The response status is incorrect.", servletResponse.getStatus(), is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
		assertThat("The QPP response body is incorrect.", qppResponse, is(ERROR_RESULT.getContent()));
	}

	@Test
	public void uploadQrdaFileNonRecoverable() throws IOException {
		when(qrdaService.convertQrda3ToQpp(any(InputStream.class))).thenReturn(NON_RECOVERABLE_RESULT);

		HttpServletResponse servletResponse = new MockHttpServletResponse();
		String qppResponse = objectUnderTest.uploadQrdaFile(nonRecoverableMultipartFile, servletResponse);

		assertThat("The response status is incorrect.", servletResponse.getStatus(), is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
		assertThat("The QPP response body is incorrect.", qppResponse, is(NON_RECOVERABLE_RESULT.getContent()));
	}
}
