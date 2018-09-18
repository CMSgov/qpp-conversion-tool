package gov.cms.qpp.conversion.correlation;

import static com.google.common.truth.Truth.assertWithMessage;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;

class QrdaQppAssociationTest {

	private static JsonWrapper qpp;
	private static ValueOriginMapper mapper = new ValueOriginMapper();

	@BeforeAll
	static void setup() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		Converter converter = new Converter(new PathSource(path));

		qpp = converter.transform();
	}

	@Test
	void testAssociation() {
		mapper.mapIt("$", qpp.getObject());
		mapper.writeAssociations();

		assertWithMessage("registered associations does not match expectation")
				.that(mapper.getAssociations()).hasSize(62);
	}

}
