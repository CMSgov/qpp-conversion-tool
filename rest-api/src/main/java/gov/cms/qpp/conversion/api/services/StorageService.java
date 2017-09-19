package gov.cms.qpp.conversion.api.services;


import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface StorageService {
	CompletableFuture<PutObjectResult> store(String keyName, InputStream inStream);
}
