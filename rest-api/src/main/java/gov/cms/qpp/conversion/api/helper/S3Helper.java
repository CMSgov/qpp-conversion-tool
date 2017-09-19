package gov.cms.qpp.conversion.api.helper;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.InputStream;

public class S3Helper {
	static final String SUBMISSION_BUCKET = "SUBMISSION_BUCKET";

	@Autowired
	private AmazonS3 s3Client;

	@Autowired
	private Environment environment;

	private String bucketName;

	public S3Helper() {
		bucketName = environment.getProperty(SUBMISSION_BUCKET);
	}

	void setS3Client(AmazonS3 amazonS3Client) {
		this.s3Client = amazonS3Client;
	}

	public Upload putObject(String key, InputStream inStream) {
		TransferManager transferManager = TransferManagerBuilder.standard()
				.withS3Client(s3Client).build();
		return transferManager.upload(bucketName, key, inStream, new ObjectMetadata());
	}
}
