package gov.cms.qpp.test.net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class InternetExecutionCondition implements ExecutionCondition {

	// Cache the result of the first reachability check (null = not yet checked)
	private static volatile Boolean connected;

	static {
		try {
			connected = InetAddress
					.getByName("google.com")
					.isReachable((int) TimeUnit.SECONDS.toMillis(3));
		} catch (IOException expected) {
		}
	}

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		if (isConnected()) {
			return ConditionEvaluationResult.enabled("The system is connected to the internet");
		}
		return ConditionEvaluationResult.disabled("The system is NOT connected to the internet");
	}

	/**
	 * Lazily performs one more reachability check, once, in a thread-safe,
	 * class-level synchronized method (no instance lock).
	 */
	private static synchronized boolean isConnected() {
		if (connected == null) {
			try {
				connected = InetAddress
						.getByName("google.com")
						.isReachable((int) TimeUnit.SECONDS.toMillis(3));
			} catch (IOException expected) {
				connected = false;
			}
		}
		return connected;
	}
}
