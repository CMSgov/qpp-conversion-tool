package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.Source;

import java.io.InputStream;

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
	ConversionReport convertQrda3ToQpp(Source source);

	/**
	 * Opens a stream to s3 to retrieve the Cpc Plus Validation file for the QPP Service
	 *
	 * @return cpc+ validation file.
	 */
	InputStream retrieveS3CpcPlusValidationFile();
}
