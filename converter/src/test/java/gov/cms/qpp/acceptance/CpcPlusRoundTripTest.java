package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;

class CpcPlusRoundTripTest {

	private static JsonWrapper wrapper = new JsonWrapper();
	private static ReadContext ctx;

	@BeforeAll
	static void setup() throws URISyntaxException {
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
	void hasTopLevelElements() {
		List<Object> toplevelAttributes = ctx.read("$.*");
		assertThat(toplevelAttributes.size()).isEqualTo(4);

//		assertThat(entityId).isEqualTo(ClinicalDocumentDecoder.ENTITY_APM);
	}

	@Test
	void hasEntityId() {
		String entityId = ctx.read("$.entityId");
		assertThat(entityId).isEqualTo("TestApmEntityId");
	}

	@Test
	void hasEntityType() {
		String entityId = ctx.read("$.entityType");
		assertThat(entityId).isEqualTo(ClinicalDocumentDecoder.ENTITY_APM);
	}

}
