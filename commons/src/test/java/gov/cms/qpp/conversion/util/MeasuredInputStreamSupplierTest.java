package gov.cms.qpp.conversion.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;

class MeasuredInputStreamSupplierTest {

	@Test
	void testTerminallyTransformInputStreamIsTerminal() throws IOException {
		InputStream use = stream("mock");
		MeasuredInputStreamSupplier.terminallyTransformInputStream(use);
		Truth.assertThat(use.available()).isEqualTo(0);
	}

	@Test
	void testTerminallyTransformInputStreamOnUsed() throws IOException {
		InputStream used = stream("mock");
		MeasuredInputStreamSupplier.terminallyTransformInputStream(used);

		MeasuredInputStreamSupplier objectToTest = MeasuredInputStreamSupplier.terminallyTransformInputStream(used);
		Truth.assertThat(objectToTest.size()).isEqualTo(0);
	}

	@Test
	void testSize() throws IOException {
		InputStream use = stream("mock");
		Truth.assertThat(MeasuredInputStreamSupplier.terminallyTransformInputStream(use).size()).isEqualTo("mock".length());
	}

	@Test
	void testGetInputStreamReturnsUnique() throws IOException {
		InputStream use = stream("mock");
		MeasuredInputStreamSupplier objectToTest = MeasuredInputStreamSupplier.terminallyTransformInputStream(use);
		int expected = 5;
		long count = Stream.generate(objectToTest::get).limit(expected).distinct().count();
		Truth.assertThat(count).isEqualTo(expected);
	}

	private InputStream stream(String data) {
		return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
	}

}
