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
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;


public class ConversionHandler implements
		RequestHandler<S3Event, String> {
	public String handleRequest(S3Event s3event, Context context) {
		try {
			S3EventNotificationRecord record = s3event.getRecords().get(0);
			String srcBucket = record.getS3().getBucket().getName();
			// Object key may have spaces or unicode non-ASCII characters.
			String srcKey = record.getS3().getObject().getKey()
					.replace('+', ' ');
			srcKey = URLDecoder.decode(srcKey, "UTF-8");
			String dstBucket = srcBucket + "-destination";
			String dstKey = "converted-" + srcKey;
			// Not sure if we need to pass credentials here
			// we can hangout and discuss this
			//BasicAWSCredentials creds = new BasicAWSCredentials("access_key", "secret_key");
			AmazonS3 s3Client = AmazonS3ClientBuilder
					.standard()
					//.withCredentials(new AWSStaticCredentialsProvider(creds))
					.build();
			S3Object s3Object = s3Client.getObject(
					new GetObjectRequest(srcBucket, srcKey));
			InputStream objectData = s3Object.getObjectContent();
			// Perform conversion here after Converter is refactored to
			// accept and produce InputStreams
			//Converter converter = new Converter(objectData);
			//converter.transform();
			ObjectMetadata meta = new ObjectMetadata();
			// Uploading to S3 destination bucket
			System.out.println("Writing to: " + dstBucket + "/" + dstKey);
			s3Client.putObject(dstBucket, dstKey, objectData, meta);
			System.out.println("Successfully converted " + srcBucket + "/"
					+ srcKey + " and uploaded to " + dstBucket + "/" + dstKey);
			return "Ok";
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
