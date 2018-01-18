package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.HighFrequencyDictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MeasureConfigs {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(MeasureConfigs.class);
	public static final String DEFAULT_MEASURE_DATA_FILE_NAME = "measures-data.json";

	private static String measureDataFileName = DEFAULT_MEASURE_DATA_FILE_NAME;
	private static Map<String, MeasureConfig> configurationMap;
	private static Map<String, List<MeasureConfig>> cpcPlusGroups;
	private static LevenShpellCheck suggestionHelper;

	/**
	 * Static initialization
	 */
	static {
		initMeasureConfigs();
	}

	/**
	 * Empty private constructor for singleton
	 */
	private MeasureConfigs() {
		//empty and private constructor because this is a singleton
	}

	/**
	 * Initialize all measure configurations
	 */
	private static void initMeasureConfigs() {
		configurationMap = grabConfiguration(measureDataFileName);
		suggestionHelper = new LevenShpellCheck(configurationMap.keySet());
		cpcPlusGroups = new HashMap<>();
		getMeasureConfigs().stream()
				.filter(config -> config.getCpcPlusGroup() != null)
				.forEach(config -> cpcPlusGroups.computeIfAbsent(
						config.getCpcPlusGroup(), key -> new ArrayList<>()).add(config));
	}

	public static Map<String, MeasureConfig> grabConfiguration(String fileName) {
		ObjectMapper mapper = new ObjectMapper();

		InputStream measuresInput = ClasspathHelper.contextClassLoader().getResourceAsStream(fileName);

		try {
			TypeReference<List<MeasureConfig>> measureConfigType = new TypeReference<List<MeasureConfig>>() {};
			List<MeasureConfig> configurations = mapper.readValue(measuresInput, measureConfigType);
			return configurations.stream()
					.collect(Collectors.toMap(MeasureConfigs::getMeasureId, Function.identity()));
		} catch (IOException e) {
			String message = "failure to correctly read measures config json";
			DEV_LOG.error(message);
			throw new IllegalArgumentException(message, e);
		}
	}

	/**
	 * Finds the first existing guid, electronicMeasureId, or measureId that exists for an aci, ia, or ecqm section
	 *
	 * @param measureConfig Measure configuration that contains the identifiers
	 * @return An identifier
	 */
	private static String getMeasureId(MeasureConfig measureConfig) {
		String guid = measureConfig.getElectronicMeasureVerUuid();
		String electronicMeasureId = measureConfig.getElectronicMeasureId();
		String measureId = measureConfig.getMeasureId();
		return (guid != null ? guid : (electronicMeasureId != null ? electronicMeasureId : measureId));
	}

	/**
	 * Reconfigures a filename and initializes the measure configurations from that file
	 *
	 * @param fileName Name to be used
	 */
	public static void setMeasureDataFile(String fileName) {
		measureDataFileName = fileName;
		initMeasureConfigs();
	}

	public static List<MeasureConfig> getMeasureConfigs() {
		return configurationMap.values().stream().collect(Collectors.toList());
	}

	/**
	 * Retrieves a mapping of the configurations
	 *
	 * @return mapped configurations
	 */
	public static Map<String, MeasureConfig> getConfigurationMap() {
		return configurationMap;
	}

	/**
	 * Retrieves a mapping of CPC+ measure groups
	 *
	 * @return mapped CPC+ measure groups
	 */
	public static Map<String, List<MeasureConfig>> getCpcPlusGroups() {
		return cpcPlusGroups;
	}

	/**
	 * Retrieves a list of required mappings for any given section
	 *
	 * @param section Specified section for measures required
	 * @return The list of required measures
	 */
	static List<String> requiredMeasuresForSection(String section) {

		return configurationMap.values().stream()
			.filter(measureConfig -> measureConfig.isRequired() && section.equals(measureConfig.getCategory()))
			.map(MeasureConfigs::getMeasureId)
			.collect(Collectors.toList());
	}

	static List<String> getSuggestions(String measureId) {
		return suggestionHelper.getSuggestions(measureId);
	}



	static void getOtherSuggestion(String measureId) throws IOException {
		Directory dir = new SimpleFSDirectory(Paths.get("./meep"));
		SpellChecker spellchecker = new SpellChecker(dir);
		// To index a field of a user index:

//		spellchecker.indexDictionary(new LuceneDictionary());
//		spellchecker.indexDictionary(new LuceneDictionary(my_lucene_reader, a_field)); // To index a file containing words:
//		String[] suggestions = spellchecker.suggestSimilar("misspelt", 5);
	}

//	private static Comparator<String> getDistanceDiff(String seed) {
//		LevenshteinDistance distanceCheck = new LevenshteinDistance(null);
//		return (one, two) -> {
//			int distanceOne = distanceCheck.apply(one, seed);
//			int distanceTwo = distanceCheck.apply(two, seed);
//			return Integer.compare(distanceOne, distanceTwo);
//		};
//	}

	static class LevenShpellCheck implements Serializable {
		private transient LevenshteinDistance distanceCheck = new LevenshteinDistance(null);
		HashSet<Entry> dictionary;

		LevenShpellCheck(Collection<String> dictionary) {
			this.dictionary = dictionary.stream()
				.map(Entry::new)
				.collect(Collectors.toCollection(HashSet::new));
		}

		List<String> getSuggestions(String word) {
			Set<Entry> dictionaryClone = SerializationUtils.clone(dictionary);

			List<String> suggestions = dictionaryClone.stream()
				.map(entry -> entry.setDistance(distanceCheck.apply(entry.key, word)))
				.filter(Entry::isGreaterThanZero)
				.sorted(Comparator.comparingInt(Entry::getDistance))
				.limit(5)
				.map(Entry::getKey)
				.collect(Collectors.toList());
			return suggestions;
		}

		class Entry implements Serializable {
			String key;
			Integer distance;

			Entry(String key) {
				this.key = key;
			}

			Entry setDistance(final int distance) {
				this.distance = distance;
				return this;
			}

			Integer getDistance() {
				return distance;
			}

			String getKey() {
				return key;
			}

			boolean isGreaterThanZero() {
				return distance > 0;
			}
		}
	}
}
