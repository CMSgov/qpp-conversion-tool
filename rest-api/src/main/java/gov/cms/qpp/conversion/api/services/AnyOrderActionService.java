package gov.cms.qpp.conversion.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A service extends from this to help it do something in a guaranteed fashion.
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
public abstract class AnyOrderActionService<T, S> {
	private static final int INITIAL_INTERVAL = 2000;
	private static final double MULTIPLIER = 2.0;
	private static final int MAX_INTERVAL = 60000;

	private static final Logger API_LOG = LoggerFactory.getLogger(AnyOrderActionService.class);

	protected final TaskExecutor taskExecutor;

	public AnyOrderActionService(TaskExecutor taskExecutor) {
		Objects.requireNonNull(taskExecutor, "taskExecutor");

		this.taskExecutor = taskExecutor;
	}

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
	protected CompletableFuture<S> actOnItem(T objectToActOn) {
		return CompletableFuture.supplyAsync(() -> {
			RetryTemplate retry = retryTemplate();

			API_LOG.info("Trying to execute action " + getActionName());
			return retry.execute(context -> {
				if (context.getLastThrowable() != null) {
					API_LOG.error("Last try resulted in a thrown throwable", context.getLastThrowable());
				}
				if (context.getRetryCount() > 0) {
					API_LOG.warn("Retry {} - trying to execute action again", context.getRetryCount());
				}
				return asynchronousAction(objectToActOn);
			});
		}, taskExecutor);
	}

	/**
	 * Returns the name of the action, for usage in monitoring
	 *
	 * @return the name of the action
	 */
	protected abstract String getActionName();

	/**
	 * Returns a retry template that always retries.  Starts with a second interval between retries and doubles that interval up
	 * to a minute for each retry.
	 *
	 * @return A retry template.
	 */
	protected RetryTemplate retryTemplate() {
		RetryTemplate retry = new RetryTemplate();

		Map<Class<? extends Throwable>, Boolean> stopExceptions =
				Collections.singletonMap(InterruptedException.class, Boolean.FALSE);
		SimpleRetryPolicy retryPolicy =
				new SimpleRetryPolicy(5, stopExceptions, true, true);

		retry.setRetryPolicy(retryPolicy);

		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(INITIAL_INTERVAL);
		backOffPolicy.setMultiplier(MULTIPLIER);
		backOffPolicy.setMaxInterval(MAX_INTERVAL);
		retry.setBackOffPolicy(backOffPolicy);

		return retry;
	}
}
