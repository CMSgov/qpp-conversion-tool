package gov.cms.qpp.conversion.api.services;


import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.task.TaskExecutor;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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

	@Before
	public void before() {
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));
	}

	@Test
	public void testPut() throws TimeoutException, InterruptedException {
		UploadResult result = new UploadResult();
		result.setKey("meep");
		when(upload.waitForUploadResult()).thenReturn(result);
		when(transferManager.upload(any(PutObjectRequest.class))).thenReturn(upload);

		CompletableFuture<String> storeResult = underTest.store(
				"submission", new ByteArrayInputStream("test file content".getBytes()));

		String objKey = storeResult.join();
		assertNotNull("key should not be null", objKey);
		verify(transferManager, times(1)).upload(any(PutObjectRequest.class));
	}

	@Test
	public void testPutHiccup() throws TimeoutException, InterruptedException {
		UploadResult result = new UploadResult();
		result.setKey("meep");
		when(upload.waitForUploadResult()).thenThrow(InterruptedException.class).thenReturn(result);
		when(transferManager.upload(any(PutObjectRequest.class))).thenReturn(upload);

		CompletableFuture<String> storeResult = underTest.store(
				"submission", new ByteArrayInputStream("test file content".getBytes()));

		String objKey = storeResult.join();
		assertNotNull("key should not be null", objKey);
		verify(transferManager, times(2)).upload(any(PutObjectRequest.class));
	}
}
