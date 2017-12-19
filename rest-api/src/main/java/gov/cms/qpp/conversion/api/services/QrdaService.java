package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.Source;

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
	Converter.ConversionReport convertQrda3ToQpp(Source source);
}
