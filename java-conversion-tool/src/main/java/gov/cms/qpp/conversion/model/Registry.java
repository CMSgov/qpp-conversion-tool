package gov.cms.qpp.conversion.model;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import gov.cms.qpp.conversion.parser.InputParser;

/**
 * This class manages the available transformation handlers.
 * Currently it takes the XPATH that the handler will transform.
 * 
 * @author daviduselmann
 */
public class Registry {
	
	// For now this is static and can be refactored into an instance
	// variable when/if we have an orchestrator that instantiates an registry
	/**
	 * This will be an XPATH string to converter handler registration
	 * Since Converter was taken for the main stub, I chose Handler for now.
	 */
	private Map<NodeId, Class<?>> registry;

	private Class<? extends Annotation> annotationClass;
	
	/**
	 * initialize and configure the registry
	 */
	public Registry(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
		init();
		registerAnnotatedHandlers();
	}
	
	
	/**
	 * This is a helper method used for testing.
	 * Singletons have trouble in testing if they cannot be reset.
	 * It is package access to only allow classes in the same package,
	 * like tests, have access.
	 */
	void init() {
		registry = new HashMap<>();
	}
	
	/**
	 * This method will scan all classes for the annotation for TransformHandlers that need registration.
	 */
	@SuppressWarnings("unchecked")
	void registerAnnotatedHandlers() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
		for (BeanDefinition bd : scanner.findCandidateComponents("gov.cms")) {
			try {
				Class<?> annotatedClass = Class.forName(bd.getBeanClassName());
				Map<String,String> params = getAnnotationParams(annotatedClass);
				register(params.get("elementName"), params.get("templateId"), 
						(Class<? extends InputParser>) Class.forName(bd.getBeanClassName()));
			} catch (Exception e) {
				e.printStackTrace();
				// TODO logger.error("Failed to register new transformation handler because: " + e.getMessage());
			}
		}
	}
	
	public Map<String,String> getAnnotationParams(Class<?> annotatedClass) {
		Annotation annotation = AnnotationUtils.findAnnotation(annotatedClass, annotationClass);
		if (annotation == null) {
			return null;
		}
		Map<String,String> params = new HashMap<>();
		if (annotation instanceof Decoder) {
			Decoder specific = (Decoder) annotation;
			params.put("elementName", specific.elementName());
			params.put("templateId", specific.templateId());
		}
		if (annotation instanceof Decoder) {
			Decoder specific = (Decoder) annotation;
			params.put("elementName", specific.elementName());
			params.put("templateId", specific.templateId());
		}
		return params;
	}

	/**
	 * This method will return a proper top level handler for the given XPATH
	 * Later iteration will examine the XPATH startsWith and return a most
	 * appropriate handler
	 * @param xpath
	 */
	public InputParser get(String elementName, String templateId) {
		try {
			Class<?> parserClass = registry.get(new NodeId(elementName,templateId));
			if (parserClass == null) {
				return null;
			}
			return (InputParser) parserClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
	
	/**
	 * Means ot register a new transformation handler
	 * @param xpath
	 * @param handler
	 */
	public void register(String elementName, String templateId, Class<? extends InputParser> parser) {
		// TODO logger.info("Registering new Handler {}, {}". xpath, handler.getClass().getName());
		// This could be a class or class name and instantiated on lookup
		registry.put(new NodeId(elementName,templateId), parser);
	}
}
