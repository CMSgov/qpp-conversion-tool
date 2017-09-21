package gov.cms.qpp.conversion.api.services;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class StorageServiceImpl extends InOrderAsyncActionService<PutObjectRequest, PutObjectResult>
		implements StorageService {
	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	@Autowired
	private AmazonS3 s3client;

	@Value("${submission.s3.bucket}")
	private String bucketName;

	@Override
	public CompletableFuture<PutObjectResult> store(String keyName, InputStream inStream) {
		return actOnItem(new PutObjectRequest(bucketName, keyName, inStream, new ObjectMetadata()));
	}

	@Override
	protected PutObjectResult asynchronousAction(PutObjectRequest objectToActOn) {
		PutObjectResult returnValue = null;

		try {
			returnValue = s3client.putObject(objectToActOn);
		} catch (AmazonServiceException ase) {
			API_LOG.error("Caught an AmazonServiceException: " + ase.getMessage(), ase);
		} catch (AmazonClientException ace) {
			API_LOG.error("Caught an AmazonClientException: " + ace.getMessage(), ace);
		}

		return returnValue;
	}
}
