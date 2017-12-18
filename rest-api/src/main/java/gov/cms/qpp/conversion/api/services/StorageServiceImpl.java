package gov.cms.qpp.conversion.api.services;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.common.base.Strings;
import gov.cms.qpp.conversion.api.exceptions.UncheckedInterruptedException;
import gov.cms.qpp.conversion.api.model.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Used to store an {@link InputStream} in S3.
 */
@Service
public class StorageServiceImpl extends AnyOrderActionService<PutObjectRequest, String>
		implements StorageService {
	private static final Logger API_LOG = LoggerFactory.getLogger(Constants.API_LOG);

	@Autowired
	private TransferManager s3TransferManager;

	@Autowired
	private Environment environment;

	/**
	 * Stores the {@link InputStream} as an object in the S3 bucket.
	 *
	 * @param keyName The requested key name for the object.
	 * @param inStream The {@link InputStream} to write out to an object in S3.
	 * @return A {@link CompletableFuture} that will eventually contain the S3 object key.
	 */
	@Override
	public CompletableFuture<String> store(String keyName, InputStream inStream) {
		final String bucketName = environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE);
		final String kmsKey = environment.getProperty(Constants.KMS_KEY_ENV_VARIABLE);
		if (Strings.isNullOrEmpty(bucketName) || Strings.isNullOrEmpty(kmsKey)) {
			API_LOG.warn("No bucket name is specified or no KMS key specified.");
			return CompletableFuture.completedFuture("");
		}

		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, inStream, new ObjectMetadata())
			.withSSEAwsKeyManagementParams(new SSEAwsKeyManagementParams(kmsKey));

		API_LOG.info("Writing object {} to S3 bucket {}", keyName, bucketName);
		return actOnItem(putObjectRequest);
	}

	/**
	 * Uses the {@link TransferManager} to upload a file.
	 *
	 * @param objectToActOn The put request.
	 * @return The object key in the bucket.
	 */
	@Override
	protected String asynchronousAction(PutObjectRequest objectToActOn) {
		String returnValue;

		try {
			Upload upload = s3TransferManager.upload(objectToActOn);
			returnValue = upload.waitForUploadResult().getKey();
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new UncheckedInterruptedException(exception);
		}

		API_LOG.info("Successfully wrote object {} to S3 bucket {}", returnValue, objectToActOn.getBucketName());

		return returnValue;
	}
}
