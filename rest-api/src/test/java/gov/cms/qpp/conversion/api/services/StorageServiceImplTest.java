package gov.cms.qpp.conversion.api.services;


import cloud.localstack.LocalstackTestRunner;
import cloud.localstack.TestUtils;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

@RunWith(LocalstackTestRunner.class)
public class StorageServiceImplTest {
	private static StorageServiceImpl underTest = new StorageServiceImpl();

	@BeforeClass
	public static void setup() throws IllegalAccessException, NoSuchFieldException {
		String bucketName = "test-bucket-https";
		TestUtils.disableSslCertChecking();

		AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
						LocalstackTestRunner.getEndpointS3(),
						LocalstackTestRunner.getDefaultRegion()))
				.withChunkedEncodingDisabled(true)
				.withPathStyleAccessEnabled(true).build();
		amazonS3Client.createBucket(bucketName);

		Field field = StorageServiceImpl.class.getDeclaredField("bucketName");
		field.setAccessible(true); // Force to access the field
		field.set(underTest, bucketName);

		field = StorageServiceImpl.class.getDeclaredField("s3client");
		field.setAccessible(true); // Force to access the field
		field.set(underTest, amazonS3Client);
	}

	@Test
	public void testPut() {
		CompletableFuture<PutObjectResult> result = underTest.store("aSubmission", new ByteArrayInputStream("test file content".getBytes()));

		Assert.assertNotNull(result);
	}
}
