package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class HierarchicalEncoderTest {
	private static Converter converter;
	private HierarchicalEncoder encoder = new HierarchicalEncoder();

	@BeforeClass
	public static void setup() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		converter = new Converter(new PathQrdaSource(path));
		converter.transform();
	}

	@Test
	public void testHierachy() {
		JsonWrapper hierarchy = new JsonWrapper();
		encoder.internalEncode(hierarchy, converter.getReport().getDecoded());

		assertThat(hierarchy.isObject()).isTrue();
	}

}
