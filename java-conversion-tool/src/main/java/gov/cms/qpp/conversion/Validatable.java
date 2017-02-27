package gov.cms.qpp.conversion;

import java.util.List;

public interface Validatable<T> extends Iterable<T> {
	
	List<String> getValidations(String templateId);
	
	void addValidation(String templateId, String validation);

}
