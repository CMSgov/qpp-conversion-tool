package gov.cms.qpp.conversion.correlation;

import static com.google.common.truth.Truth.assertWithMessage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.correlation.ValueOriginMapper.Association;
import gov.cms.qpp.conversion.encode.JsonWrapper;
//
//class QrdaQppAssociationTest {
//
//	private static JsonWrapper qpp;
//	private static ValueOriginMapper mapper = new ValueOriginMapper();
//
//	@BeforeAll
//	static void setup() {
//		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
//		Context context = new Context();
//		Converter converter = new Converter(new PathSource(path), context);
//
//		qpp = converter.transform();
//	}
//
//	@Test
//	void testAssociation() {
//		mapper.mapItJsW("$", qpp);
//		mapper.writeAssociations();
//
//		List<Association> associations = mapper.getAssociations();
//
//		assertWithMessage("registered associations does not match expectation")
//				.that(associations).isNotEmpty();
//	}
//
//}
