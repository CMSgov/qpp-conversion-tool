package gov.cms.qpp.conversion.api.services.internal;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.services.StorageService;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Used to store an {@link InputStream} in S3.
 */
@Service

public class StorageServiceImpl extends AnyOrderActionService<Supplier<S3UploadRequest>, String>
		implements StorageService {

	private static final Logger API_LOG = LoggerFactory.getLogger(StorageServiceImpl.class);

	private final S3Client s3Client;
	private final Environment environment;

	/**
	 * Injected AWS clients and environment are thread-safe and intended for sharing.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP2")
	public StorageServiceImpl(TaskExecutor taskExecutor, S3Client s3Client, Environment environment) {
		super(taskExecutor);

		Objects.requireNonNull(s3Client, "s3Client");
		Objects.requireNonNull(environment, "environment");

		this.s3Client = s3Client;
		this.environment = environment;
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

		Supplier<S3UploadRequest> putObjectRequest = () -> new S3UploadRequest(
			PutObjectRequest.builder()
				.bucket(bucketName)
				.key(keyName)
				.contentLength(size)
				.serverSideEncryption(ServerSideEncryption.AWS_KMS)
				.ssekmsKeyId(kmsKey)
				.build(),
			inStream.get());

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

		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(fileLocationId).build();
		InputStream objectContent = s3Client.getObject(getObjectRequest);

		API_LOG.info("Successfully retrieved file {} from S3 bucket {}", getObjectRequest.key(), getObjectRequest.bucket());

		return objectContent;
	}

	/**
	 * Opens a stream to retrieve the CPC+ API to NPI Validation file
	 *
	 * @return file used for cpc+ validation.
	 */
	@Override
	public InputStream getCpcPlusValidationFile() {
		String bucketName = environment.getProperty(Constants.CPC_PLUS_BUCKET_NAME_VARIABLE);
		String key = environment.getProperty(Constants.CPC_PLUS_FILENAME_VARIABLE);
		if (StringUtils.isEmpty(bucketName) || StringUtils.isEmpty(key) || Constants.VALIDATION_DISABLE_VARIABLE.equalsIgnoreCase(key)) {
			API_LOG.warn("No CPC+ bucket name and/or CPC+ key specified");
			return null;
		}
		API_LOG.info("Retrieving CPC+ validation file from bucket {}", bucketName);

		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
		return s3Client.getObject(getObjectRequest);
	}

	/**
	 * Uses the S3 client to upload a file.
	 *
	 * @param objectToActOn The put request.
	 * @return The object key in the bucket.
	 */
	@Override
	protected String asynchronousAction(Supplier<S3UploadRequest> objectToActOn) {
		S3UploadRequest uploadRequest = objectToActOn.get();
		PutObjectRequest request = uploadRequest.request();
		try (InputStream inputStream = uploadRequest.inputStream()) {
			s3Client.putObject(request, RequestBody.fromInputStream(inputStream, request.contentLength()));
		} catch (java.io.IOException exception) {
			throw new UncheckedIOException(exception);
		}

		API_LOG.info("Successfully wrote object {} to S3 bucket {}", request.key(), request.bucket());
		return request.key();
	}

	@Override
	protected String getActionName() {
		return "Write to Storage";
	}

}

record S3UploadRequest(PutObjectRequest request, InputStream inputStream) {
}