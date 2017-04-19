package gov.cms.qpp.conversion.aws;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import io.findify.s3mock.S3Mock;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class ConversionHandlerTest {

	private static final Path S3_PATH = Paths.get("s3");
	private static final String BUCKET = "qrda-conversion";

	private static S3Mock server;
	private static S3Event goodInput;
	private static S3Event badInput;
	private static S3Event uglyInput;
	private static AmazonS3 client;

	@BeforeClass
	public static void createInput() throws IOException {
		goodInput = TestUtils.parse("s3-event.put.json", S3Event.class);
		badInput = TestUtils.parse("s3-event-bad.put.json", S3Event.class);
		uglyInput = TestUtils.parse("s3-event-error.put.json", S3Event.class);
		server = S3Mock.create(8001, S3_PATH.toString());
		server.start();

		Path good = Paths.get("../qrda-files/valid-QRDA-III.xml");
		Path bad = Paths.get("../qrda-files/not-a-QDRA-III-file.xml");
		Path ugly = Paths.get("../qrda-files/QRDA-III-without-required-measure.xml");

		client = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AmazonS3ClientBuilder.EndpointConfiguration("http://127.0.0.1:8001", ""))
				.withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
				.build();
		client.createBucket(BUCKET);
		client.putObject("qrda-conversion", "pre-conversion/valid-QRDA-III.xml", good.toFile());
		client.putObject("qrda-conversion", "pre-conversion/not-a-QDRA-III-file.xml", bad.toFile());
		client.putObject("qrda-conversion", "pre-conversion/QRDA-III-without-required-measure.xml", ugly.toFile());
	}

	@AfterClass
	public static void cleanup() throws IOException {
		client.deleteBucket(BUCKET);
		server.stop();
		Files.delete(S3_PATH);
	}

	@Test
	public void testConversionHandler() {
		ConversionHandler handler = new ConversionHandler();
		ConversionHandler spyHandler = spy(handler);
		doReturn(client).when(spyHandler).getClient();

		spyHandler.handleRequest(goodInput, new TestContext());

		S3Object converted = client.getObject("qrda-conversion",
				"post-conversion/valid-QRDA-III.qpp.json");
		assertNotNull("there's a converted file", converted);
	}

	@Test(expected = AmazonS3Exception.class)
	public void testConversionHandlerOnInvalidFileNoConversion() {
		ConversionHandler handler = new ConversionHandler();
		ConversionHandler spyHandler = spy(handler);
		doReturn(client).when(spyHandler).getClient();

		spyHandler.handleRequest(badInput, new TestContext());

		S3Object converted = client.getObject("qrda-conversion",
				"post-conversion/not-a-QDRA-III-file.qpp.json");
		fail("Should not find a converted json file.");
	}

	@Test(expected = AmazonS3Exception.class)
	public void testConversionHandlerOnInvalidFileNoError() {
		ConversionHandler handler = new ConversionHandler();
		ConversionHandler spyHandler = spy(handler);
		doReturn(client).when(spyHandler).getClient();

		spyHandler.handleRequest(badInput, new TestContext());

		S3Object error = client.getObject("qrda-conversion",
				"post-conversion/not-a-QDRA-III-file.err.xml");
		fail("Should not find an error output file.");
	}

	@Test
	public void testConversionHandlerErrantFile() {
		ConversionHandler handler = new ConversionHandler();
		ConversionHandler spyHandler = spy(handler);
		doReturn(client).when(spyHandler).getClient();

		spyHandler.handleRequest(uglyInput, new TestContext());

		S3Object error = client.getObject("qrda-conversion",
						"post-conversion/QRDA-III-without-required-measure.err.txt");
		assertNotNull("there's an error file", error);
	}

	@Test(expected = RuntimeException.class)
	public void testFormatSourceKeyException() throws UnsupportedEncodingException {
		ConversionHandler handler = new ConversionHandler();
		ConversionHandler spyHandler = spy(handler);

		doThrow(new UnsupportedEncodingException()).when(spyHandler)
				.formatSourceKey(any(S3EventNotification.S3EventNotificationRecord.class));

	    spyHandler.handleRequest(goodInput, new TestContext());
	    fail("Should have thrown a RuntimeException");
	}
}