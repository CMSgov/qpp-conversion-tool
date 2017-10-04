package gov.cms.qpp.conversion.api.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class InOrderAsyncActionServiceTest {

	@InjectMocks
	private TestInOrderService objectUnderTest;

	@Mock
	private TaskExecutor taskExecutor;

	@Before
	public void runBeforeEachTest() {
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));
	}

	@After
	public void runAfterEachTest() {
		//un pause separate thread when the test stops (with success or failure)
		objectUnderTest.pauseAsynchronousAction.set(false);
	}

	@Test
	public void testDependencyOrder() {
		objectUnderTest.pauseAsynchronousAction.set(true);

		CompletableFuture<Object> completableFuture1 = objectUnderTest.actOnItem(new Object());
		CompletableFuture<Object> completableFuture2 = objectUnderTest.actOnItem(new Object());

		assertThat("One other CompletableFuture should be dependent on this one but there isn't.",
			completableFuture1.getNumberOfDependents(), is(greaterThan(0)));
		assertThat("No other CompletableFuture should be dependent on this one but there is.",
			completableFuture2.getNumberOfDependents(), is(0));
	}

	private static class TestInOrderService extends InOrderActionService<Object, Object> {

		public AtomicBoolean pauseAsynchronousAction = new AtomicBoolean(false);

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