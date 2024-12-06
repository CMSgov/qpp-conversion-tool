package gov.cms.qpp.test.logging;

import java.util.List;
import java.util.stream.Collectors;

import gov.cms.qpp.test.annotations.AroundEach;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.github.valfirst.slf4jtest.LoggingEvent;
import com.github.valfirst.slf4jtest.TestLogger;

public interface LoggerContract {

	@AroundEach
	default void clearLogs() {
		getLogger().clear();
		getLogger().clearAll();
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
