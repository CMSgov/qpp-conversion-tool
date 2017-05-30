package gov.cms.qpp.conversion.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.TransformException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class ConversionHandler implements RequestHandler<S3Event, String> {

	@Override
	public String handleRequest(S3Event s3event, Context context) {
		try {
			S3EventNotificationRecord record = s3event.getRecords().get(0);
			String srcBucket = record.getS3().getBucket().getName();
			String srcKey = formatSourceKey(record);
			String filename = srcKey.replaceAll(".*/", "");

			AmazonS3 s3Client = getClient();
			S3Object s3Object = s3Client.getObject(new GetObjectRequest(srcBucket, srcKey));

			try (InputStream input = new NamedInputStream(srcKey, s3Object.getObjectContent())) {
				Converter converter = new Converter(input);
				JsonWrapper qpp = null;
				AllErrors errors = null;
				try {
					qpp = converter.transform();
				} catch(TransformException exception) {
					errors = exception.getDetails();
				}

				InputStream returnStream = null;
				if (qpp != null) {
					returnStream = qppToInputStream(qpp);
				} else if (errors != null) {
					returnStream = errorsToInputStream(errors);
				}

				String dstKey = "post-conversion/" + converter.getOutputFile(filename);
				ObjectMetadata meta = new ObjectMetadata();
				s3Client.putObject(srcBucket, dstKey, returnStream, meta);

				return "Ok";
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public AmazonS3 getClient() {
		return AmazonS3ClientBuilder.defaultClient(); // ignore coverage
	}

	protected String formatSourceKey(S3EventNotificationRecord record) throws UnsupportedEncodingException {
		String srcKey = record.getS3().getObject().getKey().replace('+', ' ');
		return URLDecoder.decode(srcKey, "UTF-8");
	}

	private InputStream qppToInputStream(JsonWrapper jsonWrapper) {
		return new ByteArrayInputStream(jsonWrapper.toString().getBytes());
	}

	private InputStream errorsToInputStream(AllErrors allErrors) {
		ObjectWriter jsonObjectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

		byte[] errors = new byte[0];
		try {
			errors = jsonObjectWriter.writeValueAsBytes(allErrors);
		} catch (JsonProcessingException exception) {
			errors = "{ \"exception\": \"JsonProcessingException\" }".getBytes();
		}
		return new ByteArrayInputStream(errors);
	}
}
