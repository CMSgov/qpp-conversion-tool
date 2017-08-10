package gov.cms.qpp.conversion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class PathQrdaSourceTest extends QrdaSourceTestSuite {

	public PathQrdaSourceTest() {
		super("arbitrary.txt", new PathQrdaSource(Paths.get("src/test/resources/arbitrary.txt")));
	}

	@Test
	public void testInputStream() throws IOException {
		String content = IOUtils.toString(source.toInputStream(), StandardCharsets.UTF_8);
		Assert.assertEquals("hello, world", content);
	}

}