package gov.cms.qpp.test.net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class InternetArgumentSource implements ArgumentsProvider {

	private static boolean CONNECTED;

	static {
		try {
			CONNECTED = InetAddress.getByName("google.com").isReachable((int) TimeUnit.SECONDS.toMillis(3));
		} catch (IOException expected) {
		}
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
		Assumptions.assumeTrue(CONNECTED, "The system is not connected to the internet");
		return Stream.empty();
	}

}
