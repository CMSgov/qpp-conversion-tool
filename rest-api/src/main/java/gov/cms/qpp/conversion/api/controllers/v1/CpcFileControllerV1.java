package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import gov.cms.qpp.conversion.api.services.CpcFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * Controller to handle cpc file data
 */
@RestController
@RequestMapping("/cpc")
public class CpcFileControllerV1 {

	private static final Logger API_LOG = LoggerFactory.getLogger(Constants.API_LOG);

	@Autowired
	private CpcFileService cpcFileService;

	/**
	 * Endpoint to transform an uploaded file into a valid or error json response
	 *
	 * @return Valid json or error json content
	 * @throws IOException If errors occur during file upload or conversion
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/unprocessed-files",
			headers = {"Accept=" + Constants.V1_API_ACCEPT + ", application/json;charset=UTF-8"})
	public ResponseEntity<List> getUnprocessedCpcPlusFiles() throws IOException {
		API_LOG.info("CPC+ unprocessed files request received");

		List<UnprocessedCpcFileData> unprocessedCpcFileDataList = cpcFileService.getUnprocessedCpcPlusFiles();

		API_LOG.info("CPC+ unprocessed files request succeeded");

		return new ResponseEntity<>(unprocessedCpcFileDataList, HttpStatus.OK);
	}
}
