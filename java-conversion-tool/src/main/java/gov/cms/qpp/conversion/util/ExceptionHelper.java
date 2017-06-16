package gov.cms.qpp.conversion.util;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;

public class ExceptionHelper {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(DefaultDecoder.class);

	private ExceptionHelper() {
	}

	public static <T> T runOrSilence(Callable<T> callable) {
		try {
			return callable.call();
		} catch (Exception ignore) {
			DEV_LOG.warn("Silenced exception", ignore);
			return null;
		}
	}

	public static <T> T runOrPropagate(Callable<T> callable) {
		try {
			return callable.call();
		} catch (Exception thrown) {
			throw propagated(thrown);
		}
	}

	public static RuntimeException propagated(Throwable exception) {
		RuntimeException propagated = ExceptionHelper.unwrap(exception, RuntimeException.class);
		return propagated == null ? new RuntimeException(exception) : propagated;
	}

	public static <X> X unwrap(Throwable caught, Class<X> desired) {
		if (caught == null) {
			return null;
		}

		if (desired.isInstance(caught)) {
			return desired.cast(caught);
		}

		return unwrap(caught.getCause(), desired);
	}

}
