package gov.cms.qpp.conversion.model;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.Context;

/**
 * This class manages the available transformation handlers. Currently it takes
 * the XPATH that the handler will transform.
 * <p>
 * R is the stored and return interface type.
 * V is the key type to access the registered values.
 *
 * @author David Uselmann
 */
public class Registry<R> {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(Registry.class);
	private static final Map<Class<?>, Function<Context, Object>> CONSTRUCTORS = new IdentityHashMap<>();
	private static final Map<Class<? extends Annotation>, Map<ComponentKey, Class<?>>> SHARED_REGISTRY_MAP
		= new ConcurrentHashMap<>();

	private final Context context;
	private final Map<ComponentKey, Class<?>> registryMap;
	private final Class<? extends Annotation> annotationClass;

	public Registry(Context context, Class<? extends Annotation> annotationClass) {
		this.context = context;
		this.annotationClass = annotationClass;
		this.registryMap = new HashMap<>(SHARED_REGISTRY_MAP.computeIfAbsent(annotationClass, this::lookupAnnotatedClasses));
	}

	private Map<ComponentKey, Class<?>> lookupAnnotatedClasses(Class<? extends Annotation> annotationClass) {
		Reflections reflections = new Reflections("gov.cms");
		Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotationClass);
		Map<ComponentKey, Class<?>> registry = new HashMap<>();

		for (Class<?> annotatedClass : annotatedClasses) {
			for (ComponentKey key : getComponentKeys(annotatedClass)) {
				registry.put(key, annotatedClass);
			}
		}

		return registry;
	}

	Set<ComponentKey> getComponentKeys(Class<?> annotatedClass) {
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
		return instantiateHandler(findHandler(registryKey));
	}

	/**
	 * Instantiate a given handler class.
	 *
	 * @param handlerClass the class to instantiate
	 * @return an instance of the given class
	 */
	private R instantiateHandler(Class<? extends R> handlerClass) {
		if (handlerClass == null) {
			return null;
		}

		return handlerClass.cast(CONSTRUCTORS.computeIfAbsent(handlerClass, this::createHandlerConstructor).apply(context));
	}

	private Function<Context, Object> createHandlerConstructor(Class<?> handlerClass) {
		try {
			try {
				Constructor<?> constructor = handlerClass.getConstructor(Context.class);
				MethodHandle handle = MethodHandles.lookup().unreflectConstructor(constructor)
						.asType(MethodType.methodType(Object.class, Context.class));

				return constructor(handle);
			} catch (NoSuchMethodException thatsOk) {
				Constructor<?> constructor = handlerClass.getConstructor();
				MethodHandle handle = MethodHandles.lookup().unreflectConstructor(constructor)
						.asType(MethodType.methodType(Object.class));

				return constructor(handle);
			}
		} catch (NoSuchMethodException | IllegalAccessException e) {
			DEV_LOG.warn("Unable to create constructor handle", e);
			return ignore -> null;
		}
	}

	private Function<Context, Object> constructor(MethodHandle handle) {
		return ignore -> {
			try {
				return handle.invokeExact();
			} catch (Exception codeProblem) {
				DEV_LOG.warn("Unable to invoke constructor handle {}", handle.type(), codeProblem);
				return null;
			} catch (Throwable severeRuntimeError) {
				throw new RuntimeException(severeRuntimeError);
			}
		};
	}

	/**
	 * Retrieve handlers that apply generally and specifically to the given template. The
	 *
	 * @param registryKey the template for which handlers will be searched
	 * @return all applicable handlers
	 */
	public Set<R> inclusiveGet(TemplateId registryKey) {
		return findHandlers(getKeys(registryKey, true)).stream()
				.map(this::instantiateHandler)
				.collect(Collectors.toCollection(LinkedHashSet<R>::new));
	}

	/**
	 * Get a template specific list that specifies the order in which handler classes will be searched.
	 *
	 * @param registryKey a template id
	 * @param generalPriority specify the order of specificity i.e. general first or program specific first.
	 * @return list of component keys
	 */
	private List<ComponentKey> getKeys(TemplateId registryKey, boolean generalPriority) {
		Program contextProgram = context.getProgram();
		if (contextProgram == Program.ALL) {
			return Collections.singletonList(new ComponentKey(registryKey, contextProgram));
		}

		List<ComponentKey> returnValue = Arrays.asList(
				new ComponentKey(registryKey, contextProgram),
				new ComponentKey(registryKey, Program.ALL));
		if (generalPriority) {
			Collections.reverse(returnValue);
		}
		return returnValue;
	}

	/**
	 * Retrieve a handler for the given template id
	 *
	 * @param registryKey template id
	 * @return handler i.e. {@link Validator}, {@link Decoder} or {@link Encoder}
	 */
	private Class<? extends R> findHandler(TemplateId registryKey) {
		return findHandlers(getKeys(registryKey, false))
				.stream()
				.findFirst()
				.orElse(null);
	}

	/**
	 * Find and return handler classes that correspond to the given component keys.
	 *
	 * @param keys a list of potential {@link Registry#registryMap} keys
	 * @return ordered set of handler classes
	 */
	private Set<Class<? extends R>> findHandlers(List<ComponentKey> keys) {
		Set<Class<? extends R>> handlers = new LinkedHashSet<>();
		keys.forEach(key -> {
			@SuppressWarnings("unchecked")
			Class<? extends R> handler = (Class<? extends R>) registryMap.get(key);
			if (handler != null) {
				handlers.add(handler);
			}
		});
		return handlers;
	}

	/**
	 * Means to register a new transformation handler
	 *
	 * @param registryKey key that identifies a component i.e. a {@link Validator}, {@link Decoder} or {@link Encoder}
	 * @param handler the keyed {@link Validator}, {@link Decoder} or {@link Encoder}
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