package gov.cms.qpp.generator;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import gov.cms.qpp.conversion.model.error.ProblemCode;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mojo(name = "generateErrorCodeDoc")
public class ErrorCodeDocumentationGenerator extends AbstractMojo {

	public static void main(String... args) throws IOException {
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mdTemplate = mf.compile("error-code/error-code-tempate.md");

		String offsetPath = args[0];
		try (FileWriter fw = new FileWriter(offsetPath + "ERROR_MESSAGES.md", StandardCharsets.UTF_8)) {
			List<ProblemCode> errorCodes = Arrays.asList(ProblemCode.values());
			mdTemplate.execute(fw, errorCodes).flush();
			fw.flush();
		}
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String offsetPath = determinOffsetPath();
		
		try {
			getLog().info("Running Error Code documentation plugin");
			getLog().info("Parent project work directory offset " + offsetPath);
			
			ErrorCodeDocumentationGenerator.main(offsetPath.toString());
		} catch (IOException e) {
			throw new MojoExecutionException("Error code documentation problems", e);
		}
	}

	protected String determinOffsetPath() {
		// raw types used in legacy maven plugin API
		@SuppressWarnings("rawtypes")
		Map context = getPluginContext();
		
		MavenProject project = (MavenProject) context.get("project");
		MavenProject parent = project.getParent();
		String offsetPath = "";
		
		if (parent != null) {
			String parentPath = parent.getBasedir().getAbsolutePath();
			String workingDir = System.getProperty("user.dir");
			
			if (parentPath.equals(workingDir)) {
				// when the working dir is the parent project dir, use the working dir
				offsetPath = "./";
			} else {
				// when the working dir is the subproject dir, use the parent dir
				// this ensure the error messages file is written to the parent project dir
				offsetPath = "../"; 
			}
		}
		return offsetPath;
	}
}
