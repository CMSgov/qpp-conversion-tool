package gov.cms.qpp.conversion.io;

import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;


public class ByteCounterOutputStreamTest {

	@Test
	public void writeBytes() throws Exception {
		ByteCounterOutputStream out = new ByteCounterOutputStream();
		
		IOUtils.copy(IOUtils.toInputStream("asdfghqwer", Charset.defaultCharset()), out);
		
		Assert.assertEquals(out.size(), 10);
	}
}
