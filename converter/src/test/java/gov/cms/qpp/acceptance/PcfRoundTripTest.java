package gov.cms.qpp.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.CacheBuilder;
import gov.cms.qpp.model.CacheType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;

import static com.google.common.truth.Truth.assertThat;
import static gov.cms.qpp.conversion.model.Constants.ENTITY_APM;

/**
 * Positive Testing Scenarios to check PCF for valid top level attributes and validations
 * Ensures:
 * - valid apm entity
 * - Sample file with valid test tin/npi and measurement sets for 2025 go through without issue.
 */
public class PcfRoundTripTest {
	private static JsonWrapper wrapper = new JsonWrapper();
	private static HashMap<String, Object> json;

	@SuppressWarnings("unchecked")
	@BeforeAll
	static void setup() throws URISyntaxException, IOException {
		ApmEntityIds apmEntityIds = CacheBuilder.getEntityIds(CacheType.ApmPcfEntityIds);
		URL sample = PcfRoundTripTest.class.getClassLoader()
			.getResource("pcf/success/PCF_Sample_QRDA-III.xml");
		Path path = Path.of(sample.toURI());
		new JsonPathToXpathHelper(path, wrapper, false, new Context(apmEntityIds));
		json = new ObjectMapper().readValue(wrapper.copyWithoutMetadata().toString(), HashMap.class);
	}

	@AfterAll
	static void resetMeasuresData() {
		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@ParameterizedTest
	@ValueSource(strings = { "entityId", "entityType", "measurementSets", "performanceYear" })
	void testValidTopLevelAttributes(String value) {
		assertThat(json.get(value)).isNotNull();
	}

	@Test
	void testNoInvalidTopLevelAttributes() {
		assertThat(json.keySet()).containsExactly("entityId", "entityType", "measurementSets",
			"performanceYear");
	}

	@Test
	void testEntityId() {
		String entityId = (String) json.get("entityId");
		assertThat(entityId).isEqualTo("PcfTestApmEntityId");
	}

	@Test
	void testEntityType() {
		String entityId = (String) json.get("entityType");
		assertThat(entityId).isEqualTo(ENTITY_APM);
	}
}
