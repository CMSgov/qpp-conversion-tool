package gov.cms.qpp.conversion.model;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * This class manages the available transformation handlers. Currently it takes
 * the XPATH that the handler will transform.
 * 
 * R is the stored and return interface type.
 * 
 * @author daviduselmann
 */
public class Registry<R extends Object> {

	// For now this is static and can be refactored into an instance
	// variable when/if we have an orchestrator that instantiates an registry
	/**
	 * This will be an XPATH string to converter handler registration Since
	 * Converter was taken for the main stub, I chose Handler for now.
	 */
	private Map<String, Class<? extends R>> registry;

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
	 * This is a helper method used for testing. Singletons have trouble in
	 * testing if they cannot be reset. It is package access to only allow
	 * classes in the same package, like tests, have access.
	 */
	void init() {
		registry = new HashMap<>();
	}

	/**
	 * This method will scan all classes for the annotation for
	 * TransformHandlers that need registration.
	 */
	@SuppressWarnings("unchecked")
	void registerAnnotatedHandlers() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
		for (BeanDefinition bd : scanner.findCandidateComponents("gov.cms")) {
			try {
				Class<?> annotatedClass = Class.forName(bd.getBeanClassName());
				String templateId = getAnnotationParams(annotatedClass);
				register(templateId, (Class<R>) Class.forName(bd.getBeanClassName()));
			} catch (Exception e) {
				e.printStackTrace();
				// TODO logger.error("Failed to register new transformation handler because: " + e.getMessage());
			}
		}
	}

	public String getAnnotationParams(Class<?> annotatedClass) {
		Annotation annotation = AnnotationUtils.findAnnotation(annotatedClass, annotationClass);
		
		if (annotation instanceof XmlDecoder) {
			XmlDecoder decoder = (XmlDecoder) annotation;
			return decoder.templateId();
		}
		if (annotation instanceof JsonEncoder) {
			JsonEncoder encoder = (JsonEncoder) annotation;
			return encoder.templateId();
		}
		return null;
	}

	/**
	 * This method will return a proper top level handler for the given XPATH
	 * Later iteration will examine the XPATH startsWith and return a most
	 * appropriate handler
	 * 
	 * @param xpath
	 */
	public R get(String templateId) {
		try {
			Class<? extends R> handlerClass = registry.get(templateId);
			if (handlerClass == null) {
				return null;
			}
			return handlerClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}

	/**
	 * Means ot register a new transformation handler
	 * 
	 * @param xpath
	 * @param handler
	 */
	public void register(String templateId, Class<? extends R> handler) {
		// TODO logger.info("Registering new Handler {}, {}". xpath,
		// handler.getClass().getName());
		// This could be a class or class name and instantiated on lookup
		registry.put(templateId, handler);
	}
}
