package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.QrdaSource;
import gov.cms.qpp.conversion.api.model.TransformResult;

/**
 * Interface of the QRDA-III to QPP Conversion Service
 */
public interface QrdaService {

	/**
	 * Converts a given a input stream with to conversion result content
	 *
	 * @param source Object to be converted
	 * @return Results of the conversion
	 */
	TransformResult convertQrda3ToQpp(QrdaSource source);
}
