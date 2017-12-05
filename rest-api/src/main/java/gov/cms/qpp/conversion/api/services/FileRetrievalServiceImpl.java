package gov.cms.qpp.conversion.api.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.SSECustomerKey;
import com.google.common.base.Strings;
import gov.cms.qpp.conversion.api.model.Constants;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class FileRetrievalServiceImpl extends InOrderActionService<GetObjectRequest, InputStream>
		implements  FileRetrievalService {

	private static final Logger API_LOG = LoggerFactory.getLogger(Constants.API_LOG);

	@Autowired
	private AmazonS3 amazonS3Client;

	@Autowired
	private Environment environment;

	@Override
	public CompletableFuture<InputStream> getFileById(String fileId) {
		final String bucketName = environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE);
		final String kmsKey = environment.getProperty(Constants.KMS_KEY_ENV_VARIABLE);
		if (Strings.isNullOrEmpty(bucketName) || Strings.isNullOrEmpty(kmsKey)) {
			API_LOG.warn("No bucket name is specified or no KMS key specified.");
			return CompletableFuture.completedFuture(null);
		}

		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileId)
				.withSSECustomerKey(new SSECustomerKey(kmsKey));

		return actOnItem(getObjectRequest);
	}

	@Override
	protected InputStream asynchronousAction(GetObjectRequest objectToActOn) {
		S3Object s3Object = amazonS3Client.getObject(objectToActOn);

		API_LOG.info("Successfully retrieved the file from S3 bucket {}", objectToActOn.getBucketName());

		return s3Object.getObjectContent();
	}
}
