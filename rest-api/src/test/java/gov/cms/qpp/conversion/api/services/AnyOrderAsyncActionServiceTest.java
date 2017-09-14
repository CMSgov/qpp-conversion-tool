package gov.cms.qpp.conversion.api.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class AnyOrderAsyncActionServiceTest {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	@InjectMocks
	private TestAnyOrderService objectUnderTest;

	@Mock
	private TaskExecutor taskExecutor;

	private static AtomicBoolean asynchronousActionCalled;

	private static AtomicInteger timesAsynchronousActionCalled;

	private static AtomicReference<Object> objectThatWasActedOn;

	private static AtomicBoolean pauseAsynchronousAction;

	@Before
	public void runBeforeEachTest() throws InterruptedException {
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));

		asynchronousActionCalled = new AtomicBoolean(false);
		timesAsynchronousActionCalled = new AtomicInteger(0);
		objectThatWasActedOn = new AtomicReference<>(null);
		pauseAsynchronousAction = new AtomicBoolean(false);
	}

	@After
	public void runAfterEachTest() {
		pauseAsynchronousAction.set(false);
	}

	@Test
	public void testAsynchronousActionIsCalled() {
		objectUnderTest.failuresUntilSuccess(0);
		CompletableFuture<Object> completableFuture = objectUnderTest.actOnItem(new Object());

		completableFuture.join();

		assertTrue("The asynchronousAction was not called.", asynchronousActionCalled.get());
	}

	@Test
	public void testObjectToActOnPassedDown() {
		Object objectToActOn = new Object();

		objectUnderTest.failuresUntilSuccess(0);
		CompletableFuture<Object> completableFuture = objectUnderTest.actOnItem(objectToActOn);

		completableFuture.join();

		assertThat("The object to act on didn't make it down to asynchronousAction.", objectThatWasActedOn.get(),
			is(objectToActOn));
	}

	@Test
	public void testSuccessNoRetry() {
		objectUnderTest.failuresUntilSuccess(0);
		CompletableFuture<Object> completableFuture = objectUnderTest.actOnItem(new Object());

		completableFuture.join();

		assertThat("The asynchronousAction method was not called once and only once.", timesAsynchronousActionCalled.get(), is(1));
	}

	@Test
	public void testFailureRetry() {
		int failuresUntilSuccess = 3;

		objectUnderTest.failuresUntilSuccess(failuresUntilSuccess);
		CompletableFuture<Object> completableFuture = objectUnderTest.actOnItem(new Object());

		completableFuture.join();

		assertThat("The asynchronousAction method was not called enough times.", timesAsynchronousActionCalled.get(),
			is(failuresUntilSuccess + 1));
	}

	@Test
	public void testObjectToActOnPassedDownWithFailures() {
		Object objectToActOn = new Object();

		objectUnderTest.failuresUntilSuccess(2);
		CompletableFuture<Object> completableFuture = objectUnderTest.actOnItem(objectToActOn);

		completableFuture.join();

		assertThat("The object to act on didn't make it down to asynchronousAction.", objectThatWasActedOn.get(),
			is(objectToActOn));
	}

	@Test
	public void testMultipleActsResultInAsynchronousActionsSuccess() {
		int numberOfItemsToProcess = 3;

		objectUnderTest.failuresUntilSuccess(0);

		List<CompletableFuture<Object>> completableFutures = new ArrayList<>();
		for (int currentItemIndex = 0; currentItemIndex < numberOfItemsToProcess; currentItemIndex++) {
			completableFutures.add(objectUnderTest.actOnItem(new Object()));
		}

		CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture<?>[0])).join();

		assertThat("The asynchronousAction method was not called as many times as it should have.",
			timesAsynchronousActionCalled.get(), is(numberOfItemsToProcess));
	}

	@Test
	public void testMultipleActsResultInAsynchronousActionsFailure() {
		int failuresUntilSuccess = 2;
		int numberOfItemsToProcess = 3;

		objectUnderTest.failuresUntilSuccess(failuresUntilSuccess);

		List<CompletableFuture<Object>> completableFutures = new ArrayList<>();
		for (int currentItemIndex = 0; currentItemIndex < numberOfItemsToProcess; currentItemIndex++) {
			completableFutures.add(objectUnderTest.actOnItem(new Object()));
		}

		CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture<?>[0])).join();

		assertThat("The asynchronousAction method was not called as many times as it should have.",
			timesAsynchronousActionCalled.get(), is((failuresUntilSuccess + 1) * numberOfItemsToProcess));
	}

	@Test
	public void testDependencyOrder() {
		objectUnderTest.failuresUntilSuccess(0);

		pauseAsynchronousAction.set(true);

		CompletableFuture<Object> completableFuture1 = objectUnderTest.actOnItem(new Object());
		CompletableFuture<Object> completableFuture2 = objectUnderTest.actOnItem(new Object());

		assertThat("No other CompletableFuture should be dependent on this one but there is.",
			completableFuture1.getNumberOfDependents(), is(0));
		assertThat("No other CompletableFuture should be dependent on this one but there is.",
			completableFuture2.getNumberOfDependents(), is(0));
	}

	private static class TestAnyOrderService extends AnyOrderAsyncActionService<Object, Object> {

		private int failuresUntilSuccessTemplate = -1;
		private ThreadLocal<Integer> failuresUntilSuccess = ThreadLocal.withInitial(() -> -1);

		public TestAnyOrderService failuresUntilSuccess(int failuresUntilSuccess) {
			this.failuresUntilSuccessTemplate = failuresUntilSuccess;
			this.failuresUntilSuccess = ThreadLocal.withInitial(() -> this.failuresUntilSuccessTemplate);
			return this;
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
				throw new RuntimeException();
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