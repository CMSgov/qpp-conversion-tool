package gov.cms.qpp.conversion.api.services.internal;


import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;

import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.util.MeasuredInputStreamSupplier;
import gov.cms.qpp.test.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StorageServiceImplTest {

	private static final byte[] TEST_CONTENT_BYTES = "test file content".getBytes();

	@InjectMocks
	private StorageServiceImpl underTest;

	@Mock
	private TaskExecutor taskExecutor;

	@Mock
	private S3Client s3Client;

	@Mock
	private Environment environment;

	private String bucketName = "test-bucket";
	private String ksmKey = "test-key";

	@BeforeEach
	void before() {
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));

		when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
			.thenReturn(PutObjectResponse.builder().build());
	}

	@Test
	void testPut() {
		when(environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE)).thenReturn(bucketName);
		when(environment.getProperty(Constants.KMS_KEY_ENV_VARIABLE)).thenReturn(ksmKey);

		assertThat(storeFile()).isNotNull();
		ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
		verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));
		assertThat(requestCaptor.getValue().contentLength()).isEqualTo((long) TEST_CONTENT_BYTES.length);
		assertThat(requestCaptor.getValue().serverSideEncryption()).isEqualTo(ServerSideEncryption.AWS_KMS);
		assertThat(requestCaptor.getValue().ssekmsKeyId()).isEqualTo(ksmKey);
	}

	@Test
	void testPutRecoverableFailure() {
		when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
			.thenThrow(RuntimeException.class)
			.thenReturn(PutObjectResponse.builder().build());
		when(environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE)).thenReturn(bucketName);
		when(environment.getProperty(Constants.KMS_KEY_ENV_VARIABLE)).thenReturn(ksmKey);

		assertThat(storeFile()).isNotNull();
		verify(s3Client, times(2)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
	}

	@Test
	void testPutNoBucket() {
		when(environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE)).thenReturn("");

		assertThat(storeFile()).isEmpty();
		verify(s3Client, times(0)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
	}

	@Test
	void testPutNoBucketSpecified() {
		assertThat(storeFile()).isEmpty();
		verify(s3Client, times(0)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
	}

	@Test
	void testNotSpecifyKmsKey() {
		when(environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE)).thenReturn(bucketName);

		String s3ObjectId = storeFile();
		assertThat(s3ObjectId).isEqualTo("");
		verify(s3Client, times(0)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
	}

	@Test
	void noBucket() {
		when(environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE)).thenReturn(null);
		InputStream inStream = underTest.getFileByLocationId("meep");

		assertThat(inStream).isNull();
		verify(s3Client, times(0)).getObject(any(GetObjectRequest.class));
	}

	@Test
	void envVariablesPresent() {
		ResponseInputStream<GetObjectResponse> response = new ResponseInputStream<>(
			GetObjectResponse.builder().build(), AbortableInputStream.create(new ByteArrayInputStream(new byte[0])));
		when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(response);
		when(environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE)).thenReturn("meep");
		InputStream returned = underTest.getFileByLocationId("meep");

		assertThat(returned).isSameInstanceAs(response);
		ArgumentCaptor<GetObjectRequest> requestCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
		verify(s3Client).getObject(requestCaptor.capture());
		assertThat(requestCaptor.getValue().bucket()).isEqualTo("meep");
		assertThat(requestCaptor.getValue().key()).isEqualTo("meep");
	}

	private String storeFile() {
		MeasuredInputStreamSupplier source = MeasuredInputStreamSupplier.terminallyTransformInputStream(new ByteArrayInputStream(TEST_CONTENT_BYTES));
		CompletableFuture<String> storeResult = underTest.store(
				"submission", source, source.size());
		return storeResult.join();
	}
	
	@Test
	void test_getCpcPlusValidationFile_noBucket() {
		when(environment.getProperty(Constants.CPC_PLUS_BUCKET_NAME_VARIABLE)).thenReturn(null);
		InputStream ins = underTest.getCpcPlusValidationFile();
		assertThat(ins).isNull();
		
		when(environment.getProperty(Constants.CPC_PLUS_BUCKET_NAME_VARIABLE)).thenReturn("");
		ins = underTest.getCpcPlusValidationFile();
		assertThat(ins).isNull();
		
		when(environment.getProperty(Constants.CPC_PLUS_BUCKET_NAME_VARIABLE)).thenReturn("   ");
		ins = underTest.getCpcPlusValidationFile();
		assertThat(ins).isNull();
	}
	
	@Test
	void test_getCpcPlusValidationFile_noFileKey() {
		when(environment.getProperty(Constants.CPC_PLUS_BUCKET_NAME_VARIABLE)).thenReturn("Mock_Bucket");
		
		when(environment.getProperty(Constants.CPC_PLUS_FILENAME_VARIABLE)).thenReturn(null);
		InputStream ins = underTest.getCpcPlusValidationFile();
		assertThat(ins).isNull();
		
		when(environment.getProperty(Constants.CPC_PLUS_FILENAME_VARIABLE)).thenReturn("");
		ins = underTest.getCpcPlusValidationFile();
		assertThat(ins).isNull();
	}
	
	@Test
	void test_getCpcPlusValidationFile_blankKeyIsPassedToS3() {
		ResponseInputStream<GetObjectResponse> response = new ResponseInputStream<>(
			GetObjectResponse.builder().build(), AbortableInputStream.create(new ByteArrayInputStream(new byte[0])));
		when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(response);
		when(environment.getProperty(Constants.CPC_PLUS_BUCKET_NAME_VARIABLE)).thenReturn("Mock_Bucket");
		
		when(environment.getProperty(Constants.CPC_PLUS_FILENAME_VARIABLE)).thenReturn("   ");
		assertThat(underTest.getCpcPlusValidationFile()).isSameInstanceAs(response);
		ArgumentCaptor<GetObjectRequest> requestCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
		verify(s3Client).getObject(requestCaptor.capture());
		assertThat(requestCaptor.getValue().key()).isEqualTo("   ");
	}

	@Test
	void test_getCpcPlusValidationFile() {
		ResponseInputStream<GetObjectResponse> response = new ResponseInputStream<>(
			GetObjectResponse.builder().build(), AbortableInputStream.create(new ByteArrayInputStream(new byte[0])));
		when(environment.getProperty(Constants.CPC_PLUS_BUCKET_NAME_VARIABLE)).thenReturn("Mock_Bucket");
		when(environment.getProperty(Constants.CPC_PLUS_FILENAME_VARIABLE)).thenReturn("Mock_Filename");
		when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(response);
		
		assertThat(underTest.getCpcPlusValidationFile()).isSameInstanceAs(response);
	}
}
