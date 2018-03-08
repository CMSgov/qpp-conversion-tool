package gov.cms.qpp.conversion.util;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;

import static com.google.common.truth.Truth.assertThat;

public class MeasureConfigHelperTest {

	@Test
	void testGetMeasureConfigSuccess() {
		Node measureNode = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
		measureNode.putValue(MeasureConfigHelper.MEASURE_ID, "40280381-51f0-825b-0152-229bdcab1702");
		MeasureConfig config = MeasureConfigHelper.getMeasureConfig(measureNode);

		assertThat(config).isNotNull();
	}
}
