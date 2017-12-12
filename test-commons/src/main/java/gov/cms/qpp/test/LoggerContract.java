package gov.cms.qpp.test;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public interface LoggerContract {

	@AfterEach
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
