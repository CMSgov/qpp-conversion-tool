package gov.cms.qpp.conversion.aws;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import gov.cms.qpp.conversion.Converter;

public class ConversionHandler implements RequestHandler<S3Event, String> {

	private AmazonS3 client;

	@Override
	public String handleRequest(S3Event s3event, Context context) {
		try {
			S3EventNotificationRecord record = s3event.getRecords().get(0);
			String srcBucket = record.getS3().getBucket().getName();
			String srcKey = formatSourceKey(record);
			String filename = srcKey.replaceAll(".*/", "");

			AmazonS3 s3Client = getClient();
			System.out.println("scrKey: " + srcKey);
			S3Object s3Object = s3Client.getObject(new GetObjectRequest(srcBucket, srcKey));

			Converter converter = new Converter(s3Object.getObjectContent());
			Integer status = converter.transform();

			if (status < 2) {
				String dstKey = "post-conversion/" + converter.getOutputFile(filename);
				ObjectMetadata meta = new ObjectMetadata();
				s3Client.putObject(srcBucket, dstKey, converter.getConversionResult(), meta);
			}

			return "Ok";
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public AmazonS3 getClient() {
		if (this.client != null) {
			return this.client;
		}

		synchronized (this) {
			if (this.client != null) {
				return this.client;
			}

			return this.client = AmazonS3ClientBuilder.defaultClient();
		}
	}

	public void setClient(AmazonS3 client) {
		synchronized (this) {
			if (this.client != null) {
				throw new IllegalStateException(this + " already has a client, " + this.client);
			}

			this.client = client;
		}
	}

	private String formatSourceKey(S3EventNotificationRecord record) throws UnsupportedEncodingException {
		String srcKey = record.getS3().getObject().getKey().replace('+', ' ');
		return URLDecoder.decode(srcKey, "UTF-8");
	}
}
