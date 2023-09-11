package gov.cms.qpp.conversion.api.services.internal;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.api.internal.pii.SpecPiiValidator;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.PcfValidationInfoMap;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.StorageService;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the QRDA-III to QPP conversion service
 */
@Service
public class QrdaServiceImpl implements QrdaService {

	private static final Logger API_LOG = LoggerFactory.getLogger(QrdaServiceImpl.class);

	private final StorageService storageService;

	private Supplier<PcfValidationInfoMap> cpcValidationData;

	private Supplier<ApmEntityIds> apmData;

	@Autowired
	QrdaServiceImpl(StorageService storageService) {
		this.storageService = storageService;
		this.cpcValidationData = () -> null;
		this.apmData = () -> null;
	}

	/**
	 * Preloads the measure configs data
	 */
	@PostConstruct
	public void preloadMeasureConfigs() {
		MeasureConfigs.init();
	}

	@PostConstruct
	public void loadCpcValidationData() {
		cpcValidationData = Suppliers.memoizeWithExpiration(this::retrieveCpcValidationInfoMap, 2, TimeUnit.HOURS);
	}

	@PostConstruct
	public void loadApmData() {
		apmData = Suppliers.memoizeWithExpiration(this::retrieveApmEntityValidationFile, 2, TimeUnit.HOURS);
	}

	/**
	 * Converts a given a input stream with to conversion result content
	 *
	 * @param source Object to be converted
	 * @return Results of the conversion
	 */
	@Override
	public ConversionReport convertQrda3ToQpp(Source source) {
		Converter converter = initConverter(source);
		API_LOG.info("Performing QRDA3 to QPP conversion");
		converter.transform();
		return converter.getReport();
	}

	private PcfValidationInfoMap retrieveCpcValidationInfoMap() {
		API_LOG.info("Fetching CPC+ validations APM/NPI/TIN file");
		PcfValidationInfoMap file = new PcfValidationInfoMap(retrieveCpcPlusValidationFile());
		if (file.getApmTinNpiCombinationMap() != null) {
			API_LOG.info("Fetched CPC+ validations APM/NPI/TIN file");
		} else {
			API_LOG.info("Could not fetch CPC+ validations APM/NPI/TIN file");
		}
		return file;
	}

	private ApmEntityIds retrieveApmEntityValidationFile() {
		API_LOG.info("Trying to fetch the Pcf APM Validation file");
		InputStream pcfApmInputStream = retrieveApmValidationFile(Constants.PCF_APM_FILE_NAME_KEY);

		return cpcPlusApmInputStream != null ? new ApmEntityIds(cpcPlusApmInputStream, pcfApmInputStream) : new ApmEntityIds();
	}

	/**
	 * Opens a stream to retrieve the CPC+ Validation file for the QPP Service
	 *
	 * @return cpc+ validation file.
	 */
	@Override
	public InputStream retrieveCpcPlusValidationFile() {
		return storageService.getCpcPlusValidationFile();
	}

	protected InputStream retrieveApmValidationFile(String fileName) {
		return storageService.getApmValidationFile(fileName);
	}

	/**
	 * Instantiate a {@link Converter} with a given {@link Source}
	 *
	 * @param source for qrda input
	 * @return converter instance
	 */
	Converter initConverter(Source source) {
		Context context = new Context(apmData.get());
		PcfValidationInfoMap apmToNpiValidationFile = cpcValidationData.get();
		if (apmToNpiValidationFile != null && apmToNpiValidationFile.getApmTinNpiCombinationMap() != null) {
			context.setPiiValidator(new SpecPiiValidator(apmToNpiValidationFile));
		}
		return new Converter(source, context);
	}
}
