package gov.cms.qpp.conversion.model.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MeasureConfigTest {

	@Test
	public void testMeasureConfig() {
		ObjectMapper mapper = new ObjectMapper();

		ClassPathResource measuresConfigResource = new ClassPathResource("measures-data-aci-test.json");

		MeasureConfigs measureConfigs = null;

		try {
			measureConfigs = mapper.treeToValue(mapper.readTree(measuresConfigResource.getInputStream()),
					MeasureConfigs.class);
		} catch (IOException e) {
			throw new IllegalArgumentException("failure to correctly read measures config json", e);
		}

		assertThat("measure configs should not be empty", measureConfigs.getMeasureConfigs(), hasSize(1));
		List<MeasureConfig> configs = measureConfigs.getMeasureConfigs();
		MeasureConfig theConfig = configs.get(0);

		assertThat("category should be aci", theConfig.getCategory(), is("aci"));
		assertThat("firstPerformanceYear should be 2017", theConfig.getFirstPerformanceYear(), is(2017));
		assertThat("lastPerformanceYear should be 0", theConfig.getLastPerformanceYear(), is(0));
		assertThat("metricType should be proportion", theConfig.getMetricType(), is("proportion"));
		assertThat("measureId should be ACI_EP_1", theConfig.getMeasureId(), is("ACI_EP_1"));
		assertThat("title should be e-Prescribing", theConfig.getTitle(), is("e-Prescribing"));
		assertThat("description should be long", theConfig.getDescription(), is(
				"At least one permissible prescription written by the MIPS eligible clinician is queried for a drug formulary and transmitted electronically using certified EHR technology."));
		assertThat("isRequired should be true", theConfig.isRequired(), is(true));
		assertThat("weight should be 0", theConfig.getWeight(), is(0));
		assertThat("measureSet should be null", theConfig.getMeasureSet(), is(nullValue()));
		assertThat("isBonus should be false", theConfig.isBonus(), is(false));
		assertThat("objective should be electronicPrescribing", theConfig.getObjective(), is("electronicPrescribing"));
	}
}
