package gov.cms.qpp.conversion.api.services;


import cloud.localstack.LocalstackTestRunner;
import cloud.localstack.TestUtils;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import gov.cms.qpp.conversion.api.RestApiApplication;
import net.jodah.concurrentunit.Waiter;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.fail;

@SpringBootTest(classes = { StorageServiceImpl.class, RestApiApplication.class })
@RunWith(LocalstackTestRunner.class)
@PropertySource("classpath:application.properties")
public class StorageServiceImplIntegration {
	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Autowired
	private StorageServiceImpl underTest;

	@Value("${submission.s3.bucket}")
	private String bucketName;

	private AmazonS3 amazonS3Client;
	private Field s3clientField;

	@Before
	public void setup() throws IllegalAccessException, NoSuchFieldException {
		TestUtils.disableSslCertChecking();

		amazonS3Client = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
						LocalstackTestRunner.getEndpointS3(),
						LocalstackTestRunner.getDefaultRegion()))
				.withChunkedEncodingDisabled(true)
				.withPathStyleAccessEnabled(true).build();
		amazonS3Client.createBucket(bucketName);

		s3clientField = StorageServiceImpl.class.getDeclaredField("s3client");
		s3clientField.setAccessible(true); // Force to access the field
		s3clientField.set(underTest, amazonS3Client);
	}

	@Test
	public void testPut() throws TimeoutException {
		final String content = "test file content";
		final String key = "submission";
		final Waiter waiter = new Waiter();

		CompletableFuture<String> result = underTest.store(
				key, new ByteArrayInputStream(content.getBytes()));

		result.whenComplete((outcome, ex) -> {
			System.out.println("outcome: " + outcome);
			waiter.assertEquals(content, getObjectContent(key));
			waiter.resume();
		});

		waiter.await(5000);
	}

	private String getObjectContent(String key) {
		S3Object stored = amazonS3Client.getObject(bucketName, key);

		try {
			return IOUtils.toString(stored.getObjectContent(), "UTF-8");
		} catch (IOException ioe) {
			fail("should have content");
		}
		return "";
	}
}
