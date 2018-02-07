package gov.cms.qpp.generator;

import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.FSDirectory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Mojo(name = "generateLuceneIndex")
public class SpellCheckIndexGenerator extends AbstractMojo {

	@Parameter(property = "generateLuceneIndex.baseDir", defaultValue = "./commons/src/main/resources")
	private String baseDir;

	private Path dictionaryPath;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Path baseDirPath = Paths.get(baseDir);
		dictionaryPath = baseDirPath.resolve("measures-dictionary.txt");
		Path indexDir = baseDirPath.resolve("measures_index");

		getLog().info("Running lucene index generator: " + baseDir);
		try {
			writeMeasures();
			getLog().info("Prepping measures index");
			new SpellChecker(FSDirectory.open(indexDir))
				.indexDictionary(
					new PlainTextDictionary(dictionaryPath), new IndexWriterConfig(), false);
			Files.deleteIfExists(dictionaryPath);
		} catch (IOException ex) {
			throw new MojoExecutionException("Could not create lucene index", ex);
		}
	}

	private void writeMeasures() throws FileNotFoundException, UnsupportedEncodingException {
		getLog().info("Writing measures dictionary");
		Set<String> keys = MeasureConfigs.getConfigurationMap().keySet();
		try (PrintWriter writer = new PrintWriter(
			new OutputStreamWriter(new FileOutputStream(dictionaryPath.toFile()), StandardCharsets.UTF_8))) {
			for (String key : keys) {
				writer.println(key);
			}
			writer.flush();
		}
	}
}
