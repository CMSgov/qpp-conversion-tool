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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

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

	private static int timesPutCalled;

	private static Object objectThatWasActedOn;

	@Before
	public void runBeforeEachTest() throws InterruptedException {

		asyncActionService = new CompletableFuture<?>[1];

		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			asyncActionService[0] = CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));

		doNothing().when(sleepService).sleep(anyLong());

		asynchronousActionCalled = false;
		timesAsynchronousActionCalled = 0;
		timesPutCalled = 0;
		objectThatWasActedOn = null;
	}

	@Test
	public void testAsynchronousActionIsCalled() throws InterruptedException, ExecutionException {
		objectUnderTest.failuresUntilSuccess(0);
		objectUnderTest.actOnItem(new Object());

		asyncActionService[0].get();

		assertTrue("The asynchronousAction was not called.", asynchronousActionCalled);
	}

	@Test
	public void testObjectToActOnPassedDown() throws InterruptedException, ExecutionException {
		Object objectToActOn = new Object();

		objectUnderTest.failuresUntilSuccess(0);
		objectUnderTest.actOnItem(objectToActOn);

		asyncActionService[0].get();

		assertThat("The object to act on didn't make it down to asynchronousAction.", objectToActOn, is(objectThatWasActedOn));
	}

	@Test
	public void testSuccessNoRetry() throws ExecutionException, InterruptedException {
		objectUnderTest.failuresUntilSuccess(0);
		objectUnderTest.actOnItem(new Object());

		asyncActionService[0].get();

		assertThat("The asynchronousAction method was not called once and only once.", timesAsynchronousActionCalled, is(1));
	}

	@Test
	public void testNoSleepOnSuccess() throws ExecutionException, InterruptedException {
		objectUnderTest.failuresUntilSuccess(0);
		objectUnderTest.actOnItem(new Object());

		asyncActionService[0].get();

		verifyZeroInteractions(sleepService);
	}

	@Test
	public void testFailureRetry() throws ExecutionException, InterruptedException {
		int failuresUntilSuccess = 3;

		objectUnderTest.failuresUntilSuccess(failuresUntilSuccess);
		objectUnderTest.actOnItem(new Object());

		asyncActionService[0].get();

		assertThat("The asynchronousAction method was not called enough times.", timesAsynchronousActionCalled, is(failuresUntilSuccess + 1));
	}

	@Test
	public void testFailureRetryWithException() throws ExecutionException, InterruptedException {
		int failuresUntilSuccess = 3;

		objectUnderTest.failuresUntilSuccess(failuresUntilSuccess).throwExceptionOnFailure(true);
		objectUnderTest.actOnItem(new Object());

		asyncActionService[0].get();

		assertThat("The asynchronousAction method was not called enough times.", timesAsynchronousActionCalled, is(failuresUntilSuccess + 1));
	}

	@Test
	public void testSleepOnFailure() throws ExecutionException, InterruptedException {
		int failuresUntilSuccess = 2;

		objectUnderTest.failuresUntilSuccess(failuresUntilSuccess);
		objectUnderTest.actOnItem(new Object());

		asyncActionService[0].get();

		verify(sleepService, times(failuresUntilSuccess)).sleep(anyLong());
	}

	@Test
	public void testMultipleActsResultInAsynchronousActionsSuccess() throws ExecutionException, InterruptedException {
		int numberOfItemsToProcess = 3;

		objectUnderTest.failuresUntilSuccess(0).numberOfItemsToProcess(numberOfItemsToProcess);

		for(int currentItemIndex = 0; currentItemIndex < numberOfItemsToProcess; currentItemIndex++) {
			objectUnderTest.actOnItem(new Object());
		}

		asyncActionService[0].get();

		assertThat("The asynchronousAction method was not called as many times as it should have.", timesAsynchronousActionCalled, is(numberOfItemsToProcess));
	}

	@Test
	public void testMultipleActsResultInAsynchronousActionsFailure() throws ExecutionException, InterruptedException {
		int failuresUntilSuccess = 2;
		int numberOfItemsToProcess = 3;

		objectUnderTest.failuresUntilSuccess(failuresUntilSuccess).numberOfItemsToProcess(numberOfItemsToProcess);

		for(int currentItemIndex = 0; currentItemIndex < numberOfItemsToProcess; currentItemIndex++) {
			objectUnderTest.actOnItem(new Object());
		}

		asyncActionService[0].get();

		assertThat("The asynchronousAction method was not called as many times as it should have.", timesAsynchronousActionCalled, is((failuresUntilSuccess + 1) * numberOfItemsToProcess));
	}

	@Test
	public void testSleepServiceInterruptContinueRetries() throws InterruptedException, ExecutionException {
		doThrow(new InterruptedException()).when(sleepService).sleep(anyLong());
		int failuresUntilSuccess = 3;

		objectUnderTest.failuresUntilSuccess(failuresUntilSuccess);
		objectUnderTest.actOnItem(new Object());

		asyncActionService[0].get();

		assertThat("The asynchronousAction method was not called as many times as it should have.", timesAsynchronousActionCalled, is(failuresUntilSuccess + 1));
	}

	@Test
	public void testStopWaitForNewItems() throws ExecutionException, InterruptedException {
		int numberOfItemsToAdd = 3;

		objectUnderTest.failuresUntilSuccess(0).numberOfItemsToProcess(numberOfItemsToAdd).numberOfItemsToAdd(numberOfItemsToAdd);

		for(int currentItemIndex = 0; currentItemIndex < numberOfItemsToAdd + 1; currentItemIndex++) {
			objectUnderTest.actOnItem(new Object());
		}

		asyncActionService[0].get();

		assertThat("The putToExecutionQueue method was not called as many times as it should have.", timesPutCalled, is(numberOfItemsToAdd + 2));
	}

	private static class TestService<T> extends AsyncActionService<T> {

		private int failuresUntilSuccessTemplate = -1;
		private int failuresUntilSuccess = -1;
		private int numberOfItemsToProcess = 1;
		private int numberOfItemsToAdd = -1;
		private boolean throwExceptionOnFailure = false;

		public TestService<T> failuresUntilSuccess(int failuresUntilSuccess) {
			this.failuresUntilSuccessTemplate = failuresUntilSuccess;
			this.failuresUntilSuccess = this.failuresUntilSuccessTemplate;
			return this;
		}

		public TestService<T> numberOfItemsToProcess(int numberOfItemsToProcess) {
			this.numberOfItemsToProcess = numberOfItemsToProcess;
			return this;
		}

		public TestService<T> numberOfItemsToAdd(int numberOfItemsToAdd) {
			this.numberOfItemsToAdd = numberOfItemsToAdd;
			return this;
		}

		public TestService<T> throwExceptionOnFailure(boolean throwExceptionOnFailure) {
			this.throwExceptionOnFailure = throwExceptionOnFailure;
			return this;
		}

		@Override
		protected boolean asynchronousAction(final T objectToActOn) {
			asynchronousActionCalled = true;
			timesAsynchronousActionCalled++;
			objectThatWasActedOn = objectToActOn;

			if(failuresUntilSuccess != 0) {
				if(failuresUntilSuccess != -1) {
					failuresUntilSuccess--;
				}
				if(throwExceptionOnFailure) {
					throw new RuntimeException();
				}
				return false;
			}

			return true;
		}

		@Override
		protected void putToExecutionQueue(T objectToActOn) throws InterruptedException {
			timesPutCalled++;

			if(numberOfItemsToAdd-- == 0) {
				throw new InterruptedException();
			}

			super.putToExecutionQueue(objectToActOn);
		}

		@Override
		protected T takeFromExecutionQueue() throws InterruptedException {
			if(numberOfItemsToProcess-- == 0) {
				throw new InterruptedException();
			}

			failuresUntilSuccess = failuresUntilSuccessTemplate;
			return super.takeFromExecutionQueue();
		}
	}
}