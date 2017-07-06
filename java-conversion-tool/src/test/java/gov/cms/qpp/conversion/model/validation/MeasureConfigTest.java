package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.reflections.util.ClasspathHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class MeasureConfigTest {

	@Test
	public void testMeasureConfig() {
		ObjectMapper mapper = new ObjectMapper();

		InputStream stream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("measures-data-aci-test.json");
		List<MeasureConfig> measureConfigs = null;

		try {
			TypeReference<List<MeasureConfig>> measureConfigType = new TypeReference<List<MeasureConfig>>() {};
			measureConfigs = mapper.readValue(stream, measureConfigType);
		} catch (IOException e) {
			throw new IllegalArgumentException("failure to correctly read measures config json", e);
		}

		assertThat("measure configs should not be empty", measureConfigs, hasSize(1));
		MeasureConfig theConfig = measureConfigs.get(0);

		assertThat("category should be aci", theConfig.getCategory(), is("aci"));
		assertThat("firstPerformanceYear should be 2017", theConfig.getFirstPerformanceYear(), is(2017));
		assertThat("lastPerformanceYear should be 0", theConfig.getLastPerformanceYear(), is(0));
		assertThat("metricType should be proportion", theConfig.getMetricType(), is("proportion"));
		assertThat("measureId should be ACI_EP_1", theConfig.getMeasureId(), is("ACI_EP_1"));
		assertThat("title should be e-Prescribing", theConfig.getTitle(), is("e-Prescribing"));
		assertThat("description should be long", theConfig.getDescription(), is(
				"At least one permissible prescription written by the MIPS eligible clinician is queried for a drug formulary and transmitted electronically using certified EHR technology."));
		assertThat("isRequired should be true", theConfig.isRequired(), is(true));
		assertThat("measureSet should be null", theConfig.getMeasureSet(), is(nullValue()));
		assertThat("isBonus should be false", theConfig.isBonus(), is(false));
		assertThat("objective should be electronicPrescribing", theConfig.getObjective(), is("electronicPrescribing"));
	}

	@Test
	public void testSubPopulations() {
		SubPopulation subPopulation1 = new SubPopulation();
		subPopulation1.setNumeratorUuid("subPopulation1");

		SubPopulation subPopulation2 = new SubPopulation();
		subPopulation1.setNumeratorUuid("subPopulation2");

		SubPopulation subPopulation3 = new SubPopulation();
		subPopulation1.setNumeratorUuid("subPopulation3");

		Strata strata1 = new Strata();
		strata1.setElectronicMeasureUuids(subPopulation1);
		Strata strata2 = new Strata();
		strata2.setElectronicMeasureUuids(subPopulation2);
		Strata strata3 = new Strata();
		strata3.setElectronicMeasureUuids(subPopulation3);

		MeasureConfig measureConfig = new MeasureConfig();
		measureConfig.setStrata(Arrays.asList(strata1, strata2, strata3));

		List<SubPopulation> subPopulations = measureConfig.getSubPopulation();

		assertThat("The subpopulations are incorrect.", subPopulations, containsInAnyOrder(subPopulation1, subPopulation2, subPopulation3));
	}
}
