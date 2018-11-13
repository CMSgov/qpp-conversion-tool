package gov.cms.qpp.conversion.api.controllers.v1;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
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

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.CpcFileStatusUpdateRequest;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.Report;
import gov.cms.qpp.conversion.api.model.Status;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import gov.cms.qpp.conversion.api.services.CpcFileService;
import gov.cms.qpp.conversion.api.services.DbService;
import gov.cms.qpp.conversion.util.EnvironmentHelper;

/**
 * Controller to handle cpc file data
 */
@RestController
@RequestMapping("/cpc")
@CrossOrigin(allowCredentials = "true")
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
	public CpcFileControllerV1(CpcFileService cpcFileService) {
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

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(unprocessedCpcFileDataList);
	}

	/**
	 * Retrieve a stored S3 submission object.
	 *
	 * @param fileId id for the stored object
	 * @return object json or xml content
	 * @throws IOException if S3Object content stream is invalid
	 */
	@GetMapping(value = "/file/{fileId}",
			headers = {"Accept=" + Constants.V1_API_ACCEPT})
	public ResponseEntity<InputStreamResource> getFileById(@PathVariable("fileId") String fileId)
			throws IOException {
		API_LOG.info("CPC+ file retrieval request received for fileId {}", fileId);

		if (blockCpcPlusApi()) {
			API_LOG.info("CPC+ file request blocked by feature flag");
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}

		InputStreamResource content = cpcFileService.getFileById(fileId);

		API_LOG.info("CPC+ file retrieval request succeeded");

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(content);
	}

	/**
	 * Retrieve a stored S3 QPP object.
	 *
	 * @param fileId id for the stored object
	 * @return object json or xml content
	 * @throws IOException if S3Object content stream is invalid
	 */
	@GetMapping(value = "/qpp/{fileId}",
		headers = {"Accept=" + Constants.V1_API_ACCEPT})
	public ResponseEntity<InputStreamResource> getQppById(@PathVariable("fileId") String fileId)
		throws IOException {
		API_LOG.info("CPC+ QPP retrieval request received for fileId {}", fileId);

		if (blockCpcPlusApi()) {
			API_LOG.info("CPC+ QPP request blocked by feature flag");
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}

		InputStreamResource content = cpcFileService.getQppById(fileId);

		API_LOG.info("CPC+ QPP retrieval request succeeded for fileId {}", fileId);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(content);
	}

	/**
	 * Updates a file's status in the database
	 *
	 * @param fileId Identifier of the file needing to be updated
	 * @param request The new state of the file being updated
	 * @return Message if the file was updated or not
	 */
	@PutMapping(value = "/file/{fileId}",
			headers = {"Accept=" + Constants.V1_API_ACCEPT})
	public ResponseEntity<String> updateFile(@PathVariable("fileId") String fileId,
			@RequestBody(required = false) CpcFileStatusUpdateRequest request) {
		if (blockCpcPlusApi()) {
			API_LOG.info(BLOCKED_BY_FEATURE_FLAG);
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}

		API_LOG.info("CPC+ update file request received for fileId {}", fileId);

		String message;
		if (request != null && request.getProcessed() != null && !request.getProcessed()) {
			message = cpcFileService.unprocessFileById(fileId);
		} else {
			message = cpcFileService.processFileById(fileId);
		}

		API_LOG.info("CPC+ update file request succeeded for fileId {} with message: {}", fileId, message);

		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(message);
	}

	@GetMapping(value = "/report/{fileId}",
			headers = {"Accept=" + Constants.V1_API_ACCEPT})
	public ResponseEntity<Report> getReportByFileId(@PathVariable("fileId") String fileId)
			throws IOException {
		API_LOG.info("CPC+ report request received for fileId {}", fileId);

		if (blockCpcPlusApi()) {
			API_LOG.info("CPC+ report request blocked by feature flag");
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}

		Metadata metadata = cpcFileService.getMetadataById(fileId);

		Report report = new Report();
		report.setErrors(metadata.getErrors());
		report.setPracticeSiteId(metadata.getApm());
		// TODO program name
		report.setTimestamp(metadata.getCreatedDate().toEpochMilli());
		report.setWarnings(metadata.getWarnings());
		boolean hasWarnings = report.getWarnings() != null && !report.getWarnings().isEmpty();
		report.setStatus(BooleanUtils.isTrue(metadata.getConversionStatus()) ? (hasWarnings ? Status.ACCEPTED_WITH_WARNINGS : Status.ACCEPTED) : Status.REJECTED);
		API_LOG.info("CPC+ report request succeeded");

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(report);
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
