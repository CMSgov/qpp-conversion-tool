package gov.cms.qpp.conversion;

import java.util.List;

public interface Validatable<V, T> {
	
	List<T> getValidationsById (V id);
	
	Iterable<String> validations();
	
	void addValidation(V templateId, T validation);

}
