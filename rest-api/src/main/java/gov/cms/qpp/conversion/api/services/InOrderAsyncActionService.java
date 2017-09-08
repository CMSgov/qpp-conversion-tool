package gov.cms.qpp.conversion.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.CompletableFuture;

/**
 * A service extends from this to help it asynchronously do something in a guaranteed fashion.
 *
 * The main point of entry is {@link #actOnItem(Object)}.  A service extending this class would call {@link #actOnItem(Object)}
 * and implement {@link #asynchronousAction(Object)} to do an action given that item.  This class handles all the error handling
 * and retries so you don't need to in {@link #asynchronousAction(Object)}.  For multiple calls to {@link #actOnItem(Object)},
 * the actions will complete in the order that they were passed in.
 *
 * This allows an application not to deal with distributed transactions and having to solve the problem of how to rollback a
 * distributed transaction.  In lieu of a standard transaction contract, this gives the application eventual consistency.
 * http://www.grahamlea.com/2016/08/distributed-transactions-microservices-icebergs/
 *
 * @param <T> The type of object that will be acted upon in the asynchronous action.
 * @param <S> The type of object that is returned from {@link #asynchronousAction(Object)}.
 */
public abstract class InOrderAsyncActionService<T, S> extends AnyOrderAsyncActionService<T, S> {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	@Autowired
	private TaskExecutor taskExecutor;

	private CompletableFuture<S> currentThreadFuture;

	/**
	 * The main point of entry into this class.
	 *
	 * Call this with an item and {@link #asynchronousAction(Object)} will be called with the same item to do the action
	 * asynchronously.
	 *
	 * Synchronized to allow different threads to call this method to preclude incorrect results.
	 *
	 * @param objectToActOn The item to do an action on.
	 * @return A {@link CompletableFuture} that will complete once the action completes without failure.
	 */
	protected synchronized CompletableFuture<S> actOnItem(final T objectToActOn) {

		if (currentThreadFuture == null || currentThreadFuture.isDone()) {
			currentThreadFuture = super.actOnItem(objectToActOn);
		} else {
			currentThreadFuture = currentThreadFuture.thenComposeAsync(ignore -> super.actOnItem(objectToActOn), taskExecutor);
		}

		return currentThreadFuture;
	}
}
