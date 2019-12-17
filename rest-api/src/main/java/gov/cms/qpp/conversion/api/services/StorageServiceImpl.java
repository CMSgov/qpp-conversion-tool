package gov.cms.qpp.conversion.api.services;

import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import gov.cms.qpp.conversion.api.exceptions.UncheckedInterruptedException;
import gov.cms.qpp.conversion.api.model.Constants;

/**
 * Used to store an {@link InputStream} in S3.
 */
@Service
public class StorageServiceImpl extends AnyOrderActionService<Supplier<PutObjectRequest>, String>
		implements StorageService {

	private static final Logger API_LOG = LoggerFactory.getLogger(StorageServiceImpl.class);

	private final TransferManager s3TransferManager;
	private final Environment environment;
	private final AmazonS3 amazonS3;

	public StorageServiceImpl(TaskExecutor taskExecutor, TransferManager s3TransferManager,
			Environment environment, AmazonS3 amazonS3) {
		super(taskExecutor);

		Objects.requireNonNull(s3TransferManager, "s3TransferManager");
		Objects.requireNonNull(environment, "environment");
		Objects.requireNonNull(amazonS3, "amazonS3");

		this.s3TransferManager = s3TransferManager;
		this.environment = environment;
		this.amazonS3 = amazonS3;
	}

	/**
	 * Stores the {@link InputStream} as an object in the S3 bucket.
	 *
	 * @param keyName The requested key name for the object.
	 * @param inStream The {@link InputStream} to write out to an object in S3.
	 * @param size The size of the {@link InputStream}.
	 * @return A {@link CompletableFuture} that will eventually contain the S3 object key.
	 */
	@Override
	public CompletableFuture<String> store(String keyName, Supplier<InputStream> inStream, long size) {
		String bucketName = environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE);
		String kmsKey = environment.getProperty(Constants.KMS_KEY_ENV_VARIABLE);
		if (StringUtils.isEmpty(bucketName) || StringUtils.isEmpty(kmsKey)) {
			API_LOG.warn("No bucket name is specified or no KMS key specified.");
			return CompletableFuture.completedFuture("");
		}

		ObjectMetadata s3ObjectMetadata = new ObjectMetadata();
		s3ObjectMetadata.setContentLength(size);

		Supplier<PutObjectRequest> putObjectRequest = () -> new PutObjectRequest(bucketName, keyName, inStream.get(), s3ObjectMetadata)
			.withSSEAwsKeyManagementParams(new SSEAwsKeyManagementParams(kmsKey));

		API_LOG.info("Writing object {} to S3 bucket {}", keyName, bucketName);
		return actOnItem(putObjectRequest);
	}

	/**
	 * Performs a {@link GetObjectRequest} to the S3 bucket by file id for the file
	 *
	 * @param fileLocationId Id of the file to search for
	 * @return file found from S3
	 */
	@Override
	public InputStream getFileByLocationId(String fileLocationId) {
		String bucketName = environment.getProperty(Constants.BUCKET_NAME_ENV_VARIABLE);
		if (StringUtils.isEmpty(bucketName)) {
			API_LOG.warn("No bucket name is specified.");
			return null;
		}

		API_LOG.info("Retrieving file {} from bucket {}", fileLocationId, bucketName);

		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileLocationId);

		S3Object s3Object = amazonS3.getObject(getObjectRequest);

		API_LOG.info("Successfully retrieved file {} from S3 bucket {}", getObjectRequest.getKey(), getObjectRequest.getBucketName());

		return s3Object.getObjectContent();
	}

	/**
	 * Opens a stream to s3 to retrieve the CPC+ API to NPI Validation file
	 *
	 * @return file used for cpc+ validation.
	 */
	@Override
	public byte[] getCpcPlusValidationFile() {
		String fmsToken = environment.getProperty(Constants.FMS_TOKEN_ENV_VARIABLE);
		String arUrl = environment.getProperty(Constants.AR_API_URL_ENV_VARIABLE);
		if (StringUtils.isEmpty(fmsToken) || StringUtils.isEmpty(arUrl)) {
			API_LOG.warn("No token or url specified for file retrieval. Not retrieving CPC+ validation file");
			return new byte[0];
		}
		API_LOG.info("Retrieving CPC+ validation file from FMS");

		RestTemplate rest = new RestTemplate();
		RequestEntity<?> entity = RequestEntity.get(
					URI.create(arUrl))
				.header("Authorization", "Bearer: " + fmsToken)
				.build();
		ResponseEntity<String> response = rest.exchange(entity, String.class);
		String s3PresignedUrl = response.getBody();
		if (StringUtils.isNotEmpty(s3PresignedUrl)) {
			System.out.println("Received a valid presigned url: " + s3PresignedUrl);
			return new byte[0];
		}
		API_LOG.error("/qppct/cpc_plus_validation_file was empty, missing, or did not return a response body. "
				+ "HTTP Status was " + response.getStatusCodeValue());
		return new byte[0];
	}

	/**
	 * Uses the {@link TransferManager} to upload a file.
	 *
	 * @param objectToActOn The put request.
	 * @return The object key in the bucket.
	 */
	@Override
	protected String asynchronousAction(Supplier<PutObjectRequest> objectToActOn) {
		String returnValue;

		PutObjectRequest request = objectToActOn.get();
		try {
			Upload upload = s3TransferManager.upload(request);
			returnValue = upload.waitForUploadResult().getKey();
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new UncheckedInterruptedException(exception);
		}

		API_LOG.info("Successfully wrote object {} to S3 bucket {}", returnValue, request.getBucketName());

		return returnValue;
	}
}