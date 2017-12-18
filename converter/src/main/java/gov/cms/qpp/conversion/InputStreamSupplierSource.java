package gov.cms.qpp.conversion;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A {@link Source} represented by a {@link Supplier} of an {@link InputStream}.
 */
public class InputStreamSupplierSource extends SkeletalSource {

	/**
	 * The intent is that the supplier will provide a new {@link InputStream} each time it is invoked.
	 */
	private final Supplier<InputStream> streamSupplier;
	private final long size;

	/**
	 * Creates a new Source with the given name and {@link Supplier}.
	 *
	 * Because the size is not specified, this constructor loads the {@link InputStream} into memory to calculate the size.
	 *
	 * @param name The name of the source.
	 * @param supplier The supplier of an {@link InputStream}.
	 */
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

	/**
	 * Creates a new Source with the given name, {@link Supplier}, and size.
	 *
	 * The size doesn't need to be the actual size of the data inside the {@link InputStream}, but this will cause inconsistent
	 * results down the line.
	 *
	 * @param name The name of the source.
	 * @param supplier The supplier of an {@link InputStream}.
	 * @param size The size of the data inside the {@link InputStream}.
	 */
	public InputStreamSupplierSource(String name, Supplier<InputStream> supplier, long size) {
		super(name);

		Objects.requireNonNull(supplier, "supplier");
		streamSupplier = supplier;
		this.size = size;
	}

	/**
	 * An {@link InputStream} given from the supplier.
	 *
	 * @return An InputStream representing the source.
	 */
	@Override
	public InputStream toInputStream() {
		return streamSupplier.get();
	}

	/**
	 * The size of the {@link InputStream} that was previously specified.
	 *
	 * @return The source's size.
	 */
	@Override
	public long getSize() {
		return size;
	}
}
