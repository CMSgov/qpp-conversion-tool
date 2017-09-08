package gov.cms.qpp.conversion.api.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class InOrderAsyncActionServiceTest {

	@InjectMocks
	private TestInOrderService objectUnderTest;

	@Mock
	private TaskExecutor taskExecutor;

	private static boolean asynchronousActionCalled;

	private static int timesAsynchronousActionCalled;

	private static Object objectThatWasActedOn;

	@Before
	public void runBeforeEachTest() throws InterruptedException {
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));

		asynchronousActionCalled = false;
		timesAsynchronousActionCalled = 0;
		objectThatWasActedOn = null;
	}

	@Test
	public void testAsynchronousActionIsCalled() {
		objectUnderTest.failuresUntilSuccess(0);
		CompletableFuture<Object> completableFuture = objectUnderTest.actOnItem(new Object());

		completableFuture.join();

		assertTrue("The asynchronousAction was not called.", asynchronousActionCalled);
	}

	@Test
	public void testObjectToActOnPassedDown() {
		Object objectToActOn = new Object();

		objectUnderTest.failuresUntilSuccess(0);
		CompletableFuture<Object> completableFuture = objectUnderTest.actOnItem(objectToActOn);

		completableFuture.join();

		assertThat("The object to act on didn't make it down to asynchronousAction.", objectToActOn, is(objectThatWasActedOn));
	}

	@Test
	public void testSuccessNoRetry() {
		objectUnderTest.failuresUntilSuccess(0);
		CompletableFuture<Object> completableFuture = objectUnderTest.actOnItem(new Object());

		completableFuture.join();

		assertThat("The asynchronousAction method was not called once and only once.", timesAsynchronousActionCalled, is(1));
	}

	@Test
	public void testFailureRetry() {
		int failuresUntilSuccess = 3;

		objectUnderTest.failuresUntilSuccess(failuresUntilSuccess);
		CompletableFuture<Object> completableFuture = objectUnderTest.actOnItem(new Object());

		completableFuture.join();

		assertThat("The asynchronousAction method was not called enough times.", timesAsynchronousActionCalled, is(failuresUntilSuccess + 1));
	}

	@Test
	public void testMultipleActsResultInAsynchronousActionsSuccess() {
		int numberOfItemsToProcess = 3;

		objectUnderTest.failuresUntilSuccess(0);

		CompletableFuture<Object> completableFuture = null;
		for (int currentItemIndex = 0; currentItemIndex < numberOfItemsToProcess; currentItemIndex++) {
			if (completableFuture == null) {
				completableFuture = objectUnderTest.actOnItem(new Object());
			} else {
				completableFuture = completableFuture.thenCompose(ignore -> objectUnderTest.actOnItem(new Object()));
			}
		}

		completableFuture.join();

		assertThat("The asynchronousAction method was not called as many times as it should have.", timesAsynchronousActionCalled, is(numberOfItemsToProcess));
	}

	@Test
	public void testMultipleActsResultInAsynchronousActionsFailure() {
		int failuresUntilSuccess = 2;
		int numberOfItemsToProcess = 3;

		objectUnderTest.failuresUntilSuccess(failuresUntilSuccess);

		CompletableFuture<Object> completableFuture = null;
		for(int currentItemIndex = 0; currentItemIndex < numberOfItemsToProcess; currentItemIndex++) {
			if (completableFuture == null) {
				completableFuture = objectUnderTest.actOnItem(new Object());
			} else {
				completableFuture = completableFuture.thenCompose(ignore -> objectUnderTest.actOnItem(new Object()));
			}
		}

		completableFuture.join();

		assertThat("The asynchronousAction method was not called as many times as it should have.", timesAsynchronousActionCalled, is((failuresUntilSuccess + 1) * numberOfItemsToProcess));
	}

	private static class TestInOrderService extends InOrderAsyncActionService<Object, Object> {

		private int failuresUntilSuccessTemplate = -1;
		private int failuresUntilSuccess = -1;

		public TestInOrderService failuresUntilSuccess(int failuresUntilSuccess) {
			this.failuresUntilSuccessTemplate = failuresUntilSuccess;
			this.failuresUntilSuccess = this.failuresUntilSuccessTemplate;
			return this;
		}

		@Override
		protected Object asynchronousAction(final Object objectToActOn) {
			asynchronousActionCalled = true;
			timesAsynchronousActionCalled++;
			objectThatWasActedOn = objectToActOn;

			if(failuresUntilSuccess != 0) {
				if(failuresUntilSuccess != -1) {
					failuresUntilSuccess--;
				}
					throw new RuntimeException();
			}

			failuresUntilSuccess = failuresUntilSuccessTemplate;

			return new Object();
		}

		@Override
		protected RetryTemplate retryTemplate() {
			RetryTemplate retry = new RetryTemplate();

			retry.setRetryPolicy(new AlwaysRetryPolicy());
			FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
			backOffPolicy.setBackOffPeriod(0);
			retry.setBackOffPolicy(backOffPolicy);

			return retry;
		}
	}
}