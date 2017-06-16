package gov.cms.qpp.conversion;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*"})
public class MockedConversionFileWriterWrapperTest extends ConversionFileWriterWrapperTestSuite {

	@Before
	public void setupMocks() throws IOException {
		mockStatic(Files.class);
		when(Files.newBufferedWriter(any(Path.class))).thenThrow(new IOException());
	}

	@Test
	@PrepareForTest({Files.class, ConversionFileWriterWrapper.class})
	public void testFailureToWriteQpp() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		transform(converterWrapper);

		assertFileDoesNotExists("valid-QRDA-III.qpp.json");
	}

	@Test
	@PrepareForTest({Files.class, ConversionFileWriterWrapper.class})
	public void testFailureToWriteErrors() {
		Path path = Paths.get("src/test/resources/not-a-QRDA-III-file.xml");
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);

		transform(converterWrapper);

		assertFileDoesNotExists("not-a-QRDA-III-file.err.json");
	}

}