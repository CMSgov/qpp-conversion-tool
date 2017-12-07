package gov.cms.qpp.conversion.api.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.test.MockitoExtension;
import java.io.ByteArrayInputStream;
import javassist.bytecode.ByteArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileRetrievalServiceImplTest {

	@InjectMocks
	private FileRetrievalServiceImpl underTest;

	@Mock
	private Environment environment;

	@Mock
	private AmazonS3 amazonS3Client;

	@Mock
	private DbService dbService;

	@Mock
	private TaskExecutor taskExecutor;

	@BeforeEach
	void before() {
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));
	}

	@Test
	void noBucket() throws ExecutionException, InterruptedException {
		when(environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE)).thenReturn(null);
		CompletableFuture<InputStream> inStream = underTest.getFileById("meep");

		assertThat(inStream.get()).isNull();
	}

	@Test
	void envVariablesPresent() {
		S3Object s3ObjectMock = mock(S3Object.class);
		s3ObjectMock.setObjectContent(new ByteArrayInputStream("1234".getBytes()));
		when(amazonS3Client.getObject(any(GetObjectRequest.class))).thenReturn(s3ObjectMock);
		when(environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE)).thenReturn("meep");
		when(environment.getProperty(Constants.KMS_KEY_ENV_VARIABLE)).thenReturn("mawp");
		when(dbService.getFileSubmissionLocationId(anyString())).thenReturn("meep");
		underTest.getFileById("meep").join();

		verify(dbService, times(1)).getFileSubmissionLocationId(anyString());

		verify(s3ObjectMock, times(1)).getObjectContent();
	}
}
