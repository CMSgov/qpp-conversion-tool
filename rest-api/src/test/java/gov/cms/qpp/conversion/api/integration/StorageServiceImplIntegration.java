package gov.cms.qpp.conversion.api.integration;


import cloud.localstack.LocalstackTestRunner;
import cloud.localstack.TestUtils;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import gov.cms.qpp.conversion.api.config.S3Config;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.services.StorageServiceImpl;
import net.jodah.concurrentunit.Waiter;
import org.apache.commons.io.IOUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(LocalstackTestRunner.class)
@PropertySource("classpath:application.properties")
public class StorageServiceImplIntegration {

	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private Environment environment;

	@Inject
	private StorageServiceImpl underTest;

	private String bucketName = "test-bucket";
	private String kmsKey = "test-key";
	private AmazonS3 amazonS3Client;

	@Before
	public void setup() throws Exception {
		Assume.assumeTrue(System.getProperty("skip.long") == null);
		TestUtils.disableSslCertChecking();

		amazonS3Client = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
						LocalstackTestRunner.getEndpointS3(),
						LocalstackTestRunner.getDefaultRegion()))
				.withChunkedEncodingDisabled(true)
				.withPathStyleAccessEnabled(true).build();
		CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName)
			.withCannedAcl(CannedAccessControlList.PublicReadWrite);

		amazonS3Client.createBucket(createBucketRequest);

		S3Config config = new S3Config();

		Field field = StorageServiceImpl.class.getDeclaredField("s3TransferManager");
		field.setAccessible(true);
		field.set(underTest, config.s3TransferManager(amazonS3Client));

		field = StorageServiceImpl.class.getDeclaredField("environment");
		field.setAccessible(true);
		field.set(underTest, environment);
	}

	@Test
	public void testPut() throws TimeoutException {
		final String content = "test file content";
		final String key = "submission";
		final Waiter waiter = new Waiter();

		when(environment.getProperty(eq(Constants.BUCKET_NAME_ENV_VARIABLE))).thenReturn(bucketName);
		when(environment.getProperty(eq(Constants.KMS_KEY_ENV_VARIABLE))).thenReturn(kmsKey);

		CompletableFuture<String> result = underTest.store(
				key, new ByteArrayInputStream(content.getBytes()), content.getBytes().length);

		result.whenComplete((outcome, ex) -> {
			waiter.assertEquals(content, getObjectContent(key));
			waiter.resume();
		});

		waiter.await(5000);
	}

	private String getObjectContent(String key) {
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
		S3Object stored = amazonS3Client.getObject(getObjectRequest);

		try {
			return IOUtils.toString(stored.getObjectContent(), "UTF-8");
		} catch (IOException ioe) {
			fail("should have content");
		}
		return "";
	}
}