package gov.cms.qpp.conversion.util;

import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;


public class SubPopulationHelper {

	public static final Map<SubPopulationLabel, String> measureTypeMap;

	static {
		Map<SubPopulationLabel, String> measureTypeMapper = new EnumMap<>(SubPopulationLabel.class);
		measureTypeMapper.put(SubPopulationLabel.NUMER, "performanceMet");
		measureTypeMapper.put(SubPopulationLabel.DENOM, "eligiblePopulation");
		measureTypeMapper.put(SubPopulationLabel.DENEX, "eligiblePopulationExclusion");
		measureTypeMapper.put(SubPopulationLabel.DENEXCEP, "eligiblePopulationException");
		measureTypeMap = Collections.unmodifiableMap(measureTypeMapper);
	}
}
