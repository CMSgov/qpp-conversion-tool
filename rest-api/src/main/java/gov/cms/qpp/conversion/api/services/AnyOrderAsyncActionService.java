package gov.cms.qpp.conversion.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * A service extends from this to help it asynchronously do something in a guaranteed fashion.
 *
 * The main point of entry is {@link #actOnItem(Object)}.  A service extending this class would call {@link #actOnItem(Object)}
 * and implement {@link #asynchronousAction(Object)} to do an action given that item.  This class handles all the error handling
 * and retries so you don't need to in {@link #asynchronousAction(Object)}.  For multiple calls to {@link #actOnItem(Object)},
 * the actions will complete in an indeterminate order.
 *
 * This allows an application not to deal with distributed transactions and having to solve the problem of how to rollback a
 * distributed transaction.  In lieu of a standard transaction contract, this gives the application eventual consistency.
 * http://www.grahamlea.com/2016/08/distributed-transactions-microservices-icebergs/
 *
 * @param <T> The type of object that will be acted upon in the asynchronous action.
 * @param <S> The type of object that is returned from {@link #asynchronousAction(Object)}.
 */
public abstract class AnyOrderAsyncActionService<T, S> {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	@Autowired
	private TaskExecutor taskExecutor;

	/**
	 * The single action that will occur given a call to {@link #actOnItem(Object)}.
	 *
	 * Subclasses must implement this method.  An example of what could be contained is
	 * <ul>
	 *     <li>A ReST call</li>
	 *     <li>Uploading an object to AWS S3</li>
	 *     <li>Updating a database</li>
	 * </ul>
	 *
	 * @param objectToActOn An object that contains information pertinent to the execution of the action.
	 * @return An object to return that can be retrieved from a {@link CompletableFuture}.
	 */
	protected abstract S asynchronousAction(T objectToActOn);

	/**
	 * The main point of entry into this class.
	 *
	 * Call this with an item and {@link #asynchronousAction(Object)} will be called with the same item to do the action
	 * asynchronously.
	 *
	 * @param objectToActOn The item to do an action on.
	 * @return A {@link CompletableFuture} that will complete once the action completes without failure.
	 */
	protected CompletableFuture<S> actOnItem(final T objectToActOn) {
		return CompletableFuture.supplyAsync(() -> {
			RetryTemplate retry = retryTemplate();

			API_LOG.info("Trying to execute action");
			return retry.execute(context -> {
				if (context.getLastThrowable() != null) {
					API_LOG.warn("Last try resulted in a thrown throwable", context.getLastThrowable());
				}
				if (context.getRetryCount() > 0) {
					API_LOG.warn("Retry {} - trying to execute action again", context.getRetryCount());
				}
				return this.asynchronousAction(objectToActOn);
			});
		}, taskExecutor);
	}

	/**
	 * Returns a retry template that always retries.  Starts with a second interval between retries and doubles that interval up
	 * to a minute for each retry.
	 *
	 * @return A retry template.
	 */
	protected RetryTemplate retryTemplate() {
		RetryTemplate retry = new RetryTemplate();

		retry.setRetryPolicy(new AlwaysRetryPolicy());

		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(1000);
		backOffPolicy.setMultiplier(2.0);
		backOffPolicy.setMaxInterval(60000);
		retry.setBackOffPolicy(backOffPolicy);

		return retry;
	}
}
