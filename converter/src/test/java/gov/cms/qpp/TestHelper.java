package gov.cms.qpp;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.InputDecoderEngine;
import gov.cms.qpp.conversion.decode.QrdaDecoder;
import gov.cms.qpp.conversion.encode.OutputEncoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.validate.NodeValidator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestHelper {

	/**
	 * Retrieve fixture file content using "src/test/resources/fixtures/" as a base directory
	 *
	 * @param name file name
	 * @return file content
	 * @throws IOException when it can't locate / read the file
	 */
	public static String getFixture(final String name) throws IOException {
		Path path = Path.of("src/test/resources/fixtures/" + name);
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}

	/**
	 * Registers the {@link InputDecoderEngine} in the {@link gov.cms.qpp.conversion.model.Registry}.
	 *
	 * @param context The context that contains the Registry.
	 * @param decoder The decoder to register.
	 * @param componentKey The combination of a TemplateId and Program that the decoder will be registered for.
	 */
	public static void mockDecoder(Context context, Class<? extends QrdaDecoder> decoder, ComponentKey componentKey) {
		context.getRegistry(Decoder.class).register(componentKey, decoder);
	}

	/**
	 * Registers the {@link QppOutputEncoder} in the {@link gov.cms.qpp.conversion.model.Registry}.
	 *
	 * @param context The context that contains the Registry.
	 * @param encoder The encoder to register.
	 * @param componentKey The combination of a TemplateId and Program that the encoder will be registered for.
	 */
	public static void mockEncoder(Context context, Class<? extends OutputEncoder> encoder, ComponentKey componentKey) {
		context.getRegistry(Encoder.class).register(componentKey, encoder);
	}

	private TestHelper() {
	}

}
