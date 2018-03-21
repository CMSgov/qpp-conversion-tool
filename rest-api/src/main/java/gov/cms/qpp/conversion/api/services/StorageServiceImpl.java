package gov.cms.qpp.conversion.api.services;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

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
		this.s3TransferManager = s3TransferManager;
		this.environment = environment;
		this.amazonS3 = amazonS3;
	}

	@Override
	public CompletableFuture<String> store(String keyName, Supplier<InputStream> inStream, long size) {
		return CompletableFuture.completedFuture("hello world");
	}

	@Override
	public InputStream getFileByLocationId(String fileId) {
		return null;
	}

	@Override
	protected String asynchronousAction(Supplier<PutObjectRequest> objectToActOn) {
		return "hello world";
	}

}
