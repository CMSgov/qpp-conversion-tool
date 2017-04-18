package gov.cms.qpp.conversion.aws;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.amazonaws.services.s3.model.S3Object;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import io.findify.s3mock.S3Mock;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class ConversionHandlerTest {

	private static S3Event input;
	private static AmazonS3 client;
	private static S3Mock api;

	@BeforeClass
	public static void createInput() throws IOException {
		input = TestUtils.parse("s3-event.put.json", S3Event.class);
		api = S3Mock.create(8001, "/tmp/s3");
		api.start();

		Path path = Paths.get("src/test/resources/valid-QRDA-III.xml");

		client = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AmazonS3ClientBuilder.EndpointConfiguration("http://127.0.0.1:8001", ""))
				.withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
				.build();
		client.createBucket("qrda-conversion");
		client.putObject("qrda-conversion", "pre-conversion/valid-QRDA-III.xml", path.toFile());
	}

	@AfterClass
	public static void cleanUp(){
		// Delete that --> /tmp/s3
		api.stop();
	}


	private Context createContext() {
		TestContext ctx = new TestContext();

		// TODO: customize your context here if needed.
		ctx.setFunctionName("Your Function Name");

		return ctx;
	}

	@Test
	public void testConversionHandler() {
		ConversionHandler handler = new ConversionHandler();
		handler.setClient(client);
		
		String output = handler.handleRequest(input, createContext());

		S3Object converted = client.getObject("qrda-conversion", "post-conversion/valid-QRDA-III.qpp.json");
		assertNotNull("there's a converted file", converted);
	}
}