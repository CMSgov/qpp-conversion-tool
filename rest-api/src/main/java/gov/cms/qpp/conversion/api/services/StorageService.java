package gov.cms.qpp.conversion.api.services;


import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface StorageService {
	CompletableFuture<String> store(String keyName, InputStream inStream);
}
