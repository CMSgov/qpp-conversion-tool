package gov.cms.qpp.conversion;

import java.io.InputStream;
import java.util.Objects;

public class InputStreamQrdaSource extends SkeletalQrdaSource {

	private final InputStream stream;

	public InputStreamQrdaSource(String name, InputStream stream) {
		super(name);

		Objects.requireNonNull(stream, "stream");
		this.stream = stream;
	}

	@Override
	public InputStream toInputStream() {
		return stream;
	}

}