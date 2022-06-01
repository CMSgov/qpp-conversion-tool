package gov.cms.qpp.conversion.api.controllers.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
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
import gov.cms.qpp.conversion.api.model.UnprocessedFileData;
import gov.cms.qpp.conversion.api.services.AdvancedApmFileService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pcf")
@CrossOrigin
public class PcfFileControllerV1 {

	private static final String BLOCKED_BY_FEATURE_FLAG =
		"PCF request blocked by feature flag or that have an invalid organization";
	private static final Logger API_LOG = LoggerFactory.getLogger(PcfFileControllerV1.class);

	private AdvancedApmFileService advancedApmFileService;

	public PcfFileControllerV1(AdvancedApmFileService advancedApmFileService) {
		this.advancedApmFileService = advancedApmFileService;
	}

	/**
	 * Endpoint to transform an uploaded file into a valid or error json response
	 *
	 * @return Valid json or error json content
	 */
	@GetMapping(value = "/unprocessed-files/{org}",
		headers = {"Accept=" + Constants.V1_API_ACCEPT})
	public ResponseEntity<List<UnprocessedFileData>> getUnprocessedPcfPlusFiles(@PathVariable("org") String organization) {
		API_LOG.info("PCF unprocessed files request received");

		String orgAttribute = Constants.ORG_ATTRIBUTE_MAP.get(organization);

		if (AdvancedApmHelper.blockAdvancedApmApis() || ObjectUtils.isEmpty(orgAttribute)) {
			API_LOG.info(BLOCKED_BY_FEATURE_FLAG);
			return ResponseEntity
				.status(HttpStatus.FORBIDDEN)
				.body(null);
		}

		List<UnprocessedFileData> unprocessedPcfFileDataList =
			advancedApmFileService.getUnprocessedPcfFiles(orgAttribute);

		API_LOG.info("PCF unprocessed files request succeeded");

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(unprocessedPcfFileDataList);
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
		API_LOG.info("PCF QPP retrieval request received for fileId {}", fileId);

		if (AdvancedApmHelper.blockAdvancedApmApis()) {
			API_LOG.info(BLOCKED_BY_FEATURE_FLAG);
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}

		InputStreamResource content = advancedApmFileService.getQppById(fileId);

		API_LOG.info("PCF QPP retrieval request succeeded for fileId {}", fileId);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(content);
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
		API_LOG.info("PCf file retrieval request received for file id {}", fileId);

		if (AdvancedApmHelper.blockAdvancedApmApis()) {
			API_LOG.info(BLOCKED_BY_FEATURE_FLAG);
			return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
		}

		InputStreamResource content = advancedApmFileService.getPcfFileById(fileId);

		API_LOG.info("Pcf file retrieval request succeeded");

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(content);
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
		API_LOG.info("Update PCF file request received for fileId {}", fileId);

		String message = advancedApmFileService.updateFileStatus(fileId, org, request);

		API_LOG.info("Update PCF file request succeeded for fileId {} with message: {}", fileId, message);

		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(message);
	}

}
