package gov.cms.qpp.conversion.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AsyncActionService<T> {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	@Autowired
	private TaskExecutor taskExecutor;

	private BlockingQueue<T> executionQueue = new LinkedBlockingQueue<>();

	private CompletableFuture threadFuture;

	protected abstract boolean asynchronousAction(T objectToActOn);

	protected void actOnItem(final T objectToActOn) {
		addItemToExecutionQueue(objectToActOn);
		ensureExecutionThreadRunning();
	}

	private void addItemToExecutionQueue(T objectToActOn) {
		boolean added = false;
		do {
			try {
				executionQueue.put(objectToActOn);
				added = true;
			}
			catch (InterruptedException exception) {
				API_LOG.warn("Interrupting wait to add an item to the execution queue, too bad were going to try again", exception);
			}
		} while(!added);
	}

	private void ensureExecutionThreadRunning() {
		if(threadFuture == null || threadFuture.isDone()) {
			API_LOG.info("Start asynchronous execution of action queue");
			threadFuture = CompletableFuture.supplyAsync(this::asynchronousExecuteQueue, taskExecutor);
		}
	}

	private CompletableFuture<?> asynchronousExecuteQueue() {
		try {
			while (true) {
				API_LOG.info("Try to take an action off the queue");
				asynchronousRetryOperation(executionQueue.take());
			}
		} catch (InterruptedException exception) {
			API_LOG.warn("Interrupting wait for an action on the execution queue", exception);
		}

		return CompletableFuture.completedFuture(null);
	}

	private void asynchronousRetryOperation(T objectToActOn) {
		boolean success = false;
		do {
			API_LOG.info("Trying to execute action");
			try {
				success = asynchronousAction(objectToActOn);
			} catch(Exception exception) {
				API_LOG.warn("Exception while trying to execute action", exception);
				success = false;
			}

			try {
				Thread.sleep(5000);
			}
			catch (InterruptedException exception) {
				API_LOG.warn("Interrupting sleep between retries", exception);
			}
		} while (!success);
	}
}
