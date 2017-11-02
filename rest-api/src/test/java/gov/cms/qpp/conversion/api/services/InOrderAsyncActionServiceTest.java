package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.test.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class InOrderAsyncActionServiceTest {

	@InjectMocks
	private TestInOrderService objectUnderTest;

	@Mock
	private TaskExecutor taskExecutor;

	@BeforeEach
	void runBeforeEachTest() {
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));
	}

	@AfterEach
	void runAfterEachTest() {
		//un pause separate thread when the test stops (with success or failure)
		objectUnderTest.pauseAsynchronousAction.set(false);
	}

	@Test
	void testDependencyOrder() {
		objectUnderTest.pauseAsynchronousAction.set(true);

		CompletableFuture<Object> completableFuture1 = objectUnderTest.actOnItem(new Object());
		CompletableFuture<Object> completableFuture2 = objectUnderTest.actOnItem(new Object());

		assertWithMessage("One other CompletableFuture should be dependent on this one but there isn't.")
				.that(completableFuture1.getNumberOfDependents())
				.isGreaterThan(0);
		assertWithMessage("No other CompletableFuture should be dependent on this one but there is.")
				.that(completableFuture2.getNumberOfDependents())
				.isEqualTo(0);
	}

	private static class TestInOrderService extends InOrderActionService<Object, Object> {

		AtomicBoolean pauseAsynchronousAction = new AtomicBoolean(false);

		@Override
		protected Object asynchronousAction(final Object objectToActOn) {

			while (pauseAsynchronousAction.get()) {
				System.out.println("While pauseAsynchronousAction");
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException exception) {
					Thread.currentThread().interrupt();
					pauseAsynchronousAction.set(false);
				}
			}

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