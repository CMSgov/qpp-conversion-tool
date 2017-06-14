package gov.cms.qpp.conversion;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import gov.cms.qpp.conversion.util.ExceptionHelper;

/**
 * Completes a completable future, and returns whether the
 * execution was successful. This exists outside of a lambda
 * to assist with mocking in unit tests.
 *
 * @author adam
 */
public class CompletableFutureCaller implements Callable<Boolean> {

	/**
	 * @param future The CompletableFuture to call
	 * @return a CompletableFutureCaller of the given CompletableFuture
	 * @throws NullPointerException if the future is null
	 */
	public static CompletableFutureCaller of(CompletableFuture<?> future) {
		Objects.requireNonNull(future, "future");
		return new CompletableFutureCaller(future);
	}

	private final CompletableFuture<?> future;

	private CompletableFutureCaller(CompletableFuture<?> future) {
		this.future = future;
	}

	/**
	 * @return whether the future was completed exceptionally
	 */
	@Override
	public Boolean call() throws Exception {
		ExceptionHelper.runOrSilence(future::get);
		return future.isCompletedExceptionally();
	}

}
