package gov.cms.qpp.conversion.correlation;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class QrdaQppAssociationTest {
	private static JsonWrapper qpp;
	private static ValueOriginMapper mapper = new ValueOriginMapper();

	@BeforeClass
	public static void setup() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		Converter converter = new Converter(new PathQrdaSource(path));

		qpp = converter.transform();
	}

	@Test
	public void testAssociation() {
		mapper.mapIt("$", qpp.getObject());
		mapper.writeAssociations();

		assertThat("registered associations does not match expectation",
				mapper.getAssociations().size(), is(60));
	}
}
