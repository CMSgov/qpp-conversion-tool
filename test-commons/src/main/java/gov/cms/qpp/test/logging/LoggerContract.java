package gov.cms.qpp.test.logging;

import java.util.List;
import java.util.stream.Collectors;

import gov.cms.qpp.test.annotations.AroundEach;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public interface LoggerContract {

	@AroundEach
	default void clearLogs() {
		getLogger().clear();
	}

	default List<String> getLogs() {
		return getLogger().getAllLoggingEvents()
				.stream()
				.map(LoggingEvent::getMessage)
				.collect(Collectors.toList());
	}

	default TestLogger getLogger() {
		return TestLoggerFactory.getTestLogger(getLoggerType());
	}

	Class<?> getLoggerType();

}
