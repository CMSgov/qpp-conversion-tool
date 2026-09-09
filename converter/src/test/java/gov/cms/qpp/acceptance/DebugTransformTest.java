package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class DebugTransformTest {

	@Test
	void debugValidQrdaLatest() {
		Path file = Path.of("../qrda-files/valid-QRDA-III-latest.xml");
		Converter converter = new Converter(new PathSource(file));
		try {
			converter.transform();
		} catch (TransformException te) {
			te.getDetails().getErrors().forEach(e ->
				e.getDetails().forEach(d -> System.out.println(" - " + d.getMessage() + " | " + d.getErrorCode()
					+ " | path=" + (d.getLocation() == null ? null : d.getLocation().getPath()))));
		}
	}

	@Test
	void debugAppCehrt() {
		Path file = Path.of("src/test/resources/app/2025/App1-ApmEntity-Qrda-III.xml");
		Converter converter = new Converter(new PathSource(file));
		gov.cms.qpp.conversion.encode.JsonWrapper qpp = null;
		try {
			qpp = converter.transform();
		} catch (TransformException te) {
		}
	}

	@Test
	void debugAppCehrtNodeTree() throws Exception {
		Path file = Path.of("src/test/resources/app/2025/App1-ApmEntity-Qrda-III.xml");
		gov.cms.qpp.conversion.Context context = new gov.cms.qpp.conversion.Context();
		String xml = java.nio.file.Files.readString(file);
		gov.cms.qpp.conversion.model.Node root = new gov.cms.qpp.conversion.decode.QrdaDecoderEngine(context)
			.decode(gov.cms.qpp.conversion.xml.XmlUtils.stringToDom(xml));
	}
}
