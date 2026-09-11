package gov.cms.qpp.conversion.api.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.OutputStreamAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

/**
 * Loads the real logback.xml and verifies every emitted log line is valid, parseable JSON
 * (guards against a regression back to the old broken pseudo-JSON pattern).
 */
class LogbackJsonFormatIntegrationTest {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private LoggerContext context;

	@AfterEach
	void cleanup() {
		if (context != null) {
			context.stop();
		}
		MDC.clear();
	}

	@Test
	void emitsValidJsonWithExpectedMdcFields() throws Exception {
		context = new LoggerContext();
		// Use the same MDCAdapter the static org.slf4j.MDC facade writes through, so values set via
		// MDC.put(...) below are actually visible to this standalone context's appenders.
		context.setMDCAdapter(MDC.getMDCAdapter());
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(context);
		URL logbackConfig = getClass().getClassLoader().getResource("logback.xml");
		assertWithMessage("logback.xml must be present on the test classpath").that(logbackConfig).isNotNull();
		configurator.doConfigure(logbackConfig);

		// Redirect the configured appender's output stream directly, since ConsoleAppender caches
		// System.out/System.err at class-load time and won't observe a later System.setOut() swap.
		Appender<?> appender = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).getAppender("STDOUT_JSON");
		assertWithMessage("STDOUT_JSON appender must be configured in logback.xml").that(appender).isNotNull();
		OutputStreamAppender<?> outputStreamAppender = (OutputStreamAppender<?>) appender;
		ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
		outputStreamAppender.setOutputStream(capturedOut);

		Logger logger = context.getLogger("gov.cms.qpp.conversion.api.config.LogbackJsonFormatIntegrationTest");

		MDC.put("requestId", "a1b2c3d4-e5f6-7890-abcd-ef1234567890");
		MDC.put("httpMethod", "GET");
		MDC.put("statusCode", "200");
		try {
			logger.info("Request processing completed");
		} finally {
			MDC.clear();
		}
		context.stop();

		String rawOutput = capturedOut.toString(StandardCharsets.UTF_8);
		List<String> lines = rawOutput.lines().toList();
		assertWithMessage("expected log output; status manager reported: "
				+ context.getStatusManager().getCopyOfStatusList())
				.that(lines).isNotEmpty();

		for (String line : lines) {
			JsonNode json = MAPPER.readTree(line);
			assertWithMessage("every emitted log line must be a valid JSON object: " + line)
					.that(json.isObject()).isTrue();
		}

		JsonNode logEntry = MAPPER.readTree(lines.get(lines.size() - 1));
		assertThat(logEntry.get("message").asText()).isEqualTo("Request processing completed");
		assertThat(logEntry.get("requestId").asText()).isEqualTo("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
		assertThat(logEntry.get("httpMethod").asText()).isEqualTo("GET");
		assertThat(logEntry.get("statusCode").asText()).isEqualTo("200");
		assertThat(logEntry.has("@version")).isFalse();
	}
}

