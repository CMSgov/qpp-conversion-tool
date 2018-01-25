package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.CpcFileStatusUpdateRequest;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import gov.cms.qpp.conversion.api.services.CpcFileService;
import gov.cms.qpp.conversion.util.EnvironmentHelper;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle cpc file data
 */
@RestController
@RequestMapping("/cpc")
@CrossOrigin
public class CpcFileControllerV1 {

	private static final String BLOCKED_BY_FEATURE_FLAG =
			"CPC+ unprocessed files request blocked by feature flag";
	private static final Logger API_LOG = LoggerFactory.getLogger(CpcFileControllerV1.class);

	private CpcFileService cpcFileService;

	/**
	 * init instance
	 *
	 * @param cpcFileService service for processing cpc+ files
	 */
	public CpcFileControllerV1(final CpcFileService cpcFileService) {
		this.cpcFileService = cpcFileService;
	}

	/**
	 * Endpoint to transform an uploaded file into a valid or error json response
	 *
	 * @return Valid json or error json content
	 */
	@GetMapping(value = "/unprocessed-files",
			headers = {"Accept=" + Constants.V1_API_ACCEPT})
	public ResponseEntity<List<UnprocessedCpcFileData>> getUnprocessedCpcPlusFiles() {
		API_LOG.info("CPC+ unprocessed files request received");

		if (blockCpcPlusApi()) {
			API_LOG.info(BLOCKED_BY_FEATURE_FLAG);
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}

		List<UnprocessedCpcFileData> unprocessedCpcFileDataList = cpcFileService.getUnprocessedCpcPlusFiles();

		API_LOG.info("CPC+ unprocessed files request succeeded");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return new ResponseEntity<>(unprocessedCpcFileDataList, httpHeaders, HttpStatus.OK);
	}

	/**
	 * Retrieve a stored S3 object.
	 *
	 * @param fileId id for the stored object
	 * @return object json or xml content
	 * @throws IOException if S3Object content stream is invalid
	 */
	@GetMapping(value = "/file/{fileId}",
			headers = {"Accept=" + Constants.V1_API_ACCEPT})
	public ResponseEntity<InputStreamResource> getFileById(@PathVariable("fileId") String fileId)
			throws IOException {
		API_LOG.info("CPC+ file retrieval request received");

		if (blockCpcPlusApi()) {
			API_LOG.info("CPC+ file request blocked by feature flag");
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}

		InputStreamResource content = cpcFileService.getFileById(fileId);

		API_LOG.info("CPC+ file retrieval request succeeded");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_XML);

		return new ResponseEntity<>(content, httpHeaders, HttpStatus.OK);
	}

	/**
	 * Updates a file's status in the database
	 *
	 * @param fileId Identifier of the file needing to be updated
	 * @param request The new state of the file being updated
	 * @return Message if the file was updated or not
	 */
	@PutMapping(value = "/file/{fileId}",
			headers = {"Accept=" + Constants.V1_API_ACCEPT} )
	public ResponseEntity<String> updateFile(@PathVariable("fileId") String fileId,
			@RequestBody(required = false) CpcFileStatusUpdateRequest request) {
		if (blockCpcPlusApi()) {
			API_LOG.info(BLOCKED_BY_FEATURE_FLAG);
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}

		API_LOG.info("CPC+ update file request received");

		String message;
		if (request != null && request.getProcessed() != null && !request.getProcessed()) {
			message = cpcFileService.unprocessFileById(fileId);
		} else {
			message = cpcFileService.processFileById(fileId);
		}

		API_LOG.info("CPC+ update file request succeeded with message: " + message);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.TEXT_PLAIN);

		return new ResponseEntity<>(message, httpHeaders, HttpStatus.OK);
	}

	/**
	 * Checks whether the the CPC+ APIs should not be allowed to execute.
	 *
	 * @return Whether the CPC+ APIs should be blocked.
	 */
	private boolean blockCpcPlusApi() {
		return EnvironmentHelper.isPresent(Constants.NO_CPC_PLUS_API_ENV_VARIABLE);

	}
}
