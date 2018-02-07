package gov.cms.qpp.conversion.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;

/**
 * A reusable input stream supplier that is first measured for length
 */
public class MeasuredInputStreamSupplier implements Supplier<InputStream> {

	/**
	 * Terminally uses an {@link InputStream} to create a {@link MeasuredInputStreamSupplier}
	 *
	 * @param source the source for the new {@link MeasuredInputStreamSupplier}. Must not be null.
	 * @return a new {@link MeasuredInputStreamSupplier} from the given {@link InputStream}
	 */
	public static MeasuredInputStreamSupplier terminallyTransformInputStream(InputStream source) {
		Objects.requireNonNull(source, "source");

		return new MeasuredInputStreamSupplier(source);
	}

	private final Supplier<InputStream> delegate;
	private final int size;

	private MeasuredInputStreamSupplier(InputStream source) {
		byte[] byteArray;
		try {
			byteArray = IOUtils.toByteArray(source);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		delegate = () -> new ByteArrayInputStream(byteArray);
		this.size = byteArray.length;
	}

	/**
	 * Gets a new one-time use {@link InputStream}
	 *
	 * @return A new one-time use {@link InputStream}, from the original source
	 */
	@Override
	public InputStream get() {
		return delegate.get();
	}

	/**
	 * Gets the size of the original {@link InputStream}
	 *
	 * @return The size of the original {@link InputStream}. This is cached.
	 */
	public int size() {
		return size;
	}

}
