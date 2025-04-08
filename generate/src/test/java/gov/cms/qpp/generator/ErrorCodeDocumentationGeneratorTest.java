package gov.cms.qpp.generator;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ErrorCodeDocumentationGeneratorTest {
	
	ErrorCodeDocumentationGenerator gen;
	
	@BeforeEach
	protected void setUp() throws Exception {
		gen = new ErrorCodeDocumentationGenerator();
	}
	
	@Test
	public void test_determinOffsetPath_doubledot() {
		String workingDir = System.getProperty("user.dir");
		File mockBaseDir = Path.of(workingDir).toFile();

		String generate = "/generate";
		File mockParentDir = Path.of(workingDir.substring(0, workingDir.length()-generate.length())).toFile();
		MavenProject mockParentProject = mock(MavenProject.class);
		when(mockParentProject.getBasedir()).thenReturn(mockParentDir);

		MavenProject mockProject = mock(MavenProject.class);
		when(mockProject.getBasedir()).thenReturn(mockBaseDir);
		when(mockProject.getParent()).thenReturn(mockParentProject);

		Map<String, Object> mockContext = new HashMap<>();
		mockContext.put("project", mockProject);
		gen.setPluginContext(mockContext);
		
		String expected = "../";
		String actual = gen.determinOffsetPath();
		
		assertWithMessage("When the working directory is the subproject then use the parent dir.")
			.that(actual).isEqualTo(expected);
	}
	
	@Test
	public void test_determinOffsetPath_dot() {
		String workingDir = System.getProperty("user.dir");
		File mockBaseDir = Path.of(workingDir).toFile();
		
		String generate = "/generate";
		workingDir = workingDir.substring(0, workingDir.length()-generate.length());
		System.setProperty("user.dir", workingDir);
		
		File mockParentDir = Path.of(workingDir).toFile();
		MavenProject mockParentProject = mock(MavenProject.class);
		when(mockParentProject.getBasedir()).thenReturn(mockParentDir);

		MavenProject mockProject = mock(MavenProject.class);
		when(mockProject.getBasedir()).thenReturn(mockBaseDir);
		when(mockProject.getParent()).thenReturn(mockParentProject);

		Map<String, Object> mockContext = new HashMap<>();
		mockContext.put("project", mockProject);
		gen.setPluginContext(mockContext);
		
		String expected = "./";
		String actual = gen.determinOffsetPath();
		
		assertWithMessage("When the working directory is the parent project then use the working dir.")
			.that(actual).isEqualTo(expected);
	}
}
