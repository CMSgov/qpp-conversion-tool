package gov.cms.qpp.conversion.api.services;


import cloud.localstack.LocalstackTestRunner;
import cloud.localstack.TestUtils;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import gov.cms.qpp.conversion.api.RestApiApplication;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = { StorageServiceImpl.class, RestApiApplication.class })
@EnableConfigurationProperties
@RunWith(LocalstackTestRunner.class)
@PropertySource("classpath:application.properties")
public class StorageServiceImplTest {
	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Autowired
	private StorageServiceImpl underTest;

	@Value("${submission.s3.bucket}")
	private String bucketName;

	@Before
	public void setup() throws IllegalAccessException, NoSuchFieldException {
		String bucketName = "test-bucket-https";
		TestUtils.disableSslCertChecking();

		AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
						LocalstackTestRunner.getEndpointS3(),
						LocalstackTestRunner.getDefaultRegion()))
				.withChunkedEncodingDisabled(true)
				.withPathStyleAccessEnabled(true).build();
		amazonS3Client.createBucket(bucketName);

		Field field = StorageServiceImpl.class.getDeclaredField("s3client");
		field.setAccessible(true); // Force to access the field
		field.set(underTest, amazonS3Client);
	}

	@Test
	public void testPut() throws ExecutionException, InterruptedException {
		CompletableFuture<PutObjectResult> result = underTest.store(
				"aSubmission", new ByteArrayInputStream("test file content".getBytes()));

		result.whenComplete((outcome, ex) -> {
			assertNotNull("No outcome returned", outcome);
			assertNotNull("No content", outcome.getContentMd5());
			System.out.println("Result content: ===============> " + outcome.getContentMd5());
		});
	}
}
