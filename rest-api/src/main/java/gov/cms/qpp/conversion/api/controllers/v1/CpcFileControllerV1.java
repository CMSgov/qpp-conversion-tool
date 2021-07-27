package gov.cms.qpp.conversion.api.controllers.v1;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.cms.qpp.conversion.api.helper.AdvancedApmHelper;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.FileStatusUpdateRequest;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.Report;
import gov.cms.qpp.conversion.api.model.Status;
import gov.cms.qpp.conversion.api.model.UnprocessedFileData;
import gov.cms.qpp.conversion.api.services.AdvancedApmFileService;

import java.io.IOException;
import java.util.List;

/**
 * Controller to handle cpc file data
 */
@RestController
@RequestMapping("/cpc")
@CrossOrigin(allowCredentials = "true")
public class CpcFileControllerV1 {

	private static final String BLOCKED_BY_FEATURE_FLAG =
		"CPC+ request blocked by feature flag or that have an invalid organization";
	private static final Logger API_LOG = LoggerFactory.getLogger(CpcFileControllerV1.class);

	private AdvancedApmFileService advancedApmFileService;

	/**
	 * init instance
	 *
	 * @param advancedApmFileService service for processing cpc+ files
	 */
	public CpcFileControllerV1(AdvancedApmFileService advancedApmFileService) {
		this.advancedApmFileService = advancedApmFileService;
	}

	/**
	 * Endpoint to transform an uploaded file into a valid or error json response
	 *
	 * @return Valid json or error json content
	 */
	@GetMapping(value = "/unprocessed-files/{org}",
		headers = {"Accept=" + Constants.V1_API_ACCEPT})
	public ResponseEntity<List<UnprocessedFileData>> getUnprocessedCpcPlusFiles(@PathVariable("org") String organization) {
		API_LOG.info("CPC+ unprocessed files request received");

		String orgAttribute = Constants.ORG_ATTRIBUTE_MAP.get(organization);

		if (AdvancedApmHelper.blockAdvancedApmApis() || StringUtils.isEmpty(orgAttribute)) {
			API_LOG.info(BLOCKED_BY_FEATURE_FLAG);
			return ResponseEntity
				.status(HttpStatus.FORBIDDEN)
				.body(null);
		}

		List<UnprocessedFileData> unprocessedFileDataList =
			advancedApmFileService.getUnprocessedCpcPlusFiles(orgAttribute);

		API_LOG.info("CPC+ unprocessed files request succeeded");

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(unprocessedFileDataList);
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

		if (AdvancedApmHelper.blockAdvancedApmApis()) {
			API_LOG.info(BLOCKED_BY_FEATURE_FLAG);
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}

		InputStreamResource content = advancedApmFileService.getCpcFileById(fileId);

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

		if (AdvancedApmHelper.blockAdvancedApmApis()) {
			API_LOG.info(BLOCKED_BY_FEATURE_FLAG);
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}

		InputStreamResource content = advancedApmFileService.getQppById(fileId);

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
	@PutMapping(value = "/file/{fileId}/{org}",
		headers = {"Accept=" +
			Constants.V1_API_ACCEPT})
	public ResponseEntity<String> updateFile(@PathVariable("fileId") String fileId, @PathVariable("org") String org,
		@RequestBody(required = false) FileStatusUpdateRequest request) {
		if (AdvancedApmHelper.blockAdvancedApmApis()) {
			API_LOG.info(BLOCKED_BY_FEATURE_FLAG);
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}
		API_LOG.info("CPC+ update file request received for fileId {}", fileId);
		String message = advancedApmFileService.updateFileStatus(fileId, org, request);
		API_LOG.info("CPC+ update file request succeeded for fileId {} with message: {}", fileId, message);

		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(message);
	}

	@GetMapping(value = "/report/{fileId}",
		headers = {"Accept=" + Constants.V1_API_ACCEPT})
	public ResponseEntity<Report> getReportByFileId(@PathVariable("fileId") String fileId) {
		API_LOG.info("CPC+ report request received for fileId {}", fileId);

		if (AdvancedApmHelper.blockAdvancedApmApis()) {
			API_LOG.info("CPC+ report request blocked by feature flag");
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}

		Metadata metadata = advancedApmFileService.getMetadataById(fileId);

		// If the Metadata object was created before a certain version, it will
		// not contain error information, so a report would be inaccurate
		if (metadata.getMetadataVersion() == null || metadata.getMetadataVersion() < 2) {
			return new ResponseEntity<>(null, null, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		Report report = new Report();
		report.setErrors(metadata.getErrors() == null ? null : metadata.getErrors().getDetails());
		report.setPracticeSiteId(metadata.getApm());
		report.setProgramName(metadata.getProgramName());
		report.setTimestamp(metadata.getCreatedDate().toEpochMilli());
		report.setWarnings(metadata.getWarnings() == null ? null : metadata.getWarnings().getDetails());
		boolean hasWarnings = report.getWarnings() != null && !report.getWarnings().isEmpty();
		report.setStatus(BooleanUtils.isTrue(metadata.getConversionStatus())
			? (hasWarnings ? Status.ACCEPTED_WITH_WARNINGS : Status.ACCEPTED) : Status.REJECTED);
		API_LOG.info("CPC+ report request succeeded");

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(report);
	}
}

