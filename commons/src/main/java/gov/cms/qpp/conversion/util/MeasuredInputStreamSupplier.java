package gov.cms.qpp.conversion.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;

public class MeasuredInputStreamSupplier implements Supplier<InputStream> {

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

	@Override
	public InputStream get() {
		return delegate.get();
	}

	public int size() {
		return size;
	}

}
