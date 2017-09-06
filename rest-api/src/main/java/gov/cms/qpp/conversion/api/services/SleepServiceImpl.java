package gov.cms.qpp.conversion.api.services;

import org.springframework.stereotype.Service;

/**
 * A service for sleeping a thread.
 *
 * Use this instead of {@link Thread#sleep(long)} to allow for easy testing.  This will prevent long running tests due to
 * sleeping threads and prevent the need for using PowerMock to mock the static method.
 */
@Service
public class SleepServiceImpl implements SleepService {

	/**
	 * See {@link Thread#sleep(long)}.
	 *
	 * @param millis
	 * @throws InterruptedException
	 * @see Thread#sleep(long)
	 */
	@Override
	public void sleep(final long millis) throws InterruptedException {
		Thread.sleep(millis);
	}
}
