package gov.cms.qpp.conversion.api.services;


import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class StorageServiceImpl extends InOrderAsyncActionService<PutObjectRequest, String>
		implements StorageService {
	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	@Autowired
	private TransferManager s3TransferManager;

	@Value("${submission.s3.bucket}")
	private String bucketName;

	@Override
	public CompletableFuture<String> store(String keyName, InputStream inStream) {
		return actOnItem(new PutObjectRequest(bucketName, keyName, inStream, new ObjectMetadata()));
	}

	@Override
	protected String asynchronousAction(PutObjectRequest objectToActOn) {
		String returnValue;

		try {
			Upload upload = s3TransferManager.upload(objectToActOn);
			returnValue = upload.waitForUploadResult().getKey();
		} catch (InterruptedException ie) {
			API_LOG.error("Upload interrupted", ie);
			throw new RuntimeException(ie);
		}

		return returnValue;
	}
}
