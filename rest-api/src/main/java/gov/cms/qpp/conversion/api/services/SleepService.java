package gov.cms.qpp.conversion.api.services;

/**
 * A service for sleeping a thread.
 *
 * Use this instead of {@link Thread#sleep(long)} to allow for easy testing.  This will prevent long running tests due to
 * sleeping threads and prevent the need for using PowerMock to mock the static method.
 */
public interface SleepService {
	/**
	 * See {@link Thread#sleep(long)}.
	 *
	 * @param millis
	 * @throws InterruptedException
	 * @see Thread#sleep(long)
	 */
	public void sleep(long millis) throws InterruptedException;
}
