package gov.cms.qpp.conversion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.decode.XmlInputDecoder;
import gov.cms.qpp.conversion.decode.XmlInputFileException;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.validate.QrdaValidator;
// import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

/**
 * Converter provides the command line processing for QRDA III to QPP json.
 * 
 * @author David Uselmann
 *
 */
public class Converter implements Callable<Integer> {
	static final Logger LOG = LoggerFactory.getLogger(Converter.class);

	static final int MAX_THREADS = 10;

	final File inFile;

	public Converter(File inFile) {
		this.inFile = inFile;
	}

	@Override
	public Integer call() throws Exception {

		if (!inFile.exists()) {
			return 0; // it should if check prior to instantiation.
		}

		try {
			Node decoded = XmlInputDecoder.decodeXml(XmlUtils.fileToDOM(inFile));

			QrdaValidator validator = new QrdaValidator();

			List<ValidationError> validationErrors = validator.validate(decoded);

			String name = inFile.getName().trim();

			if (validationErrors.isEmpty()) {

				JsonOutputEncoder encoder = new QppOutputEncoder();

				LOG.info("Decoded template ID {} from file '{}'", decoded.getId(), name);
				// do something with decode validations
				// Validations.clear();
				// Validations.init();

				String outName = name.replaceFirst("(?i)(\\.xml)?$", ".qpp.json");

				File outFile = new File(outName);
				LOG.info("Writing to file '{}'", outFile.getAbsolutePath());

				Writer writer = null;
				try {
					writer = new FileWriter(outFile);
					encoder.setNodes(Arrays.asList(decoded));
					encoder.encode(writer);
					// do something with encode validations
				} catch (IOException | EncodeException e) {
					throw new XmlInputFileException("Issues decoding/encoding.", e);
				} finally {
					Validations.clear();
					IOUtils.closeQuietly(writer);
				}
			} else {
				String errName = name.replaceFirst("(?i)(\\.xml)?$", ".err.txt");

				File outFile = new File(errName);
				LOG.info("Writing to file '{}'", outFile.getAbsolutePath());

				Writer errWriter = null;
				try {
					errWriter = new FileWriter(outFile);
					for (ValidationError error : validationErrors) {
						errWriter.write("Validation Error: " + error.getErrorText() + System.lineSeparator());
					}
				} catch (IOException e) {
					LOG.error("Could not write to file: {}", errName);
				} finally {
					Validations.clear();
					IOUtils.closeQuietly(errWriter);
				}
			}
		} catch (XmlInputFileException | XmlException xe) {
			LOG.error("The file is not a valid XML document");
		} catch (Exception allE) {
			// Eat all exceptions in the call
			LOG.error(allE.getMessage());
		}
		return null;
	}

	public static Collection<File> validArgs(String[] args) {
		if (args.length < 1) {
			LOG.error("No filename found...");
			return new LinkedList<>();
		}

		Collection<File> validFiles = new LinkedList<>();

		for (String arg : args) {
			validFiles.addAll(checkPath(arg));
		}

		return validFiles;
	}

	public static Collection<File> checkPath(String path) {
		Collection<File> existingFiles = new LinkedList<>();

		if (path == null || path.trim().length() == 0) {
			return existingFiles;
		}

		if (path.contains("*")) {
			return manyPath(path);
		}

		File file = new File(path);
		if (file.exists()) {
			existingFiles.add(file);
		} else {
			LOG.error(path + " does not exist.");
		}

		return existingFiles;
	}

	public static Collection<File> manyPath(String path) {

		File inDir = new File(extractDir(path));
		String fileRegex = wildCardToRegex(path);
		try {
			Collection<File> existingFiles = FileUtils.listFiles(inDir, new RegexFileFilter(fileRegex),
					DirectoryFileFilter.DIRECTORY);
			return existingFiles;
		} catch (Exception e) {
			LOG.error("Cannot file path {}{}", inDir, fileRegex);
			return new LinkedList<>();
		}
	}

	public static String extractDir(String path) {

		String[] parts = path.split("[\\/\\\\]");

		StringBuilder dirPath = new StringBuilder();
		for (String part : parts) {
			// append until a wild card
			if (part.contains("*")) {
				break;
			}
			dirPath.append(part).append(File.separator);
		}
		// if no path then use the current dir
		if (dirPath.length() == 0) {
			dirPath.append('.');
		}

		return dirPath.toString();
	}

	public static String wildCardToRegex(String path) {
		String regex = "";

		// this replace should work if the user does not give conflicting OS
		// path separators
		String dirPath = extractDir(path);
		String wild = path;
		if (dirPath.length() > 1) {
			wild = wild.substring(dirPath.length());
		}

		String[] parts = wild.split("[\\/\\\\]");

		if (parts.length > 2) {
			LOG.error("Too many wild cards in {}", path);
			return "";
		}
		String lastPart = parts[parts.length - 1];

		if ("**".equals(lastPart)) {
			regex = "."; // any and all files
		} else {

			// turn the last part into REGEX from file wild cards
			regex = lastPart.replaceAll("\\.", "\\\\.");
			regex = regex.replaceAll("\\*", ".*");
		}

		return regex;
	}

	public static void main(String[] args) {
		Collection<File> filenames = validArgs(args);
		processFiles(filenames);
	}

	private static void processFiles(Collection<File> filenames) {
		int threads = Math.min(MAX_THREADS, filenames.size());

		final ExecutorService execService = Executors.newFixedThreadPool(threads);

		try {
			CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(execService);

			startAllFileConversions(filenames, completionService);
			waitForAllToFinish(filenames.size(), completionService);
		} finally {
			execService.shutdown();
		}
	}

	private static void startAllFileConversions(Collection<File> filenames,
			CompletionService<Integer> completionService) {
		for (File filename : filenames) {
			Converter instance = new Converter(filename);
			completionService.submit(instance);
		}
	}

	private static void waitForAllToFinish(int count, CompletionService<Integer> completionService) {
		int finished = 0;
		while (finished < count) {
			Future<Integer> resultFuture = null;
			try {
				resultFuture = completionService.take();
				Integer result = resultFuture.get(); // TODO do something with
														// result (and pick a
														// good result)
				finished++;

			} catch (InterruptedException | ExecutionException e) {
				LOG.error("Transformation interrupted.");
				LOG.error(e.getMessage());
				throw new RuntimeException("Could not process file(s).", e);
			}
		}
	}

}
