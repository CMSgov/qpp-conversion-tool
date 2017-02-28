package gov.cms.qpp.conversion.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Validations {

	protected static final ThreadLocal<Map<String, List<String>>> validations = new ThreadLocal<>();

	public static Iterable<String> values() {
		List<String> validationMsgs = new ArrayList<>();
		
		for (Entry<String, List<String>> templateEntry : validations.get().entrySet()) {
			for (String msg : templateEntry.getValue()) {
				validationMsgs.add(templateEntry.getKey() + " - " + msg);
			}
		}
				
		return validationMsgs;
	}

	public static List<String> getValidationsById(String templateId) {
		return validations.get().get(templateId);
	}
	
	public static void addValidation(String templateId, String validation) {
		List<String> validationList = getValidationsById(templateId);
		
		if (null == validationList) {
			validationList = new ArrayList<>();
			validations.get().put(templateId, validationList);
		}
		
		validationList.add(validation);
		
	}
	
	public static void clear() {
		validations.set(null);
	}
	
	public static void init() {
		validations.set(new LinkedHashMap<>());
	}

}
