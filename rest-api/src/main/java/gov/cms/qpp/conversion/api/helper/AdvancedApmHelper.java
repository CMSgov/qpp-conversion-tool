package gov.cms.qpp.conversion.api.helper;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedFileData;

import java.util.List;
import java.util.stream.Collectors;

public class AdvancedApmHelper {

	public static final String INVALID_FILE = "The file was not a CPC+ or PCF file.";
	public static final String FILE_NOT_FOUND = "File not found!";
	public static final String FILE_FOUND_PROCESSED = "The file was found and will be updated as processed.";
	public static final String FILE_FOUND_UNPROCESSED = "The file was found and will be updated as unprocessed.";

	/**
	 * private constructor for helper class
	 */
	private AdvancedApmHelper() {
		// empty
	}

	/**
	 * Determines if the file is unprocessed and is CPC+
	 *
	 * @param metadata Data to be determined valid or invalid
	 * @return result of the check
	 */
	public static boolean isAnUnprocessedCpcFile(Metadata metadata) {
		return isCpcFile(metadata) && (!metadata.getCpcProcessed() || !metadata.getRtiProcessed());
	}

	/**
	 * Determines if the file is a CPC+ submission
	 *
	 * @param metadata Data to be determined valid or invalid
	 * @return result of the check
	 */
	public static boolean isCpcFile(Metadata metadata) {
		return metadata != null && metadata.getCpc() != null;
	}

	/**
	 * Determines if the file is a unprocessed PCF submission
	 *
	 * @param metadata Data to be determined valid or invalid
	 * @return result of the check
	 */
	public static boolean isAValidUnprocessedFile(Metadata metadata) {
		return (isPcfFile(metadata)) && (!metadata.getCpcProcessed() || !metadata.getRtiProcessed());
	}

	/**
	 * Determines if the file is a Pcf submission
	 *
	 * @param metadata Data to be determined valid or invalid
	 * @return result of the check
	 */
	public static boolean isPcfFile(Metadata metadata) {
		return metadata != null && metadata.getPcf() != null;
	}

	/**
	 * Service to transform a {@link Metadata} list into the {@link UnprocessedFileData}
	 *
	 * @param metadataList object to hold the list of {@link Metadata} from DynamoDb
	 * @return transformed list of {@link UnprocessedFileData}
	 */
	public static List<UnprocessedFileData> transformMetaDataToUnprocessedFileData(List<Metadata> metadataList) {
		return metadataList.stream().map(UnprocessedFileData::new).collect(Collectors.toList());
	}
}
