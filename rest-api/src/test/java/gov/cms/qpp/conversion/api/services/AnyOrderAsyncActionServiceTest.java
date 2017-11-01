package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.exceptions.UncheckedInterruptedException;
import gov.cms.qpp.conversion.api.helper.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class AnyOrderAsyncActionServiceTest {

	@InjectMocks
	private TestAnyOrderService objectUnderTest;

	@Mock
	private TaskExecutor taskExecutor;

	@BeforeEach
	void runBeforeEachTest() throws InterruptedException {
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));
	}

	@AfterEach
	void runAfterEachTest() {
		objectUnderTest.pauseAsynchronousAction.set(false);
	}

	@Test
	void testAsynchronousActionIsCalled() {
		runSimpleScenario(0);

		assertTrue("The asynchronousAction was not called.", objectUnderTest.asynchronousActionCalled.get());
	}

	@Test
	void testObjectToActOnPassedDown() {
		Object objectToActOn = runSimpleScenario(0);

		assertThat(objectUnderTest.objectThatWasActedOn.get()).isSameAs(objectToActOn);
	}

	@Test
	void testSuccessNoRetry() {
		runSimpleScenario(0);

		assertThat(objectUnderTest.timesAsynchronousActionCalled.get()).isEqualTo(1);
	}

	@Test
	void testFailureRetry() {
		int failuresUntilSuccess = 3;

		runSimpleScenario(failuresUntilSuccess);

		assertThat(objectUnderTest.timesAsynchronousActionCalled.get()).isEqualTo(failuresUntilSuccess + 1);
	}

	@Test
	void testObjectToActOnPassedDownWithFailures() {
		Object objectToActOn = runSimpleScenario(2);

		assertThat(objectUnderTest.objectThatWasActedOn.get()).isSameAs(objectToActOn);
	}

	@Test
	void testMultipleActsResultInAsynchronousActionsSuccess() {
		int numberOfItemsToProcess = 3;

		objectUnderTest.failuresUntilSuccess(0);

		List<CompletableFuture<Object>> completableFutures = new ArrayList<>();
		for (int currentItemIndex = 0; currentItemIndex < numberOfItemsToProcess; currentItemIndex++) {
			completableFutures.add(objectUnderTest.actOnItem(new Object()));
		}

		CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture<?>[0])).join();

		assertThat(objectUnderTest.timesAsynchronousActionCalled.get()).isEqualTo(numberOfItemsToProcess);
	}

	@Test
	void testMultipleActsResultInAsynchronousActionsFailure() {
		int failuresUntilSuccess = 2;
		int numberOfItemsToProcess = 3;

		objectUnderTest.failuresUntilSuccess(failuresUntilSuccess);

		List<CompletableFuture<Object>> completableFutures = new ArrayList<>();
		for (int currentItemIndex = 0; currentItemIndex < numberOfItemsToProcess; currentItemIndex++) {
			completableFutures.add(objectUnderTest.actOnItem(new Object()));
		}

		CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture<?>[0])).join();

		assertThat(objectUnderTest.timesAsynchronousActionCalled.get())
				.isEqualTo((failuresUntilSuccess + 1) * numberOfItemsToProcess);
	}

	@Test
	void testDependencyOrder() {
		objectUnderTest.failuresUntilSuccess(0);
		objectUnderTest.pauseAsynchronousAction.set(true);

		CompletableFuture<Object> completableFuture1 = objectUnderTest.actOnItem(new Object());
		CompletableFuture<Object> completableFuture2 = objectUnderTest.actOnItem(new Object());

		assertThat(completableFuture1.getNumberOfDependents()).isEqualTo(0);
		assertThat(completableFuture2.getNumberOfDependents()).isEqualTo(0);
	}

	@Test
	void testInterruptedException() {
		objectUnderTest.failuresUntilSuccess(1).failWithInterruptException();

		CompletableFuture<Object> completableFuture = objectUnderTest.actOnItem(new Object());
		CompletionException exception = assertThrows(CompletionException.class, completableFuture::join);
		assertThat(exception).hasCauseThat().isInstanceOf(UncheckedInterruptedException.class);
		assertThat(objectUnderTest.timesAsynchronousActionCalled.get()).isEqualTo(1);  //not two
	}

	private Object runSimpleScenario(int failuresUntilSuccess) {
		Object objectToActOn = new Object();

		objectUnderTest.failuresUntilSuccess(failuresUntilSuccess);
		CompletableFuture<Object> completableFuture = objectUnderTest.actOnItem(objectToActOn);

		completableFuture.join();

		return objectToActOn;
	}

	private static class TestAnyOrderService extends AnyOrderActionService<Object, Object> {
		AtomicBoolean asynchronousActionCalled = new AtomicBoolean(false);
		AtomicInteger timesAsynchronousActionCalled = new AtomicInteger(0);
		AtomicReference<Object> objectThatWasActedOn = new AtomicReference<>(null);
		AtomicBoolean pauseAsynchronousAction = new AtomicBoolean(false);

		private int failuresUntilSuccessTemplate = -1;
		private ThreadLocal<Integer> failuresUntilSuccess = ThreadLocal.withInitial(() -> -1);
		private ThreadLocal<Boolean> failWithInterruptException = ThreadLocal.withInitial(() -> Boolean.FALSE);

		TestAnyOrderService failuresUntilSuccess(int failuresUntilSuccess) {
			this.failuresUntilSuccessTemplate = failuresUntilSuccess;
			this.failuresUntilSuccess = ThreadLocal.withInitial(() -> this.failuresUntilSuccessTemplate);
			return this;
		}

		void failWithInterruptException() {
			this.failWithInterruptException = ThreadLocal.withInitial(() -> Boolean.TRUE);
		}

		@Override
		protected Object asynchronousAction(final Object objectToActOn) {
			asynchronousActionCalled.set(true);
			timesAsynchronousActionCalled.incrementAndGet();
			objectThatWasActedOn.set(objectToActOn);

			while (pauseAsynchronousAction.get()) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException exception) {
					Thread.currentThread().interrupt();
					pauseAsynchronousAction.set(false);
				}
			}

			if(failuresUntilSuccess.get() != 0) {
				if(failuresUntilSuccess.get() != -1) {
					failuresUntilSuccess.set(failuresUntilSuccess.get() - 1);
				}
				if(failWithInterruptException.get()) {
					throw new UncheckedInterruptedException(new InterruptedException());
				}
				else {
					throw new RuntimeException();
				}
			}

			failuresUntilSuccess.set(failuresUntilSuccessTemplate);

			return new Object();
		}

		@Override
		protected RetryTemplate retryTemplate() {
			RetryTemplate retry = super.retryTemplate();

			FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
			backOffPolicy.setBackOffPeriod(0);
			retry.setBackOffPolicy(backOffPolicy);

			return retry;
		}
	}
}