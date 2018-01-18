package gov.cms.qpp.conversion.model.validation;

import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Set;

public class MakeIndex {
	public static void main(String... yep) throws IOException {
		Path dir = Paths.get("./meep");
		Directory directory = FSDirectory.open(dir);
		SpellChecker spellChecker = new SpellChecker(directory);

//		IndexWriterConfig blah = new IndexWriterConfig();

//		writeMeasures();
//		spellChecker.indexDictionary(
//			new PlainTextDictionary(Paths.get(
//				"/Users/clydetedrick/projects/adele/qpp-conversion-tool/measure-spell.txt")), blah, false);

		String wordForSuggestions = "IA_PCMG";

		int suggestionsNumber = 5;

		System.out.println(Instant.now());
		String[] suggestions = spellChecker.
			suggestSimilar(wordForSuggestions, suggestionsNumber);
		System.out.println(Instant.now());

		if (suggestions!=null && suggestions.length>0) {
			for (String word : suggestions) {
				System.out.println("Did you mean:" + word);
			}
		}
		else {
			System.out.println("No suggestions found for word:"+wordForSuggestions);
		}
	}

	private static void writeMeasures() throws FileNotFoundException, UnsupportedEncodingException {
		Set<String> keys = MeasureConfigs.getConfigurationMap().keySet();
		Path path = Paths.get("./measure-spell.txt");
		try (PrintWriter pw = new PrintWriter(
			new OutputStreamWriter(new FileOutputStream(path.toFile()), "UTF-8"))) {

			for (String s : keys) {
				pw.println(s);
			}
			pw.flush();
		}
	}
}
