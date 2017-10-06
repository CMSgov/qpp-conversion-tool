package gov.cms.qpp.conversion.api.services;


import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import gov.cms.qpp.conversion.api.model.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(MockitoJUnitRunner.class)
public class StorageServiceImplTest {

	@InjectMocks
	private StorageServiceImpl underTest;

	@Mock
	private TransferManager transferManager;

	@Mock
	private Upload upload;

	@Mock
	private TaskExecutor taskExecutor;

	@Mock
	private Environment environment;

	private String bucketName = "test-bucket";
	private UploadResult result;

	@Before
	public void before() {
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
	public void testPut() throws TimeoutException, InterruptedException {
		when(upload.waitForUploadResult()).thenReturn(result);
		Mockito.when(environment.getProperty(eq(Constants.BUCKET_NAME_ENV_VARIABLE))).thenReturn(bucketName);

		assertNotNull("key should not be null", storeFile());
		verify(transferManager, times(1)).upload(any(PutObjectRequest.class));
	}

	@Test(expected = CompletionException.class)
	public void testPutFail() throws TimeoutException, InterruptedException {
		when(upload.waitForUploadResult()).thenThrow(InterruptedException.class);
		Mockito.when(environment.getProperty(eq(Constants.BUCKET_NAME_ENV_VARIABLE))).thenReturn(bucketName);

		storeFile();
	}

	@Test
	public void testPutRecoverableFailure() throws TimeoutException, InterruptedException {
		when(upload.waitForUploadResult()).thenThrow(Exception.class).thenReturn(result);
		Mockito.when(environment.getProperty(eq(Constants.BUCKET_NAME_ENV_VARIABLE))).thenReturn(bucketName);

		assertNotNull("key should not be null", storeFile());
		verify(transferManager, times(2)).upload(any(PutObjectRequest.class));
	}

	@Test
	public void testPutNoBucket() throws TimeoutException, InterruptedException {
		Mockito.when(environment.getProperty(eq(Constants.BUCKET_NAME_ENV_VARIABLE))).thenReturn("");

		assertEquals("key should be empty", "", storeFile());
		verify(transferManager, times(0)).upload(any(PutObjectRequest.class));
	}

	@Test
	public void testPutNoBucketSpecified() throws TimeoutException, InterruptedException {
		assertEquals("key should be empty", "", storeFile());
		verify(transferManager, times(0)).upload(any(PutObjectRequest.class));
	}

	private String storeFile() {
		CompletableFuture<String> storeResult = underTest.store(
				"submission", new ByteArrayInputStream("test file content".getBytes()));
		return storeResult.join();
	}
}
