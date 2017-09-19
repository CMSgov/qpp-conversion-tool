package gov.cms.qpp.conversion.api.helper;


import cloud.localstack.LocalstackTestRunner;
import cloud.localstack.TestUtils;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.transfer.Upload;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;

@RunWith(LocalstackTestRunner.class)
public class S3HelperTest {
	private AmazonS3 amazonS3Client;
	private String bucketName = "test-bucket-https";


	@Before
	public void setup() {
		TestUtils.disableSslCertChecking();

		amazonS3Client = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
						LocalstackTestRunner.getEndpointS3(),
						LocalstackTestRunner.getDefaultRegion()))
				.withChunkedEncodingDisabled(true)
				.withPathStyleAccessEnabled(true).build();
		//TODO: think about how bucketName will be provided
		amazonS3Client.createBucket(bucketName);
	}

	@Test
	public void testS3Helper() {

		S3Helper helper = new S3Helper();
		helper.setS3Client(amazonS3Client);
		Upload result = helper.putObject("key1", new ByteArrayInputStream("test file content".getBytes()));

		Assert.assertNotNull(result);
	}

}