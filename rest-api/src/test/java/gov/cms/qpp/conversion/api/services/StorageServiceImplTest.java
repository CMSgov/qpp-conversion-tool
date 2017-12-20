package gov.cms.qpp.conversion.api.services;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.test.MockitoExtension;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;


@ExtendWith(MockitoExtension.class)
class StorageServiceImplTest {

	@InjectMocks
	private StorageServiceImpl underTest;

	@Mock
	private TransferManager transferManager;

	@Mock
	private Upload upload;

	@Mock
	private TaskExecutor taskExecutor;

	@Mock
	private AmazonS3 amazonS3Client;

	@Mock
	private Environment environment;

	private String bucketName = "test-bucket";
	private String ksmKey = "test-key";
	private UploadResult result;

	@BeforeEach
	void before() {
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));

		result = new UploadResult();
		result.setKey("meep");
		when(transferManager.upload(any(PutObjectRequest.class))).thenReturn(upload);
	}

	@Test
	void testPut() throws TimeoutException, InterruptedException {
		when(upload.waitForUploadResult()).thenReturn(result);
		Mockito.when(environment.getProperty(eq(Constants.BUCKET_NAME_ENV_VARIABLE))).thenReturn(bucketName);
		Mockito.when(environment.getProperty(eq(Constants.KMS_KEY_ENV_VARIABLE))).thenReturn(ksmKey);

		assertThat(storeFile()).isNotNull();
		verify(transferManager, times(1)).upload(any(PutObjectRequest.class));
	}

	@Test
	void testPutFail() throws TimeoutException, InterruptedException {
		when(upload.waitForUploadResult()).thenThrow(InterruptedException.class);
		Mockito.when(environment.getProperty(eq(Constants.BUCKET_NAME_ENV_VARIABLE))).thenReturn(bucketName);
		Mockito.when(environment.getProperty(eq(Constants.KMS_KEY_ENV_VARIABLE))).thenReturn(ksmKey);

		assertThrows(CompletionException.class, this::storeFile);
	}

	@Test
	void testPutRecoverableFailure() throws TimeoutException, InterruptedException {
		when(upload.waitForUploadResult()).thenThrow(Exception.class).thenReturn(result);
		Mockito.when(environment.getProperty(eq(Constants.BUCKET_NAME_ENV_VARIABLE))).thenReturn(bucketName);
		Mockito.when(environment.getProperty(eq(Constants.KMS_KEY_ENV_VARIABLE))).thenReturn(ksmKey);

		assertThat(storeFile()).isNotNull();
		verify(transferManager, times(2)).upload(any(PutObjectRequest.class));
	}

	@Test
	void testPutNoBucket() throws TimeoutException, InterruptedException {
		Mockito.when(environment.getProperty(eq(Constants.BUCKET_NAME_ENV_VARIABLE))).thenReturn("");

		assertThat(storeFile()).isEmpty();
		verify(transferManager, times(0)).upload(any(PutObjectRequest.class));
	}

	@Test
	void testPutNoBucketSpecified() throws TimeoutException, InterruptedException {
		assertThat(storeFile()).isEmpty();
		verify(transferManager, times(0)).upload(any(PutObjectRequest.class));
	}

	@Test
	void testNotSpecifyKmsKey() {
		Mockito.when(environment.getProperty(eq(Constants.BUCKET_NAME_ENV_VARIABLE))).thenReturn(bucketName);

		String s3ObjectId = storeFile();
		assertThat(s3ObjectId).isEqualTo("");
		verify(transferManager, times(0)).upload(any(PutObjectRequest.class));
	}

	@Test
	void noBucket() throws ExecutionException, InterruptedException {
		Mockito.when(environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE)).thenReturn(null);
		InputStream inStream = underTest.getFileByLocationId("meep");

		assertThat(inStream).isNull();
	}

	@Test
	void envVariablesPresent() {
		S3Object s3ObjectMock = mock(S3Object.class);
		s3ObjectMock.setObjectContent(new ByteArrayInputStream("1234".getBytes()));
		Mockito.when(amazonS3Client.getObject(any(GetObjectRequest.class))).thenReturn(s3ObjectMock);
		Mockito.when(environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE)).thenReturn("meep");
		underTest.getFileByLocationId("meep");

		verify(s3ObjectMock, times(1)).getObjectContent();
	}

	private String storeFile() {
		CompletableFuture<String> storeResult = underTest.store(
				"submission", new ByteArrayInputStream("test file content".getBytes()));
		return storeResult.join();
	}
}
