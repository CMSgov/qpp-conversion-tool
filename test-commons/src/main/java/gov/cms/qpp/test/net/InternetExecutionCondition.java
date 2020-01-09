package gov.cms.qpp.test.net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class InternetExecutionCondition implements ExecutionCondition {

	private static Boolean connected;

	static {
		try {
			connected = InetAddress.getByName("google.com").isReachable((int) TimeUnit.SECONDS.toMillis(3));
		} catch (IOException expected) {
		}
	}

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		if (connected()) {
			return ConditionEvaluationResult.enabled("The system is connected to the internet");
		}
		return ConditionEvaluationResult.disabled("The system is NOT connected to the internet");
	}

	private synchronized boolean connected() {
		if (connected == null) {
			try {
				connected = InetAddress.getByName("google.com").isReachable((int) TimeUnit.SECONDS.toMillis(3));
			} catch (IOException expected) {
				connected = false;
			}
		}
		return connected;
	}

}
