package gov.cms.qpp.conversion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Validations<V, T> implements Validatable<V, T> {
	// keep it ordered since we can only 
	// use this storage method on a single threaded app anyway
	protected Map<V, List<T>> validations = new LinkedHashMap<>();

	
	@Override
	public Iterable<String> validations() {
		List<String> validationMsgs = new LinkedList<>();
		
		for (Entry<V, List<T>> templateEntry : validations.entrySet()) {
			for (T msg : templateEntry.getValue()) {
				validationMsgs.add(templateEntry.getKey() + " - " + msg);
			}
		}
				
		return validationMsgs;
	}

	@Override
	public List<T> getValidationsById(V templateId) {
		return validations.get(templateId);
	}
	
	@Override
	public void addValidation(V templateId, T validation) {
		List<T> validationList = getValidationsById(templateId);
		
		if (null == validationList) {
			validationList = new ArrayList<>();
			validations.put(templateId, validationList);
		}
		
		validationList.add(validation);
		
	}

}
