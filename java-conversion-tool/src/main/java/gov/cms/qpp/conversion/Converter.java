package gov.cms.qpp.conversion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.decode.DecodeException;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.decode.XmlFileDecoder;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;

public class Converter {
    static final Logger LOG = LoggerFactory.getLogger(Converter.class);

	public static void main(String[] args) {
		if (args.length <  1) {
			System.out.println("No filename found...");
		} else if (args.length> 1) {
			System.out.println("Too many arguments...");
		} else {
			String filename = args[0];
			File inFile = new File(filename);
			if (inFile.exists()) {
				Writer writer = null;
				try {
					XmlFileDecoder fileDecoder = new XmlFileDecoder(inFile, new QppXmlDecoder());
					Node decoded = fileDecoder.decode();
					JsonOutputEncoder encoder = new QppOutputEncoder();
					
					String name = inFile.getName().trim();
					System.out.println("Decoded template ID " + decoded.getId() + " from file '" + name + "'");
					
					String outName = name.replaceFirst("(?i)(\\.xml)?$", ".qpp.json");
					
					File outFile = new File(outName);
					System.out.println("Writing to file '" + outFile.getAbsolutePath() + "'");
					writer = new FileWriter(outFile);
					encoder.setNodes(Arrays.asList(decoded));
					writer.write("Begin\n");
					encoder.encode(writer);
					writer.write("\nEnd\n");

				} catch (DecodeException | IOException | EncodeException e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(writer);
				}
			}
		}
	}

}
