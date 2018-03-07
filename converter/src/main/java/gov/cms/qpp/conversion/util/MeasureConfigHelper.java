package gov.cms.qpp.conversion.util;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;

public class MeasureConfigHelper {

	public static final String MEASURE_ID = "measureId";

	private MeasureConfigHelper() {
		// private for this helper class
	}

	public static MeasureConfig getMeasureConfig(Node node) {
		String measureId =  node.getValue(MEASURE_ID);
		return MeasureConfigs.getConfigurationMap().get(measureId);
	}
}
