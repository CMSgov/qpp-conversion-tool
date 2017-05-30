package gov.cms.qpp.conversion.model;

import java.lang.annotation.Annotation;
import java.util.EnumMap;
import java.util.EnumSet;
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
 * <p>
 * R is the stored and return interface type.
 * V is the key type to access the registered values.
 *
 * @author David Uselmann
 */
public class Registry<R extends Object> {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(Registry.class);

	// For now this is static and can be refactored into an instance
	// variable when/if we have an orchestrator that instantiates an registry
	/**
	 * This will be an XPATH string to converter handler registration Since
	 * Converter was taken for the main stub, I chose Handler for now.
	 */
	private Map<TemplateId, Class<? extends R>> registryMap;

	private Class<? extends Annotation> annotationClass;

	/**
	 * initialize and configure the registry
	 */
	public Registry(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
		load();
	}

	/**
	 * load or reload registry contents
	 */
	public void load() {
		init();
		registerAnnotatedHandlers();
	}

	/**
	 * This is a helper method used for testing. Singletons have trouble in
	 * testing if they cannot be reset. It is package access to only allow
	 * classes in the same package, like tests, have access.
	 */
	void init() {
		registryMap = new EnumMap<>(TemplateId.class);
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
				for (TemplateId key : getTemplateIds(annotatedClass)) {
					register(key, (Class<R>) annotatedClass);
				}
			} catch (ClassNotFoundException e) {
				DEV_LOG.error("Failed to register new transformation handler because: ", e);
			}
		}
	}

	// This allows for testing the ClassNotFoundException
	protected Class<?> getAnnotatedClass(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}

	public Set<TemplateId> getTemplateIds(Class<?> annotatedClass) {
		Set<TemplateId> values = EnumSet.noneOf(TemplateId.class);
		Annotation annotation = AnnotationUtils.findAnnotation(annotatedClass, annotationClass);

		if (annotation instanceof Decoder) {
			Decoder decoder = (Decoder) annotation;
			values.add(decoder.value());
		}
		if (annotation instanceof Encoder) {
			Encoder encoder = (Encoder) annotation;
			values.add(encoder.value());
		}
		if (annotation instanceof Validator) {
			Validator validator = (Validator) annotation;
			values.add(validator.value());
		}
		return values;
	}

	/**
	 * This method will return a proper top level handler for the given XPATH
	 * Later iteration will examine the XPATH startsWith and return a most
	 * appropriate handler
	 *
	 * @param registryKey String
	 */
	public R get(TemplateId registryKey) {
		try {
			Class<? extends R> handlerClass = registryMap.get(registryKey);
			if (handlerClass == null) {
				return null;
			}
			return handlerClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			DEV_LOG.warn("Unable to instantiate the class", e);
			return null;
		}
	}

	/**
	 * Means ot register a new transformation handler
	 *
	 * @param registryKey String
	 * @param handler
	 */
	public void register(TemplateId registryKey, Class<? extends R> handler) {
		DEV_LOG.debug("Registering " + handler.getName() + " to '" + registryKey + "' for "
				+ annotationClass.getSimpleName() + ".");
		// This could be a class or class name and instantiated on lookup
		if (registryMap.containsKey(registryKey)) {
			DEV_LOG.error("Duplicate registered handler for " + registryKey
						+ " both " + registryMap.get(registryKey).getName()
						+ " and " + handler.getName());
		}
		
		registryMap.put(registryKey, handler);
	}

	public Set<TemplateId> getKeys() {
		return registryMap.keySet();
	}


	public int size() {
		return registryMap.size();
	}
}