package gov.cms.qpp.conversion.api.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SleepServiceImpl.class)
public class SleepServiceImplTest {

	SleepService objectUnderTest = new SleepServiceImpl();

	@Test
	public void testSleep() throws Exception {

		PowerMockito.mockStatic(Thread.class);
		objectUnderTest.sleep(4321);

		verifyStatic(Thread.class, times(1));
		Thread.sleep(eq(4321L));
	}
}
