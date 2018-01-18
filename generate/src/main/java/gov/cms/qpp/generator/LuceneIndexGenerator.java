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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Mojo( name = "generateLuceneIndex")
public class LuceneIndexGenerator extends AbstractMojo {
	private static final Path BASE_DIR = Paths.get("./converter/src/main/resources");
	private static final Path DICTIONARY_PATH = BASE_DIR.resolve("measures-dictionary.txt");
	private static final Path INDEX_DIR = BASE_DIR.resolve("measures_index");

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info( "Running lucene index generator" );
		try {
			writeMeasures();
			getLog().info( "Prepping measures index" );
			new SpellChecker(FSDirectory.open(INDEX_DIR))
				.indexDictionary(
					new PlainTextDictionary(DICTIONARY_PATH), new IndexWriterConfig(), false);
			Files.deleteIfExists(DICTIONARY_PATH);
		} catch (IOException ex) {
			throw new MojoExecutionException("Could not create lucene index", ex);
		}
	}

	private void writeMeasures() throws FileNotFoundException, UnsupportedEncodingException {
		getLog().info( "Writing measures dictionary" );
		Set<String> keys = MeasureConfigs.getConfigurationMap().keySet();
		try (PrintWriter pw = new PrintWriter(
			new OutputStreamWriter(new FileOutputStream(DICTIONARY_PATH.toFile()), "UTF-8"))) {
			for (String s : keys) {
				pw.println(s);
			}
			pw.flush();
		}
	}
}
