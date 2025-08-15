package gov.cms.qpp.conversion.util;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MeasuredInputStreamSupplierTest {

	@Test
	void testTerminallyTransformInputStreamIsTerminal() throws IOException {
		InputStream use = stream("mock");
		MeasuredInputStreamSupplier.terminallyTransformInputStream(use);
		Truth.assertThat(use.available()).isEqualTo(0);
	}

	@Test
	void testTerminallyTransformInputStreamOnUsed() {
		InputStream used = stream("mock");
		MeasuredInputStreamSupplier.terminallyTransformInputStream(used);

		MeasuredInputStreamSupplier objectToTest = MeasuredInputStreamSupplier.terminallyTransformInputStream(used);
		Truth.assertThat(objectToTest.size()).isEqualTo(0);
	}

	@Test
	void testSize() {
		InputStream use = stream("mock");
		Truth.assertThat(MeasuredInputStreamSupplier.terminallyTransformInputStream(use).size())
				.isEqualTo("mock".length());
	}

	@Test
	void testGetInputStreamReturnsUnique() {
		InputStream use = stream("mock");
		MeasuredInputStreamSupplier objectToTest = MeasuredInputStreamSupplier.terminallyTransformInputStream(use);
		int expected = 5;
		long count = Stream.generate(objectToTest::get).limit(expected).distinct().count();
		Truth.assertThat(count).isEqualTo(expected);
	}

	@Test
	void testGetInputStreamContentIsSameEachTime() throws IOException {
		InputStream use = stream("data");
		MeasuredInputStreamSupplier supplier = MeasuredInputStreamSupplier.terminallyTransformInputStream(use);

		for (int i = 0; i < 3; i++) {
			try (InputStream in = supplier.get()) {
				String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
				assertEquals("data", content);
			}
		}
	}

	@Test
	void testNullSourceThrowsNpe() {
		NullPointerException ex = assertThrows(NullPointerException.class,
				() -> MeasuredInputStreamSupplier.terminallyTransformInputStream(null));
		assertEquals("source", ex.getMessage());
	}

	@Test
	void testIOExceptionWrapsInUncheckedIOException() {
		InputStream brokenStream = new InputStream() {
			@Override
			public int read() throws IOException {
				throw new IOException("boom");
			}
		};
		UncheckedIOException ex = assertThrows(UncheckedIOException.class,
				() -> MeasuredInputStreamSupplier.terminallyTransformInputStream(brokenStream));
		assertEquals("boom", ex.getCause().getMessage());
	}

	private InputStream stream(String data) {
		return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
	}
}
