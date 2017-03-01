package gov.cms.qpp.conversion.model;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class Registry<V extends Object, R extends Object> {
	static final Logger LOG = LoggerFactory.getLogger(Registry.class);

	// For now this is static and can be refactored into an instance
	// variable when/if we have an orchestrator that instantiates an registry
	/**
	 * This will be an XPATH string to converter handler registration Since
	 * Converter was taken for the main stub, I chose Handler for now.
	 */
	Map<V, Class<? extends R>> registry;

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
				Class<?> annotatedClass = getAnnotatedClass(bd.getBeanClassName());
				register(getAnnotationParam(annotatedClass), (Class<R>) annotatedClass);
			} catch (ClassNotFoundException e) {
				LOG.error("Failed to register new transformation handler because: ", e);
			}
		}
	}

	// This allows for testing the ClassNotFoundException
	protected Class<?> getAnnotatedClass(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}

	@SuppressWarnings("unchecked")
	public V getAnnotationParam(Class<?> annotatedClass) {
		Annotation annotation = AnnotationUtils.findAnnotation(annotatedClass, annotationClass);

		if (annotation instanceof XmlRootDecoder) {
			XmlRootDecoder decoder = (XmlRootDecoder) annotation;
			return (V) decoder.rootElement();
		}
		if (annotation instanceof XmlDecoder) {
			XmlDecoder decoder = (XmlDecoder) annotation;
			return (V) decoder.templateId();
		}
		if (annotation instanceof Encoder) {
			Encoder encoder = (Encoder) annotation;
			return (V) encoder.templateId();
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
	public R get(String registryKey) {
		try {
			Class<? extends R> handlerClass = registry.get(registryKey);
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
	public void register(V registryKey, Class<? extends R> handler) {
		LOG.debug("Registering " + handler.getName() + " to '" + registryKey + "' for "
				+ annotationClass.getSimpleName() + ".");
		// This could be a class or class name and instantiated on lookup
		registry.put(registryKey, handler);
	}

	public Set<V> getKeys() {
		return registry.keySet();
	}
}
