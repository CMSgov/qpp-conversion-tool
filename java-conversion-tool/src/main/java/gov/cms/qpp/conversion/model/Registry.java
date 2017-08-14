package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.util.ProgramContext;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	private Map<ComponentKey, Class<? extends R>> registryMap;

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
		registryMap = new HashMap<>();
	}

	/**
	 * This method will scan all classes for the annotation for
	 * TransformHandlers that need registration.
	 */
	@SuppressWarnings("unchecked")
	void registerAnnotatedHandlers() {
		Reflections reflections = new Reflections("gov.cms");
		Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotationClass);

		for (Class<?> annotatedClass : annotatedClasses) {
			for (ComponentKey key : getComponentKeys(annotatedClass)) {
				register(key, (Class<R>) annotatedClass);
			}
		}
	}

	public Set<ComponentKey> getComponentKeys(Class<?> annotatedClass) {
		Annotation annotation = annotatedClass.getAnnotation(annotationClass);
		Set<ComponentKey> values = new HashSet<>();

		if (annotation instanceof Decoder) {
			Decoder decoder = (Decoder) annotation;
			values.add(new ComponentKey(decoder.value(), decoder.program()));
		}
		if (annotation instanceof Encoder) {
			Encoder encoder = (Encoder) annotation;
			values.add(new ComponentKey(encoder.value(), encoder.program()));
		}
		if (annotation instanceof Validator) {
			Validator validator = (Validator) annotation;
			values.add(new ComponentKey(validator.value(), validator.program()));
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
		ComponentKey key = getComponentKey(registryKey);
		try {
			Class<? extends R> handlerClass = registryMap.get(key);
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
	 * Create a ComponentKey using templateId and stashed Program.
	 *
	 * @param registryKey a template id
	 * @return a component key
	 */
	private ComponentKey getComponentKey(TemplateId registryKey) {
		Program program = ProgramContext.get();
		return new ComponentKey(registryKey, program);
	}

	/**
	 * Means ot register a new transformation handler
	 *
	 * @param registryKey key that identifies a component i.e. a {@link Validator}, {@link Decoder} or {@link Encoder}
	 * @param handler
	 */
	public void register(ComponentKey registryKey, Class<? extends R> handler) {
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

	public int size() {
		return registryMap.size();
	}
}