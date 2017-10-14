package gov.cms.qpp.conversion;

import java.io.InputStream;
import java.util.Objects;
import java.util.function.Supplier;

public class InputStreamSupplierQrdaSource extends SkeletalQrdaSource {

	private final Supplier<InputStream> streamSupplier;

	public InputStreamSupplierQrdaSource(String name, Supplier<InputStream> supplier) {
		super(name);

		Objects.requireNonNull(supplier, "supplier");
		streamSupplier = supplier;
	}

	@Override
	public InputStream toInputStream() {
		return streamSupplier.get();
	}

}