package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;

class CpcPlusRoundTripTest {

	private static JsonWrapper wrapper = new JsonWrapper();
	private static ReadContext ctx;


	@BeforeAll
	static void setup() throws URISyntaxException, IOException {
		ApmEntityIds.setApmDataFile("test_apm_entity_ids.json");
		URL sample = CpcPlusRoundTripTest.class.getClassLoader()
				.getResource("cpc_plus/success/CPCPlus_CMSPrgrm_LowerCase_SampleQRDA-III.xml");
		Path path = Paths.get(sample.toURI());
		new JsonPathToXpathHelper(path, wrapper, false);
		ctx = JsonPath.parse(wrapper.toString());
	}

	@AfterAll
	static void resetApmIds() {
		ApmEntityIds.setApmDataFile(ApmEntityIds.DEFAULT_APM_ENTITY_FILE_NAME);
	}

	@Test
	void hasSingleNpiAndTin() {
		String tin = ctx.read("$.taxpayerIdentificationNumber");
		String npi = ctx.read("$.nationalProviderIdentifier");
		assertThat(tin).isEqualTo("990000099");
		assertThat(npi).isEqualTo("2567891421");
	}
}
