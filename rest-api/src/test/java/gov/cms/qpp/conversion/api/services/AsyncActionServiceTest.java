package gov.cms.qpp.conversion.api.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class AsyncActionServiceTest {

	@InjectMocks
	private TestService<Object> objectUnderTest;

	@Mock
	private TaskExecutor taskExecutor;

	@Mock
	private SleepService sleepService;

	private CompletableFuture<?>[] asyncActionService;

	private static boolean asynchronousActionCalled;

	private static int timesAsynchronousActionCalled;

	private static Object objectThatWasActedOn;

	@Before
	public void runBeforeEachTest() {

		asyncActionService = new CompletableFuture<?>[1];

		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			asyncActionService[0] = CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));

		asynchronousActionCalled = false;
		timesAsynchronousActionCalled = 0;
		objectThatWasActedOn = null;
	}

	@Test
	public void testAsynchronousActionIsCalled() throws InterruptedException, ExecutionException {
		objectUnderTest.setup(0, 1);
		objectUnderTest.actOnItem(new Object());

		asyncActionService[0].get();

		assertTrue("The asynchronousAction was not called.", asynchronousActionCalled);
	}

	@Test
	public void testObjectToActOnPassedDown() throws InterruptedException, ExecutionException {
		Object objectToActOn = new Object();

		objectUnderTest.setup(0, 1);
		objectUnderTest.actOnItem(objectToActOn);

		asyncActionService[0].get();

		assertThat("The object to act on didn't make it down to asynchronousAction.", objectToActOn, is(objectThatWasActedOn));
	}

	@Test
	public void testSuccessNoRetry() throws ExecutionException, InterruptedException {
		objectUnderTest.setup(0, 1);
		objectUnderTest.actOnItem(new Object());

		asyncActionService[0].get();

		assertThat("The asynchronousAction method was not called once and only once.", timesAsynchronousActionCalled, is(1));
	}

	@Test
	public void testFailureRetry() throws ExecutionException, InterruptedException {
		int failuresUntilSuccess = 3;

		objectUnderTest.setup(failuresUntilSuccess, 1);
		objectUnderTest.actOnItem(new Object());

		asyncActionService[0].get();

		assertThat("The asynchronousAction method was not called enough times.", timesAsynchronousActionCalled, is(failuresUntilSuccess + 1));
	}

	@Test
	public void testMultipleActsResultInAsynchronousActionsSuccess() throws ExecutionException, InterruptedException {
		int numberOfItemsToProccess = 3;

		objectUnderTest.setup(0, numberOfItemsToProccess);

		for(int currentItemIndex = 0; currentItemIndex < numberOfItemsToProccess; currentItemIndex++) {
			objectUnderTest.actOnItem(new Object());
		}

		asyncActionService[0].get();

		assertThat("The asynchronousAction method was not called twice.", timesAsynchronousActionCalled, is(numberOfItemsToProccess));
	}

	@Test
	public void testMultipleActsResultInAsynchronousActionsFailure() throws ExecutionException, InterruptedException {
		int failuresUntilSuccess = 2;
		int numberOfItemsToProccess = 3;

		objectUnderTest.setup(failuresUntilSuccess, numberOfItemsToProccess);

		for(int currentItemIndex = 0; currentItemIndex < numberOfItemsToProccess; currentItemIndex++) {
			objectUnderTest.actOnItem(new Object());
		}

		asyncActionService[0].get();

		assertThat("The asynchronousAction method was not called twice.", timesAsynchronousActionCalled, is((failuresUntilSuccess + 1) * numberOfItemsToProccess));
	}

	private static class TestService<T> extends AsyncActionService<T> {

		private int failuresUntilSuccessTemplate = -1;
		private int failuresUntilSuccess = -1;
		private int numberOfItemsToProcess = 1;

		public void setup(int failuresUntilSuccess, int numberOfItemsToProcess) {
			this.failuresUntilSuccessTemplate = failuresUntilSuccess;
			this.failuresUntilSuccess = this.failuresUntilSuccessTemplate;
			this.numberOfItemsToProcess = numberOfItemsToProcess;
		}

		@Override
		protected boolean asynchronousAction(final T objectToActOn) {
			asynchronousActionCalled = true;
			timesAsynchronousActionCalled++;
			objectThatWasActedOn = objectToActOn;

			return (failuresUntilSuccess != -1 && failuresUntilSuccess--==0);
		}

		@Override
		protected T takeFromExecutionQueue() throws InterruptedException {
			if(numberOfItemsToProcess--==0) {
				throw new InterruptedException();
			}

			failuresUntilSuccess = failuresUntilSuccessTemplate;
			return super.takeFromExecutionQueue();
		}
	}
}