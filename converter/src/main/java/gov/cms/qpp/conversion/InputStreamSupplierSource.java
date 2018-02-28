package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.util.MeasuredInputStreamSupplier;

import java.io.InputStream;
import java.util.Objects;
import java.util.function.Supplier;


/**
 * A {@link Source} represented by a {@link Supplier} of an {@link InputStream}.
 */
public class InputStreamSupplierSource extends SkeletalSource {

	/**
	 * The intent is that the supplier will provide a new {@link InputStream} each time it is invoked.
	 */
	private final MeasuredInputStreamSupplier stream;

	private final boolean test;

	/**
	 * Creates a new Source with the given name and {@link Supplier}.
	 *
	 * Because the size is not specified, this constructor loads the {@link InputStream} into memory to calculate the size.
	 *
	 * @param name The name of the source.
	 * @param source an {@link InputStream}.
	 */
	public InputStreamSupplierSource(String name, InputStream source) {
		this(name, source, false);
	}

	/**
	 * Creates a new Source with the given name and {@link Supplier}.
	 *
	 * Because the size is not specified, this constructor loads the {@link InputStream} into memory to calculate the size.
	 *
	 * @param name The name of the source.
	 * @param source an {@link InputStream}.
	 * @param test Whether the source is a test
	 */
	public InputStreamSupplierSource(String name, InputStream source, boolean test) {
		super(name);

		Objects.requireNonNull(source, "source");

		this.stream = MeasuredInputStreamSupplier.terminallyTransformInputStream(source);
		this.test = test;
	}

	/**
	 * An {@link InputStream} given from the supplier.
	 *
	 * @return An InputStream representing the source.
	 */
	@Override
	public InputStream toInputStream() {
		return stream.get();
	}

	/**
	 * The size of the {@link InputStream} that was previously specified.
	 *
	 * @return The source's size.
	 */
	@Override
	public long getSize() {
		return stream.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTest() {
		return test;
	}
}
