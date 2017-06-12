package gov.cms.qpp.conversion.util;

import java.util.concurrent.Callable;

public class ExceptionHelper {

	public static <T> T runOrSilence(Callable<T> callable) {
		try {
			return callable.call();
		} catch (Exception thrown) {
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

	private ExceptionHelper() {
	}

}
