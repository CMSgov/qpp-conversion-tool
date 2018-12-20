package gov.cms.qpp.acceptance;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

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


	@ParameterizedTest
	@ValueSource(strings = { "entityId", "entityType", "measurementSets", "performanceYear" })
	void hasAppropriateTopLevelAttributes(String value) {
		assertThat(ctx.<Object>read("$." + value)).isNotNull();
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
