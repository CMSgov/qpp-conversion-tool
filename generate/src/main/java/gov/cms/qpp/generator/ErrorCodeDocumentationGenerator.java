package gov.cms.qpp.generator;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import javax.xml.transform.TransformerConfigurationException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Mojo(name = "generateErrorCodeDoc")
public class ErrorCodeDocumentationGenerator extends AbstractMojo {

	public static void main(String... args) throws IOException, TransformerConfigurationException {
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mdTemplate = mf.compile("error-code/error-code-tempate.md");

		try (FileWriter fw = new FileWriter("./ERROR_MESSAGES.md")) {
			List<ErrorCode> errorCodes = Arrays.asList(ErrorCode.values());
			mdTemplate.execute(fw, errorCodes).flush();
			fw.flush();
		}
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("Running Error Code documentation plugin");
			ErrorCodeDocumentationGenerator.main();
		} catch (IOException | TransformerConfigurationException e) {
			throw new MojoExecutionException("Error code documentation problems", e);
		}
	}
}
