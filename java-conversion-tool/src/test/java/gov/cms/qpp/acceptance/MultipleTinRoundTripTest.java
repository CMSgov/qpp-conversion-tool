package gov.cms.qpp.acceptance;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;

public class MultipleTinRoundTripTest {
	private static JsonWrapper wrapper = new JsonWrapper();
	private static ReadContext ctx;

	@BeforeClass
	public static void setup() throws Exception {
		Path path = Paths.get("../qrda-files/ComprehensivePrimaryCare_Sample_QRDA_III.xml");
		new JsonPathToXpathHelper(path, wrapper, false);
		ctx = JsonPath.parse(wrapper.toString());
	}

	@Test
	public void hasMultipleNpiTinCombo() {
		List<Map<String, Object>> topLevel = ctx.read("$");
		assertThat("There should be five", topLevel.size(), is(5));
	}

}
