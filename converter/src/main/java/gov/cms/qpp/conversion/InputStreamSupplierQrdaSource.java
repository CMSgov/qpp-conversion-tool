package gov.cms.qpp.conversion;

import java.io.InputStream;
import java.util.Objects;
import java.util.function.Supplier;

public class InputStreamSupplierQrdaSource extends SkeletalQrdaSource {

	/**
	 * The intent is that the supplier will provide a new {@link InputStream} each time it is invoked.
	 */
	private final Supplier<InputStream> streamSupplier;
	private final long size;

	public InputStreamSupplierQrdaSource(String name, Supplier<InputStream> supplier, long size) {
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
