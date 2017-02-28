package gov.cms.qpp.conversion;

import java.util.List;

public interface Validatable<V, T> {
	
	List<T> getValidationsById (String id);
	
	Iterable<T> validations();
	
	void addValidation(V templateId, T validation);

}
