package gov.cms.qpp.conversion;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.Supplier;

public class InputStreamSupplierSource extends SkeletalSource {

	/**
	 * The intent is that the supplier will provide a new {@link InputStream} each time it is invoked.
	 */
	private final Supplier<InputStream> streamSupplier;
	private final long size;

	public InputStreamSupplierSource(String name, Supplier<InputStream> supplier) {
		super(name);

		Objects.requireNonNull(supplier, "supplier");

		try {
			final byte[] byteArray = IOUtils.toByteArray(supplier.get());
			streamSupplier = () -> new ByteArrayInputStream(byteArray);
			this.size = byteArray.length;
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	public InputStreamSupplierSource(String name, Supplier<InputStream> supplier, long size) {
		super(name);

		Objects.requireNonNull(supplier, "supplier");
		streamSupplier = supplier;
		this.size = size;
	}

	@Override
	public InputStream toInputStream() {
		return streamSupplier.get();
	}

	@Override
	public long getSize() {
		return size;
	}
}
