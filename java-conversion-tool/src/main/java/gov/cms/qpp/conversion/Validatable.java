package gov.cms.qpp.conversion;

import java.util.List;

/**
 * Top level interface to store and retrieve validations during processing.
 * @author David Puglielli
 *
 */
public interface Validatable<V, T> {
	
	List<T> getValidationsById (V id);
	
	Iterable<String> validations();
	
	void addValidation(V templateId, T validation);

}
