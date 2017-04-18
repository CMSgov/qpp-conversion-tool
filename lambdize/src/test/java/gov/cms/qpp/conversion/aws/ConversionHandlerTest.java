package gov.cms.qpp.conversion.aws;

import java.io.IOException;

import com.amazonaws.client.AwsSyncClientParams;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;


/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class ConversionHandlerTest {

	private static S3Event input;

	@BeforeClass
	public static void createInput() throws IOException {
		input = TestUtils.parse("s3-event.put.json", S3Event.class);
	}

	private Context createContext() {
		TestContext ctx = new TestContext();

		// TODO: customize your context here if needed.
		ctx.setFunctionName("Your Function Name");

		return ctx;
	}

	@Test
	public void testConversionHandler() {
		//return AmazonS3ClientBuilder.standard().build();
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();

		ConversionHandler handler = mock(ConversionHandler.class, CALLS_REAL_METHODS);
		when(handler.getClient()).thenReturn(s3Client);

		String output = handler.handleRequest(input, createContext());

		// TODO: validate output here if needed.
		if (output != null) {
			System.out.println(output.toString());
		}
	}
}