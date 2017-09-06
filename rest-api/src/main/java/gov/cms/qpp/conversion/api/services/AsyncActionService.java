package gov.cms.qpp.conversion.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A service extends from this to help it asynchronously do something in a guaranteed fashion.
 *
 * The main point of entry is {@link #actOnItem(Object)}.  A service extending this class would call {@link #actOnItem(Object)}
 * and implement {@link #asynchronousAction(Object)} to do an action given that item.  This class handles all the error handling
 * and retries so you don't need to in {@link #asynchronousAction(Object)}.  For each service that extends this class, a thread
 * is created and executes the action as items are passed in, in the order they are pass in.  If there are no active items left
 * to proccess, the thread spins down until an item is passed in again.
 *
 * This allows an application not to deal with distributed transactions and having to solve the problem of how to rollback a
 * distributed transaction.  In lieu of a standard transaction contract, this gives the application eventual consistency.
 * http://www.grahamlea.com/2016/08/distributed-transactions-microservices-icebergs/
 *
 * @param <T> The type of object that will be acted upon in the asynchronous action.
 */
public abstract class AsyncActionService<T> {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private SleepService sleepService;

	private BlockingQueue<T> executionQueue = new LinkedBlockingQueue<>();

	private CompletableFuture threadFuture;

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
	 * @return {@code true} on success or {@code false} on failure
	 */
	protected abstract boolean asynchronousAction(T objectToActOn);

	/**
	 * The main point of entry into this class.  Call this with an item and {@link #asynchronousAction(Object)} will be called
	 * with the same item to do the action asynchronously.
	 *
	 * @param objectToActOn The item to do an action on.
	 */
	protected void actOnItem(final T objectToActOn) {
		addItemToExecutionQueue(objectToActOn);
		ensureExecutionThreadRunning();
	}

	/**
	 * Continue trying to put an item onto a queue until it is added successfully.
	 *
	 * @param objectToActOn The item to do an action on.
	 */
	private void addItemToExecutionQueue(T objectToActOn) {
		try {
			putToExecutionQueue(objectToActOn);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			API_LOG.error("Interrupting wait to add an item to the execution queue! This item will not be completed!",
				exception);
		}
	}

	/**
	 * Put the item on the queue.  Blocks the thread if the blocking queue is full.
	 *
	 * @param objectToActOn The item to do an action on.
	 * @throws InterruptedException While waiting to add an object to the queue.
	 */
	protected void putToExecutionQueue(T objectToActOn) throws InterruptedException {
		executionQueue.put(objectToActOn);
	}

	/**
	 * Ensures the separate thread is running that will execute the action.
	 */
	private void ensureExecutionThreadRunning() {
		if (threadFuture == null || threadFuture.isDone()) {
			API_LOG.info("Start asynchronous execution of action queue");
			threadFuture = CompletableFuture.supplyAsync(this::asynchronousExecuteQueue, taskExecutor);
		}
	}

	/**
	 * Waits for an object to be added to the queue.  Blocks the thread until and object is added.  Once and object is available,
	 * it is taken from the queue, acted upon, and the thread stops if there are no more items on the queue.
	 * This is the top-level method executed on a separate thread.
	 *
	 * @return A {@code CompletableFuture} to know when the thread has completed.
	 */
	private CompletableFuture<?> asynchronousExecuteQueue() {
		try {
			do {
				API_LOG.info("Try to take an action off the queue");
				T objectToActOn = takeFromExecutionQueue();
				asynchronousRetryOperation(objectToActOn);
			} while (!executionQueue.isEmpty());
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			if(executionQueue.isEmpty()) {
				API_LOG.info("Interrupt waiting for an action on the execution queue", exception);
			} else {
				API_LOG.error("Interrupt with additional items on the queue! These items will not be completed!", exception);
			}
		}

		return CompletableFuture.completedFuture(null);
	}

	/**
	 * Take an item from the queue.  Blocks the thread if this blocking queue is empty.
	 *
	 * @return The item to do an action on.
	 * @throws InterruptedException While waiting for an object to be added to the queue.
	 */
	protected T takeFromExecutionQueue() throws InterruptedException {
		return executionQueue.take();
	}

	/**
	 * Repeatedly call {@link #asynchronousAction(Object)} until it succeeds.
	 *
	 * Sleeps five seconds in between retries.
	 *
	 * @param objectToActOn The item to do an action on.
	 */
	private void asynchronousRetryOperation(T objectToActOn) {
		boolean success = false;
		try {
			do {
				checkThreadInterrupted();

				API_LOG.info("Trying to execute action");
				try {
					success = asynchronousAction(objectToActOn);
				} catch (Exception exception) {
					API_LOG.warn("Exception while trying to execute action", exception);
					success = false;
				}

				if (!success) {
					sleepService.sleep(5000);
				}
			} while (!success);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			API_LOG.error("Interrupting retry logic! This item will not be completed!", exception);
		}
	}

	private void checkThreadInterrupted() throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
	}
}
