package gov.cms.qpp.conversion.util;

import java.util.concurrent.CompletableFuture;

public class FutureHelper {

	public static <T> CompletableFuture<T> empty() {
		return CompletableFuture.completedFuture(null);
	}

	public static <T> CompletableFuture<T> exceptionally(Throwable throwable) {
		CompletableFuture<T> future = empty();
		future.obtrudeException(throwable);
		return future;
	}

}
